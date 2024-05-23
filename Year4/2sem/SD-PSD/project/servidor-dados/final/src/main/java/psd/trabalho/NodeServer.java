package psd.trabalho;

import com.google.protobuf.ByteString;
import io.grpc.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import node.proto.NewNodeRequest;
import node.proto.NewNodeResponse;
import node.proto.UploadFileRequest;
import node.proto.UploadFileResponse;
import node.proto.UploadFileRequestTransfer;
import node.proto.UploadFileResponseTransfer;
import node.proto.PingRequest;
import node.proto.PingResponse;

import node.proto.DownloadFileRequest;
import node.proto.DownloadFileResponse;
import node.proto.DownloadFileResponseTransfer;
import node.proto.RemoveResponse;
import node.proto.RemoveRequest;
import node.proto.Rx3DataServerNodeGrpc;
import node.proto.Rx3DataServerNodeGrpc.RxDataServerNodeStub;



public class NodeServer extends Rx3DataServerNodeGrpc.DataServerNodeImplBase {
    private NodeState nodeState = new NodeState();
    private final int number_vNodes = 5;
    private final HashFunction hash = new HashFunction();
    private final int buffersize = 100;
    private String  serverStorageFilePath = "";



    //CONSTRUCTORS
    public NodeServer() {
        super();
        nodeState.setIpPort("");
        nodeState.setIpAddress("");
        nodeState.setKey(new byte[0]);
        serverStorageFilePath = "./final/src/main/java/psd/trabalho/files_server_" + nodeState.getIpPort() +"/";
    }

    public NodeServer(String ip_add, String ip_port) throws NoSuchAlgorithmException {
        super();
        nodeState.setIpAddress( ip_add);
        nodeState.setIpPort(ip_port);
        nodeState.setKey(hash.generateHash(ip_add + "-" + ip_port));
        serverStorageFilePath = "./final/src/main/java/psd/trabalho/files_server_" + nodeState.getIpPort() +"/";
        System.out.println("#### Node created: " + ip_add + " | " + ip_port + " | " + nodeState.getKey());
    }



    //START -> method to start the node
    public void start( ArrayList<Pair<String, String>> neighborNodes) throws NoSuchAlgorithmException, IOException, InterruptedException {
       System.out.println("#### Starting node: " + nodeState.getIpAddress() + " | " + nodeState.getIpPort() + " | " + nodeState.getKey());

       System.out.println("##Adding Vnodes to ring");
        //add virtual nodes to ring
        for (int i = 0; i < number_vNodes; i++) {
            VirtualNode<NodeState> vNode = new VirtualNode<>(this.nodeState,i);
            nodeState.addRing(vNode.getKey(),vNode);
        }


        File storageDir = new File(serverStorageFilePath);
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }

        System.out.println("##Informing neighbor nodes " + neighborNodes);
        for (Pair<String,String> neighbor : neighborNodes) {
            if (!neighbor.getPair2().equals(nodeState.getIpPort())) {
                try {
                    System.out.println("#Connecting with neighbor: " + neighbor.getPair1() + " | " + neighbor.getPair2());
                    String neighbor_ip = neighbor.getPair1();
                    String neighbor_port = neighbor.getPair2();

                    ManagedChannel channel = ManagedChannelBuilder.forAddress(neighbor_ip, Integer.parseInt(neighbor_port))
                            .usePlaintext()
                            .build();

                    //send message to neighbor node that NEW_NODE is joining
                    RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

                    Single<NewNodeRequest> request = Single.just(NewNodeRequest.newBuilder().setNodeIpAdd(nodeState.getIpAddress()).setNodeIpPort(nodeState.getIpPort()).build());
                    Single<NewNodeResponse> response = stub.newNode(request);

                    response.subscribe(
                            r -> {
                                try {
                                    System.out.println("#### New node response: " + r.getNodeIpAdd() + " | " + r.getNodeIpPort());
                                    addNode(r.getNodeIpAdd(),r.getNodeIpPort());


                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            },
                            throwable -> {
                                System.out.println("#Error connecting to neighbor: " + neighbor.getPair1() + ":" + neighbor.getPair2());
                            });


                    channel.shutdown();



                } catch (Exception e) {
                    System.out.println("#Error connecting to neighbor: " + neighbor.getPair1() + " | " + neighbor.getPair2());
                }
            }
        }




        System.out.println("##Opening server socket");
        Server server = ServerBuilder.forPort(Integer.parseInt(nodeState.getIpPort()))
                .addService(this)
                .build().start();


        System.out.println("##Server started, listening on " +  nodeState.getKeyString() );

        server.awaitTermination();
    }

