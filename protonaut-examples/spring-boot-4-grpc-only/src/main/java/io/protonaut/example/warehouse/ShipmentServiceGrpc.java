package io.protonaut.example.warehouse;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: warehouse.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ShipmentServiceGrpc {

  private ShipmentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "protonaut.warehouse.ShipmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.warehouse.TrackShipmentRequest,
      io.protonaut.example.warehouse.TrackShipmentResponse> getTrackShipmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TrackShipment",
      requestType = io.protonaut.example.warehouse.TrackShipmentRequest.class,
      responseType = io.protonaut.example.warehouse.TrackShipmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.warehouse.TrackShipmentRequest,
      io.protonaut.example.warehouse.TrackShipmentResponse> getTrackShipmentMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.warehouse.TrackShipmentRequest, io.protonaut.example.warehouse.TrackShipmentResponse> getTrackShipmentMethod;
    if ((getTrackShipmentMethod = ShipmentServiceGrpc.getTrackShipmentMethod) == null) {
      synchronized (ShipmentServiceGrpc.class) {
        if ((getTrackShipmentMethod = ShipmentServiceGrpc.getTrackShipmentMethod) == null) {
          ShipmentServiceGrpc.getTrackShipmentMethod = getTrackShipmentMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.warehouse.TrackShipmentRequest, io.protonaut.example.warehouse.TrackShipmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TrackShipment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.TrackShipmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.TrackShipmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ShipmentServiceMethodDescriptorSupplier("TrackShipment"))
              .build();
        }
      }
    }
    return getTrackShipmentMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ShipmentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceStub>() {
        @java.lang.Override
        public ShipmentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ShipmentServiceStub(channel, callOptions);
        }
      };
    return ShipmentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static ShipmentServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceBlockingV2Stub>() {
        @java.lang.Override
        public ShipmentServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ShipmentServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return ShipmentServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ShipmentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceBlockingStub>() {
        @java.lang.Override
        public ShipmentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ShipmentServiceBlockingStub(channel, callOptions);
        }
      };
    return ShipmentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ShipmentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ShipmentServiceFutureStub>() {
        @java.lang.Override
        public ShipmentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ShipmentServiceFutureStub(channel, callOptions);
        }
      };
    return ShipmentServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void trackShipment(io.protonaut.example.warehouse.TrackShipmentRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.TrackShipmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTrackShipmentMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ShipmentService.
   */
  public static abstract class ShipmentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ShipmentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ShipmentService.
   */
  public static final class ShipmentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ShipmentServiceStub> {
    private ShipmentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShipmentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ShipmentServiceStub(channel, callOptions);
    }

    /**
     */
    public void trackShipment(io.protonaut.example.warehouse.TrackShipmentRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.TrackShipmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTrackShipmentMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ShipmentService.
   */
  public static final class ShipmentServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<ShipmentServiceBlockingV2Stub> {
    private ShipmentServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShipmentServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ShipmentServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.warehouse.TrackShipmentResponse trackShipment(io.protonaut.example.warehouse.TrackShipmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTrackShipmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service ShipmentService.
   */
  public static final class ShipmentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ShipmentServiceBlockingStub> {
    private ShipmentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShipmentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ShipmentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.warehouse.TrackShipmentResponse trackShipment(io.protonaut.example.warehouse.TrackShipmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTrackShipmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ShipmentService.
   */
  public static final class ShipmentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ShipmentServiceFutureStub> {
    private ShipmentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ShipmentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ShipmentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.warehouse.TrackShipmentResponse> trackShipment(
        io.protonaut.example.warehouse.TrackShipmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTrackShipmentMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRACK_SHIPMENT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRACK_SHIPMENT:
          serviceImpl.trackShipment((io.protonaut.example.warehouse.TrackShipmentRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.TrackShipmentResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getTrackShipmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.warehouse.TrackShipmentRequest,
              io.protonaut.example.warehouse.TrackShipmentResponse>(
                service, METHODID_TRACK_SHIPMENT)))
        .build();
  }

  private static abstract class ShipmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ShipmentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.warehouse.WarehouseProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ShipmentService");
    }
  }

  private static final class ShipmentServiceFileDescriptorSupplier
      extends ShipmentServiceBaseDescriptorSupplier {
    ShipmentServiceFileDescriptorSupplier() {}
  }

  private static final class ShipmentServiceMethodDescriptorSupplier
      extends ShipmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ShipmentServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ShipmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ShipmentServiceFileDescriptorSupplier())
              .addMethod(getTrackShipmentMethod())
              .build();
        }
      }
    }
    return result;
  }
}
