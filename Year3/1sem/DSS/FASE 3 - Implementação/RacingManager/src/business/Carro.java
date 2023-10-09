package business;
public abstract class Carro {
    private String marca;
    private String modelo;
    private int celindrada;
    private int potencia;
    private float fiabilidade;
    private int pac;
    private String id;
    private TipoPneus pneus;
    private ModoMotor modoMotor;
    private String categoria;

    public Carro() {
        this.categoria = "";
        this.marca = "";
        this.modelo = "";
        this.celindrada = 0;
        this.potencia = 0;
        this.fiabilidade = 0;
        this.pac = 0;
        this.id = "0";
    }

    public Carro(String categoria,String marca, String modelo, int celindrada, int potencia, float fiabilidade, int pac, String id) {
        this.categoria = categoria;
        this.marca = marca;
        this.modelo = modelo;
        this.celindrada = celindrada;
        this.potencia = potencia;
        this.fiabilidade = fiabilidade;
        this.pac = pac;
        this.id = id;
    }
    
    public Carro(String categoria,String marca, String modelo, int celindrada, int potencia, float fiabilidade, int pac, String id,TipoPneus pneus,ModoMotor modo) {
        this.categoria = categoria;
        this.marca = marca;
        this.modelo = modelo;
        this.celindrada = celindrada;
        this.potencia = potencia;
        this.fiabilidade = fiabilidade;
        this.pac = pac;
        this.id = id;
        this.modoMotor = modo;
        this.pneus = pneus;
    }

    public Carro(Carro c) {
        this.categoria = c.getCategoria();
        this.marca = c.getMarca();
        this.modelo = c.getModelo();
        this.celindrada = c.getCelindrada();
        this.potencia = c.getPotencia();
        this.fiabilidade = c.getFiabilidade();
        this.pac = getPac();
        this.id = getId();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public abstract Carro clone();


    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCelindrada() {
        return celindrada;
    }

    public void setCelindrada(int celindrada) {
        this.celindrada = celindrada;
    }

    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public float getFiabilidade() {
        return fiabilidade;
    }

    public void setFiabilidade(float fiabilidade) {
        this.fiabilidade = fiabilidade;
    }

    public int getPac() {
        return pac;
    }

    public void setPac(int pac) {
        this.pac = pac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPneus(TipoPneus pneus){
        this.pneus = pneus;
    }
    public TipoPneus getPneus(){
        return this.pneus;
    }

    public ModoMotor getMotor(){
        return this.modoMotor;
    }
    public void setMotor(ModoMotor modo){
        this.modoMotor = modo;
    }
    public abstract void calculaFiabilidade();
    public abstract void calculaPotencia();
    public void alteraafinacao(int pac, ModoMotor motor){
        setPac(pac);
        setMotor(motor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro:  ");
        sb.append("marca = "+ marca);
        sb.append(" | modelo= " + modelo);
        sb.append(" | cilindrada= " + celindrada);
        sb.append(" | potencia= " + potencia );
        sb.append(" | fiabilidade= " + fiabilidade);
        sb.append(" | pac= " + pac );
        sb.append(" | id= " + id );
        return sb.toString();
    }
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro:  ");
        sb.append("id=" + id );
        sb.append(" | marca= "+ marca);
        return sb.toString();
    }
}
