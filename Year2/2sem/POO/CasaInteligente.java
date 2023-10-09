import ErrorHandling.CasaInteligenteException;
import ErrorHandling.FaturaException;
import ErrorHandling.FornecedorException;
import ErrorHandling.SmartDeviceException;
import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

/**
 * A CasaInteligente faz a gestão dos SmartDevices que existem e dos espaços (as salas) que existem na casa.
 *
 * @author josefonte
 * @version 0.1
 */
public class CasaInteligente implements Serializable, Comparable {

    private String id;
    private String owner;
    private int nif;

    private Fornecedor fornecedor;
    private Map<String, SmartDevice> devices; // identificador -> SmartDevice
    private Map<String, List<String>> locations; // Espaço -> Lista codigo dos devices

    /**
     * Constructor for objects of class CasaInteligente
     */
    public CasaInteligente() {
        // initialise instance variables
        this.id = "";
        this.owner = "";
        this.nif = 0;
        this.fornecedor = null;
        this.devices = new HashMap<>();
        this.locations = new HashMap<>();
    }

    public CasaInteligente(String id, String owner, int nif, Fornecedor fornecedor, Map<String,SmartDevice> ndevices, Map<String, List<String>> nlocations ) throws CasaInteligenteException {
        if(owner.equals("") || nif<0) throw new CasaInteligenteException("Informação do Dono Errada");

        this.id = id;
        this.owner = owner;
        this.nif = nif;
        this.fornecedor = fornecedor.clone();
        this.devices = new HashMap<>();
        for (Map.Entry<String,SmartDevice> e  : ndevices.entrySet()){
            this.devices.put(e.getKey(),e.getValue().clone());
        }

        this.locations = new HashMap<>();
        for (Map.Entry<String,List<String>> e  : nlocations.entrySet()){
            List<String> ids = new ArrayList<>(e.getValue());
            this.locations.put(e.getKey(),ids);
        }
    }

    public CasaInteligente(String id, String owner,int nif, Fornecedor fornecedor) throws CasaInteligenteException {
        if(owner.equals("") || nif<0) throw new CasaInteligenteException("Informação do Dono Errada");

        this.id = id;
        this.owner = owner;
        this.nif = nif;
        this.fornecedor = fornecedor.clone();
        this.devices = new HashMap<>();
        this.locations = new HashMap<>();
    }

    public CasaInteligente(CasaInteligente casa) throws CasaInteligenteException {
        if(casa.getOwner().equals("") || casa.getNif()<0) throw new CasaInteligenteException("Informação do Dono Errada");

        this.id = casa.getID();
        this.owner = casa.getOwner();
        this.nif = casa.getNif();
        this.fornecedor = casa.getFornecedor();
        this.devices = casa.getDevices();
        this.locations = casa.getLocations();

    }

    public String getID() {return this.id;}

    public String getOwner() {return this.owner;}

    public int getNif() {return this.nif;}

    public Fornecedor getFornecedor() {return this.fornecedor.clone();}

    public Map<String, SmartDevice> getDevices() {
        Map<String, SmartDevice> devs = new HashMap<>();
        for (Map.Entry<String,SmartDevice> e  : this.devices.entrySet()){
            devs.put(e.getKey(),e.getValue().clone());
        }
        return devs;
    }

    public Map<String, List<String>> getLocations() {
        Map<String, List<String>> locs = new HashMap<>();
        for (Map.Entry<String,List<String>> e  : this.locations.entrySet()){
            List<String> ids = new ArrayList<>(e.getValue());
            locs.put(e.getKey(),ids);
        }
        return locs;
    }

    public void setID(String id) {
        this.id = id;
    }
    
    public void setOwner(String owner) throws CasaInteligenteException  {
        if (owner.equals("")) throw new CasaInteligenteException("Nome inválido");
        this.owner = owner;
    }

    public void setNif(int nif) throws CasaInteligenteException {
        if (nif<0) throw new CasaInteligenteException("Nif Negativo");
        this.nif = nif;
    }

