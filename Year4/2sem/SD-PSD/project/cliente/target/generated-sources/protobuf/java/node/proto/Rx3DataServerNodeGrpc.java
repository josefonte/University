package node.proto;

import static node.proto.DataServerNodeGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;


@javax.annotation.Generated(
value = "by RxGrpc generator",
comments = "Source: Node.proto")
public final class Rx3DataServerNodeGrpc {
    private Rx3DataServerNodeGrpc() {}

    public static RxDataServerNodeStub newRxStub(io.grpc.Channel channel) {
        return new RxDataServerNodeStub(channel);
    }

    public static final class RxDataServerNodeStub extends io.grpc.stub.AbstractStub<RxDataServerNodeStub> {
        private DataServerNodeGrpc.DataServerNodeStub delegateStub;

        private RxDataServerNodeStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = DataServerNodeGrpc.newStub(channel);
        }

        private RxDataServerNodeStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = DataServerNodeGrpc.newStub(channel).build(channel, callOptions);
        }

        @java.lang.Override
        protected RxDataServerNodeStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new RxDataServerNodeStub(channel, callOptions);
        }

        public io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse> newNode(io.reactivex.rxjava3.core.Single<node.proto.NewNodeRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.NewNodeRequest, io.grpc.stub.StreamObserver<node.proto.NewNodeResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.NewNodeRequest request, io.grpc.stub.StreamObserver<node.proto.NewNodeResponse> observer) {
                        delegateStub.newNode(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse> downloadFile(io.reactivex.rxjava3.core.Single<node.proto.DownloadFileRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.DownloadFileRequest, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.DownloadFileRequest request, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse> observer) {
                        delegateStub.downloadFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer> downloadFileTransfer(io.reactivex.rxjava3.core.Single<node.proto.DownloadFileRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToMany(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.DownloadFileRequest, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer>>() {
                    @java.lang.Override
                    public void accept(node.proto.DownloadFileRequest request, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer> observer) {
                        delegateStub.downloadFileTransfer(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse> uploadFile(io.reactivex.rxjava3.core.Single<node.proto.UploadFileRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.UploadFileRequest, io.grpc.stub.StreamObserver<node.proto.UploadFileResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.UploadFileRequest request, io.grpc.stub.StreamObserver<node.proto.UploadFileResponse> observer) {
                        delegateStub.uploadFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponseTransfer> uploadFileTransfer(io.reactivex.rxjava3.core.Flowable<node.proto.UploadFileRequestTransfer> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.manyToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.Function<io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer>, io.grpc.stub.StreamObserver<node.proto.UploadFileRequestTransfer>>() {
                    @java.lang.Override
                    public io.grpc.stub.StreamObserver<node.proto.UploadFileRequestTransfer> apply(io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer> observer) {
                        return delegateStub.uploadFileTransfer(observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse> removeFile(io.reactivex.rxjava3.core.Single<node.proto.RemoveRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.RemoveRequest, io.grpc.stub.StreamObserver<node.proto.RemoveResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.RemoveRequest request, io.grpc.stub.StreamObserver<node.proto.RemoveResponse> observer) {
                        delegateStub.removeFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.PingResponse> ping(io.reactivex.rxjava3.core.Single<node.proto.PingRequest> rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(rxRequest,
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.PingRequest, io.grpc.stub.StreamObserver<node.proto.PingResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.PingRequest request, io.grpc.stub.StreamObserver<node.proto.PingResponse> observer) {
                        delegateStub.ping(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse> newNode(node.proto.NewNodeRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.NewNodeRequest, io.grpc.stub.StreamObserver<node.proto.NewNodeResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.NewNodeRequest request, io.grpc.stub.StreamObserver<node.proto.NewNodeResponse> observer) {
                        delegateStub.newNode(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse> downloadFile(node.proto.DownloadFileRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.DownloadFileRequest, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.DownloadFileRequest request, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse> observer) {
                        delegateStub.downloadFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer> downloadFileTransfer(node.proto.DownloadFileRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToMany(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.DownloadFileRequest, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer>>() {
                    @java.lang.Override
                    public void accept(node.proto.DownloadFileRequest request, io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer> observer) {
                        delegateStub.downloadFileTransfer(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse> uploadFile(node.proto.UploadFileRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.UploadFileRequest, io.grpc.stub.StreamObserver<node.proto.UploadFileResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.UploadFileRequest request, io.grpc.stub.StreamObserver<node.proto.UploadFileResponse> observer) {
                        delegateStub.uploadFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse> removeFile(node.proto.RemoveRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.RemoveRequest, io.grpc.stub.StreamObserver<node.proto.RemoveResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.RemoveRequest request, io.grpc.stub.StreamObserver<node.proto.RemoveResponse> observer) {
                        delegateStub.removeFile(request, observer);
                    }
                }, getCallOptions());
        }

        public io.reactivex.rxjava3.core.Single<node.proto.PingResponse> ping(node.proto.PingRequest rxRequest) {
            return com.salesforce.rx3grpc.stub.ClientCalls.oneToOne(io.reactivex.rxjava3.core.Single.just(rxRequest),
                new com.salesforce.reactivegrpc.common.BiConsumer<node.proto.PingRequest, io.grpc.stub.StreamObserver<node.proto.PingResponse>>() {
                    @java.lang.Override
                    public void accept(node.proto.PingRequest request, io.grpc.stub.StreamObserver<node.proto.PingResponse> observer) {
                        delegateStub.ping(request, observer);
                    }
                }, getCallOptions());
        }

    }

    public static abstract class DataServerNodeImplBase implements io.grpc.BindableService {

        public io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse> newNode(node.proto.NewNodeRequest request) {
            return newNode(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse> newNode(io.reactivex.rxjava3.core.Single<node.proto.NewNodeRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse> downloadFile(node.proto.DownloadFileRequest request) {
            return downloadFile(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse> downloadFile(io.reactivex.rxjava3.core.Single<node.proto.DownloadFileRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer> downloadFileTransfer(node.proto.DownloadFileRequest request) {
            return downloadFileTransfer(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer> downloadFileTransfer(io.reactivex.rxjava3.core.Single<node.proto.DownloadFileRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse> uploadFile(node.proto.UploadFileRequest request) {
            return uploadFile(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse> uploadFile(io.reactivex.rxjava3.core.Single<node.proto.UploadFileRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }


        public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponseTransfer> uploadFileTransfer(io.reactivex.rxjava3.core.Flowable<node.proto.UploadFileRequestTransfer> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse> removeFile(node.proto.RemoveRequest request) {
            return removeFile(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse> removeFile(io.reactivex.rxjava3.core.Single<node.proto.RemoveRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.reactivex.rxjava3.core.Single<node.proto.PingResponse> ping(node.proto.PingRequest request) {
            return ping(io.reactivex.rxjava3.core.Single.just(request));
        }

        public io.reactivex.rxjava3.core.Single<node.proto.PingResponse> ping(io.reactivex.rxjava3.core.Single<node.proto.PingRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getNewNodeMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            node.proto.NewNodeRequest,
                                            node.proto.NewNodeResponse>(
                                            this, METHODID_NEW_NODE)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getDownloadFileMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            node.proto.DownloadFileRequest,
                                            node.proto.DownloadFileResponse>(
                                            this, METHODID_DOWNLOAD_FILE)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getDownloadFileTransferMethod(),
                            asyncServerStreamingCall(
                                    new MethodHandlers<
                                            node.proto.DownloadFileRequest,
                                            node.proto.DownloadFileResponseTransfer>(
                                            this, METHODID_DOWNLOAD_FILE_TRANSFER)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getUploadFileMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            node.proto.UploadFileRequest,
                                            node.proto.UploadFileResponse>(
                                            this, METHODID_UPLOAD_FILE)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getUploadFileTransferMethod(),
                            asyncClientStreamingCall(
                                    new MethodHandlers<
                                            node.proto.UploadFileRequestTransfer,
                                            node.proto.UploadFileResponseTransfer>(
                                            this, METHODID_UPLOAD_FILE_TRANSFER)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getRemoveFileMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            node.proto.RemoveRequest,
                                            node.proto.RemoveResponse>(
                                            this, METHODID_REMOVE_FILE)))
                    .addMethod(
                            node.proto.DataServerNodeGrpc.getPingMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            node.proto.PingRequest,
                                            node.proto.PingResponse>(
                                            this, METHODID_PING)))
                    .build();
        }

        protected io.grpc.CallOptions getCallOptions(int methodId) {
            return null;
        }

        protected Throwable onErrorMap(Throwable throwable) {
            return com.salesforce.rx3grpc.stub.ServerCalls.prepareError(throwable);
        }

    }

    public static final int METHODID_NEW_NODE = 0;
    public static final int METHODID_DOWNLOAD_FILE = 1;
    public static final int METHODID_DOWNLOAD_FILE_TRANSFER = 2;
    public static final int METHODID_UPLOAD_FILE = 3;
    public static final int METHODID_UPLOAD_FILE_TRANSFER = 4;
    public static final int METHODID_REMOVE_FILE = 5;
    public static final int METHODID_PING = 6;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final DataServerNodeImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(DataServerNodeImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_NEW_NODE:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToOne((node.proto.NewNodeRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.NewNodeResponse>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.NewNodeRequest, io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Single<node.proto.NewNodeResponse> apply(node.proto.NewNodeRequest single) {
                                    return serviceImpl.newNode(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                case METHODID_DOWNLOAD_FILE:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToOne((node.proto.DownloadFileRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.DownloadFileResponse>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.DownloadFileRequest, io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Single<node.proto.DownloadFileResponse> apply(node.proto.DownloadFileRequest single) {
                                    return serviceImpl.downloadFile(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                case METHODID_DOWNLOAD_FILE_TRANSFER:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToMany((node.proto.DownloadFileRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.DownloadFileResponseTransfer>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.DownloadFileRequest, io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Flowable<node.proto.DownloadFileResponseTransfer> apply(node.proto.DownloadFileRequest single) {
                                    return serviceImpl.downloadFileTransfer(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                case METHODID_UPLOAD_FILE:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToOne((node.proto.UploadFileRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.UploadFileResponse>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.UploadFileRequest, io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Single<node.proto.UploadFileResponse> apply(node.proto.UploadFileRequest single) {
                                    return serviceImpl.uploadFile(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                case METHODID_REMOVE_FILE:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToOne((node.proto.RemoveRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.RemoveResponse>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.RemoveRequest, io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Single<node.proto.RemoveResponse> apply(node.proto.RemoveRequest single) {
                                    return serviceImpl.removeFile(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                case METHODID_PING:
                    com.salesforce.rx3grpc.stub.ServerCalls.oneToOne((node.proto.PingRequest) request,
                            (io.grpc.stub.StreamObserver<node.proto.PingResponse>) responseObserver,
                            new com.salesforce.reactivegrpc.common.Function<node.proto.PingRequest, io.reactivex.rxjava3.core.Single<node.proto.PingResponse>>() {
                                @java.lang.Override
                                public io.reactivex.rxjava3.core.Single<node.proto.PingResponse> apply(node.proto.PingRequest single) {
                                    return serviceImpl.ping(single);
                                }
                            }, serviceImpl::onErrorMap);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_UPLOAD_FILE_TRANSFER:
                    return (io.grpc.stub.StreamObserver<Req>) com.salesforce.rx3grpc.stub.ServerCalls.manyToOne(
                            (io.grpc.stub.StreamObserver<node.proto.UploadFileResponseTransfer>) responseObserver,
                            serviceImpl::uploadFileTransfer, serviceImpl::onErrorMap, serviceImpl.getCallOptions(methodId));
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }

}
