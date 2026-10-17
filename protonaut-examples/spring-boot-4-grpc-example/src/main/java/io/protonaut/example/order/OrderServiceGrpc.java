package io.protonaut.example.order;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service providing order management capabilities
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: order.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class OrderServiceGrpc {

  private OrderServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "io.protonaut.example.order.OrderService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.order.CreateOrderRequest,
      io.protonaut.example.order.Order> getCreateOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateOrder",
      requestType = io.protonaut.example.order.CreateOrderRequest.class,
      responseType = io.protonaut.example.order.Order.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.order.CreateOrderRequest,
      io.protonaut.example.order.Order> getCreateOrderMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.order.CreateOrderRequest, io.protonaut.example.order.Order> getCreateOrderMethod;
    if ((getCreateOrderMethod = OrderServiceGrpc.getCreateOrderMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getCreateOrderMethod = OrderServiceGrpc.getCreateOrderMethod) == null) {
          OrderServiceGrpc.getCreateOrderMethod = getCreateOrderMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.order.CreateOrderRequest, io.protonaut.example.order.Order>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.CreateOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.Order.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("CreateOrder"))
              .build();
        }
      }
    }
    return getCreateOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.order.GetOrderRequest,
      io.protonaut.example.order.Order> getGetOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetOrder",
      requestType = io.protonaut.example.order.GetOrderRequest.class,
      responseType = io.protonaut.example.order.Order.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.order.GetOrderRequest,
      io.protonaut.example.order.Order> getGetOrderMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.order.GetOrderRequest, io.protonaut.example.order.Order> getGetOrderMethod;
    if ((getGetOrderMethod = OrderServiceGrpc.getGetOrderMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrderMethod = OrderServiceGrpc.getGetOrderMethod) == null) {
          OrderServiceGrpc.getGetOrderMethod = getGetOrderMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.order.GetOrderRequest, io.protonaut.example.order.Order>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.GetOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.Order.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("GetOrder"))
              .build();
        }
      }
    }
    return getGetOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.order.CancelOrderRequest,
      io.protonaut.example.order.Order> getCancelOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelOrder",
      requestType = io.protonaut.example.order.CancelOrderRequest.class,
      responseType = io.protonaut.example.order.Order.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.order.CancelOrderRequest,
      io.protonaut.example.order.Order> getCancelOrderMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.order.CancelOrderRequest, io.protonaut.example.order.Order> getCancelOrderMethod;
    if ((getCancelOrderMethod = OrderServiceGrpc.getCancelOrderMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getCancelOrderMethod = OrderServiceGrpc.getCancelOrderMethod) == null) {
          OrderServiceGrpc.getCancelOrderMethod = getCancelOrderMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.order.CancelOrderRequest, io.protonaut.example.order.Order>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.CancelOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.order.Order.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("CancelOrder"))
              .build();
        }
      }
    }
    return getCancelOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OrderServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub>() {
        @java.lang.Override
        public OrderServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceStub(channel, callOptions);
        }
      };
    return OrderServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static OrderServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingV2Stub>() {
        @java.lang.Override
        public OrderServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return OrderServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OrderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub>() {
        @java.lang.Override
        public OrderServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceBlockingStub(channel, callOptions);
        }
      };
    return OrderServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OrderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub>() {
        @java.lang.Override
        public OrderServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceFutureStub(channel, callOptions);
        }
      };
    return OrderServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Create and submit a new customer order
     * </pre>
     */
    default void createOrder(io.protonaut.example.order.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateOrderMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieve an existing order by identifier
     * </pre>
     */
    default void getOrder(io.protonaut.example.order.GetOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrderMethod(), responseObserver);
    }

    /**
     * <pre>
     * Cancel an existing order with a reason
     * </pre>
     */
    default void cancelOrder(io.protonaut.example.order.CancelOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelOrderMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service OrderService.
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public static abstract class OrderServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return OrderServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service OrderService.
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public static final class OrderServiceStub
      extends io.grpc.stub.AbstractAsyncStub<OrderServiceStub> {
    private OrderServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create and submit a new customer order
     * </pre>
     */
    public void createOrder(io.protonaut.example.order.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieve an existing order by identifier
     * </pre>
     */
    public void getOrder(io.protonaut.example.order.GetOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Cancel an existing order with a reason
     * </pre>
     */
    public void cancelOrder(io.protonaut.example.order.CancelOrderRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.order.Order> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service OrderService.
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public static final class OrderServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<OrderServiceBlockingV2Stub> {
    private OrderServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Create and submit a new customer order
     * </pre>
     */
    public io.protonaut.example.order.Order createOrder(io.protonaut.example.order.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrderMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieve an existing order by identifier
     * </pre>
     */
    public io.protonaut.example.order.Order getOrder(io.protonaut.example.order.GetOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Cancel an existing order with a reason
     * </pre>
     */
    public io.protonaut.example.order.Order cancelOrder(io.protonaut.example.order.CancelOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service OrderService.
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public static final class OrderServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<OrderServiceBlockingStub> {
    private OrderServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create and submit a new customer order
     * </pre>
     */
    public io.protonaut.example.order.Order createOrder(io.protonaut.example.order.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrderMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieve an existing order by identifier
     * </pre>
     */
    public io.protonaut.example.order.Order getOrder(io.protonaut.example.order.GetOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Cancel an existing order with a reason
     * </pre>
     */
    public io.protonaut.example.order.Order cancelOrder(io.protonaut.example.order.CancelOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service OrderService.
   * <pre>
   * Service providing order management capabilities
   * </pre>
   */
  public static final class OrderServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<OrderServiceFutureStub> {
    private OrderServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create and submit a new customer order
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.order.Order> createOrder(
        io.protonaut.example.order.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieve an existing order by identifier
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.order.Order> getOrder(
        io.protonaut.example.order.GetOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Cancel an existing order with a reason
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.order.Order> cancelOrder(
        io.protonaut.example.order.CancelOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_ORDER = 0;
  private static final int METHODID_GET_ORDER = 1;
  private static final int METHODID_CANCEL_ORDER = 2;

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
        case METHODID_CREATE_ORDER:
          serviceImpl.createOrder((io.protonaut.example.order.CreateOrderRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.order.Order>) responseObserver);
          break;
        case METHODID_GET_ORDER:
          serviceImpl.getOrder((io.protonaut.example.order.GetOrderRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.order.Order>) responseObserver);
          break;
        case METHODID_CANCEL_ORDER:
          serviceImpl.cancelOrder((io.protonaut.example.order.CancelOrderRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.order.Order>) responseObserver);
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
          getCreateOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.order.CreateOrderRequest,
              io.protonaut.example.order.Order>(
                service, METHODID_CREATE_ORDER)))
        .addMethod(
          getGetOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.order.GetOrderRequest,
              io.protonaut.example.order.Order>(
                service, METHODID_GET_ORDER)))
        .addMethod(
          getCancelOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.order.CancelOrderRequest,
              io.protonaut.example.order.Order>(
                service, METHODID_CANCEL_ORDER)))
        .build();
  }

  private static abstract class OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OrderServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.order.OrderProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OrderService");
    }
  }

  private static final class OrderServiceFileDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier {
    OrderServiceFileDescriptorSupplier() {}
  }

  private static final class OrderServiceMethodDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    OrderServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (OrderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OrderServiceFileDescriptorSupplier())
              .addMethod(getCreateOrderMethod())
              .addMethod(getGetOrderMethod())
              .addMethod(getCancelOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
