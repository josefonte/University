package business;

public class Piloto {
    private String nome;
    private int sva;
    private int cts;

    public Piloto(){
        this.nome = "";
        this.sva = 0;
        this.cts = 0;
    }
    public Piloto(String nome, int sva, int cts) {
        this.nome = nome;
        this.sva = sva;
        this.cts = cts;
    }

    public Piloto(Piloto p) {
        this.nome = p.getNome();
        this.sva = p.getSva();
        this.cts = p.getCts();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getSva() {
        return sva;
    }

    public void setSva(int sva) {
        this.sva = sva;
    }

    public int getCts() {
        return cts;
    }

    public void setCts(int cts) {
        this.cts = cts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Piloto:  ");
        sb.append("nome=" + nome );
        sb.append(" | sva=" + sva );
        sb.append(" | cts=" + cts);
        return sb.toString();
    }
    
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Piloto:  ");
        sb.append(" nome='" + nome );
        return sb.toString();
    }

    public Piloto clone(){
        return new Piloto(this);
    }
}
