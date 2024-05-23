package psd.trabalho;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class VirtualNode<T extends NodeState>  {
    final T physicalNode;
    final int replicaIndex;
    final HashFunction hash = new HashFunction() ;

    public VirtualNode(T physicalNode, int replicaIndex) {
        this.replicaIndex = replicaIndex;
        this.physicalNode = physicalNode;
    }

    public byte[] getKey() throws NoSuchAlgorithmException {
        return hash.generateHash( physicalNode.getKeyString() + "-" + replicaIndex);
    }

    public String getKeyString() throws NoSuchAlgorithmException {
        return physicalNode.getKeyString() + "-" + replicaIndex;
    }

    public boolean isVirtualNodeOf(T pNode) {
        return physicalNode.getKey().equals(pNode.getKey());
    }

    public T getPhysicalNode() {
        return physicalNode;
    }


    @Override
    public VirtualNode<T> clone() {
        try {
            return new VirtualNode<>(physicalNode, replicaIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}