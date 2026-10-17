package io.protonaut.example.user;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service providing comprehensive user management capabilities
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: user.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "io.protonaut.example.user.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.user.GetUserRequest,
      io.protonaut.example.user.User> getGetUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUser",
      requestType = io.protonaut.example.user.GetUserRequest.class,
      responseType = io.protonaut.example.user.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.user.GetUserRequest,
      io.protonaut.example.user.User> getGetUserMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.user.GetUserRequest, io.protonaut.example.user.User> getGetUserMethod;
    if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
          UserServiceGrpc.getGetUserMethod = getGetUserMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.user.GetUserRequest, io.protonaut.example.user.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.GetUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.User.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUser"))
              .build();
        }
      }
    }
    return getGetUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.user.CreateUserRequest,
      io.protonaut.example.user.User> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateUser",
      requestType = io.protonaut.example.user.CreateUserRequest.class,
      responseType = io.protonaut.example.user.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.user.CreateUserRequest,
      io.protonaut.example.user.User> getCreateUserMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.user.CreateUserRequest, io.protonaut.example.user.User> getCreateUserMethod;
    if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
          UserServiceGrpc.getCreateUserMethod = getCreateUserMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.user.CreateUserRequest, io.protonaut.example.user.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.CreateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.User.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("CreateUser"))
              .build();
        }
      }
    }
    return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.user.UpdateUserRequest,
      io.protonaut.example.user.User> getUpdateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateUser",
      requestType = io.protonaut.example.user.UpdateUserRequest.class,
      responseType = io.protonaut.example.user.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.protonaut.example.user.UpdateUserRequest,
      io.protonaut.example.user.User> getUpdateUserMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.user.UpdateUserRequest, io.protonaut.example.user.User> getUpdateUserMethod;
    if ((getUpdateUserMethod = UserServiceGrpc.getUpdateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateUserMethod = UserServiceGrpc.getUpdateUserMethod) == null) {
          UserServiceGrpc.getUpdateUserMethod = getUpdateUserMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.user.UpdateUserRequest, io.protonaut.example.user.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.UpdateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.User.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("UpdateUser"))
              .build();
        }
      }
    }
    return getUpdateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.protonaut.example.user.ListUsersRequest,
      io.protonaut.example.user.User> getListUsersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListUsers",
      requestType = io.protonaut.example.user.ListUsersRequest.class,
      responseType = io.protonaut.example.user.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.protonaut.example.user.ListUsersRequest,
      io.protonaut.example.user.User> getListUsersMethod() {
    io.grpc.MethodDescriptor<io.protonaut.example.user.ListUsersRequest, io.protonaut.example.user.User> getListUsersMethod;
    if ((getListUsersMethod = UserServiceGrpc.getListUsersMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getListUsersMethod = UserServiceGrpc.getListUsersMethod) == null) {
          UserServiceGrpc.getListUsersMethod = getListUsersMethod =
              io.grpc.MethodDescriptor.<io.protonaut.example.user.ListUsersRequest, io.protonaut.example.user.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListUsers"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.ListUsersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.protonaut.example.user.User.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ListUsers"))
              .build();
        }
      }
    }
    return getListUsersMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceStub>() {
        @java.lang.Override
        public UserServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceStub(channel, callOptions);
        }
      };
    return UserServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static UserServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingV2Stub>() {
        @java.lang.Override
        public UserServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return UserServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub>() {
        @java.lang.Override
        public UserServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingStub(channel, callOptions);
        }
      };
    return UserServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub>() {
        @java.lang.Override
        public UserServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceFutureStub(channel, callOptions);
        }
      };
    return UserServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Retrieve a user by unique identifier, username, or email address
     * </pre>
     */
    default void getUser(io.protonaut.example.user.GetUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Register and provision a new user account
     * </pre>
     */
    default void createUser(io.protonaut.example.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Modify attributes of an existing user account
     * </pre>
     */
    default void updateUser(io.protonaut.example.user.UpdateUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Stream users matching the specified filter criteria
     * </pre>
     */
    default void listUsers(io.protonaut.example.user.ListUsersRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListUsersMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UserService.
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public static abstract class UserServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UserServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UserService.
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public static final class UserServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UserServiceStub> {
    private UserServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve a user by unique identifier, username, or email address
     * </pre>
     */
    public void getUser(io.protonaut.example.user.GetUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Register and provision a new user account
     * </pre>
     */
    public void createUser(io.protonaut.example.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Modify attributes of an existing user account
     * </pre>
     */
    public void updateUser(io.protonaut.example.user.UpdateUserRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Stream users matching the specified filter criteria
     * </pre>
     */
    public void listUsers(io.protonaut.example.user.ListUsersRequest request,
        io.grpc.stub.StreamObserver<io.protonaut.example.user.User> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getListUsersMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UserService.
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public static final class UserServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingV2Stub> {
    private UserServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve a user by unique identifier, username, or email address
     * </pre>
     */
    public io.protonaut.example.user.User getUser(io.protonaut.example.user.GetUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Register and provision a new user account
     * </pre>
     */
    public io.protonaut.example.user.User createUser(io.protonaut.example.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Modify attributes of an existing user account
     * </pre>
     */
    public io.protonaut.example.user.User updateUser(io.protonaut.example.user.UpdateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stream users matching the specified filter criteria
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, io.protonaut.example.user.User>
        listUsers(io.protonaut.example.user.ListUsersRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getListUsersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service UserService.
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public static final class UserServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve a user by unique identifier, username, or email address
     * </pre>
     */
    public io.protonaut.example.user.User getUser(io.protonaut.example.user.GetUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Register and provision a new user account
     * </pre>
     */
    public io.protonaut.example.user.User createUser(io.protonaut.example.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Modify attributes of an existing user account
     * </pre>
     */
    public io.protonaut.example.user.User updateUser(io.protonaut.example.user.UpdateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stream users matching the specified filter criteria
     * </pre>
     */
    public java.util.Iterator<io.protonaut.example.user.User> listUsers(
        io.protonaut.example.user.ListUsersRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getListUsersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UserService.
   * <pre>
   * Service providing comprehensive user management capabilities
   * </pre>
   */
  public static final class UserServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UserServiceFutureStub> {
    private UserServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Retrieve a user by unique identifier, username, or email address
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.user.User> getUser(
        io.protonaut.example.user.GetUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Register and provision a new user account
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.user.User> createUser(
        io.protonaut.example.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Modify attributes of an existing user account
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.protonaut.example.user.User> updateUser(
        io.protonaut.example.user.UpdateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateUserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER = 0;
  private static final int METHODID_CREATE_USER = 1;
  private static final int METHODID_UPDATE_USER = 2;
  private static final int METHODID_LIST_USERS = 3;

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
        case METHODID_GET_USER:
          serviceImpl.getUser((io.protonaut.example.user.GetUserRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.user.User>) responseObserver);
          break;
        case METHODID_CREATE_USER:
          serviceImpl.createUser((io.protonaut.example.user.CreateUserRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.user.User>) responseObserver);
          break;
        case METHODID_UPDATE_USER:
          serviceImpl.updateUser((io.protonaut.example.user.UpdateUserRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.user.User>) responseObserver);
          break;
        case METHODID_LIST_USERS:
          serviceImpl.listUsers((io.protonaut.example.user.ListUsersRequest) request,
              (io.grpc.stub.StreamObserver<io.protonaut.example.user.User>) responseObserver);
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
          getGetUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.user.GetUserRequest,
              io.protonaut.example.user.User>(
                service, METHODID_GET_USER)))
        .addMethod(
          getCreateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.user.CreateUserRequest,
              io.protonaut.example.user.User>(
                service, METHODID_CREATE_USER)))
        .addMethod(
          getUpdateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              io.protonaut.example.user.UpdateUserRequest,
              io.protonaut.example.user.User>(
                service, METHODID_UPDATE_USER)))
        .addMethod(
          getListUsersMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              io.protonaut.example.user.ListUsersRequest,
              io.protonaut.example.user.User>(
                service, METHODID_LIST_USERS)))
        .build();
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.protonaut.example.user.UserProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UserServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getGetUserMethod())
              .addMethod(getCreateUserMethod())
              .addMethod(getUpdateUserMethod())
              .addMethod(getListUsersMethod())
              .build();
        }
      }
    }
    return result;
  }
}