    public void addNode(String ip_add, String ip_port) throws NoSuchAlgorithmException, IOException {
        System.out.println("#### Adding node: " + ip_add + ":" + ip_port);
        NodeState newNode = new NodeState(ip_add,ip_port,hash.generateHash(ip_add + "-" + ip_port));

        for (int i = 0; i < number_vNodes; i++) {
            VirtualNode<NodeState> vNode = new VirtualNode<>(newNode,i);
            if (!nodeState.getRing().containsKey(vNode.getKey())){
                nodeState.addRing(vNode.getKey(),vNode);
            }
        }
        System.out.println("#### Node added");
    }

    private VirtualNode<NodeState> lookupClosestNode(byte[] key) throws NoSuchAlgorithmException {
        TreeMap<byte[], VirtualNode<NodeState>> nodes = nodeState.getRing();
        if (nodes.isEmpty()) {
            return null;
        }
        SortedMap<byte[], VirtualNode<NodeState>> tailMap = nodes.tailMap(key, false);
        if (tailMap.isEmpty()) {
            return nodes.firstEntry().getValue();
        }
        byte[] closestKey = tailMap.firstKey();
        return nodes.get(closestKey);
    }

    //Migrate keys -> method to migrate keys from a node to another
    private void migrateKeys() throws NoSuchAlgorithmException, IOException {
        System.out.println("\n\n#### Key Migration | Starting Migration");
        for (byte[] keyStored : nodeState.getStorage().keySet()){
            VirtualNode<NodeState> closestNode = lookupClosestNode(keyStored);
            System.out.println("Closest Node - " + closestNode.getKeyString());

            if(!closestNode.isVirtualNodeOf(nodeState)){
                String dest_ip_add =  closestNode.getPhysicalNode().getIpAddress();
                String dest_ip_port = closestNode.getPhysicalNode().getIpPort();
                System.out.println("\n#### Key Migration | Key transfer");
                Boolean success = migrateFileTransfer(dest_ip_add,Integer.parseInt(dest_ip_port),keyStored);
                if (success){
                    System.out.println("#### Key Migration | Key removed");
                    nodeState.removeStorage(keyStored);
                    System.out.println("#### Key Migration | Key migrated successfully\n");
                } else {System.out.println("#### Key Migration | Error transferring key");}

            }
            else{
                System.out.println("#### Key Migration | Key belongs to node");
            }

        }
        System.out.println("#### Key Migration | Migration completed successfully\n\n");
        nodeState.printState();
    }

    private Boolean migrateFileTransfer(String ip_add,int port, byte[] file_key) throws IOException{

        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip_add, port)
                .usePlaintext()
                .build();
        RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        String filename = nodeState.getStorage().get(file_key).replaceAll(serverStorageFilePath, "");
        System.out.println("Sending file transfer request -> " + filename + " to " + ip_add + ":" + port);

        UploadFileRequest req = UploadFileRequest.newBuilder().setFileName(filename).build();
        UploadFileResponse auth = stub.uploadFile(req).blockingGet();
        System.out.println("Request -> " + auth.getSuccess());

