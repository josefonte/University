package business;

import java.util.*;

public class Campeonato {
    private String nomeCampeonato;
    private int corridaAtual;
    private List<Corrida> corridas;
    private Map<String,Participante> campParticipantes;

    private TipoCampeonato tipoCampeonato;

    public Campeonato(){
        this.nomeCampeonato = "";
        this.corridaAtual = 0;
        this.corridas = new ArrayList<>();
        this.campParticipantes = new HashMap<String, Participante>();
        this.tipoCampeonato = TipoCampeonato.C1;
    }
    
    public Campeonato(String nome,int corAtual,List<Corrida> cor, Map<String, Participante> participantes,TipoCampeonato tipoCamp) {
        this.nomeCampeonato = nome;
        this.corridaAtual = corAtual;
        this.setCorridas(cor);
        this.setCampParticipantes(participantes);
        this.tipoCampeonato = tipoCamp;
    }
    
    public Campeonato(Campeonato c){
        this.nomeCampeonato = c.getNomeCampeonato();
        this.corridaAtual = c.getCorridaAtual();
        this.corridas = c.getCorridas();
        this.campParticipantes = c.getCampParticipantes();
        this.tipoCampeonato = c.getTipoCampeonato();
    }

    public TipoCampeonato getTipoCampeonato() {
        return tipoCampeonato;
    }

    public void setTipoCampeonato(TipoCampeonato tipoCampeonato) {
        this.tipoCampeonato = tipoCampeonato;
    }

    public String getNomeCampeonato() {
        return nomeCampeonato;
    }

    public void setNomeCampeonato(String nomeCampeonato) {
        this.nomeCampeonato = nomeCampeonato;
    }

    public int getCorridaAtual() {
        return corridaAtual;
    }

    public void setCorridaAtual(int corridaAtual) {
        this.corridaAtual = corridaAtual;
    }

    public List<Corrida> getCorridas() {
        List<Corrida> r = new ArrayList<>();

        for (Corrida c : this.corridas){
            r.add(c.clone());
        }
        return r;
    }

    public void setCorridas(List<Corrida> corridas) {
        this.corridas = new ArrayList<>();
        for (Corrida c : corridas){
            this.corridas.add(c.clone());
        }
    }

    public void addCorrida(Corrida corrida){
        this.corridas.add(corrida.clone());
    }
    public Map<String, Participante> getCampParticipantes() {
        Map<String, Participante> res = new HashMap<String,Participante>();
        for(Participante p : this.campParticipantes.values()){
            res.put(p.getNome(),p.clone());
        }
        return res;
    }

    public void setCampParticipantes(Map<String, Participante> campParticipantes) {
        this.campParticipantes = new HashMap<String,Participante>();
        for(Participante p : campParticipantes.values()){
            this.campParticipantes.put(p.getNome(),p.clone());
        }
    }

    public void addParticipante(Participante participante){
        this.campParticipantes.put(participante.getNome(),participante.clone());
    }
    
    public void addParticipantes2Corridas(Map<String,Participante> participantes) {
        List<Corrida> corridasP = new ArrayList<>();
        for(Corrida cor : this.corridas){
            cor.setParticipantes(participantes);
            corridasP.add(cor.clone());
        }
        this.corridas = new ArrayList<>(corridasP);
    }

    private void addPontuacaoCampeonato(Corrida corrida){
        List<Participante> pList = corrida.listaClacificacao();
        for(int i = 0 ; i<10 ; i++) {
            addParticipante(pList.get(i));
        }

    }

    public void simularCorrida(){
        if (corridaAtual >= corridas.size()) return;
        Corrida c = this.corridas.get(corridaAtual);
        c.simularCorrida();
        addPontuacaoCampeonato(c);
        corridaAtual++;
    }

    public List<Participante> classificacaoFinal(){
        List<Participante> r = new ArrayList<>();
        for (Participante p : this.campParticipantes.values()){
            r.add(p.clone());
        }
        Collections.sort(r, new Sortbypoints());
        return r;
    }

    public int calculaAfinacoes(){
        int s = this.getCorridas().size();
        int max = Math.round((2*s) /3);
        Map<String, Participante> old = this.getCampParticipantes();
        Map<String, Participante> res = new HashMap<String,Participante>();
        for (Map.Entry<String, Participante> aux : old.entrySet()) {
            aux.getValue().setAfinacoesRestantes(max);
            res.put(aux.getKey(), aux.getValue());
        }
        this.setCampParticipantes(res);
        return max;
    }
    public Corrida proximacorrida(){
        return corridas.get(corridaAtual-1);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Campeonato:  ");
        sb.append(" nome= " + nomeCampeonato);
        sb.append(" | corridaAtual=" + corridaAtual);
        sb.append(" | corridas=" + corridas);
        sb.append(" | campParticipantes=" + campParticipantes );
        sb.append(" | tipoCampeonato=" + tipoCampeonato  );
        return sb.toString();
    }
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Campeonato:  ");
        sb.append(" | nome= " + nomeCampeonato);
        return sb.toString();
    }
    
    public Campeonato clone(){
        return new Campeonato(this);
    }
}
