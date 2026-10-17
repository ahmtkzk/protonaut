package io.protonaut.example.product;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service providing catalog product querying capabilities
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: product.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProductServiceGrpc {

  private ProductServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "io.protonaut.example.product.ProductService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.product.GetProductRequest,
      io.protonaut.example.product.Product> getGetProductMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetProduct",
      requestType = io.protonaut.example.product.GetProductRequest.class,
      responseType = io.protonaut.example.product.Product.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.product.GetProductRequest,
      io.protonaut.example.product.Product> getGetProductMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.product.GetProductRequest, io.protonaut.example.product.Product> getGetProductMethod;
    if ((getGetProductMethod = ProductServiceGrpc.getGetProductMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getGetProductMethod = ProductServiceGrpc.getGetProductMethod) == null) {
          ProductServiceGrpc.getGetProductMethod = getGetProductMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.product.GetProductRequest, io.protonaut.example.product.Product>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetProduct"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.product.GetProductRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.product.Product.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("GetProduct"))
              .build();
        }
      }
    }
    return getGetProductMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.product.ListProductsRequest,
      io.protonaut.example.product.Product> getListProductsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListProducts",
      requestType = io.protonaut.example.product.ListProductsRequest.class,
      responseType = io.protonaut.example.product.Product.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.protonaut.example.product.ListProductsRequest,
      io.protonaut.example.product.Product> getListProductsMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.product.ListProductsRequest, io.protonaut.example.product.Product> getListProductsMethod;
    if ((getListProductsMethod = ProductServiceGrpc.getListProductsMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getListProductsMethod = ProductServiceGrpc.getListProductsMethod) == null) {
          ProductServiceGrpc.getListProductsMethod = getListProductsMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.product.ListProductsRequest, io.protonaut.example.product.Product>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListProducts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.product.ListProductsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.product.Product.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("ListProducts"))
              .build();
        }
      }
    }
    return getListProductsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProductServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceStub>() {
        @java.lang.Override
        public ProductServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceStub(channel, callOptions);
        }
      };
    return ProductServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static ProductServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingV2Stub>() {
        @java.lang.Override
        public ProductServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return ProductServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProductServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingStub>() {
        @java.lang.Override
        public ProductServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceBlockingStub(channel, callOptions);
        }
      };
    return ProductServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProductServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceFutureStub>() {
        @java.lang.Override
        public ProductServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceFutureStub(channel, callOptions);
        }
      };
    return ProductServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Retrieve product details by unique product identifier
     * </pre>
     */
    default void getProduct(io.protonaut.example.product.GetProductRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.product.Product> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetProductMethod(), responseObserver);
    }

    /**
     * <pre>
     * Stream products matching the category and availability filters
     * </pre>
     */
    default void listProducts(io.protonaut.example.product.ListProductsRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.product.Product> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListProductsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ProductService.
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public static abstract class ProductServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ProductServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ProductService.
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public static final class ProductServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ProductServiceStub> {
    private ProductServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve product details by unique product identifier
     * </pre>
     */
    public void getProduct(io.protonaut.example.product.GetProductRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.product.Product> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetProductMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Stream products matching the category and availability filters
     * </pre>
     */
    public void listProducts(io.protonaut.example.product.ListProductsRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.product.Product> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getListProductsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ProductService.
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public static final class ProductServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<ProductServiceBlockingV2Stub> {
    private ProductServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve product details by unique product identifier
     * </pre>
     */
    public io.protonaut.example.product.Product getProduct(io.protonaut.example.product.GetProductRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetProductMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stream products matching the category and availability filters
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, io.protonaut.example.product.Product>
        listProducts(io.protonaut.example.product.ListProductsRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getListProductsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service ProductService.
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public static final class ProductServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ProductServiceBlockingStub> {
    private ProductServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve product details by unique product identifier
     * </pre>
     */
    public io.protonaut.example.product.Product getProduct(io.protonaut.example.product.GetProductRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetProductMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stream products matching the category and availability filters
     * </pre>
     */
    public java.util.Iterator<io.protonaut.example.product.Product> listProducts(
        io.protonaut.example.product.ListProductsRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getListProductsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ProductService.
   * <pre>
   * Service providing catalog product querying capabilities
   * </pre>
   */
  public static final class ProductServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ProductServiceFutureStub> {
    private ProductServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve product details by unique product identifier
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.product.Product> getProduct(
        io.protonaut.example.product.GetProductRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetProductMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PRODUCT = 0;
  private static final int METHODID_LIST_PRODUCTS = 1;

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
        case METHODID_GET_PRODUCT:
          serviceImpl.getProduct((io.protonaut.example.product.GetProductRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.product.Product>) responseObserver);
          break;
        case METHODID_LIST_PRODUCTS:
          serviceImpl.listProducts((io.protonaut.example.product.ListProductsRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.product.Product>) responseObserver);
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
          getGetProductMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.product.GetProductRequest,
              io.protonaut.example.product.Product>(
                service, METHODID_GET_PRODUCT)))
        .addMethod(
          getListProductsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              io.protonaut.example.product.ListProductsRequest,
              io.protonaut.example.product.Product>(
                service, METHODID_LIST_PRODUCTS)))
        .build();
  }

  private static abstract class ProductServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProductServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.product.ProductProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProductService");
    }
  }

  private static final class ProductServiceFileDescriptorSupplier
      extends ProductServiceBaseDescriptorSupplier {
    ProductServiceFileDescriptorSupplier() {}
  }

  private static final class ProductServiceMethodDescriptorSupplier
      extends ProductServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ProductServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (ProductServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProductServiceFileDescriptorSupplier())
              .addMethod(getGetProductMethod())
              .addMethod(getListProductsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
