import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TripInfo {

    private Float cost;

    private Float reward=null;
    public TripInfo(float c){
        cost=c;
    }

    public TripInfo(float c,float r){
        cost=c;
        reward=r;
    }

    public void serialize (DataOutputStream out) throws IOException {
        out.writeFloat(cost);
        out.writeBoolean(reward!=null);
        if (reward!=null) out.writeFloat(reward);
    }
    public static  TripInfo deserialize (DataInputStream in) throws IOException {
        float tripCost = in.readFloat();
        boolean hasRewards = in.readBoolean();
        float r;
        TripInfo ti;
        if (hasRewards) ti = new TripInfo(tripCost,in.readFloat());
        else ti=new TripInfo(tripCost);
        return ti;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getReward() {
        return reward;
    }

    public void setReward(Float reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "TripInfo{" +
                "cost=" + cost +
                ", reward=" + reward +
                '}';
    }
}