    public void setFornecedor(Fornecedor fornecedor)  {
        this.fornecedor = fornecedor.clone();
    }

    public void setDevices(Map<String, SmartDevice> devs){
        this.devices = new HashMap<>();
        for(SmartDevice smt : devs.values()){
            this.devices.put(smt.getID(),smt.clone());
        }
    }

    public void setLocations(Map<String, List<String>> locs){
        this.locations = new HashMap<>();
        for (Map.Entry<String,List<String>> e  : locs.entrySet()){
            List<String> ids = new ArrayList<>(e.getValue());
            this.locations.put(e.getKey(),ids);
        }
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CasaInteligente)) return false;
        CasaInteligente casa = (CasaInteligente) o;
        return ( this.owner.equals(casa.getOwner()) &&
                this.id == casa.getID() &&
                this.nif == casa.getNif() &&
                this.fornecedor.equals(casa.getFornecedor()) &&
                this.devices.equals(casa.getDevices()) &&
                this.locations.equals(casa.getLocations()));
    }

    public String toString(){
        StringBuilder sb = new StringBuilder( "\n### House ###");
        sb.append("\nID: " + this.id +
                " | Owner: " + this.owner +
                " | NIF: " + this.nif +
                " | Fornecedor: " + this.fornecedor); 
        sb.append ("\nDispositivos: "); 
        
        for(SmartDevice smtd : this.devices.values()) {
            sb.append(smtd);
        }
        sb.append("\n\nDivisões: ");
        
        for(Map.Entry<String,List<String>> r : this.locations.entrySet()) {
            sb.append("\n" + r.getKey() + ": ");
            for(String id : r.getValue()) {
                sb.append(id + ", ");
            }
        }
        sb.append("\n");
        return sb.toString();

    }


    public CasaInteligente clone(){
        try {
            return new CasaInteligente(this);
        } catch (CasaInteligenteException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardaCasa(String nomeFicheiro) throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static CasaInteligente carregaCasa(String nomeFicheiro) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fos = new FileInputStream(nomeFicheiro);
        ObjectInputStream oos = new ObjectInputStream(fos);
        CasaInteligente c = (CasaInteligente) oos.readObject();
        oos.close();
        fos.close();
        return c;
    }


    public int compareTo(Object o) {
        CasaInteligente casa = (CasaInteligente) o;
        if (casa.custoCasa()>this.custoCasa()) return 1;
        else if (casa.custoCasa()<this.custoCasa()) return -1;
        else {
            return casa.getID().compareTo( this.getID());
        }
    }



    //---------------------------------------------------------------------------------


    public void addDevice(SmartDevice s, String room) throws CasaInteligenteException {
        if(existsDevice(s.getID())) throw new CasaInteligenteException("Device ID já existente");

        this.devices.put(s.getID(),s.clone());

        if(this.locations.containsKey(room)) this.locations.get(room).add(s.getID());
        else {
            addRoom(room);
            this.locations.get(room).add(s.getID());
        }
    }

    public void removeDevice(SmartDevice s) throws CasaInteligenteException {
        if(!existsDevice(s.getID())) throw new CasaInteligenteException("Device ID não existe");
        this.devices.remove(s.getID());
        for(List<String> devs_ids : this.locations.values()){
            devs_ids.remove(s.getID());
        }
    }

    public void turnDeviceOn(String id) throws CasaInteligenteException {
        if(!existsDevice(id)) throw new CasaInteligenteException("Device ID não existe");
        this.devices.get(id).turnOn();
    }

    public void turnDeviceOFF(String id) throws CasaInteligenteException {
        if(!existsDevice(id)) throw new CasaInteligenteException("Device ID não existe");
        this.devices.get(id).turnOff();}

    public boolean existsDevice(String id) {return this.devices.containsKey(id);}

    public SmartDevice getDevice(String id) throws CasaInteligenteException{
        if(this.devices.get(id) == null) throw new CasaInteligenteException("Não Existe");
        return this.devices.get(id).clone();
    }



    public void setRoomOn(String room, boolean b) throws CasaInteligenteException {
        if(this.locations.get(room) == null) throw new CasaInteligenteException("Divisão" + room + "não existe");
        for (String id : this.locations.get(room)){
            this.devices.get(id).setOn(b);
        }
    }

    public void turnRoomOn(String room) throws CasaInteligenteException {
        if(this.locations.get(room) == null) throw new CasaInteligenteException("Divisão" + room + "não existe");
        setRoomOn(room,true);
    }

    public void turnRoomOff(String room) throws CasaInteligenteException {
        if(this.locations.get(room) == null) throw new CasaInteligenteException("Divisão" + room + "não existe");
        setRoomOn(room,false);
    }

    public void setAllOn(boolean b) throws CasaInteligenteException {
        this.devices.values().stream().forEach(a->a.setOn(b));
    }

    public void turnAllOn() throws CasaInteligenteException {
        setAllOn(true);
    }

    public void turnAllOff() throws CasaInteligenteException {
        setAllOn(false);
    }


    public void addRoom(String room) throws CasaInteligenteException {
        if(this.locations.containsKey(room)) throw new CasaInteligenteException("Divisão " + room + " já existe");
        if(room.equals("")) throw new CasaInteligenteException("Divisão inválida");
        
        List<String> ids = new ArrayList<>();
        this.locations.put(room,ids);
    }

    public void addRoom(String room, List<String> ids) throws CasaInteligenteException {
        if(this.locations.containsKey(room)) throw new CasaInteligenteException("Divisão " + room + " não existe");
        if(ids==null) throw new CasaInteligenteException("Lista de Devices null");

        List<String> ids_cp = new ArrayList<>(ids);
        this.locations.put(room,ids_cp);
    }

    public boolean hasRoom(String room) {
        return this.locations.containsKey(room);
    }

    public boolean roomHasDevice(String room, String id) {return this.locations.get(room).contains(id);}


    public double custoRoom(String room) throws CasaInteligenteException{
        if(this.locations.get(room)==null) throw new CasaInteligenteException("Divisão "+room+"não existe");

        return this.locations.get(room).stream()
                .mapToDouble(id-> {
                    try {
                        return this.fornecedor.formulaPreco(this.devices.get(id), this);
                    } catch (SmartDeviceException | CasaInteligenteException | FornecedorException e) {
                        throw new RuntimeException("Impossível Computar o Consumo"+e);
                    }
                })
                .sum();

    }

    public double custoCasa(){
        return this.devices.values().stream()
                .filter(SmartDevice::getOn)
                .mapToDouble(a -> {
                try {
                    return this.fornecedor.formulaPreco(a,this);
                } catch (SmartDeviceException | CasaInteligenteException | FornecedorException e) {
                    throw new RuntimeException("Impossível Computar o Consumo da Casa"+e);
                }
                }).sum() +
                this.devices.values().stream()
                        .mapToDouble(SmartDevice::getCusto)
                        .sum();
    }

    public double consumoCasa(){
        return this.devices.values().stream()
                .filter(SmartDevice::getOn)
                .mapToDouble(SmartDevice::getConsumo)
                .sum();
    }

    public Fatura faturaCasa(LocalDate inicio, LocalDate fim) throws FaturaException, ParseException {
        return new Fatura(this,inicio, fim);
    }


    public Set<String> listaDevices(){
        Set<String> lista = new HashSet<>();
        this.getDevices().values().stream().forEach(a-> lista.add(a.getID()));
        return lista;
    }


    public int numberDevices(){
       return this.devices.keySet().size();
    }


    public List<SmartDevice> getDevicesinRoom(String room){
        List<SmartDevice> devs = new ArrayList<>();

        for (String id : this.locations.get(room)){
            devs.add( this.devices.get(id).clone());
        }
        return devs;
    }

    public List<Boolean> getRoomStates(String room) throws CasaInteligenteException {

        if(room == null) throw new CasaInteligenteException("Divisão não existe");

        List<Boolean> lista = new ArrayList<>();
        for (String id : this.locations.get(room)) {
            lista.add(this.devices.get(id).getOn());
        }
        return lista;
    }

}