package io.protonaut.example.warehouse;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: warehouse.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InventoryServiceGrpc {

  private InventoryServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "protonaut.warehouse.InventoryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.warehouse.StockCheckRequest,
      io.protonaut.example.warehouse.StockCheckResponse> getCheckStockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckStock",
      requestType = io.protonaut.example.warehouse.StockCheckRequest.class,
      responseType = io.protonaut.example.warehouse.StockCheckResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.warehouse.StockCheckRequest,
      io.protonaut.example.warehouse.StockCheckResponse> getCheckStockMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.warehouse.StockCheckRequest, io.protonaut.example.warehouse.StockCheckResponse> getCheckStockMethod;
    if ((getCheckStockMethod = InventoryServiceGrpc.getCheckStockMethod) == null) {
      synchronized (InventoryServiceGrpc.class) {
        if ((getCheckStockMethod = InventoryServiceGrpc.getCheckStockMethod) == null) {
          InventoryServiceGrpc.getCheckStockMethod = getCheckStockMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.warehouse.StockCheckRequest, io.protonaut.example.warehouse.StockCheckResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckStock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.StockCheckRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.StockCheckResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryServiceMethodDescriptorSupplier("CheckStock"))
              .build();
        }
      }
    }
    return getCheckStockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.warehouse.ReserveStockRequest,
      io.protonaut.example.warehouse.ReserveStockResponse> getReserveStockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReserveStock",
      requestType = io.protonaut.example.warehouse.ReserveStockRequest.class,
      responseType = io.protonaut.example.warehouse.ReserveStockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.warehouse.ReserveStockRequest,
      io.protonaut.example.warehouse.ReserveStockResponse> getReserveStockMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.warehouse.ReserveStockRequest, io.protonaut.example.warehouse.ReserveStockResponse> getReserveStockMethod;
    if ((getReserveStockMethod = InventoryServiceGrpc.getReserveStockMethod) == null) {
      synchronized (InventoryServiceGrpc.class) {
        if ((getReserveStockMethod = InventoryServiceGrpc.getReserveStockMethod) == null) {
          InventoryServiceGrpc.getReserveStockMethod = getReserveStockMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.warehouse.ReserveStockRequest, io.protonaut.example.warehouse.ReserveStockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReserveStock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.ReserveStockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.warehouse.ReserveStockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryServiceMethodDescriptorSupplier("ReserveStock"))
              .build();
        }
      }
    }
    return getReserveStockMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InventoryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryServiceStub>() {
        @java.lang.Override
        public InventoryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryServiceStub(channel, callOptions);
        }
      };
    return InventoryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static InventoryServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryServiceBlockingV2Stub>() {
        @java.lang.Override
        public InventoryServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return InventoryServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InventoryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryServiceBlockingStub>() {
        @java.lang.Override
        public InventoryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryServiceBlockingStub(channel, callOptions);
        }
      };
    return InventoryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InventoryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryServiceFutureStub>() {
        @java.lang.Override
        public InventoryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryServiceFutureStub(channel, callOptions);
        }
      };
    return InventoryServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void checkStock(io.protonaut.example.warehouse.StockCheckRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.StockCheckResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckStockMethod(), responseObserver);
    }

    /**
     */
    default void reserveStock(io.protonaut.example.warehouse.ReserveStockRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.ReserveStockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReserveStockMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service InventoryService.
   */
  public static abstract class InventoryServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return InventoryServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service InventoryService.
   */
  public static final class InventoryServiceStub
      extends io.grpc.stub.AbstractAsyncStub<InventoryServiceStub> {
    private InventoryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryServiceStub(channel, callOptions);
    }

    /**
     */
    public void checkStock(io.protonaut.example.warehouse.StockCheckRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.StockCheckResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckStockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reserveStock(io.protonaut.example.warehouse.ReserveStockRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.ReserveStockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReserveStockMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service InventoryService.
   */
  public static final class InventoryServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<InventoryServiceBlockingV2Stub> {
    private InventoryServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.warehouse.StockCheckResponse checkStock(io.protonaut.example.warehouse.StockCheckRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckStockMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.protonaut.example.warehouse.ReserveStockResponse reserveStock(io.protonaut.example.warehouse.ReserveStockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReserveStockMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service InventoryService.
   */
  public static final class InventoryServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<InventoryServiceBlockingStub> {
    private InventoryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.warehouse.StockCheckResponse checkStock(io.protonaut.example.warehouse.StockCheckRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckStockMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.protonaut.example.warehouse.ReserveStockResponse reserveStock(io.protonaut.example.warehouse.ReserveStockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReserveStockMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service InventoryService.
   */
  public static final class InventoryServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<InventoryServiceFutureStub> {
    private InventoryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.warehouse.StockCheckResponse> checkStock(
        io.protonaut.example.warehouse.StockCheckRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckStockMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.warehouse.ReserveStockResponse> reserveStock(
        io.protonaut.example.warehouse.ReserveStockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReserveStockMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_STOCK = 0;
  private static final int METHODID_RESERVE_STOCK = 1;

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
        case METHODID_CHECK_STOCK:
          serviceImpl.checkStock((io.protonaut.example.warehouse.StockCheckRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.StockCheckResponse>) responseObserver);
          break;
        case METHODID_RESERVE_STOCK:
          serviceImpl.reserveStock((io.protonaut.example.warehouse.ReserveStockRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.warehouse.ReserveStockResponse>) responseObserver);
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
          getCheckStockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.warehouse.StockCheckRequest,
              io.protonaut.example.warehouse.StockCheckResponse>(
                service, METHODID_CHECK_STOCK)))
        .addMethod(
          getReserveStockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.warehouse.ReserveStockRequest,
              io.protonaut.example.warehouse.ReserveStockResponse>(
                service, METHODID_RESERVE_STOCK)))
        .build();
  }

  private static abstract class InventoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InventoryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.warehouse.WarehouseProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InventoryService");
    }
  }

  private static final class InventoryServiceFileDescriptorSupplier
      extends InventoryServiceBaseDescriptorSupplier {
    InventoryServiceFileDescriptorSupplier() {}
  }

  private static final class InventoryServiceMethodDescriptorSupplier
      extends InventoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    InventoryServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (InventoryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InventoryServiceFileDescriptorSupplier())
              .addMethod(getCheckStockMethod())
              .addMethod(getReserveStockMethod())
              .build();
        }
      }
    }
    return result;
  }
}
