import ErrorHandling.ResolutionException;
import ErrorHandling.SmartCameraException;
import ErrorHandling.SmartDeviceException;
import ErrorHandling.SmartSpeakerException;

public class SmartSpeaker extends SmartDevice {
    public static final int MAX = 100;
    private int volume;
    private String channel;
    private String brand;
    private float brand_comsuption;

    public SmartSpeaker() {
        super();
        this.volume = MAX/2;
        this.channel = "";
        this.brand = "";
        this.brand_comsuption = 0;
    }

    public SmartSpeaker(String id) {
        super(id);
        this.volume = MAX/2;
        this.channel = "";
        this.brand = "";
        this.brand_comsuption = 0;
    }

    public SmartSpeaker(String id, boolean state, float cost, int vol, String channel, String brand,float bd_comp) throws SmartDeviceException, SmartSpeakerException {
        super(id,state,cost);

        if(channel.equals("") || brand.equals("") || bd_comp<0) throw  new  SmartSpeakerException("Valores Negativos");

        this.volume = Math.min(vol, MAX);
        this.channel = channel;
        this.brand = brand;
        this.brand_comsuption = bd_comp;
        super.setConsumo(this.brand_comsuption * (float) this.volume);
    }

    public SmartSpeaker(SmartSpeaker smt) throws SmartDeviceException,SmartSpeakerException {
        super(smt.getID(), smt.getOn(),smt.getCusto());
        if(smt.getBrand().equals("") || smt.getChannel().equals("") || smt.getBrand_comsuption()<0) throw  new  SmartSpeakerException("Valores Negativos");

        super.setConsumo(smt.getConsumo());
        this.volume = Math.min(smt.getVolume(), MAX);
        this.channel = smt.getChannel();
        this.brand = smt.getBrand();
        this.brand_comsuption = smt.getBrand_comsuption();
    }


    public int getVolume() {return this.volume;}
    
    public String getChannel() {return this.channel;}

    public String getBrand(){
        return this.brand;
    }

    public float getBrand_comsuption() {return this.brand_comsuption;}

    public void setVolume(int volume)  {

        this.volume = Math.min(volume,MAX);
    }

    public void setChannel(String c) {this.channel=c;}

    public void setBrand(String c) {this.brand=c;}

    public void setBrand_comsuption(float brand_comsuption) throws SmartSpeakerException {
        if(brand_comsuption<0) throw new SmartSpeakerException("Atribuição de Consumo da Marca Negativo");
        this.brand_comsuption = brand_comsuption;
    }

    public SmartSpeaker clone(){
        try {
            return new SmartSpeaker(this);
        } catch (SmartDeviceException | SmartSpeakerException e) {
            throw new RuntimeException("Clone Failed"+e);
        }
    }
    @Override
    public boolean equals(Object o){
        if (!super.equals(o)) return false;
        if (this==o) return true;
        if (!(o instanceof SmartSpeaker)) return false;
        SmartSpeaker smt = (SmartSpeaker) o;
        return (this.volume==smt.getVolume() &&
                this.getChannel().equals(smt.getChannel()) &&
                this.getBrand().equals(smt.getBrand()))&&
                this.brand_comsuption == smt.getBrand_comsuption();
    }

    public String toString() {
        return super.toString() +
                "\nType: SmartSpeaker" +
                " | Volume: " + this.volume +
                " | Channel: " + this.channel +
                " | Brand: " + this.brand;
    }


    public void volumeUp() {
        if (this.volume<MAX) this.volume++;
    }

    public void volumeDown() {
        if (this.volume>0) this.volume--;
    }

}

