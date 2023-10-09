import java.io.DataOutputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Rewards {
    private Map<Coordinate,Coordinate> rewards = new HashMap<>();
    private ReentrantLock l = new ReentrantLock();
    private NotificationsWorker addRewards = new NotificationsWorker();
    private List<Coordinate> freeScooters;
    private int fixedDistance = 2;

    public Rewards(List<Coordinate> freeScooters){
        this.freeScooters = freeScooters.stream().map(a -> a.clone()).collect(Collectors.toList());
    }

    public List<Coordinate> getFreeScooters(Coordinate c1){
        List<Coordinate> r = new ArrayList<>();
        l.lock();
        try {
            for (Coordinate c2 : freeScooters) {
                if ((java.lang.Math.abs(c1.getX() - c2.getX()))
                        + (java.lang.Math.abs(c1.getY() - c2.getY())) <= fixedDistance)
                    r.add(c2.clone());
            }
            return r;
        }
        finally {
            l.unlock();
        }
    }

    public boolean containsKey(Coordinate c){
        l.lock();
        try{
            return rewards.containsKey(c);
        }
        finally{
            l.unlock();
        }
    }

    public Coordinate get(Coordinate c){
        l.lock();
        try{
            return rewards.get(c);
        }
        finally{
            l.unlock();
        }
    }

    public void removeScooter(Coordinate c){
        l.lock();
        freeScooters.remove(c);
        l.unlock();
    }

    public void addScooter(Coordinate c){
        l.lock();
        freeScooters.add(c);
        l.unlock();
    }

    public Map<Coordinate,Coordinate> getRewards(Coordinate c){
        Map<Coordinate,Coordinate> r = new HashMap<>();
        List<Coordinate> freeS = getFreeScooters(c);
        l.lock();
        try{
        for (Coordinate fc: freeS)
            if (rewards.keySet().contains(fc)) r.put(fc,rewards.get(fc));
        return r;}
        finally {
            l.unlock();
        }
    }

    public boolean activateRewards(DataOutputStream out, Coordinate c, ReentrantLock lSocket) {
        addRewards.waitNotifications(c,out,lSocket);
        return true;
    }

    public boolean deactivateRewards(DataOutputStream out) {
        addRewards.removeNotifications(out);
        return true;
    }

    public void updateRewards(int tamanho){
        l.lock();
        List<Coordinate> freeCoords = new ArrayList<>();

        Map<Coordinate,Coordinate> newRewards = new HashMap<>();
        for (int i=0; i<tamanho; i++)
            for(int j=0; j<tamanho;j++)
                if (getFreeScooters(new Coordinate(i,j)).isEmpty())
                    freeCoords.add(new Coordinate(i,j));
        Map<Coordinate,Integer> mapa = new HashMap<>();
        for (Coordinate c: freeScooters) {
            Integer val = mapa.get(c);
            mapa.put(c, val == null ? 1 : val + 1);
        }
        Comparator<Coordinate> compareCoordinate = (Coordinate c1, Coordinate c2)-> mapa.get(c2) - mapa.get(c1);
        List<Coordinate> scooters  = mapa.keySet().stream().sorted(compareCoordinate).toList();
        for (int i = 0; i<freeCoords.size() && i<scooters.size() && mapa.get(scooters.get(i))>1; i++)
            newRewards.put(scooters.get(i),freeCoords.get(i));
        //adicionar a novas rewards
        for (Coordinate c: newRewards.keySet()){
            if (!rewards.containsKey(c)) addRewards.addNewCoordinate(c);
        }
        rewards = newRewards;
        l.unlock();


    }
}
