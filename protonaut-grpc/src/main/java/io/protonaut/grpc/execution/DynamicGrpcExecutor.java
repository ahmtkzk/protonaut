package io.protonaut.grpc.execution;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.MetadataUtils;
import io.protonaut.core.metadata.MetadataGenerator;
import io.protonaut.core.models.ExecutionRequest;
import io.protonaut.core.models.ExecutionResponse;
import io.protonaut.core.models.GrpcStatusModel;
import io.protonaut.core.spi.GrpcExecutionProvider;
import io.protonaut.grpc.metadata.MetadataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicGrpcExecutor implements GrpcExecutionProvider {
    private static final Logger log = LoggerFactory.getLogger(DynamicGrpcExecutor.class);

    private volatile Channel channel;
    private final Map<String, MethodRegistration> methodRegistry = new ConcurrentHashMap<>();
    private final JsonFormat.Parser jsonParser;
    private final JsonFormat.Printer jsonPrinter;

    public record MethodRegistration(
            String fullMethodName,
            Descriptors.MethodDescriptor protoMethod,
            MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethod
    ) {}

    public DynamicGrpcExecutor(Channel channel) {
        this.channel = channel;
        this.jsonParser = JsonFormat.parser().ignoringUnknownFields();
        this.jsonPrinter = JsonFormat.printer()
                .includingDefaultValueFields()
                .preservingProtoFieldNames();
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void registerService(Descriptors.ServiceDescriptor serviceDesc) {
        if (serviceDesc == null) return;
        for (Descriptors.MethodDescriptor method : serviceDesc.getMethods()) {
            registerMethod(method);
        }
    }

    public void registerMethod(Descriptors.MethodDescriptor methodDesc) {
        String fullMethodName = methodDesc.getService().getFullName() + "/" + methodDesc.getName();

        MethodDescriptor.MethodType type = switch (methodDesc.toProto().getClientStreaming() ? 1 : 0 | (methodDesc.toProto().getServerStreaming() ? 2 : 0)) {
            case 3 -> MethodDescriptor.MethodType.BIDI_STREAMING;
            case 2 -> MethodDescriptor.MethodType.SERVER_STREAMING;
            case 1 -> MethodDescriptor.MethodType.CLIENT_STREAMING;
            default -> MethodDescriptor.MethodType.UNARY;
        };

        MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethod = MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setType(type)
                .setFullMethodName(fullMethodName)
                .setRequestMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(methodDesc.getInputType())))
                .setResponseMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(methodDesc.getOutputType())))
                .build();

        MethodRegistration reg = new MethodRegistration(fullMethodName, methodDesc, grpcMethod);
        methodRegistry.put(fullMethodName, reg);
        methodRegistry.put(methodDesc.getName(), reg);
        methodRegistry.put(methodDesc.getService().getName() + "/" + methodDesc.getName(), reg);
    }

    @Override
    public ExecutionResponse execute(ExecutionRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.nanoTime();

        // 1. Resolve method registration
        String lookupKey = request.methodId();
        if (lookupKey == null && request.serviceId() != null) {
            lookupKey = request.serviceId();
        } else if (lookupKey != null && !lookupKey.contains("/") && request.serviceId() != null) {
            lookupKey = request.serviceId() + "/" + lookupKey;
        }

        MethodRegistration registration = methodRegistry.get(lookupKey);
        if (registration == null) {
            // Try matching method name ignoring case
            String finalLookupKey = lookupKey;
            registration = methodRegistry.values().stream()
                    .filter(m -> m.fullMethodName().equalsIgnoreCase(finalLookupKey) ||
                                 m.protoMethod().getName().equalsIgnoreCase(finalLookupKey))
                    .findFirst()
                    .orElse(null);
        }

        if (registration == null) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            return ExecutionResponse.failure(
                    requestId,
                    request.serviceId(),
                    request.methodId(),
                    new GrpcStatusModel(Status.Code.NOT_FOUND.value(), "NOT_FOUND", "Method not registered: " + lookupKey, null),
                    durationMs,
                    "Method not registered in Protonaut gRPC registry: " + lookupKey,
                    Map.of(),
                    Map.of()
            );
        }

        // 2. Resolve metadata & dynamic tokens
        Map<String, String> resolvedHeaders = MetadataGenerator.resolveMetadata(request.metadata());
        Metadata requestMetadata = MetadataConverter.toGrpcMetadata(resolvedHeaders);

        // 3. Parse JSON to DynamicMessage
        DynamicMessage requestMessage;
        try {
            DynamicMessage.Builder msgBuilder = DynamicMessage.newBuilder(registration.protoMethod().getInputType());
            String json = (request.requestJson() != null && !request.requestJson().isBlank())
                    ? request.requestJson()
                    : "{}";
            jsonParser.merge(json, msgBuilder);
            requestMessage = msgBuilder.build();
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            return ExecutionResponse.failure(
                    requestId,
                    request.serviceId(),
                    request.methodId(),
                    new GrpcStatusModel(Status.Code.INVALID_ARGUMENT.value(), "INVALID_ARGUMENT", e.getMessage(), null),
                    durationMs,
                    "Failed to parse JSON into Protobuf request: " + e.getMessage(),
                    Map.of(),
                    Map.of()
            );
        }

        // 4. Prepare execution with timeout deadline & metadata interceptors
        AtomicReference<Metadata> responseHeadersRef = new AtomicReference<>(new Metadata());
        AtomicReference<Metadata> responseTrailersRef = new AtomicReference<>(new Metadata());

        ClientInterceptor metadataInterceptor = new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    MethodDescriptor<ReqT, RespT> method,
                    CallOptions callOptions,
                    Channel next) {
                return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        headers.merge(requestMetadata);
                        super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                            @Override
                            public void onHeaders(Metadata headers) {
                                responseHeadersRef.set(headers);
                                super.onHeaders(headers);
                            }

                            @Override
                            public void onClose(Status status, Metadata trailers) {
                                responseTrailersRef.set(trailers);
                                super.onClose(status, trailers);
                            }
                        }, headers);
                    }
                };
            }
        };

        Channel interceptedChannel = ClientInterceptors.intercept(channel, metadataInterceptor);
        int timeoutSeconds = request.timeoutSeconds() != null ? request.timeoutSeconds() : 30;
        CallOptions callOptions = CallOptions.DEFAULT.withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS);

        // 5. Execute gRPC call
        try {
            if (registration.grpcMethod().getType() == MethodDescriptor.MethodType.UNARY) {
                DynamicMessage responseMsg = ClientCalls.blockingUnaryCall(
                        interceptedChannel,
                        registration.grpcMethod(),
                        callOptions,
                        requestMessage
                );

                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                String responseJson = jsonPrinter.print(responseMsg);
                long responseSizeBytes = responseJson.getBytes(StandardCharsets.UTF_8).length;

                Map<String, String> respHeaders = MetadataConverter.toMap(responseHeadersRef.get());
                Map<String, String> respTrailers = MetadataConverter.toMap(responseTrailersRef.get());

                return ExecutionResponse.success(
                        requestId,
                        request.serviceId(),
                        request.methodId(),
                        responseJson,
                        durationMs,
                        responseSizeBytes,
                        respHeaders,
                        respTrailers
                );
            } else if (registration.grpcMethod().getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
                Iterator<DynamicMessage> streamIter = ClientCalls.blockingServerStreamingCall(
                        interceptedChannel,
                        registration.grpcMethod(),
                        callOptions,
                        requestMessage
                );

                List<String> messages = new ArrayList<>();
                long totalBytes = 0;
                while (streamIter.hasNext()) {
                    DynamicMessage msg = streamIter.next();
                    String msgJson = jsonPrinter.print(msg);
                    messages.add(msgJson);
                    totalBytes += msgJson.getBytes(StandardCharsets.UTF_8).length;
                }

                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                String combinedJson = "[" + String.join(",\n", messages) + "]";

                Map<String, String> respHeaders = MetadataConverter.toMap(responseHeadersRef.get());
                Map<String, String> respTrailers = MetadataConverter.toMap(responseTrailersRef.get());

                return new ExecutionResponse(
                        requestId,
                        request.serviceId(),
                        request.methodId(),
                        combinedJson,
                        GrpcStatusModel.OK,
                        durationMs,
                        totalBytes,
                        respHeaders,
                        respTrailers,
                        messages,
                        true,
                        null
                );
            } else {
                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                return ExecutionResponse.failure(
                        requestId,
                        request.serviceId(),
                        request.methodId(),
                        new GrpcStatusModel(Status.Code.UNIMPLEMENTED.value(), "UNIMPLEMENTED", "Streaming mode " + registration.grpcMethod().getType() + " interactive execution not supported yet", null),
                        durationMs,
                        "Client streaming and Bidi streaming methods must be invoked via streaming channels",
                        MetadataConverter.toMap(responseHeadersRef.get()),
                        MetadataConverter.toMap(responseTrailersRef.get())
                );
            }
        } catch (StatusRuntimeException e) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            Status status = e.getStatus();
            GrpcStatusModel statusModel = new GrpcStatusModel(
                    status.getCode().value(),
                    status.getCode().name(),
                    status.getDescription() != null ? status.getDescription() : status.getCode().name(),
                    status.getCause() != null ? status.getCause().getMessage() : null
            );

            Map<String, String> respHeaders = MetadataConverter.toMap(responseHeadersRef.get());
            Map<String, String> respTrailers = e.getTrailers() != null
                    ? MetadataConverter.toMap(e.getTrailers())
                    : MetadataConverter.toMap(responseTrailersRef.get());

            return ExecutionResponse.failure(
                    requestId,
                    request.serviceId(),
                    request.methodId(),
                    statusModel,
                    durationMs,
                    status.getDescription() != null ? status.getDescription() : status.getCode().name(),
                    respHeaders,
                    respTrailers
            );
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            GrpcStatusModel statusModel = new GrpcStatusModel(
                    Status.Code.INTERNAL.value(),
                    "INTERNAL",
                    e.getMessage(),
                    e.getClass().getName()
            );

            return ExecutionResponse.failure(
                    requestId,
                    request.serviceId(),
                    request.methodId(),
                    statusModel,
                    durationMs,
                    e.getMessage(),
                    MetadataConverter.toMap(responseHeadersRef.get()),
                    MetadataConverter.toMap(responseTrailersRef.get())
            );
        }
    }
}
