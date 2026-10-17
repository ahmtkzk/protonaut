package io.protonaut.codegen.generators;

import io.protonaut.core.models.FieldDescriptorModel;
import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.Map;

public class JavaClientCodeGenerator implements ClientCodeGenerator {

    @Override
    public String getLanguage() {
        return "java";
    }

    @Override
    public String getDisplayName() {
        return "Java (grpc-java)";
    }

    @Override
    public String generate(
            ServiceDescriptorModel service,
            MethodDescriptorModel method,
            String sampleJson,
            Map<String, String> metadata,
            String host,
            int port
    ) {
        String serviceName = service.name();
        String methodName = method.name();
        String inputType = method.inputType() != null ? method.inputType().name() : "Request";
        String outputType = method.outputType() != null ? method.outputType().name() : "Response";
        String pkg = service.packageName() != null && !service.packageName().isEmpty() ? service.packageName() : "com.example";

        StringBuilder sb = new StringBuilder();
        sb.append("package client;\n\n");
        sb.append("import io.grpc.*;\n");
        sb.append("import io.grpc.stub.MetadataUtils;\n");
        sb.append("import java.util.concurrent.TimeUnit;\n\n");
        sb.append("public class ").append(serviceName).append("ClientExample {\n");
        sb.append("    public static void main(String[] args) throws Exception {\n");
        sb.append("        // 1. Initialize ManagedChannel\n");
        sb.append("        ManagedChannel channel = ManagedChannelBuilder.forAddress(\"")
                .append(host != null ? host : "localhost").append("\", ").append(port > 0 ? port : 6570)
                .append(")\n");
        sb.append("                .usePlaintext()\n");
        sb.append("                .build();\n\n");

        sb.append("        try {\n");
        sb.append("            // 2. Attach Custom Metadata Headers\n");
        sb.append("            Metadata headers = new Metadata();\n");
        if (metadata != null && !metadata.isEmpty()) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("            headers.put(Metadata.Key.of(\"").append(entry.getKey())
                        .append("\", Metadata.ASCII_STRING_MARSHALLER), \"").append(entry.getValue()).append("\");\n");
            }
        } else {
            sb.append("            headers.put(Metadata.Key.of(\"authorization\", Metadata.ASCII_STRING_MARSHALLER), \"Bearer <TOKEN>\");\n");
        }
        sb.append("            Channel interceptedChannel = ClientInterceptors.intercept(channel, MetadataUtils.newAttachHeadersInterceptor(headers));\n\n");

        sb.append("            // 3. Create gRPC BlockingStub\n");
        sb.append("            // Note: Ensure compiled stubs from protobuf are on your classpath\n");
        sb.append("            var stub = ").append(serviceName).append("Grpc.newBlockingStub(interceptedChannel)\n");
        sb.append("                    .withDeadlineAfter(30, TimeUnit.SECONDS);\n\n");

        sb.append("            // 4. Build Request Message\n");
        sb.append("            var request = ").append(inputType).append(".newBuilder()\n");
        if (method.inputType() != null && !method.inputType().fields().isEmpty()) {
            for (FieldDescriptorModel f : method.inputType().fields()) {
                String setter = "set" + Character.toUpperCase(f.name().charAt(0)) + f.name().substring(1);
                if (f.type().isNumeric()) {
                    sb.append("                    .").append(setter).append("(100)\n");
                } else if (f.type().isBoolean()) {
                    sb.append("                    .").append(setter).append("(true)\n");
                } else if (f.type().isString()) {
                    sb.append("                    .").append(setter).append("(\"Sample Value\")\n");
                }
            }
        }
        sb.append("                    .build();\n\n");

        sb.append("            // 5. Execute Method Invocation\n");
        if (method.type().isServerStreaming()) {
            sb.append("            var responses = stub.").append(Character.toLowerCase(methodName.charAt(0))).append(methodName.substring(1)).append("(request);\n");
            sb.append("            responses.forEachRemaining(res -> System.out.println(\"Stream item: \" + res));\n");
        } else {
            sb.append("            var response = stub.").append(Character.toLowerCase(methodName.charAt(0))).append(methodName.substring(1)).append("(request);\n");
            sb.append("            System.out.println(\"Response: \" + response);\n");
        }

        sb.append("        } catch (StatusRuntimeException e) {\n");
        sb.append("            System.err.println(\"RPC Failed: \" + e.getStatus());\n");
        sb.append("        } finally {\n");
        sb.append("            // 6. Graceful Channel Shutdown\n");
        sb.append("            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }
}
