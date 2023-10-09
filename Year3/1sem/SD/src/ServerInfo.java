
import java.io.DataOutputStream;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Random;

public class ServerInfo {
    private Map<String, User> users= new HashMap<>();
    private Map<String, Reserve> reserves = new HashMap<>();
    private List<Coordinate> freeScooters = new ArrayList<>();

    private ReentrantReadWriteLock lUsers = new ReentrantReadWriteLock();
    private ReentrantLock lReserves = new ReentrantLock();
    private ReentrantReadWriteLock lScooters = new ReentrantReadWriteLock();

    private Rewards rewards;

    private int fixedDistance = 2;
    private int tamanho = 100;

    public ServerInfo(int tamanhoMap, int trotinetes) {
            Random nrRandom = new Random();
            for ( int i=0; i < trotinetes;i++){
                freeScooters.add(new Coordinate(nrRandom.nextInt(tamanhoMap+1),nrRandom.nextInt(tamanhoMap+1)));
            }
            rewards = new Rewards(freeScooters);
        }



        public boolean addUser(String user, String pass){
            lUsers.writeLock().lock();
            try {
                if (!users.containsKey(user)) {
                    users.put(user, new User(user, pass));
                    return true;
                } else return false;
            }
            finally {
                lUsers.writeLock().unlock();
            }
        }

        public boolean authentication(String user,String pass){
            lUsers.readLock().lock();
            try {
                User u = users.get(user);
                if (u != null && u.getPass().equals(pass)) return true;
                else return false;
            }
            finally {
                lUsers.readLock().unlock();
            }
        }



        public List<Coordinate> getFreeScooters(Coordinate c1){
            List<Coordinate> r = new ArrayList<>();
            lScooters.readLock().lock();
            try {
                for (Coordinate c2 : freeScooters) {
                    if ((java.lang.Math.abs(c1.getX() - c2.getX()))
                            + (java.lang.Math.abs(c1.getY() - c2.getY())) <= fixedDistance)
                        r.add(c2.clone());
                }
                return r;
            }
            finally {
                lScooters.readLock().unlock();
            }
        }

        public Reserve reserveScooter(Coordinate c1){
            int  dist  = -1;
            Coordinate best=new Coordinate(-1,-1);
            lScooters.writeLock().lock();
            for (Coordinate c2: freeScooters) {
                int newDist = c1.distance(c2);
                if ( dist ==-1 || newDist< dist) {
                    dist=newDist;
                    best=c2;
                }

            }
            try{
                freeScooters.remove(best);
                rewards.removeScooter(best);
                String newID = UUID.randomUUID().toString();
                Reserve r;
                if (rewards.containsKey(c1)) {
                    r= new Reserve(best,newID, rewards.get(c1));
                    Thread thread = new Thread(()->{rewards.updateRewards(tamanho);});
                    thread.start();
                }
                else r=new Reserve(best,newID);
                lReserves.lock();
                lScooters.writeLock().unlock();
                reserves.put(r.getId(),r);
                return r;
            }
            finally {
                lReserves.unlock();
            }
        }

        private float calculateCost(Coordinate c1, Coordinate c2){
            return c1.distance(c2);
        }

        public TripInfo parkScooter(String reserveID, Coordinate c){
            lScooters.writeLock().lock();
            lReserves.lock();
            try {
                Reserve r = reserves.remove(reserveID);
                if (r == null) return null;
                float cost = calculateCost(c, r.getC());
                TripInfo t = new TripInfo(cost);

                if (r.hasReward() && r.getReward().equals(c)) {
                    t.setReward((float) (cost * 0.75));
                }
                freeScooters.add(c);
                rewards.addScooter(c);
                Thread thread = new Thread(()->{rewards.updateRewards(tamanho);});
                thread.start();
                return t;
            }
            finally {
                lScooters.writeLock().unlock();
                lReserves.unlock();
            }
        }

        public Map<Coordinate,Coordinate> getRewards(Coordinate c){
            return rewards.getRewards(c);
        }



    public boolean activateRewards(DataOutputStream out,Coordinate c, ReentrantLock lSocket) {
        Thread thread = new Thread(()->{rewards.activateRewards(out,c,lSocket);});
        thread.start();

        return true;
    }

    public boolean deactivateRewards(DataOutputStream out) {
        Thread thread = new Thread(()->{rewards.deactivateRewards(out);});
        thread.start();
        return true;
    }

    public void updateRewards(){
        Thread thread = new Thread(()->{rewards.updateRewards(tamanho);});
        thread.start();

    }

    public List<Coordinate> getFreeScooters() {
            lScooters.readLock().lock();
            try{
                return freeScooters;}
            finally {
                lScooters.readLock().unlock();
            }
        }
    }





