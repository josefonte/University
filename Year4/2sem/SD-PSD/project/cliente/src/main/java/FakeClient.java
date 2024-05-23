

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import node.proto.Rx3DataServerNodeGrpc;
import node.proto.Rx3DataServerNodeGrpc.RxDataServerNodeStub;

import node.proto.UploadFileRequest;
import node.proto.UploadFileResponse;
import node.proto.UploadFileRequestTransfer;
import node.proto.UploadFileResponseTransfer;
import node.proto.DownloadFileRequest;
import node.proto.DownloadFileResponse;
import node.proto.PingRequest;
import node.proto.PingResponse;
import node.proto.RemoveRequest;
import node.proto.RemoveResponse;
public class FakeClient {
    private static  String  filesPath = "./final/src/main/java/psd/trabalho/files/";
    private static String  downloadsPath = "./final/src/main/java/psd/trabalho/client_downloads/";


    public static void main(String[] args) throws IOException, InterruptedException {
        if (args[0].equals("1")) {
            fileUpload("localhost",8000,"eliseu.jpg");
            fileUpload("localhost",8000,"marega.jpg");
            fileUpload("localhost",8000,"esgaio.jpg");
            fileUpload("localhost",8000,"paulinho.jpeg");
            fileUpload("localhost",8000,"picanhas.jpg");
            fileUpload("localhost",8000,"video.mp4");
            fileUpload("localhost",8000,"zaidu.jpeg");
            fileUpload("localhost",8000,"banza.jpg");
            fileUpload("localhost",8000,"vitinha.jpg");
            fileUpload("localhost",8000,"felix.jpeg");
        }
        if (args[0].equals("2")) {
            downloadFile("localhost",8000,"eliseu.jpg");
            downloadFile("localhost",8000,"marega.jpg");
            downloadFile("localhost",8000,"paulinho.jpeg");
            downloadFile("localhost",8000,"picanhas.jpg");
            downloadFile("localhost",8000,"video.mp4");
        }
        if (args[0].equals("3")) {
            removeFile("localhost",8000,"eliseu.jpg");
            removeFile("localhost",8000,"marega.jpg");
        }
    }

    private static ManagedChannel  createChannel(String ip, String port){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, Integer.parseInt(port))
                .usePlaintext()
                .build();

