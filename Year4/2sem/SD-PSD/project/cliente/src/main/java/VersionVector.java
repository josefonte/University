import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class VersionVector extends HashMap<Long, Integer> {

    public boolean firstMessage = true;

    public VersionVector lastDependency = null;

    public VersionVector lastReceived = null;

    public VersionVector() {
        super();
    }

    public VersionVector(VersionVector other) {
        this.putAll(other);
    }

    public void update(VersionVector other) {
        for (HashMap.Entry<Long, Integer> entry : other.entrySet()) {
            long pid = entry.getKey();
            int seqNum = entry.getValue();
            if (this.containsKey(pid)) {
                int currentSeqNum = this.get(pid);
                if (seqNum > currentSeqNum) {
                    this.put(pid, seqNum);
                }
            } else {
                this.put(pid, seqNum);
            }
        }

    }
    public String serializeVersionVector() {
        StringBuilder sb = new StringBuilder();

        for (HashMap.Entry<Long, Integer> entry : this.entrySet()) {
            if (lastDependency!=null && lastDependency.containsKey(entry.getKey()) &&
                    lastDependency.get(entry.getKey()).equals(entry.getValue())) {
                continue;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        lastDependency = new VersionVector(this);
        lastReceived = null;
        //System.out.println("Sending: " + sb.toString());
        return sb.toString();
    }

    public String serializeCausalContext() {
        StringBuilder sb = new StringBuilder();

        for (HashMap.Entry<Long, Integer> entry : this.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        return sb.toString();
    }


    public static VersionVector deserializeVersionVector(String vvString) {
        VersionVector versionVector = new VersionVector();
        String[] entries = vvString.split(",");
        for (String entry : entries) {
            String[] parts = entry.split("=");
            long pid = Long.parseLong(parts[0]);
            int seqNum = Integer.parseInt(parts[1]);
            versionVector.put(pid, seqNum);
        }
        return versionVector;
    }

    public static VersionVector deserializeCausalContext(String vvString) {
        VersionVector versionVector = new VersionVector();
        String[] entries = vvString.split(",");
        for (String entry : entries) {
            String[] parts = entry.split("=");
            long pid = Long.parseLong(parts[0]);
            int seqNum = Integer.parseInt(parts[1]);
            versionVector.put(pid, seqNum);
        }
        return versionVector;
    }
}
