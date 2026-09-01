package marketplace.saga;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: saga.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SagaServiceGrpc {

  private SagaServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "marketplace.saga.SagaService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<marketplace.saga.Saga.CreateOrderRequest,
      marketplace.saga.Saga.CreateOrderResponse> getCreateOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateOrder",
      requestType = marketplace.saga.Saga.CreateOrderRequest.class,
      responseType = marketplace.saga.Saga.CreateOrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<marketplace.saga.Saga.CreateOrderRequest,
      marketplace.saga.Saga.CreateOrderResponse> getCreateOrderMethod() {
    io.grpc.MethodDescriptor<marketplace.saga.Saga.CreateOrderRequest, marketplace.saga.Saga.CreateOrderResponse> getCreateOrderMethod;
    if ((getCreateOrderMethod = SagaServiceGrpc.getCreateOrderMethod) == null) {
      synchronized (SagaServiceGrpc.class) {
        if ((getCreateOrderMethod = SagaServiceGrpc.getCreateOrderMethod) == null) {
          SagaServiceGrpc.getCreateOrderMethod = getCreateOrderMethod =
              io.grpc.MethodDescriptor.<marketplace.saga.Saga.CreateOrderRequest, marketplace.saga.Saga.CreateOrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  marketplace.saga.Saga.CreateOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  marketplace.saga.Saga.CreateOrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SagaServiceMethodDescriptorSupplier("CreateOrder"))
              .build();
        }
      }
    }
    return getCreateOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SagaServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SagaServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SagaServiceStub>() {
        @java.lang.Override
        public SagaServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SagaServiceStub(channel, callOptions);
        }
      };
    return SagaServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SagaServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SagaServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SagaServiceBlockingStub>() {
        @java.lang.Override
        public SagaServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SagaServiceBlockingStub(channel, callOptions);
        }
      };
    return SagaServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SagaServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SagaServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SagaServiceFutureStub>() {
        @java.lang.Override
        public SagaServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SagaServiceFutureStub(channel, callOptions);
        }
      };
    return SagaServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createOrder(marketplace.saga.Saga.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<marketplace.saga.Saga.CreateOrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateOrderMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SagaService.
   */
  public static abstract class SagaServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return SagaServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SagaService.
   */
  public static final class SagaServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SagaServiceStub> {
    private SagaServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SagaServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SagaServiceStub(channel, callOptions);
    }

    /**
     */
    public void createOrder(marketplace.saga.Saga.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<marketplace.saga.Saga.CreateOrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SagaService.
   */
  public static final class SagaServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SagaServiceBlockingStub> {
    private SagaServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SagaServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SagaServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public marketplace.saga.Saga.CreateOrderResponse createOrder(marketplace.saga.Saga.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SagaService.
   */
  public static final class SagaServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SagaServiceFutureStub> {
    private SagaServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SagaServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SagaServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<marketplace.saga.Saga.CreateOrderResponse> createOrder(
        marketplace.saga.Saga.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_ORDER = 0;

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
          serviceImpl.createOrder((marketplace.saga.Saga.CreateOrderRequest) request,
              (io.grpc.stub.StreamObserver<marketplace.saga.Saga.CreateOrderResponse>) responseObserver);
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
              marketplace.saga.Saga.CreateOrderRequest,
              marketplace.saga.Saga.CreateOrderResponse>(
                service, METHODID_CREATE_ORDER)))
        .build();
  }

  private static abstract class SagaServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SagaServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return marketplace.saga.Saga.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SagaService");
    }
  }

  private static final class SagaServiceFileDescriptorSupplier
      extends SagaServiceBaseDescriptorSupplier {
    SagaServiceFileDescriptorSupplier() {}
  }

  private static final class SagaServiceMethodDescriptorSupplier
      extends SagaServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    SagaServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (SagaServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SagaServiceFileDescriptorSupplier())
              .addMethod(getCreateOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
