import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ORSetCRDT {
    VersionVector cc;
    String name;
    HashMap<String, HashSet<Dot>> m;

    ReentrantLock lock = new ReentrantLock();

    public ORSetCRDT(){
        this.cc = new VersionVector();
        this.m = new HashMap<>();
    }

    public ORSetCRDT(String name){
        this.cc = new VersionVector();
        this.m = new HashMap<>();
        this.name = name;
    }

    public ORSetCRDT(VersionVector cc, HashMap<String, HashSet<Dot>> m){
        this.cc = cc;
        this.m = m;
    }

    public HashSet<String> elements(){
        HashSet<String> elements = new HashSet<>();
        lock.lock();
        for (String elem : m.keySet()){
            elements.add(elem);
        }
        lock.unlock();
        return elements;

    }


    public void add(String name, String pid) {
        Long pid_long = Long.parseLong(pid);
        lock.lock();
        int seqNum = cc.getOrDefault(pid_long,1)+1;
        cc.put(pid_long,seqNum);
        Dot dot = new Dot(pid,seqNum);
        HashSet<Dot> dots = new HashSet<>();
        dots.add(dot);
        m.put(name, dots);
        lock.unlock();
    }

    public void simpleAdd(String name,String pid) {
        Long pid_long = Long.parseLong(pid);
        lock.lock();
        Dot dot = new Dot(pid,1);
        cc.put(pid_long,1);
        HashSet<Dot> dots = new HashSet<>();
        dots.add(dot);
        m.put(name, dots);
        lock.unlock();
    }

    public void remove(String name, String pid) {
        lock.lock();
        m.remove(name);
        lock.unlock();
    }

    public void join(ORSetCRDT other){
        VersionVector cc_m = other.cc;
        HashMap<String, HashSet<Dot>> m_m = other.m;

        // (m, c) ⊔ (m′, c′) = ({k -→ v(k) | k ∈ dom m ∪ dom m′ ∧ v(k) ̸= ⊥}, )
        // where v(k) = fst((m(k), c) ⊔ (m′(k), c′))

        HashMap<String, HashSet<Dot>> new_m = new HashMap<>();
        HashSet<String> keys = new HashSet<>();
        lock.lock();
        keys.addAll(m.keySet());
        keys.addAll(m_m.keySet());
        for (String key : keys){
            // calcular v(k) = fst((m(k), c) ⊔ (m′(k), c′))
            // (s ∩ s′) ∪ (s \ c′) ∪ (s′\ c),
            HashSet<Dot> dots = new HashSet<>();
            HashSet<Dot> s = m.getOrDefault(key, new HashSet<>());
            HashSet<Dot> s_m = m_m.getOrDefault(key, new HashSet<>());
            HashSet<Dot> v_k = new HashSet();
            //v_k.retainAll(s_m); --> não estava a funcionar
            for (Dot dot1 : s) {
                for (Dot dot2 : s_m) {
                    if (dot1.equals(dot2)) {
                        v_k.add(dot1);
                    }
                }
            }
            for (Dot dot : s){
                Long pid = Long.parseLong(dot.pid);
                if (cc_m.getOrDefault(pid,0) < dot.seqNum){
                    v_k.add(dot);
                }
            }
            for (Dot dot : s_m){
                Long pid = Long.parseLong(dot.pid);
                if (cc.getOrDefault(pid,0) < dot.seqNum){
                    v_k.add(dot);
                }
            }
            if (!v_k.isEmpty()){
                new_m.put(key, v_k);
            }

        }
        // m = c ∪ c′
        cc.update(cc_m);
        // communicate to the client if there were changes
        if (!new_m.equals(m)){
            System.out.println("Novo conjunto de " + this.name+" : " + new_m.keySet());
        }
        m = new_m;
        lock.unlock();


    }

    public String serialize(){
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, HashSet<Dot>> entry : m.entrySet()) {
            sb.append(entry.getKey()).append("=");
            for (Dot dot : entry.getValue()){
                sb.append(dot.serializeDot()).append(";");
            }
            sb.append("|");
        }
        sb.append("#");
        sb.append(cc.serializeCausalContext());
        return sb.toString();
    }

    // ["name1"|"name2"|"name3"]
    public String serializeNames() {
        StringJoiner joiner = new StringJoiner("|");
        for (String name : m.keySet()) {
            joiner.add(name);
        }
        return "[" + joiner + "]";
    }

    // {file2=>{nuno=>10}|file3=>{nuno=>4}}
