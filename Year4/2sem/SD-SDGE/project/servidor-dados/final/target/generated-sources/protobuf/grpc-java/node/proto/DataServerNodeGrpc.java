package node.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.1)",
    comments = "Source: Node.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DataServerNodeGrpc {

  private DataServerNodeGrpc() {}

  public static final java.lang.String SERVICE_NAME = "node.proto.DataServerNode";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<node.proto.NewNodeRequest,
      node.proto.NewNodeResponse> getNewNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "NewNode",
      requestType = node.proto.NewNodeRequest.class,
      responseType = node.proto.NewNodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<node.proto.NewNodeRequest,
      node.proto.NewNodeResponse> getNewNodeMethod() {
    io.grpc.MethodDescriptor<node.proto.NewNodeRequest, node.proto.NewNodeResponse> getNewNodeMethod;
    if ((getNewNodeMethod = DataServerNodeGrpc.getNewNodeMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getNewNodeMethod = DataServerNodeGrpc.getNewNodeMethod) == null) {
          DataServerNodeGrpc.getNewNodeMethod = getNewNodeMethod =
              io.grpc.MethodDescriptor.<node.proto.NewNodeRequest, node.proto.NewNodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "NewNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.NewNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.NewNodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("NewNode"))
              .build();
        }
      }
    }
    return getNewNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.DownloadFileRequest,
      node.proto.DownloadFileResponse> getDownloadFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DownloadFile",
      requestType = node.proto.DownloadFileRequest.class,
      responseType = node.proto.DownloadFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<node.proto.DownloadFileRequest,
      node.proto.DownloadFileResponse> getDownloadFileMethod() {
    io.grpc.MethodDescriptor<node.proto.DownloadFileRequest, node.proto.DownloadFileResponse> getDownloadFileMethod;
    if ((getDownloadFileMethod = DataServerNodeGrpc.getDownloadFileMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getDownloadFileMethod = DataServerNodeGrpc.getDownloadFileMethod) == null) {
          DataServerNodeGrpc.getDownloadFileMethod = getDownloadFileMethod =
              io.grpc.MethodDescriptor.<node.proto.DownloadFileRequest, node.proto.DownloadFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DownloadFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.DownloadFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.DownloadFileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("DownloadFile"))
              .build();
        }
      }
    }
    return getDownloadFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.DownloadFileRequest,
      node.proto.DownloadFileResponseTransfer> getDownloadFileTransferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DownloadFileTransfer",
      requestType = node.proto.DownloadFileRequest.class,
      responseType = node.proto.DownloadFileResponseTransfer.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<node.proto.DownloadFileRequest,
      node.proto.DownloadFileResponseTransfer> getDownloadFileTransferMethod() {
    io.grpc.MethodDescriptor<node.proto.DownloadFileRequest, node.proto.DownloadFileResponseTransfer> getDownloadFileTransferMethod;
    if ((getDownloadFileTransferMethod = DataServerNodeGrpc.getDownloadFileTransferMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getDownloadFileTransferMethod = DataServerNodeGrpc.getDownloadFileTransferMethod) == null) {
          DataServerNodeGrpc.getDownloadFileTransferMethod = getDownloadFileTransferMethod =
              io.grpc.MethodDescriptor.<node.proto.DownloadFileRequest, node.proto.DownloadFileResponseTransfer>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DownloadFileTransfer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.DownloadFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.DownloadFileResponseTransfer.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("DownloadFileTransfer"))
              .build();
        }
      }
    }
    return getDownloadFileTransferMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.UploadFileRequest,
      node.proto.UploadFileResponse> getUploadFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UploadFile",
      requestType = node.proto.UploadFileRequest.class,
      responseType = node.proto.UploadFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<node.proto.UploadFileRequest,
      node.proto.UploadFileResponse> getUploadFileMethod() {
    io.grpc.MethodDescriptor<node.proto.UploadFileRequest, node.proto.UploadFileResponse> getUploadFileMethod;
    if ((getUploadFileMethod = DataServerNodeGrpc.getUploadFileMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getUploadFileMethod = DataServerNodeGrpc.getUploadFileMethod) == null) {
          DataServerNodeGrpc.getUploadFileMethod = getUploadFileMethod =
              io.grpc.MethodDescriptor.<node.proto.UploadFileRequest, node.proto.UploadFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UploadFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.UploadFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.UploadFileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("UploadFile"))
              .build();
        }
      }
    }
    return getUploadFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.UploadFileRequestTransfer,
      node.proto.UploadFileResponseTransfer> getUploadFileTransferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UploadFileTransfer",
      requestType = node.proto.UploadFileRequestTransfer.class,
      responseType = node.proto.UploadFileResponseTransfer.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<node.proto.UploadFileRequestTransfer,
      node.proto.UploadFileResponseTransfer> getUploadFileTransferMethod() {
    io.grpc.MethodDescriptor<node.proto.UploadFileRequestTransfer, node.proto.UploadFileResponseTransfer> getUploadFileTransferMethod;
    if ((getUploadFileTransferMethod = DataServerNodeGrpc.getUploadFileTransferMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getUploadFileTransferMethod = DataServerNodeGrpc.getUploadFileTransferMethod) == null) {
          DataServerNodeGrpc.getUploadFileTransferMethod = getUploadFileTransferMethod =
              io.grpc.MethodDescriptor.<node.proto.UploadFileRequestTransfer, node.proto.UploadFileResponseTransfer>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UploadFileTransfer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.UploadFileRequestTransfer.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.UploadFileResponseTransfer.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("UploadFileTransfer"))
              .build();
        }
      }
    }
    return getUploadFileTransferMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.RemoveRequest,
      node.proto.RemoveResponse> getRemoveFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveFile",
      requestType = node.proto.RemoveRequest.class,
      responseType = node.proto.RemoveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<node.proto.RemoveRequest,
      node.proto.RemoveResponse> getRemoveFileMethod() {
    io.grpc.MethodDescriptor<node.proto.RemoveRequest, node.proto.RemoveResponse> getRemoveFileMethod;
    if ((getRemoveFileMethod = DataServerNodeGrpc.getRemoveFileMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getRemoveFileMethod = DataServerNodeGrpc.getRemoveFileMethod) == null) {
          DataServerNodeGrpc.getRemoveFileMethod = getRemoveFileMethod =
              io.grpc.MethodDescriptor.<node.proto.RemoveRequest, node.proto.RemoveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.RemoveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.RemoveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("RemoveFile"))
              .build();
        }
      }
    }
    return getRemoveFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<node.proto.PingRequest,
      node.proto.PingResponse> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = node.proto.PingRequest.class,
      responseType = node.proto.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<node.proto.PingRequest,
      node.proto.PingResponse> getPingMethod() {
    io.grpc.MethodDescriptor<node.proto.PingRequest, node.proto.PingResponse> getPingMethod;
    if ((getPingMethod = DataServerNodeGrpc.getPingMethod) == null) {
      synchronized (DataServerNodeGrpc.class) {
        if ((getPingMethod = DataServerNodeGrpc.getPingMethod) == null) {
          DataServerNodeGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<node.proto.PingRequest, node.proto.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  node.proto.PingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DataServerNodeMethodDescriptorSupplier("Ping"))
              .build();
        }
      }
    }
    return getPingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataServerNodeStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServerNodeStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataServerNodeStub>() {
        @java.lang.Override
        public DataServerNodeStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataServerNodeStub(channel, callOptions);
        }
      };
    return DataServerNodeStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataServerNodeBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServerNodeBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataServerNodeBlockingStub>() {
        @java.lang.Override
        public DataServerNodeBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataServerNodeBlockingStub(channel, callOptions);
        }
      };
    return DataServerNodeBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataServerNodeFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServerNodeFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataServerNodeFutureStub>() {
        @java.lang.Override
        public DataServerNodeFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataServerNodeFutureStub(channel, callOptions);
        }
      };
    return DataServerNodeFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void newNode(node.proto.NewNodeRequest request,
        io.grpc.stub.StreamObserver<node.proto.NewNodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getNewNodeMethod(), responseObserver);
    }

    /**
     */
    default void downloadFile(node.proto.DownloadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDownloadFileMethod(), responseObserver);
    }

    /**
     */
    default void downloadFileTransfer(node.proto.DownloadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDownloadFileTransferMethod(), responseObserver);
    }

    /**
     */
    default void uploadFile(node.proto.UploadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.UploadFileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUploadFileMethod(), responseObserver);
    }

    /**
     */
    default io.grpc.stub.StreamObserver<node.proto.UploadFileRequestTransfer> uploadFileTransfer(
        io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getUploadFileTransferMethod(), responseObserver);
    }

    /**
     */
    default void removeFile(node.proto.RemoveRequest request,
        io.grpc.stub.StreamObserver<node.proto.RemoveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveFileMethod(), responseObserver);
    }

    /**
     */
    default void ping(node.proto.PingRequest request,
        io.grpc.stub.StreamObserver<node.proto.PingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DataServerNode.
   */
  public static abstract class DataServerNodeImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return DataServerNodeGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DataServerNode.
   */
  public static final class DataServerNodeStub
      extends io.grpc.stub.AbstractAsyncStub<DataServerNodeStub> {
    private DataServerNodeStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServerNodeStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataServerNodeStub(channel, callOptions);
    }

    /**
     */
    public void newNode(node.proto.NewNodeRequest request,
        io.grpc.stub.StreamObserver<node.proto.NewNodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getNewNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void downloadFile(node.proto.DownloadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDownloadFileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void downloadFileTransfer(node.proto.DownloadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getDownloadFileTransferMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void uploadFile(node.proto.UploadFileRequest request,
        io.grpc.stub.StreamObserver<node.proto.UploadFileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUploadFileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<node.proto.UploadFileRequestTransfer> uploadFileTransfer(
        io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getUploadFileTransferMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void removeFile(node.proto.RemoveRequest request,
        io.grpc.stub.StreamObserver<node.proto.RemoveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveFileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(node.proto.PingRequest request,
        io.grpc.stub.StreamObserver<node.proto.PingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DataServerNode.
   */
  public static final class DataServerNodeBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DataServerNodeBlockingStub> {
    private DataServerNodeBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServerNodeBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataServerNodeBlockingStub(channel, callOptions);
    }

    /**
     */
    public node.proto.NewNodeResponse newNode(node.proto.NewNodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getNewNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public node.proto.DownloadFileResponse downloadFile(node.proto.DownloadFileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDownloadFileMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<node.proto.DownloadFileResponseTransfer> downloadFileTransfer(
        node.proto.DownloadFileRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getDownloadFileTransferMethod(), getCallOptions(), request);
    }

    /**
     */
    public node.proto.UploadFileResponse uploadFile(node.proto.UploadFileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUploadFileMethod(), getCallOptions(), request);
    }

    /**
     */
    public node.proto.RemoveResponse removeFile(node.proto.RemoveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveFileMethod(), getCallOptions(), request);
    }

    /**
     */
    public node.proto.PingResponse ping(node.proto.PingRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DataServerNode.
   */
  public static final class DataServerNodeFutureStub
      extends io.grpc.stub.AbstractFutureStub<DataServerNodeFutureStub> {
    private DataServerNodeFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServerNodeFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataServerNodeFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<node.proto.NewNodeResponse> newNode(
        node.proto.NewNodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getNewNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<node.proto.DownloadFileResponse> downloadFile(
        node.proto.DownloadFileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDownloadFileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<node.proto.UploadFileResponse> uploadFile(
        node.proto.UploadFileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUploadFileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<node.proto.RemoveResponse> removeFile(
        node.proto.RemoveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveFileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<node.proto.PingResponse> ping(
        node.proto.PingRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_NEW_NODE = 0;
  private static final int METHODID_DOWNLOAD_FILE = 1;
  private static final int METHODID_DOWNLOAD_FILE_TRANSFER = 2;
  private static final int METHODID_UPLOAD_FILE = 3;
  private static final int METHODID_REMOVE_FILE = 4;
  private static final int METHODID_PING = 5;
  private static final int METHODID_UPLOAD_FILE_TRANSFER = 6;

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
        case METHODID_NEW_NODE:
          serviceImpl.newNode((node.proto.NewNodeRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.NewNodeResponse>) responseObserver);
          break;
        case METHODID_DOWNLOAD_FILE:
          serviceImpl.downloadFile((node.proto.DownloadFileRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse>) responseObserver);
          break;
        case METHODID_DOWNLOAD_FILE_TRANSFER:
          serviceImpl.downloadFileTransfer((node.proto.DownloadFileRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer>) responseObserver);
          break;
        case METHODID_UPLOAD_FILE:
          serviceImpl.uploadFile((node.proto.UploadFileRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.UploadFileResponse>) responseObserver);
          break;
        case METHODID_REMOVE_FILE:
          serviceImpl.removeFile((node.proto.RemoveRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.RemoveResponse>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((node.proto.PingRequest) request,
              (io.grpc.stub.StreamObserver<node.proto.PingResponse>) responseObserver);
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
        case METHODID_UPLOAD_FILE_TRANSFER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.uploadFileTransfer(
              (io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getNewNodeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              node.proto.NewNodeRequest,
              node.proto.NewNodeResponse>(
                service, METHODID_NEW_NODE)))
        .addMethod(
          getDownloadFileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              node.proto.DownloadFileRequest,
              node.proto.DownloadFileResponse>(
                service, METHODID_DOWNLOAD_FILE)))
        .addMethod(
          getDownloadFileTransferMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              node.proto.DownloadFileRequest,
              node.proto.DownloadFileResponseTransfer>(
                service, METHODID_DOWNLOAD_FILE_TRANSFER)))
        .addMethod(
          getUploadFileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              node.proto.UploadFileRequest,
              node.proto.UploadFileResponse>(
                service, METHODID_UPLOAD_FILE)))
        .addMethod(
          getUploadFileTransferMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              node.proto.UploadFileRequestTransfer,
              node.proto.UploadFileResponseTransfer>(
                service, METHODID_UPLOAD_FILE_TRANSFER)))
        .addMethod(
          getRemoveFileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              node.proto.RemoveRequest,
              node.proto.RemoveResponse>(
                service, METHODID_REMOVE_FILE)))
        .addMethod(
          getPingMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              node.proto.PingRequest,
              node.proto.PingResponse>(
                service, METHODID_PING)))
        .build();
  }

  private static abstract class DataServerNodeBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataServerNodeBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return node.proto.NodeProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataServerNode");
    }
  }

  private static final class DataServerNodeFileDescriptorSupplier
      extends DataServerNodeBaseDescriptorSupplier {
    DataServerNodeFileDescriptorSupplier() {}
  }

  private static final class DataServerNodeMethodDescriptorSupplier
      extends DataServerNodeBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    DataServerNodeMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (DataServerNodeGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataServerNodeFileDescriptorSupplier())
              .addMethod(getNewNodeMethod())
              .addMethod(getDownloadFileMethod())
              .addMethod(getDownloadFileTransferMethod())
              .addMethod(getUploadFileMethod())
              .addMethod(getUploadFileTransferMethod())
              .addMethod(getRemoveFileMethod())
              .addMethod(getPingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