        return channel;
    }

    private static void downloadFile(String ip_add,int port, String fileName) {
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));

        RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        Single<DownloadFileRequest> req = Single.just(DownloadFileRequest.newBuilder().setFileName(fileName).build());

        AtomicLong totalFileSize = new AtomicLong(0);
        AtomicBoolean isFirstChunk = new AtomicBoolean(true);
        AtomicReference<File> file = new AtomicReference<>();
        AtomicReference<FileOutputStream> fileOutputStream = new AtomicReference<>();
        List<node.proto.DownloadFileResponseTransfer> chunkBuffer = new ArrayList<>();

        System.out.println("Sending download request");

        DownloadFileResponse response = stub.downloadFile(req).blockingGet();

        if (response.getSuccess()) {
            stub.downloadFileTransfer(req)
                    .onBackpressureBuffer()
                    .doOnNext(chunk -> {
                        if (isFirstChunk.get()) {
                            System.out.println("#### File Download started -> " + chunk.getFileName());

                            try {
                                File newFile = new File(downloadsPath + chunk.getFileName());
                                file.set(newFile);
                                fileOutputStream.set(new FileOutputStream(String.valueOf(file)));

                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            isFirstChunk.set(false);
                            System.out.println("File created successfully! ");
                        }
                        totalFileSize.addAndGet(chunk.getFileData().size());
                        chunkBuffer.add(chunk);
                        if (chunkBuffer.size() == 40) {
                            writeBufferToFile(chunkBuffer, fileOutputStream.get());
                            chunkBuffer.clear();
                        }
                    })
                    .ignoreElements()
                    .doOnComplete(() -> {
                        if (!chunkBuffer.isEmpty()) {
                            writeBufferToFile(chunkBuffer, fileOutputStream.get());
                            chunkBuffer.clear();
                            System.out.println("#### File downloaded successfully! | Total size: " + totalFileSize.get() + " bytes");
                        }
                        if (file.get() != null && fileOutputStream.get() != null) {
                            fileOutputStream.get().close();
                        }
                    })
                    .onErrorComplete(error -> {
                        System.err.println("Error occurred during file download: " + error.getMessage());
                        if (file.get() != null && fileOutputStream.get() != null) {
                            file.get().delete();
                            fileOutputStream.get().close();
                        }
                        return false;
                    })
                    .blockingSubscribe();
        }
        else {
            System.out.println("Error downloading file: " + response.getFileName());
            if(!response.getNodeIp().isEmpty() && !response.getNodePort().isEmpty()){
                System.out.println("Error: " + response.getErrorMessage() + " | search in " + response.getNodeIp() + ":" + response.getNodePort());
                channel.shutdown();
                downloadFile(response.getNodeIp(), Integer.parseInt(response.getNodePort()), fileName);
            }
        }

        channel.shutdown();

    }

    private static void writeBufferToFile(List<node.proto.DownloadFileResponseTransfer> chunkBuffer, FileOutputStream fileOutputStream) {
        try {
            System.out.println("Writing buffer to file");
            for (node.proto.DownloadFileResponseTransfer chunk : chunkBuffer) {fileOutputStream.write(chunk.getFileData().toByteArray());}
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void fileUpload(String ip_add,int port, String fileName) throws IOException {
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        UploadFileRequest req = UploadFileRequest.newBuilder().setFileName(fileName).build();
        UploadFileResponse auth = stub.uploadFile(req).blockingGet();

        if(auth.getSuccess()){
            File file = new File(filesPath + fileName);

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                Flowable<UploadFileRequestTransfer> requestFlowable = Flowable.generate(emitter -> {
                    byte[] buffer = new byte[1024]; // Chunk size
                    int bytesRead = fileInputStream.read(buffer);
                    if (bytesRead == -1) {
                        emitter.onComplete(); // No more data to read
                    } else {
                        byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                        emitter.onNext(UploadFileRequestTransfer.newBuilder()
                                .setFileName(file.getName())
                                .setFileData(ByteString.copyFrom(chunk))
                                .build()); // Emit next chunk
                    }
                });
                System.out.println("### Starting file transfer -> " + file.getName() );
                stub.uploadFileTransfer(requestFlowable).onErrorComplete(
                                throwable -> {
                                    System.err.println("Error uploading file: " + throwable.getMessage());
                                    return false;
                                }
                        )
                        .blockingSubscribe(response -> {
                            System.out.println("File Transfered: " + response.getSuccess() );
                        });}}
        else {
            System.out.println("Error uploading file: " + auth.getFileName());
            if(!auth.getNodeIp().isEmpty() && !auth.getNodePort().isEmpty()){
                System.out.println("Error: " + auth.getErrorMessage() + " | search in " + auth.getNodeIp() + ":" + auth.getNodePort());
                channel.shutdown();
                fileUpload(auth.getNodeIp(), Integer.parseInt(auth.getNodePort()), fileName);
            }
        }

        channel.shutdown();
    }

    private static void removeFile(String ip_add,int port, String fileName) {

        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        System.out.println("Sending remove request");
        Single<RemoveRequest> req = Single.just(RemoveRequest.newBuilder().setFileName(fileName).build());
        RemoveResponse response = stub.removeFile(req).blockingGet();

        if (response.getSuccess()) {
            System.out.println("File removed successfully");
        }
        else {
            System.out.println("Error removing file: ");
            if(!response.getNodeIp().isEmpty() && !response.getNodePort().isEmpty()){
                System.out.println("Search in " + response.getNodeIp() + ":" + response.getNodePort());
                channel.shutdown();
                removeFile(response.getNodeIp(), Integer.parseInt(response.getNodePort()), fileName);
            }
        }
        channel.shutdown();
    }

    private static void ping(String ip_add,int port){
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        Single<PingRequest> req = Single.just(PingRequest.newBuilder().setNodeIp("localhost").setNodePort("0000").setMessage("PING").build());

        System.out.println("Sending ping");
        stub.ping(req).blockingSubscribe(r->{
            System.out.println("Response: " + r.getMessage() + " from " + r.getNodeIp() + ":" + r.getNodePort());
        });
    }
}


