package psd.trabalho;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class NodeState {

    private final Comparator<byte[]> byteArrayComparator = Arrays::compare;
    private TreeMap<byte[], VirtualNode<NodeState>> ring = new TreeMap<>(byteArrayComparator);
    private TreeMap<byte[], String> storage = new TreeMap<>(byteArrayComparator);
    private String ip_address;
    private String ip_port;
    private byte[] key;

    public NodeState() {}

    public NodeState(String ip_address, String ip_port, byte[] key) {
        this.ip_address = ip_address;
        this.ip_port = ip_port;
        this.key = key;
    }



    public void setRing(TreeMap<byte[], VirtualNode<NodeState>> ring) {
        this.ring = cloneRingTreeMap(ring);
    }

    public TreeMap<byte[], VirtualNode<NodeState>> getRing() {
        return cloneRingTreeMap(ring);
    }

    private TreeMap<byte[], VirtualNode<NodeState>> cloneRingTreeMap(TreeMap<byte[], VirtualNode<NodeState>> original) {
        TreeMap<byte[], VirtualNode<NodeState>> clone = new TreeMap<>(byteArrayComparator);
        for (Map.Entry<byte[], VirtualNode<NodeState>> entry : original.entrySet()) {
            clone.put(entry.getKey().clone(), entry.getValue().clone());
        }
        return clone;
    }
    public void addRing(byte[] key, VirtualNode<NodeState> vNode) {
        this.ring.put(key, vNode);
    }
    public void removeRing(byte[] key) {
        this.ring.remove(key);
    }



    public void setStorage(TreeMap<byte[], String> storage) {
        this.storage = cloneStorageTreeMap(storage);
    }

    public TreeMap<byte[], String> getStorage() {
        return cloneStorageTreeMap(storage);
    }

    private TreeMap<byte[], String> cloneStorageTreeMap(TreeMap<byte[], String> original) {
        TreeMap<byte[], String> clone = new TreeMap<>(byteArrayComparator);
        for (Map.Entry<byte[], String> entry : original.entrySet()) {
            clone.put(entry.getKey().clone(), entry.getValue());
        }
        return clone;
    }
    public void addStorage(byte[] key, String value) {
        this.storage.put(key, value);
    }
    public void removeStorage(byte[] key) {
        if (this.storage.containsKey(key)){
            File file = new File(storage.get(key));
            if (file.exists()) {
                file.delete();
            }
            this.storage.remove(key);
        }
    }



    public void setIpAddress(String ip_address) {
        this.ip_address = ip_address;
    }
    public String getIpAddress() {
        return ip_address;
    }



    public void setIpPort(String ip_port) {
        this.ip_port = ip_port;
    }
    public String getIpPort() {
        return ip_port;
    }



    public void setKey(byte[] key) {
        this.key = key;
    }
    public byte[] getKey() {
        return key;
    }

    public String getKeyString() {
        return this.ip_address + "-" + this.ip_port;
    }


    public NodeState clone() {
        NodeState clone = new NodeState();
        clone.setIpAddress(this.ip_address);
        clone.setIpPort(this.ip_port);
        clone.setKey(this.key);
        clone.setRing(this.ring);
        clone.setStorage(this.storage);
        return clone;
    }
    public void printState() {
        System.out.print("\n\n########### NODE STATE" +
                "\n# KEY: " + key + "\n# IP_ADD: " + ip_address + " | IP_PORT : " + ip_port + "\n");

        System.out.println("# NODES");
        for ( byte[] node : ring.keySet()){
            try {
                System.out.println(ring.get(node).getKeyString());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("# STORAGE");

        for (byte[] key : storage.keySet()){
            System.out.println(key + " -> " + storage.get(key) );
        }
        System.out.println("###########\n\n");
    }
}