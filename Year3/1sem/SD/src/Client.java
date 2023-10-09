import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    private Socket s;

    private String id;
    private DataInputStream in;
    private DataOutputStream out;

    public Client() throws IOException {
        s = new Socket("localhost", 12345);
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }


    public boolean authentication(String user,String pass)throws AuthenticationFailedException, IOException{
        out.writeUTF("authentication");
        out.writeUTF(user);
        out.writeUTF(pass);
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("authentication")) {
            receiveNotification();
            operation = in.readUTF();
        }
        if (!in.readBoolean()) throw new AuthenticationFailedException();
        return true;
    }

    public boolean createAccount(String user,String pass)throws IOException,UserAlreadyExistsException{
        out.writeUTF("createAcount");
        out.writeUTF(user);
        out.writeUTF(pass);
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("createAcount")) {
            receiveNotification();
            operation = in.readUTF();
        }
        if (!in.readBoolean())  throw new UserAlreadyExistsException();
        return true;
    }

    public void receiveNotification() throws IOException {
        Coordinate c = Coordinate.deserialize(in);
        System.out.print("NOTIFICAÇÃO:");
        System.out.println(c);
        System.in.read();
    }


    public List<Coordinate> listTrotinetes() throws IOException {
        out.writeUTF("mapa");
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("mapa")) {
            receiveNotification();
            operation = in.readUTF();
        }
        int size = in.readInt();
        List<Coordinate> lista = new ArrayList<>();
        for ( int i=0; i < size;i++){
            Coordinate c = Coordinate.deserialize(in);
            lista.add(c);
        }
        return  lista;
    }

    public List<Coordinate> freeScooters(Coordinate c) throws IOException {
        out.writeUTF("trotinetesLivres");
        out.writeInt(c.getX());
        out.writeInt(c.getY());
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("trotinetesLivres")) {
            receiveNotification();
            operation = in.readUTF();
        }

        int size = in.readInt();
        List<Coordinate> freecooters = new ArrayList<>();
        for ( int i=0; i<size; i++) {
            int x = in.readInt();
            int y = in.readInt();
            Coordinate scooters = new Coordinate(x,y);
            freecooters.add(scooters);

        }

        return freecooters;

    }

    public Reserve reserveScooter(Coordinate c) throws IOException {
        out.writeUTF("reservar");
        c.serialize(out);
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("reservar")) {
            receiveNotification();
            operation = in.readUTF();
        }
        Reserve reserve = Reserve.deserialize(in);

        return reserve;
    }

    public TripInfo parkScooter(String reserveID, Coordinate c) throws IOException {
        out.writeUTF("estacionar");
        out.writeUTF(reserveID);
        c.serialize(out);
        out.flush();
        String operation = in.readUTF();
        while (operation.equals("notification")) {
            receiveNotification();
            operation = in.readUTF();
        }
        if (operation.equals("Id Inexistente")) return null;
        return TripInfo.deserialize(in);

    }

    public Map<Coordinate,Coordinate> rewardsRaio(Coordinate c) throws IOException{
        out.writeUTF("rewardsRaio");
        out.writeInt(c.getX());
        out.writeInt(c.getY());
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("rewardsRaio")) {
            receiveNotification();
            operation = in.readUTF();
        }
        int size = in.readInt();
        Map<Coordinate,Coordinate> rewards = new HashMap<>();
        for ( int i=0; i<size; i++) {
            Coordinate c1 = Coordinate.deserialize(in);
            Coordinate c2 = Coordinate.deserialize(in);
            rewards.put(c1,c2);
        }
        return rewards;

    }

    public boolean activateRewards(Coordinate c) throws IOException{
        out.writeUTF("notifications");
        c.serialize(out);
        out.flush();
        String operation = in.readUTF();
        while (!(operation.equals("notifications"))) {
            receiveNotification();
            operation = in.readUTF();
        }
        return in.readBoolean();
    }

    public boolean deactivateRewards() throws IOException{
        out.writeUTF("cancelNotifications");
        out.flush();
        String operation = in.readUTF();
        while (!operation.equals("cancelNotifications")) {
            receiveNotification();
            operation = in.readUTF();
        }
        return in.readBoolean();
    }

}