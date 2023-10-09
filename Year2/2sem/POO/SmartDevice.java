import ErrorHandling.SmartDeviceException;

import java.io.*;

public class SmartDevice implements Serializable {

    private String id;
    private boolean on;
    private float custo_inst;
    private float consumo_diario;


    public SmartDevice() {
        this.id = "";
        this.on = false;
        this.custo_inst=0;
        this.consumo_diario = 0;
    }

    public SmartDevice(String id) {
        this.id = id;
        this.on = false;
        this.custo_inst = 0;
        this.consumo_diario = 0;
    }

    public SmartDevice(String id, boolean state, float custo) throws SmartDeviceException{
        if(id.equals("") || custo<0) throw new SmartDeviceException(id + "Valores Negativos");
        this.id = id;
        this.on = state;
        this.custo_inst = custo;
        this.consumo_diario = 0;
    }

    public SmartDevice(SmartDevice smt) throws SmartDeviceException{
        if(smt.getID().equals("") || smt.getCusto()<0 || smt.getConsumo()<0) throw new SmartDeviceException( smt.getID()+ " Valores Negativos");
        this.id = smt.getID();
        this.on = smt.getOn();
        this.custo_inst = smt.getCusto();
        this.consumo_diario= smt.getConsumo();
    }

    public String getID() {
        return this.id;
    }

    public boolean getOn() {
        return this.on;
    }

    public float getCusto(){
        return this.custo_inst;
    }

    public float getConsumo(){
        return this.consumo_diario;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOn(boolean b) {
        this.on=b;
    }

    public void setCusto_inst(float custo_inst) throws SmartDeviceException {
        if (custo_inst<0) throw new SmartDeviceException(this.id +" Atribuição de Custo De Instalacação Negativo");
        this.custo_inst = custo_inst;
    }

    public void setConsumo(float c)  throws SmartDeviceException{
        if (c<0) throw new SmartDeviceException(this.id +" Atribuição de Consumo Negativo");
        this.consumo_diario = c;
    }

    public SmartDevice clone() {
        try {
            return new SmartDevice(this);
        } catch (SmartDeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o){
        if (this==o) return true;
        if (o==null || this.getClass()!=o.getClass()) return false;
        SmartDevice smt = (SmartDevice) o;
        return (this.id.equals(smt.getID()) &&
                (this.on == smt.getOn()) &&
                this.consumo_diario== smt.getConsumo() &&
                this.custo_inst == smt.getCusto());
    }

    public String toString() {
        return  "\n\n### SmartDevice ###" +
                "\nID: " + this.id +
                " | Ligado: " + this.on +
                " | Custo de Instalação: " + this.custo_inst +
                " | Consumo Diário: " + this.consumo_diario;
    }

    public void guardaSmartDevice(String nomeFicheiro) throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static SmartDevice carregaSmartDevice(String nomeFicheiro) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fos = new FileInputStream(nomeFicheiro);
        ObjectInputStream oos = new ObjectInputStream(fos);
        SmartDevice c = (SmartDevice) oos.readObject();
        oos.close();
        fos.close();
        return c;
    }
    public void turnOn(){
        this.on = true;
    }

    public void turnOff() {
        this.on = false;
    }

}
