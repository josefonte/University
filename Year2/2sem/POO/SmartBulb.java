import ErrorHandling.SmartBulbException;
import ErrorHandling.SmartDeviceException;

public class SmartBulb extends SmartDevice {
    public static final int WARM = 2;
    public static final int NEUTRAL = 1;
    public static final int COLD = 0;
    
    private int tone;
    private float dimensao;
    private float valor_fixo;


    public SmartBulb() {
        super();
        this.tone = NEUTRAL;
        this.dimensao = 0;
        this.valor_fixo = 0;
    }

    public SmartBulb(String id) {
        super(id);
        this.tone = NEUTRAL;
        this.dimensao = 0;
        this.valor_fixo = 0;
    }

    public SmartBulb(String id, boolean state, float custo, int tone, float dimensao, float valor_fixo) throws SmartDeviceException,SmartBulbException{
        super(id,state,custo);

        if(dimensao<0 || valor_fixo<0) throw new SmartBulbException( id + " Valores Negativos");

        if (tone>WARM) this.tone = WARM;
        else this.tone = Math.max(tone, COLD);

        this.dimensao = dimensao;
        this.valor_fixo= valor_fixo;

        super.setConsumo(this.valor_fixo+((float)(this.tone+0.5)*0.005f));
    }

    public SmartBulb(SmartBulb smt) throws SmartDeviceException,SmartBulbException{

        super(smt.getID(), smt.getOn(),smt.getCusto());

        if(smt.getValorFixo()<0 || smt.getDimensao()<0) throw new SmartBulbException(smt.getID() + " Valores Negativos");

        int t=smt.getTone();
        if (t>WARM) this.tone = WARM;
        else this.tone = Math.max(t, COLD);

        this.dimensao = smt.getDimensao();
        this.valor_fixo= smt.getValorFixo();

        super.setConsumo(this.valor_fixo+((float)(this.tone+0.5)*0.005f));
    }

    public int getTone() {
        return this.tone;
    }

    public float getDimensao(){
        return this.dimensao;
    }

    public float getValorFixo(){ return this.valor_fixo;}

    public void setTone(int t) {
        if (t>WARM) this.tone = WARM;
        else this.tone = Math.max(t, COLD);
    }

    public void setDimensao(float dimensao) throws SmartBulbException{
        if (dimensao<0 ) throw new SmartBulbException("Atribuição de Dimensão Negativa");
        this.dimensao= dimensao;
    }

    public void setValorFixo(int valor_fixo) throws SmartBulbException{
        if (valor_fixo<0 ) throw new SmartBulbException("Atribuição de Valor Fixo Negativo");
        this.valor_fixo=valor_fixo;
    }

    public SmartBulb clone(){
        try {
            return new SmartBulb(this);
        } catch (SmartDeviceException | SmartBulbException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o){
        if (!super.equals(o)) return false;
        if (this==o) return true;
        if (!(o instanceof SmartBulb)) return false;
        SmartBulb smt = (SmartBulb) o;
        return (this.tone==smt.getTone() &&
                this.dimensao==smt.getDimensao() &&
                this.valor_fixo == smt.getValorFixo());
    }

    public String toString() {
        return super.toString() +
                "\nType: SmartBulb" +
                " | Tone: " + this.tone +
                " | Dimensão: " + this.dimensao +
                " | Valor Fixo: " + this.valor_fixo;
    }

}

