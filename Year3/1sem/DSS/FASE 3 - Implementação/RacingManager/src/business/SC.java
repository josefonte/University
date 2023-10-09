package business;

public class SC extends Carro{

    public SC() {
        super("SC","", "", 2500, 0, 0, 0, "0");
    }

    public SC(String marca, String modelo, int celindrada, int potencia, float fiabilidade, int pac, String id) {
        super("SC",marca, modelo, celindrada, potencia, fiabilidade, pac, id);
    }

    public SC(SC c) {
        super("SC",c.getMarca(), c.getModelo(), c.getCelindrada(), c.getPotencia(), c.getFiabilidade(), c.getPac(), c.getId());
    }

    @Override
    public void calculaFiabilidade() {
        setFiabilidade((float) (75+0.006*getCelindrada()));

    }

    @Override
    public void calculaPotencia(){}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro SC:  ");
        sb.append(" marca = "+ super.getMarca());
        sb.append(" | modelo = " + super.getModelo());
        sb.append(" | cilindrada=" + super.getCelindrada());
        sb.append(" | potencia=" + super.getPotencia());
        sb.append(" | fiabilidade=" + super.getFiabilidade());
        sb.append(" | pac=" + super.getPac());
        sb.append(" | id=" + super.getId());
        return sb.toString();
    }
    public SC clone(){
        return new SC(this);
    }
}