//    public String serializeFiles(){
//        StringJoiner joiner = new StringJoiner("|");
//
//    }

    public static ORSetCRDT deserialize(String orSetString){
        ORSetCRDT orSet = new ORSetCRDT();
        String[] contents = orSetString.split("#");
        if (contents.length>0 && !contents[0].equals("")){
            String[] entries = contents[0].split("\\|");
            for (String entry : entries) {
                String[] parts = entry.split("=");
                String name = parts[0];
                HashSet<Dot> dots = new HashSet<>();
                String[] dotsString = parts[1].split(";");
                for (String dotString : dotsString){
                    dots.add(Dot.deserializeDot(dotString));
                }
                orSet.m.put(name, dots);
            }
        }
        if (contents.length > 1){
            orSet.cc = VersionVector.deserializeCausalContext(contents[1]);
        }
        return orSet;
    }




    public ORSetCRDT addDelta(String name, String pid) {
        //  ({(i, n, e)}, {(i, n + 1)})
        //  with n = max({k | (i, k) ∈ c})
        int n = cc.getOrDefault(Long.parseLong(pid),0);
        VersionVector cc_delta = new VersionVector();
        cc_delta.put(Long.parseLong(pid),n+1);
        HashMap<String, HashSet<Dot>> m_delta = new HashMap<>();
        Dot dot = new Dot(pid,n+1);
        HashSet<Dot> dots = new HashSet<>();
        dots.add(dot);
        m_delta.put(name, dots);
        ORSetCRDT delta = new ORSetCRDT(cc_delta,m_delta);
        joinDelta(delta);
        return delta;
    }

    public ORSetCRDT removeDelta(String name, String pid) {
        //  ({}, {(j, n) | (j, n, e) ∈ s})
        HashSet<Dot> dots = m.getOrDefault(name,new HashSet<>());
        VersionVector cc_delta = new VersionVector();
        for (Dot dot : dots){
            cc_delta.put(Long.parseLong(dot.pid),dot.seqNum);
        }
        HashMap<String, HashSet<Dot>> m_delta = new HashMap<>();
        ORSetCRDT delta = new ORSetCRDT(cc_delta,m_delta);
        joinDelta(delta);
        return delta;
    }

    public void joinDelta(ORSetCRDT other){
        VersionVector cc_m = other.cc;
        HashMap<String, HashSet<Dot>> m_m = other.m;
        // m = (s ∩ s') ∪ {(i, n, e) ∈ s | (i, n) ∉ c'}∪{(i, n, e) ∈ s' | (i, n) ∉ c}
        HashMap<String, HashSet<Dot>> new_m = new HashMap<>();
        // (s ∩ s')
        for(String key : m.keySet()){
            if (m_m.containsKey(key)){
                HashSet<Dot> dots = new HashSet<>();
                HashSet<Dot> s = m.get(key);
                HashSet<Dot> s_m = m_m.get(key);
                for (Dot dot : s){
                    if (s_m.contains(dot)){
                        dots.add(dot);
                    }
                }
                if (!dots.isEmpty()){
                    new_m.put(key,dots);
                }
            }
        }
        // { (i, n, e) ∈ s | (i, n) ∉ c'}
        for(Map.Entry<String, HashSet<Dot>> entry : m.entrySet()){
            String key = entry.getKey();
            HashSet<Dot> dots = new HashSet<>();
            HashSet<Dot> s = entry.getValue();
            for(Dot dot : s){
                Long pid = Long.parseLong(dot.pid);
                if (!cc_m.containsKey(pid) || !cc_m.get(pid).equals(dot.seqNum)){
                    dots.add(dot);
                }
            }
            if (!dots.isEmpty()){
                HashSet<Dot> current_dots = new_m.getOrDefault(key,new HashSet<>());
                current_dots.addAll(dots);
                new_m.put(key,current_dots);
            }

        }

        // {(i, n, e) ∈ s' | (i, n) ∉ c}
        for(Map.Entry<String, HashSet<Dot>> entry : m_m.entrySet()){
            String key = entry.getKey();
            HashSet<Dot> dots = new HashSet<>();
            HashSet<Dot> s = entry.getValue();
            for(Dot dot : s){
                Long pid = Long.parseLong(dot.pid);
                if (!cc.containsKey(pid) || !cc.get(pid).equals(dot.seqNum)){
                    dots.add(dot);
                }
            }
            if (!dots.isEmpty()){
                HashSet<Dot> current_dots = new_m.getOrDefault(key,new HashSet<>());
                current_dots.addAll(dots);
                new_m.put(key,current_dots);
            }

        }


        // c = c ∪ c'
        cc.update(cc_m);

        m = new_m;
        System.out.println("New m: " + m);
    }





}
