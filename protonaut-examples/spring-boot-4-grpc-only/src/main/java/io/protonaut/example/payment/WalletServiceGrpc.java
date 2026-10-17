package io.protonaut.example.payment;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: payment.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class WalletServiceGrpc {

  private WalletServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "protonaut.payment.WalletService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.payment.BalanceRequest,
      io.protonaut.example.payment.BalanceResponse> getGetBalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBalance",
      requestType = io.protonaut.example.payment.BalanceRequest.class,
      responseType = io.protonaut.example.payment.BalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.payment.BalanceRequest,
      io.protonaut.example.payment.BalanceResponse> getGetBalanceMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.payment.BalanceRequest, io.protonaut.example.payment.BalanceResponse> getGetBalanceMethod;
    if ((getGetBalanceMethod = WalletServiceGrpc.getGetBalanceMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getGetBalanceMethod = WalletServiceGrpc.getGetBalanceMethod) == null) {
          WalletServiceGrpc.getGetBalanceMethod = getGetBalanceMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.payment.BalanceRequest, io.protonaut.example.payment.BalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.payment.BalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.payment.BalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("GetBalance"))
              .build();
        }
      }
    }
    return getGetBalanceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.payment.TransferRequest,
      io.protonaut.example.payment.TransferResponse> getTransferFundsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransferFunds",
      requestType = io.protonaut.example.payment.TransferRequest.class,
      responseType = io.protonaut.example.payment.TransferResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.payment.TransferRequest,
      io.protonaut.example.payment.TransferResponse> getTransferFundsMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.payment.TransferRequest, io.protonaut.example.payment.TransferResponse> getTransferFundsMethod;
    if ((getTransferFundsMethod = WalletServiceGrpc.getTransferFundsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getTransferFundsMethod = WalletServiceGrpc.getTransferFundsMethod) == null) {
          WalletServiceGrpc.getTransferFundsMethod = getTransferFundsMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.payment.TransferRequest, io.protonaut.example.payment.TransferResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TransferFunds"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.payment.TransferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.payment.TransferResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("TransferFunds"))
              .build();
        }
      }
    }
    return getTransferFundsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WalletServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WalletServiceStub>() {
        @java.lang.Override
        public WalletServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WalletServiceStub(channel, callOptions);
        }
      };
    return WalletServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static WalletServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WalletServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WalletServiceBlockingV2Stub>() {
        @java.lang.Override
        public WalletServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WalletServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return WalletServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WalletServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WalletServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WalletServiceBlockingStub>() {
        @java.lang.Override
        public WalletServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WalletServiceBlockingStub(channel, callOptions);
        }
      };
    return WalletServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WalletServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WalletServiceFutureStub>() {
        @java.lang.Override
        public WalletServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WalletServiceFutureStub(channel, callOptions);
        }
      };
    return WalletServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getBalance(io.protonaut.example.payment.BalanceRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.payment.BalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBalanceMethod(), responseObserver);
    }

    /**
     */
    default void transferFunds(io.protonaut.example.payment.TransferRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.payment.TransferResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTransferFundsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service WalletService.
   */
  public static abstract class WalletServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return WalletServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service WalletService.
   */
  public static final class WalletServiceStub
      extends io.grpc.stub.AbstractAsyncStub<WalletServiceStub> {
    private WalletServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WalletServiceStub(channel, callOptions);
    }

    /**
     */
    public void getBalance(io.protonaut.example.payment.BalanceRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.payment.BalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transferFunds(io.protonaut.example.payment.TransferRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.payment.TransferResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTransferFundsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service WalletService.
   */
  public static final class WalletServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<WalletServiceBlockingV2Stub> {
    private WalletServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WalletServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.payment.BalanceResponse getBalance(io.protonaut.example.payment.BalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBalanceMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.protonaut.example.payment.TransferResponse transferFunds(io.protonaut.example.payment.TransferRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTransferFundsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service WalletService.
   */
  public static final class WalletServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<WalletServiceBlockingStub> {
    private WalletServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WalletServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.protonaut.example.payment.BalanceResponse getBalance(io.protonaut.example.payment.BalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBalanceMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.protonaut.example.payment.TransferResponse transferFunds(io.protonaut.example.payment.TransferRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTransferFundsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service WalletService.
   */
  public static final class WalletServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<WalletServiceFutureStub> {
    private WalletServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WalletServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.payment.BalanceResponse> getBalance(
        io.protonaut.example.payment.BalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.payment.TransferResponse> transferFunds(
        io.protonaut.example.payment.TransferRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTransferFundsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_BALANCE = 0;
  private static final int METHODID_TRANSFER_FUNDS = 1;

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
        case METHODID_GET_BALANCE:
          serviceImpl.getBalance((io.protonaut.example.payment.BalanceRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.payment.BalanceResponse>) responseObserver);
          break;
        case METHODID_TRANSFER_FUNDS:
          serviceImpl.transferFunds((io.protonaut.example.payment.TransferRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.payment.TransferResponse>) responseObserver);
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
          getGetBalanceMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.payment.BalanceRequest,
              io.protonaut.example.payment.BalanceResponse>(
                service, METHODID_GET_BALANCE)))
        .addMethod(
          getTransferFundsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.payment.TransferRequest,
              io.protonaut.example.payment.TransferResponse>(
                service, METHODID_TRANSFER_FUNDS)))
        .build();
  }

  private static abstract class WalletServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WalletServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.payment.PaymentProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WalletService");
    }
  }

  private static final class WalletServiceFileDescriptorSupplier
      extends WalletServiceBaseDescriptorSupplier {
    WalletServiceFileDescriptorSupplier() {}
  }

  private static final class WalletServiceMethodDescriptorSupplier
      extends WalletServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    WalletServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (WalletServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletServiceFileDescriptorSupplier())
              .addMethod(getGetBalanceMethod())
              .addMethod(getTransferFundsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
