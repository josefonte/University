import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NotificationsWorker {
    private ReentrantLock l = new ReentrantLock();
    private Condition condition = l.newCondition();
    private List<Coordinate> newCoordinates = new ArrayList<>();

    private Set<DataOutputStream> outs = new HashSet<>();


    public NotificationsWorker(){
    }

    public void addNewCoordinate(Coordinate c){
        l.lock();
        newCoordinates.add(c);
        condition.signalAll();
        l.unlock();
    }

    public void removeNotifications(DataOutputStream out){
        l.lock();
        outs.add(out);
        condition.signalAll();
        l.unlock();
    }

    private void notificate(Coordinate c, Coordinate c2, DataOutputStream out, ReentrantLock lSocket) throws IOException {
        if ((java.lang.Math.abs(c.getX() - c2.getX()))
                + (java.lang.Math.abs(c.getY() - c2.getY())) <= 2){
            lSocket.lock();
            out.writeUTF("notification");
            c2.serialize(out);
            out.flush();
            lSocket.unlock();
        }
    }

    public void waitNotifications(Coordinate c, DataOutputStream out, ReentrantLock lSocket){
        l.lock();
        try{
            outs.remove(out);
        while (!outs.contains(out)){
            int size = newCoordinates.size();
            while (size== newCoordinates.size() && !outs.contains(out)) {
                condition.await();
            }
            if (size < newCoordinates.size())
                for (int i=size; i!=newCoordinates.size(); i++)
                    notificate(c,newCoordinates.get(i),out,lSocket);


        }}
        catch (Exception e){

        }
        finally {
            l.unlock();
        }

    }

}
