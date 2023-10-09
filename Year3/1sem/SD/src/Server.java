import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ServerWorker implements Runnable {
    private Socket s;
    private DataOutputStream out;
    private DataInputStream in;

    private ServerInfo serverI;

    private ReentrantLock l = new ReentrantLock();



    public ServerWorker(Socket socket, ServerInfo si) throws IOException {
        s = socket;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
        serverI = si;

    }

    // @TODO
    @Override
    public void run() {
        try{
        String operation = in.readUTF();
        boolean authenticated = false;

        while (operation!=null){
            switch (operation) {
                case "authentication":{
                    String user = in.readUTF();
                    String pass = in.readUTF();
                    authenticated = serverI.authentication(user,pass);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeBoolean(authenticated);
                    break;
                }
                case "createAcount":{
                    String user = in.readUTF();
                    String pass = in.readUTF();
                    boolean b = serverI.addUser(user,pass);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeBoolean(b);
                    break;
                }
                case "mapa":{
                    List<Coordinate> trotinetesLivres = serverI.getFreeScooters();
                    l.lock();
                    out.writeUTF(operation);
                    out.writeInt(trotinetesLivres.size());
                    for (Coordinate c: trotinetesLivres) c.serialize(out);
                    break;
                }
                case "trotinetesLivres":{
                    Coordinate coord = Coordinate.deserialize(in);
                    List<Coordinate> trotinetesLivres = serverI.getFreeScooters(coord);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeInt(trotinetesLivres.size());
                    for (Coordinate c: trotinetesLivres) c.serialize(out);
                    break;
                }
                case "reservar":{
                    Coordinate coord = Coordinate.deserialize(in);
                    Reserve r = serverI.reserveScooter(coord);
                    l.lock();
                    out.writeUTF(operation);
                    r.serialize(out);
                    break;
                }
                case "estacionar":{
                    String idReserve = in.readUTF();
                    Coordinate c = Coordinate.deserialize(in);
                    TripInfo tripInfo = serverI.parkScooter(idReserve,c);
                    l.lock();
                    if (tripInfo==null) out.writeUTF("Id Inexistente");
                    else{
                        out.writeUTF(operation);
                        tripInfo.serialize(out);
                    }
                    break;
                }
                case "rewardsRaio":{
                    Coordinate coord = Coordinate.deserialize(in);
                    Map<Coordinate,Coordinate> rewards = serverI.getRewards(coord);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeInt(rewards.size());
                    for (Map.Entry<Coordinate,Coordinate> c: rewards.entrySet()){
                        c.getKey().serialize(out);
                        c.getValue().serialize(out);
                    }
                    break;
                }
                case "notifications":{
                    Coordinate coord = Coordinate.deserialize(in);
                    boolean b = serverI.activateRewards(out,coord,l);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeBoolean(b);
                    break;
                }
                case "cancelNotifications":{
                    boolean b = serverI.deactivateRewards(out);
                    l.lock();
                    out.writeUTF(operation);
                    out.writeBoolean(b);
                    break;
                }
            }
            out.flush();
            l.unlock();
            operation=in.readUTF();}
        }
        catch (Exception e){
            System.out.println(e);

        }
    }
}


public class Server {

    public static void main(String[] args)  throws IOException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Insira o tamanho do mapa desejado:");
        int tamanhoMap = scin.nextInt();
        System.out.println("Insira o número de trotinetes que deseja adicionar:");
        int nrTrotinetes = scin.nextInt();
        System.out.println("A criar novas trotinetes...");
        ReentrantLock l = new ReentrantLock();

        ServerSocket ss = new ServerSocket(12345);
        ServerInfo si = new ServerInfo(tamanhoMap, nrTrotinetes);

        si.updateRewards();

        while (true) {
            Socket s = ss.accept();
            Thread worker = new Thread(new ServerWorker(s,si));
            worker.start();
        }
    }
}
