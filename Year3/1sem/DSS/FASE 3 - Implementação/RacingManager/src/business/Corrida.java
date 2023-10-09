package business;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;
import java.util.Collections;
import java.lang.*;

public class Corrida implements Serializable
{
    //variaveis de instancia

    private Circuito circuito;
    private Map<String, Participante> participantes;
    private int clima; //1-chove 0-sol
    private int voltas;


    public Corrida(){
        this.circuito = new Circuito();
        this.participantes = new HashMap<String, Participante>();
        this.clima = 0;
        this.voltas = 0;
    }

    public Corrida(Circuito circuito, Map<String, Participante> participantes, int clima, int voltas) {
        this.circuito = circuito.clone();
        this.setParticipantes(participantes);
        this.clima = clima;
        this.voltas = voltas;
    }

    public Corrida(Corrida c) {
        this.circuito = c.getCircuito();
        this.participantes = c.getParticipantes();
        this.clima = c.getClima();
        this.voltas = c.getVoltas();
    }

    public Circuito getCircuito() {
        return circuito;
    }

    public int getClima() {
        return clima;
    }

    public int getVoltas() {
        return voltas;
    }

    public Map<String, Participante> getParticipantes() {
        Map<String,Participante> result =  new TreeMap<>();

        for(Participante p : this.participantes.values()){
            result.put(p.getNome(),p.clone());
        }

        return result;
    }

    public void setCircuito(Circuito circuito) {
        this.circuito = circuito.clone();
    }


    public void setClima(int clima) {
        this.clima = clima;
    }

    public void setParticipantes(Map<String, Participante> participantes) {
        this.participantes = new HashMap<String,Participante>();
        for(Participante p : participantes.values()){
            this.participantes.put(p.getNome(),p.clone());
        }
    }

    public void addParticipante(Participante p){
        this.participantes.put(p.getNome(),p.clone());
    }

    public void setVoltas(int voltas) {
        this.voltas = voltas;
    }

    public void simularCorrida(){
        int i = this.voltas;
        for (;i>0;i--){
            simularVolta();
        }
        simularDespistes();
        addPontuaçãoCorrida();
    }

    public void addPontuaçãoCorrida(){
        List<Participante> pList = listaClacificacao();
        for(int i = 0 ; i<10 ; i++) {
            Participante p = pList.get(i).clone();
            switch (i) {
                case 0: p.setPontuacao(25);
                case 1: p.setPontuacao(18);
                case 2: p.setPontuacao(15);
                case 3: p.setPontuacao(12);
                case 4: p.setPontuacao(10);
                case 5: p.setPontuacao(8);
                case 6: p.setPontuacao(6);
                case 7: p.setPontuacao(4);
                case 8: p.setPontuacao(2);
                case 9: p.setPontuacao(1);

            }
            addParticipante(p.clone());
        }
    }

    public void simularVolta(){
        for(Participante p : this.participantes.values()){
            LocalTime t = LocalTime.of(0,0,0);
            int x;
            if (p.getCarro().getMotor() == ModoMotor.AGRESSIVO) {
                x = 110;
            } else if (p.getCarro().getMotor() == ModoMotor.NORMAL) {
                x = 100;
            } else {
                x = 90;
            }
            int y;
            if (p.getCarro().getPneus() == TipoPneus.CHUVA && this.clima==1) {
                y = 120;
            } else if (this.clima == 1) {
                y = 50;
            } else if (p.getCarro().getPneus() == TipoPneus.CHUVA){
                y = 70;
            } else {
                y = 100;
            }

            double porcemtagem = (p.getPiloto().getSva()*0.5 + (p.getPiloto().getCts())*this.clima*0.1 +  x*0.1 + y*0.1 + Math.random()*0.3)/100;
            t.plusSeconds((int)(tempoideal(p)*porcemtagem));
            p.addTempo(t);
        }
    }

    private int tempoideal(Participante p) {
        int pot = p.getCarro().getPotencia();
        float dist =this.circuito.getDistancia();
        int i = 0;
        i=(int)(pot*1.7/dist);
        return i;
    }


    public void simularDespistes(){
        for(Participante p : this.participantes.values()){
            float fiabilidade = p.calculaFiabilidadeFinal();
            if (fiabilidade < Math.random()*100){
                List<LocalTime> tempos = p.getTempos();
                int rng = (this.voltas - (int)(Math.random()*(voltas-2)+1));
                for(int i = rng;rng>0;i--){
                    tempos.remove(tempos.size() - 1);
                }
                List<SegmentoDePista> seguementos = this.circuito.getSegmentosdepista();
                p.setLocalizacaoPista((int)(Math.random()*(seguementos.toArray().length - 1)));
            } else{
                p.setLocalizacaoPista(-1);
            }
        }
    }

    public List<Participante> listaClacificacao(){
        List<Participante> r = new ArrayList<>();
        for (Participante p : this.participantes.values()){
            r.add(p.clone());
        }
        Collections.sort(r, new Sortbytime());
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Corrida:  ");
        sb.append(" circuito=" + circuito);
        sb.append(" | participantes=" + participantes);
        sb.append(" | clima=" + clima );
        sb.append(" | voltas=" + voltas  );
        return sb.toString();
    }
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Corrida:  ");
        sb.append(" circuito=" + circuito);
        return sb.toString();
    }

    public Corrida clone(){
        return new Corrida(this);
    }
}
