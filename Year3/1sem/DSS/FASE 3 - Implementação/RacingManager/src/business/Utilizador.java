package business;

import java.util.Objects;

public class Utilizador {
    public String nomeUtilizador;
    public int pontosRanking;
    private TipoUtilizador tipoutiliador;

    public Utilizador (){
        this.nomeUtilizador = "";
        this.pontosRanking = 0;
        this.tipoutiliador = TipoUtilizador.JOGADOR;
    }
    
    public Utilizador(String nomeUtilizador, TipoUtilizador tipoutiliador) {
        this.setNomeUtilizador(nomeUtilizador);
        this.setPontosRanking(0);
        this.setTipoutiliador(tipoutiliador);
    }
    
    public Utilizador(String nomeUtilizador, TipoUtilizador tipoutiliador, int pontosRanking) {
        this.setNomeUtilizador(nomeUtilizador);
        this.setTipoutiliador(tipoutiliador);
        this.setPontosRanking(pontosRanking);
    }

    public Utilizador(Utilizador u){
        this.setNomeUtilizador(u.getNomeUtilizador());
        this.setPontosRanking(u.getPontosRanking());
        this.setTipoutiliador(u.getTipoutiliador());
    }

    public String getNomeUtilizador() {
        return this.nomeUtilizador;
    }

    public void setNomeUtilizador(String nomeUtilizador) {
        this.nomeUtilizador = nomeUtilizador;
    }

    public int getPontosRanking() {
        return this.pontosRanking;
    }

    public void setPontosRanking(int pontosRanking) {
        this.pontosRanking = pontosRanking;
    }

    public TipoUtilizador getTipoutiliador() {
        return this.tipoutiliador;
    }

    public void setTipoutiliador(TipoUtilizador tipoutiliador) {
        this.tipoutiliador = tipoutiliador;
    }

    public void addPontucao(int pontos){
        int total = this.getPontosRanking() + pontos;
        this.setPontosRanking(total);
    }

    public boolean getAdminMode(){
        return this.tipoutiliador == TipoUtilizador.ADMIN;
    }

    public void setAdminMode(TipoUtilizador tipoutilizador){

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Utilizador:  ");
        sb.append("nomeUtilizador= " + nomeUtilizador);
        sb.append(" | pontosRanking= " + pontosRanking);
        sb.append(" | tipoutiliador=" + tipoutiliador);
        return sb.toString();
    }

    public Utilizador clone(){
        return new Utilizador(this);
    }
}
