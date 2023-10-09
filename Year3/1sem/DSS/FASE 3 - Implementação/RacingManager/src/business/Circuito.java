package business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Circuito {
    private float distancia;
    private String nomeCircuito;
    private List<SegmentoDePista> segmentosdepista = new ArrayList<>();

    public Circuito(){
        this.distancia = 0;
        this.nomeCircuito = "";
        this.segmentosdepista = new ArrayList<SegmentoDePista>();
    }
    
     public Circuito(float distancia, String nomeCircuito, ArrayList<SegmentoDePista> segmentos) {
        this.distancia = distancia;
        this.nomeCircuito = nomeCircuito;
        this.setSegmentosdepista(segmentos);
    }

    public Circuito(Circuito c) {
        this.distancia = c.getDistancia();
        this.nomeCircuito = c.getNomeCircuito();
        this.segmentosdepista = c.getSegmentosdepista();
    }
    public Circuito(String nome, float distancia){
        this.distancia=distancia;
        this.nomeCircuito=nome;
        this.segmentosdepista = new ArrayList<SegmentoDePista>();
        this.calculaSegmentos();
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public String getNomeCircuito() {
        return nomeCircuito;
    }

    public void setNomeCircuito(String nomeCircuito) {
        this.nomeCircuito = nomeCircuito;
    }

    public List<SegmentoDePista> getSegmentosdepista() {
        List<SegmentoDePista> seg = new ArrayList<>();

        for(SegmentoDePista s: this.segmentosdepista){
            seg.add(s.clone());
        }
        return seg;
    }

    public void setSegmentosdepista(List<SegmentoDePista> segmentosdepista) {
        this.segmentosdepista = segmentosdepista.stream().map((s)->s.clone()).collect(Collectors.toList());
    }

    public void addSegmentoDePista(SegmentoDePista s){
        segmentosdepista.add(s.clone());
    }
    public void calculaSegmentos(){
        int reta = 0, curva = 0, chicane = 0;
        float distTotal = getDistancia();
        for(SegmentoDePista s: segmentosdepista){
            if (s.getNome() == TipoSegmento.RETA) reta++;
            else if (s.getNome() == TipoSegmento.CURVA) curva++;
            else chicane++;
        }
        for(SegmentoDePista s: segmentosdepista){
            if (s.getNome() == TipoSegmento.RETA) s.setDistancia((float) (distTotal * 0.6 / reta));
            else if (s.getNome() == TipoSegmento.CURVA) s.setDistancia((float) (distTotal * 0.3 /curva));
            else s.setDistancia((float) (distTotal * 0.6 / chicane));
        }
    }

    public List<Float> constroiCircuito(){
        calculaSegmentos();
        return segmentosdepista.stream().map((s)->s.getDistancia()).collect(Collectors.toList());
    }

    public int numSegmento(TipoSegmento seg) {
        int r = 0;
        for(SegmentoDePista s: this.segmentosdepista){
            if (s.getNome()== seg){ r++;}
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Circuito:  ");
        sb.append("  distancia=" + distancia );
        sb.append(" | nomeCircuito='" + nomeCircuito );
        sb.append(" | segmentosdepista=" + segmentosdepista);
        return sb.toString();
    }
    
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Circuito:  ");
        sb.append(" nomeCircuito='" + nomeCircuito );
        return sb.toString();
    }

    public Circuito clone(){
        return new Circuito(this);
    }
}
