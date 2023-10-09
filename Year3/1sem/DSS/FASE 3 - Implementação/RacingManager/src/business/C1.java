package business;
public class C1 extends Carro {
    private int potenciaHibrida;

    public C1() {
        super("C1", "", "", 6000, 0, 0, 0, "");

        this.potenciaHibrida = 0;
    }

    public C1(String marca, String modelo, int celindrada, int potencia, float fiabilidade, int pac, String id, int potenciaHibrida) {
        super("C1",marca, modelo, celindrada, potencia, fiabilidade, pac, id);
        this.potenciaHibrida = potenciaHibrida;
    }
    public C1(C1 c) {
        super("C1",c.getMarca(), c.getModelo(), c.getCelindrada(), c.getPotencia(), c.getFiabilidade(), c.getPac(), c.getId());
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
        setFiabilidade(95);

    }
    @Override
    public void calculaPotencia() {
        setPotencia(getPotenciaHibrida()+getPotencia());
    }
    
    public C1 clone(){
        return new C1(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro C1:  ");
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
