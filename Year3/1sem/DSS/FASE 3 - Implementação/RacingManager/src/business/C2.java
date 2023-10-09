package business;

public class C2 extends Carro{

    public C2() {
        super("C2","", "", 3000, 0, 0, 0, "0");
        this.potenciaHibrida = 0;
    }
    private int potenciaHibrida;
    public C2(String marca, String modelo, int celindrada, int potencia, float fiabilidade, int pac, String id, int potenciaHibrida) {
        super("C2",marca, modelo, celindrada, potencia, fiabilidade, pac, id);
        this.potenciaHibrida = potenciaHibrida;
    }

    public C2(C2 c) {
        super("C2",c.getMarca(), c.getModelo(), c.getCelindrada(), c.getPotencia(), c.getFiabilidade(), c.getPac(), c.getId());
        this.potenciaHibrida = c.getPotenciaHibrida();
    }

    public int getPotenciaHibrida() {
        return potenciaHibrida;
    }

    public void setPotenciaHibrida(int potenciaHibrida) {
        this.potenciaHibrida = potenciaHibrida;
    }

    @Override
    public void calculaFiabilidade() {
        setFiabilidade((float) (80+0.001*getCelindrada()));
    }

    @Override
    public void calculaPotencia() {
        setPotencia(getPotenciaHibrida()+getPotencia());
    }
    public C2 clone(){
        return new C2(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro C2:  ");
        sb.append("marca = "+ super.getMarca());
        sb.append(" | modelo = " + super.getModelo());
        sb.append(" | cilindrada=" + super.getCelindrada());
        sb.append(" | potencia=" + super.getPotencia());
        sb.append(" | fiabilidade=" + super.getFiabilidade());
        sb.append(" | pac=" + super.getPac());
        sb.append(" | id=" + super.getId());
        sb.append(" | potencia hibrida= "+ getPotenciaHibrida());
        return sb.toString();
    }
}
