package business;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Participante{
    private int id;
    private int pontuacao;
    private List<LocalTime> tempos;
    private int afinacoesRestantes;
    private int voltasTotais;
    private int localizacaoPista;
    private Carro carro;
    private Utilizador utilizador;
    private Piloto piloto;

   public Participante(){
        this.pontuacao = 0;
        this.tempos = new ArrayList<LocalTime>();
        this.afinacoesRestantes = 3;
        this.voltasTotais = 0;
        this.localizacaoPista = 0;
        this.carro = new C1();
        this.utilizador = new Utilizador();
        this.piloto = new Piloto();
    }

    public Participante(int id ,int pontuacao, List<LocalTime> tempos, int afinacoesRestantes, int voltasTotais, int localizacaoPista, Carro carro, Utilizador utilizador,Piloto piloto){
       this.id = id;
       this.pontuacao = pontuacao;
        this.tempos = tempos;
        this.afinacoesRestantes = afinacoesRestantes;
        this.voltasTotais = voltasTotais;
        this.localizacaoPista = localizacaoPista;
        this.carro = carro.clone();
        this.utilizador = utilizador.clone();
        this.piloto = piloto.clone();
    }
    
    public Participante(Participante u){
        this.setId(u.getId());
        this.setPontuacao(u.getPontuacao());
        this.setTempos(u.getTempos());
        this.setAfinacoesRestantes(u.getAfinacoesRestantes());
        this.setVoltasTotais(u.getVoltasTotais());
        this.setLocalizacaoPista(u.getLocalizacaoPista());
        this.setCarro(u.getCarro());
        this.setUtilizador(u.getUtilizador());
        this.setPiloto(u.getPiloto());
    }

        public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Piloto getPiloto() {
        return piloto.clone();
    }

    public void setPiloto(Piloto piloto) {
        this.piloto = piloto.clone();
    }

    public int getPontuacao() {
        return this.pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public List<LocalTime> getTempos() {
        return new ArrayList<LocalTime>(tempos);
    }

    public void setTempos(List<LocalTime> tempos) {
        this.tempos = new ArrayList<LocalTime>();
        for (LocalTime t : tempos){
            this.tempos.add(t);
        }
    }

    public int getAfinacoesRestantes() {
        return afinacoesRestantes;
    }

    public void setAfinacoesRestantes(int afinacoesRestantes) {
        this.afinacoesRestantes = afinacoesRestantes;
    }

    public int getVoltasTotais() {
        return voltasTotais;
    }

    public Carro getCarro() {
        return this.carro.clone();
    }

    public void setCarro(Carro carro) {
        this.carro = carro.clone();
    }

    public void setVoltasTotais(int voltasTotais) {
        this.voltasTotais = voltasTotais;
    }

    public int getLocalizacaoPista() {
        return localizacaoPista;
    }

    public void setLocalizacaoPista(int localizacaoPista) {
        this.localizacaoPista = localizacaoPista;
    }

    public Utilizador getUtilizador(){
        return this.utilizador.clone();
    }

    public void setUtilizador(Utilizador u){
        this.utilizador = u.clone();
    }

    public void addTempo(LocalTime tempo){
        List<LocalTime> res = this.getTempos();
        res.add(tempo);
        this.setTempos(res);
    }

    public void addPontuacao(int pont){
        int total = this.getPontuacao() + pont;
        this.setPontuacao(total);
    }

    public LocalTime tempoTotal(){
        LocalTime r = LocalTime.of(0,0,0);
        List<LocalTime> list = this.getTempos();
        for(LocalTime lt: list){
            r.plusHours(lt.getHour()).plusMinutes(lt.getMinute()).plusSeconds(lt.getSecond());
        }
        return r;
    }
    public float calculaFiabilidadeFinal(){
        float f = carro.getFiabilidade();
        return  f;
    }
    public String getNome(){
        return this.getUtilizador().getNomeUtilizador();
    }

    public Participante clone(){
        return new Participante(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Participante:  ");
        sb.append("id=" + id );
        sb.append(" | pontuacao=" + pontuacao);
        sb.append(" | tempos=" + tempos);
        sb.append(" | afinacoesRestantes=" + afinacoesRestantes);
        sb.append(" | voltasTotais=" + voltasTotais);
        sb.append(" | localizacaoPista=" + localizacaoPista );
        sb.append(" | carro=" + carro);
        sb.append(" | utilizador=" + utilizador );
        sb.append(" | piloto=" + piloto);
        return sb.toString();
    }
}
