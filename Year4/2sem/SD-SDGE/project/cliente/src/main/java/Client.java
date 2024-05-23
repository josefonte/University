import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import node.proto.*;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import node.proto.Rx3DataServerNodeGrpc;




import node.proto.UploadFileRequest;
import node.proto.UploadFileResponse;
import node.proto.UploadFileRequestTransfer;
import node.proto.DownloadFileRequest;
import node.proto.DownloadFileResponse;
import node.proto.PingRequest;
import node.proto.RemoveRequest;
import node.proto.RemoveResponse;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Socket socket ;
    private static PrintWriter out;
    private static BufferedReader in;

    private static String User = null;

    private static  String  filesPath = "./src/main/files/";
    private static String  downloadsPath = "./src/main/client_downloads/";


    private static String pid = Long.toString(ProcessHandle.current().pid());

    private static long session_counter = 0;


    public static void main(String[] args) throws IOException {

        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        frontPage();

    }



    public static void frontPage() {
        int returnValue = 1;
        try {
            while (returnValue==1) {
                System.out.println("Select an option:");
                System.out.println("1. Register");
                System.out.println("2. Authenticate");
                System.out.println("3. Exit");

                int choice;
                try {
                    choice = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        returnValue = register();
                        break;
                    case 2:
                        returnValue = authenticate();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        out.close();
                        socket.close();
                        returnValue = -1;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (returnValue==0) clientPage();
    }

    private static int register() throws IOException {
        System.out.println("Enter username:");
        String username = reader.readLine();
        System.out.println("Enter password:");
        String password = reader.readLine();
        out.println("create_account," + username + "," + password);
        String response = in.readLine();
        if (response.equals("Account created")) {
            User = username;
            System.out.println("Registration successful.");
            return 0;
        } else {
            System.out.println("Registration failed. Please try again. " + response);
            return 1;
        }
    }

    private static int authenticate() throws IOException {
        System.out.println("Enter username:");
        String username = reader.readLine();
        System.out.println("Enter password:");
        String password = reader.readLine();
        out.println("login," + username + "," + password);
        String response = in.readLine();
        if (response.equals("Logged in")) {
            User = username;
            System.out.println("Authentication successful.");
            return 0;
        } else {
            System.out.println("Authentication failed. Please try again. "+ response);
            return 1;
        }
    }



    public static void clientPage() {

        try {
            while (true) {
                System.out.println("Select an option:");
                System.out.println("1. Create Album");
                System.out.println("2. Edit Album");
                System.out.println("3. Request Album Metadata");
                System.out.println("4. Request File Data");
                System.out.println("5. Exit");
                int choice;
                try {
                    choice = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        create_album();
                        break;
                    case 2:
                        edit_album();
                        break;
                    case 3:
                        request_album_content();
                        break;
                    case 4:
                        request_file_data();
                        break;
                    case 5:
                        int result = logout();
                        if (result == 0){
                            System.out.println("Exiting...");
                            frontPage();
                            return;
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int logout() throws IOException {
        out.println("logout");
        String response = in.readLine();
        if (response.equals("Logged out")) {
            User = null;
            System.out.println("Logged out successfully.");
            return 0;
        } else {
            System.out.println("Logout failed. Please try again. "+ response);
            return 1;
        }
    }

    private static int create_album() throws IOException {
        System.out.println("Enter album name:");
        String album_name = reader.readLine();
        out.println("create_album," + album_name);
        String response = in.readLine();
        if (response.equals("Album created")) {
            System.out.println("Album created successfully.");
        } else {
            System.out.println("Album creation failed. Please try again. "+ response);
        }
        return 0;
    }




    private static Boolean canBeDelivered(Long pid, VersionVector vv, VersionVector vvm){
        if (vv.getOrDefault(pid,0)+1 == vvm.getOrDefault(pid,0)){
            for (HashMap.Entry<Long, Integer> entry : vvm.entrySet()) {
                if (!entry.getKey().equals(pid)) {
                    if (entry.getValue() > vv.getOrDefault((entry.getKey()),0)) {
                        return false;
                    }
                }
            }
            if (vv.lastReceived != null) {
                vv.lastDependency = new VersionVector(vv.lastReceived);
            }
            vv.lastReceived = new VersionVector(vvm);
            return true;
        }
        return false;
    }

    private static void checkMessagesPending(ArrayList<String> pending_messages, ArrayList<Long> pending_pids,
                                             ArrayList<VersionVector> pending_vv, VersionVector selfVV){
        boolean flag = true;
        while (flag && pending_messages.size() > 0){
            flag = false;
            for (int i = 0; i < pending_messages.size(); i++){
                String message = pending_messages.get(i);
                Long id = pending_pids.get(i);
                VersionVector vv = pending_vv.get(i);
                if (canBeDelivered(id,selfVV,vv)){
                    selfVV.put(id,selfVV.getOrDefault(id,0)+1);
                    pending_messages.remove(i);
                    pending_pids.remove(i);
                    pending_vv.remove(i);
                    System.out.println("Received: " + message);
                    flag = true;
                    break;
                }
            }
        }
    }

    private static void handle_chat(String[] parts,VersionVector selfVV,
                                      ArrayList<String> pending_messages, ArrayList<Long> pending_pids,
                                      ArrayList<VersionVector> pending_vv, Long mypid){

        String[] chat_parts = parts[2].split(";");
        Long id = Long.parseLong(chat_parts[0]);
        if (id.equals(mypid)){
            return;
        }
        VersionVector vv = VersionVector.deserializeVersionVector(chat_parts[1]);

        String message = parts[3];
        if (selfVV.firstMessage){
            selfVV.putAll(vv);
            System.out.println("Received: " + message);
            selfVV.lastReceived = new VersionVector(vv);
            selfVV.firstMessage = false;
        }
        else{
            if (canBeDelivered(id,selfVV,vv)){
                selfVV.put(id,selfVV.getOrDefault(id,0)+1);
                System.out.println("Received: " + message);
                checkMessagesPending(pending_messages,pending_pids,pending_vv,selfVV);
            }
            else{
                pending_messages.add(message);
                pending_pids.add(id);
                pending_vv.add(vv);
            }
        }

    }






    private static void chat(String album) {
        session_counter++;
        try (ZContext context = new ZContext()) {

            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);

            subscriber.connect("tcp://localhost:" + 5556);
            subscriber.subscribe(album.getBytes());

            publisher.connect("tcp://localhost:" + 5555);


            String pid_string = pid + session_counter;
            long pid = Long.parseLong(pid_string);

            // estado do chat
            ReentrantLock vvlock = new ReentrantLock();
            VersionVector versionVector = new VersionVector();

            out.println("session_join," + User);
            String sessionResponse = in.readLine();
            System.out.println(sessionResponse);

            out.println("get_album_info," + album);

            // ["nuno","joao","tony"]
            String usersResponse = in.readLine();
            String[] users = usersResponse.substring(1, usersResponse.length() - 1).split(",");

            ORSetCRDT utilizadores = new ORSetCRDT("utilizadores");

            for (String user : users) {
                if (user.isEmpty()) {
                    continue;
                }
                utilizadores.simpleAdd(user.substring(1, user.length() - 1),pid_string);
            }

            // #{"file" => "7","file2" => "null"}
            String filesResponse = in.readLine();
            String[] files = filesResponse.substring(2, filesResponse.length() - 1).split(",");

            ORSetCRDT ficheiros = new ORSetCRDT("ficheiros");
            HashMap<String,String> rates = new HashMap<>();


            for (String file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                String[] parts = file.split(" => ");
                String file_name = parts[0].trim();
                String file_rating = parts[1].trim();
                file_name = file_name.substring(1, file_name.length() - 1);
                file_rating = file_rating.substring(1, file_rating.length() - 1);

                ficheiros.simpleAdd(file_name,pid_string);
                if (!file_rating.equals("null")){

                    System.out.println("Adding file " + file_name + " with rating: " + file_rating);

                    rates.put(file_name,file_rating);
                }
            }
            System.out.println("Files: " + ficheiros.elements());
            System.out.println("Users: " + utilizadores.elements());



            // Start a thread to handle incoming messages
            new Thread(() -> {

                try{
                    ArrayList<VersionVector> pending_vv = new ArrayList<>();
                    ArrayList<Long> pending_pids = new ArrayList<>();
                    ArrayList<String> pending_messages = new ArrayList<>();
                    while (!Thread.currentThread().isInterrupted()) {
                        String receivedMessage = new String(subscriber.recv());
                        String[] parts = receivedMessage.split(":");
                        String type = parts[1];
                        if (type.equals("chat")) {
                            vvlock.lock();
                            handle_chat(parts,versionVector,pending_messages,pending_pids,pending_vv,pid);
                            vvlock.unlock();
                        } else {
                            String mpid = parts[2];
                            if (mpid.equals(Long.toString(pid))){
                                continue;
                            }
                            if (parts[3].equals("users")){
                                ORSetCRDT utilizadores_m = ORSetCRDT.deserialize(parts[4]);
                                utilizadores.join(utilizadores_m);
                            }
                            else if (parts[3].equals("files")){
                                ORSetCRDT ficheiros_m = ORSetCRDT.deserialize(parts[4]);
                                ficheiros.join(ficheiros_m);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Leaving chat...");
                    subscriber.close();
                    return;
                }
            }).start();

            String command = reader.readLine();
            // String message;
            String crdt_name;
            String crdt;
           // ORSetCRDT delta;
            while (!command.startsWith("\\exit")){
                if (!command.startsWith("\\")){
                    vvlock.lock();
                    int newSeqNum = versionVector.getOrDefault(pid,0) + 1;
                    versionVector.put(pid, newSeqNum);
                    String vv = versionVector.serializeVersionVector();
                    vvlock.unlock();
                    String new_message = String.format("%s:chat:%s;%s:%s", album, pid,vv,command);
                    publisher.send(new_message.getBytes());
                }
                else{
                    if (command.startsWith("\\add_file")) {
                        String[] parts = command.split(" ");
                        String file_name = parts[1];
                        fileUpload("localhost", 8000, album,file_name);
                        // message = "add_file," + album + "," + file_name;
                        crdt_name = "files";
                        ficheiros.add(file_name,pid_string);
                        crdt = ficheiros.serialize();
                    } else if (command.startsWith("\\remove_file")) {
                        String[] parts = command.split(" ");
                        String file_name = parts[1];
                        removeFile("localhost", 8000,album, file_name);
                        // message = "remove_file," + album + "," + file_name;
                        crdt_name = "files";
                        ficheiros.remove(file_name,pid_string);
                        crdt = ficheiros.serialize();
                    } else if (command.startsWith("\\rate_file")) {
                        String[] parts = command.split(" ");
                        String file_name = parts[1];
                        String rating = parts[2];
                        if (rates.containsKey(file_name)){
                            System.out.println("You have already rated this file. Please try again.");
                        }
                        else if (Integer.parseInt(rating) < 0 || Integer.parseInt(rating) > 10){
                            System.out.println("Invalid rating. Please try again.");
                        }
                        else if(!ficheiros.m.containsKey(file_name)) {
                            System.out.println("File does not exist. Please try again.");
                        }
                        else{
                            rates.put(file_name,rating);
                        }
                        command = reader.readLine();
                        rates.put(file_name,rating);
                        continue;
                    } else if (command.startsWith("\\add_user")) {
                        String[] parts = command.split(" ");
                        String username = parts[1];
                        // message = "add_user," + album + "," + username;
                        crdt_name = "users";
                        utilizadores.add(username,pid_string);
                        crdt = utilizadores.serialize();
                    } else if (command.startsWith("\\remove_user")) {
                        String[] parts = command.split(" ");
                        String username = parts[1];
                        // message = "remove_user," + album + "," + username;
                        crdt_name = "users";
                        utilizadores.remove(username,pid_string);
                        crdt = utilizadores.serialize();
                    }
                    else {
                        System.out.println("Invalid command. Please try again.");
                        command = reader.readLine();
                        continue;
                    }
                    String new_message = String.format("%s:command:%s:%s:%s", album, pid, crdt_name,crdt);
                    publisher.send(new_message.getBytes());

                }
                command = reader.readLine();
            }

            String usersToSend = utilizadores.serializeNames();
            System.out.println("Users: " + usersToSend);

            // {file2=>10|file3=>null}
            StringJoiner filesToSend = new StringJoiner("|");
            for (String file : ficheiros.m.keySet()) {
                filesToSend.add(file + "=>" + rates.getOrDefault(file, "null"));
            }
            System.out.println("Files: " + filesToSend);

            out.println("update_album," + album + "," + usersToSend + ",{" + filesToSend + "}");
            String response = in.readLine();
            System.out.println(response);

            String new_message = String.format("%s:command:%s:%s:%s", album, pid, "users",utilizadores.serialize());
            publisher.send(new_message.getBytes());
            new_message = String.format("%s:command:%s:%s:%s", album, pid, "files",ficheiros.serialize());
            publisher.send(new_message.getBytes());
            out.println("session_leave," + User);
            String leaveResponse = in.readLine();
            System.out.println(leaveResponse);
            publisher.close();
        }
        catch (Exception e) {
            System.out.println("Leaving session...");
        }
    }


    private static int edit_album() throws IOException {
        String command;
        String album_name;
        do{
            System.out.println("Enter album name (or enter to leave):");
            album_name = reader.readLine();
            out.println("check_user," + album_name);
            command = in.readLine();
            if (!command.equals("OK")){
                System.out.println("You are not in the album. Please try again.");
            }

        } while(!command.equals("OK") && !album_name.equals(""));

        if (album_name.equals("")){
            return 1;
        }

        System.out.println("""
                To use the chat functionality, simply type your message and hit enter. Your message will be sent to all users currently in the chat.
                                
                To perform editing operations on albums, start your input with a backslash "\\" followed by the command:
                                
                - To add a file to an album:
                  \\add_file <name_of_file> <content>
                                
                - To remove a file from an album:
                  \\remove_file <name_of_file>
                                
                - To rate a file in an album:
                  \\rate_file <name_of_file> <rating>
                                
                - To add a user to an album:
                  \\add_user <username>
                                
                - To remove a user from an album:
                  \\remove_user <username>
                
                To exit the chat, type "\\exit" and hit enter.               
                """);
        chat(album_name);
        return 1;
    }

    private static int request_album_content() throws IOException {

        String album_name;
        String response;
        System.out.println("Enter album name (or enter to leave):");
        album_name = reader.readLine();
        if (album_name.equals("")){
            return 1;
        }
        out.println("get_files," + album_name);
        response = in.readLine();
        if (response.equals("You must be a user of the album to get its content")){
            System.out.println("You are not in the album. Please try again.");
            request_album_content();
            return 1;
        }

        response = response.substring(2, response.length() - 1);
        String[] files = response.split(",");
        if (files.length == 0) {
            System.out.println("No files in the album.");
            return 1;
        }
        for (String file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String[] parts = file.split(" => ");
            String file_name = parts[0].trim();
            String file_rating = parts[1].trim();
            file_name = file_name.substring(1, file_name.length() - 1);
            System.out.println("File: " + file_name + " | Rating: " + file_rating);
        }
        return 1;
    }

    private static int request_file_data() throws IOException {

        String album_name,file_name,command;

        do{
            System.out.println("Enter album name (or enter to leave):");
            album_name = reader.readLine();
            if (album_name.equals("")){
                return 1;
            }
            System.out.println("Enter file name:");
            file_name = reader.readLine();
            out.println("check_file," + album_name + "," + file_name);
            command = in.readLine();
            if (!command.equals("OK")){
                System.out.println(command + ". Please try again.");
            }

        } while(!command.equals("OK"));


        downloadFile("localhost", 8000, album_name,file_name);

        return 1;
    }


    private static ManagedChannel createChannel(String ip, String port){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, Integer.parseInt(port))
                .usePlaintext()
                .build();

        return channel;
    }

    private static void downloadFile(String ip_add,int port,String albumName, String fileName) {
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));

        Rx3DataServerNodeGrpc.RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        Single<DownloadFileRequest> req = Single.just(DownloadFileRequest.newBuilder().setFileName(albumName+"_"+fileName).build());

        AtomicLong totalFileSize = new AtomicLong(0);
        AtomicBoolean isFirstChunk = new AtomicBoolean(true);
        AtomicReference<File> file = new AtomicReference<>();
        AtomicReference<FileOutputStream> fileOutputStream = new AtomicReference<>();
        List<DownloadFileResponseTransfer> chunkBuffer = new ArrayList<>();

        System.out.println("Sending download request");

        DownloadFileResponse response = stub.downloadFile(req).blockingGet();

        if (response.getSuccess()) {
            stub.downloadFileTransfer(req)
                    .onBackpressureBuffer()
                    .doOnNext(chunk -> {
                        if (isFirstChunk.get()) {
                            System.out.println("#### File Download started -> " + fileName);

                            try {
                                File newFile = new File(downloadsPath + fileName);
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
            System.out.println("Error downloading file: " + fileName);
            if(!response.getNodeIp().isEmpty() && !response.getNodePort().isEmpty()){
                System.out.println("Error: " + response.getErrorMessage() + " | search in " + response.getNodeIp() + ":" + response.getNodePort());
                channel.shutdown();
                downloadFile(response.getNodeIp(), Integer.parseInt(response.getNodePort()), albumName,fileName);
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

    private static void fileUpload(String ip_add,int port, String albumName, String fileName) throws IOException {
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        Rx3DataServerNodeGrpc.RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        UploadFileRequest req = UploadFileRequest.newBuilder().setFileName(albumName+"_"+fileName).build();
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
                                .setFileName(albumName+"_"+fileName)
                                .setFileData(ByteString.copyFrom(chunk))
                                .build()); // Emit next chunk
                    }
                });
                System.out.println("### Starting file transfer -> " + fileName );
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
            System.out.println("Error uploading file: " + fileName);
            if(!auth.getNodeIp().isEmpty() && !auth.getNodePort().isEmpty()){
                System.out.println("Error: " + auth.getErrorMessage() + " | search in " + auth.getNodeIp() + ":" + auth.getNodePort());
                channel.shutdown();
                fileUpload(auth.getNodeIp(), Integer.parseInt(auth.getNodePort()),albumName, fileName);
            }
        }

        channel.shutdown();
    }

    private static void removeFile(String ip_add,int port, String albumName, String fileName) {

        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        Rx3DataServerNodeGrpc.RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        System.out.println("Sending remove request");
        Single<RemoveRequest> req = Single.just(RemoveRequest.newBuilder().setFileName(albumName +"_"+fileName).build());
        RemoveResponse response = stub.removeFile(req).blockingGet();

        if (response.getSuccess()) {
            System.out.println("File removed successfully");
        }
        else {
            System.out.println("Error removing file: ");
            if(!response.getNodeIp().isEmpty() && !response.getNodePort().isEmpty()){
                System.out.println("Search in " + response.getNodeIp() + ":" + response.getNodePort());
                channel.shutdown();
                removeFile(response.getNodeIp(), Integer.parseInt(response.getNodePort()), albumName,fileName);
            }
        }
        channel.shutdown();
    }

    private static void ping(String ip_add,int port){
        ManagedChannel channel = createChannel(ip_add, String.valueOf(port));
        Rx3DataServerNodeGrpc.RxDataServerNodeStub stub = Rx3DataServerNodeGrpc.newRxStub(channel);

        Single<PingRequest> req = Single.just(PingRequest.newBuilder().setNodeIp("localhost").setNodePort("0000").setMessage("PING").build());

        System.out.println("Sending ping");
        stub.ping(req).blockingSubscribe(r->{
            System.out.println("Response: " + r.getMessage() + " from " + r.getNodeIp() + ":" + r.getNodePort());
        });
    }









}