        if(auth.getSuccess()){
                File file = new File(nodeState.getStorage().get(file_key));

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
                    UploadFileResponseTransfer transferResponse = stub.uploadFileTransfer(requestFlowable).onErrorComplete(throwable -> {
                        System.err.println("Error uploading file: " + throwable.getMessage());
                        return false;
                    }).blockingGet();

                    if (transferResponse.getSuccess()) {
                        System.out.println("File Transfered: " + transferResponse.getSuccess());
                        return true;
                    } else {
                        System.out.println("Error uploading file: " + auth.getFileName());
                        return false;
                    }

                }
                catch (IOException e) {
                    return false;
                }
        }
        else {
            System.out.println("Error migrating file: " + auth.getFileName());

        }


        channel.shutdown();
        return true;
    }

    // Aux method to write buffer to file - happens every 100 chunks
    private void writeBufferToFile(List<UploadFileRequestTransfer> chunkBuffer, FileOutputStream fileOutputStream) {
        try {
            for (UploadFileRequestTransfer chunk : chunkBuffer) {fileOutputStream.write(chunk.getFileData().toByteArray());}
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ############ RPC METHODS ############

    //newNode -> used to add a new node to the ring
    @Override
    public Single<NewNodeResponse> newNode (Single<NewNodeRequest> request) {
        return request.map(req -> {
            System.out.println("#### New Node request: " + req.getNodeIpAdd() + " | " + req.getNodeIpPort());
            String ip_add = req.getNodeIpAdd();
            String ip_port = req.getNodeIpPort();
            try {
                this.addNode(ip_add,ip_port);
                migrateKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return NewNodeResponse.newBuilder().setNodeIpAdd(nodeState.getIpAddress()).setNodeIpPort(nodeState.getIpPort()).build();
        }); }

    //Download -> used to download a file from the storage
    @Override
    public Single<DownloadFileResponse> downloadFile(Single<DownloadFileRequest> request) {
        return request.map(req -> {
            System.out.println("#### Download request: " + req.getFileName());

            String file_name = req.getFileName();
            byte[] file_key = hash.generateHash(file_name);
            VirtualNode<NodeState> storedNode = lookupClosestNode(file_key);
            DownloadFileResponse.Builder resp = DownloadFileResponse.newBuilder().setSuccess(false).setFileName(file_name);
            System.out.println("Closest node : " + storedNode.getKeyString());

            if (storedNode.isVirtualNodeOf(nodeState) && nodeState.getStorage().containsKey(file_key)) {
                System.out.println("#### File found in node: " + nodeState.getIpAddress() + " | " + nodeState.getIpPort());
                resp.setSuccess(true);

            }
            else {
                if (storedNode.isVirtualNodeOf(nodeState)) {
                    System.out.println("#### File not found in node storage");
                    resp.setErrorMessage("File not found in node storage");
                } else {
                    System.out.println("#### Incorrect Node | It should be in node: " + storedNode.getPhysicalNode().getIpAddress() + " | " + storedNode.getPhysicalNode().getIpPort());
                    resp.setNodeIp(storedNode.getPhysicalNode().getIpAddress())
                            .setNodePort(storedNode.getPhysicalNode().getIpPort())
                            .setErrorMessage("Incorrect Node");
                }
            }
            return resp.build();
        });


    }
    @Override
    public Flowable<DownloadFileResponseTransfer> downloadFileTransfer(Single<DownloadFileRequest> request){
        return request.flatMapPublisher(req -> {
            System.out.println("#### Download request: " + req.getFileName());
            String file_name = req.getFileName();
            byte[] file_key = hash.generateHash(file_name);
            File file = new File(nodeState.getStorage().get(file_key));

            if (file.exists()) {
                try {
                    byte[] buffer = new byte[1024];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    return Flowable.generate(emitter -> {
                        int bytesRead = fileInputStream.read(buffer);
                        if (bytesRead == -1) {
                            emitter.onComplete();
                        } else {
                            byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                            emitter.onNext(DownloadFileResponseTransfer.newBuilder()
                                    .setFileName(file_name)
                                    .setSuccess(true)
                                    .setFileData(ByteString.copyFrom(chunk))
                                    .build());
                        }
                    });
                } catch (IOException e) {
                    return Flowable.just(DownloadFileResponseTransfer.newBuilder().setFileName(file_name).setSuccess(false).setErrorMessage("Error reading file").build());
                }
            }

            return Flowable.just(DownloadFileResponseTransfer.newBuilder().setFileName(file_name).setSuccess(false).setErrorMessage("File not Found").build());
        });
    }

    //Upload -> used to upload a file to the storage
    @Override
    public Single<UploadFileResponse> uploadFile(Single<UploadFileRequest> request) {
        return request.map(req -> {
            System.out.println("\n#### Upload request: " + req.getFileName());

            String file_name = req.getFileName();
            byte[] file_key = hash.generateHash(file_name);
            VirtualNode<NodeState> storedNode = lookupClosestNode(file_key);
            UploadFileResponse.Builder resp = UploadFileResponse.newBuilder().setSuccess(false).setFileName(file_name);
            System.out.println("Closest node : " + storedNode.getKeyString());
            if (storedNode.isVirtualNodeOf(nodeState)) {
                System.out.println("#### File belongs to node");
                resp.setSuccess(true);
            }
            else {
                System.out.println("#### Incorrect Node | It should be in node: " + storedNode.getPhysicalNode().getIpAddress() + " | " + storedNode.getPhysicalNode().getIpPort());
                resp.setNodeIp(storedNode.getPhysicalNode().getIpAddress())
                        .setNodePort(storedNode.getPhysicalNode().getIpPort())
                        .setErrorMessage("Incorrect Node");

            }
            return resp.build();
        });
    }


    @Override
    public Single<UploadFileResponseTransfer> uploadFileTransfer(Flowable<UploadFileRequestTransfer> request) {
        AtomicLong totalFileSize = new AtomicLong(0);
        AtomicBoolean isFirstChunk = new AtomicBoolean(true);
        AtomicReference<File> file = new AtomicReference<>();
        AtomicReference<FileOutputStream> fileOutputStream = new AtomicReference<>();
        List<UploadFileRequestTransfer>chunkBuffer = new ArrayList<>();

        return request
                .onBackpressureBuffer()
                .doOnNext(chunk -> {
                    if (isFirstChunk.get()) {
                        System.out.println("#### File upload started -> " + chunk.getFileName());

                        try {
                            File newFile = new File(serverStorageFilePath+chunk.getFileName());
                            byte[] file_key = hash.generateHash(chunk.getFileName());
                            file.set(newFile);
                            fileOutputStream.set(new FileOutputStream(String.valueOf(file)));
                            nodeState.addStorage(file_key, newFile.toString());
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        isFirstChunk.set(false);
                    }
                    totalFileSize.addAndGet(chunk.getFileData().size());
                    chunkBuffer.add(chunk);
                    if (chunkBuffer.size() == 100) {
                        writeBufferToFile(chunkBuffer, fileOutputStream.get());
                        chunkBuffer.clear();
                    }
                })
                .ignoreElements()
                .andThen(Single.just(UploadFileResponseTransfer.newBuilder().setSuccess(true).build()))
                .doOnSuccess(response -> {
                    if (!chunkBuffer.isEmpty()){
                        writeBufferToFile(chunkBuffer, fileOutputStream.get());
                        chunkBuffer.clear();
                    }
                    fileOutputStream.get().close();
                    System.out.println("#### File uploaded successfully! | Total size: " + totalFileSize.get() + " bytes\n");

                })
                .onErrorReturn(error -> {
                    System.err.println("Error occurred during file upload: " + error.getMessage());
                    file.get().delete();
                    fileOutputStream.get().close();
                    return UploadFileResponseTransfer.newBuilder().setSuccess(false).build();
                });
    }


    //Remove -> used to remove a file from the storage
    @Override
    public Single<RemoveResponse> removeFile(Single<RemoveRequest> request) {
        return request.map(req -> {
            System.out.println("#### Remove request: " + req.getFileName());
            String file_name = req.getFileName();
            byte[] file_key = hash.generateHash(file_name);

            VirtualNode<NodeState> storedNode = lookupClosestNode(file_key);
            if (storedNode.isVirtualNodeOf(nodeState) && nodeState.getStorage().containsKey(file_key)){
               System.out.println("#### File removed -> " + file_name );
               nodeState.removeStorage(file_key);
               return RemoveResponse.newBuilder()
                       .setSuccess(true)
                       .setNodeIp(nodeState.getIpAddress())
                       .setNodePort(nodeState.getIpPort())
                       .build();
           }
              else {
                  if (storedNode.isVirtualNodeOf(nodeState)) {
                        System.out.println("#### Remove File -> File not found in storage");
                        return RemoveResponse.newBuilder()
                                .setSuccess(false)
                                .build();
                  }else {
                      System.out.println("#### Remove File -> Incorrect Node");
                      return RemoveResponse.newBuilder()
                              .setSuccess(false)
                              .setNodeIp(storedNode.getPhysicalNode().getIpAddress())
                              .setNodePort(storedNode.getPhysicalNode().getIpPort())
                              .build();
                  }

              }


        });
    }

    //Ping -> used to test connection
    @Override
    public Single<PingResponse> ping(Single<PingRequest> request) {
        return request.map(req -> {
            System.out.println("#### Ping Request from " +  req.getNodeIp() + ":" + req.getNodePort() );
            return PingResponse.newBuilder().setMessage("PONG!").setNodeIp(nodeState.getIpAddress()).setNodePort(nodeState.getIpPort()).build();
        });
    }

}
