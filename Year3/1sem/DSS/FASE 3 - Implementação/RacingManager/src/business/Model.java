package business;

import data.*;

import java.util.*;

public class Model {
    public Map<String,Campeonato> campeonatos;
    public Map<String,Carro> carros;
    public Map<String,Circuito> circuitos;
    public Map<String,Piloto> pilotos;
    public Map<String,Utilizador> utilizadores;
    public int contadorParticipante;

    public Model(){
        campeonatos = CampeonatoDAO.getInstance();
        carros = CarroDAO.getInstance();
        circuitos = CircuitoDAO.getInstance();
        pilotos = PilotoDAO.getInstance();
        utilizadores = UtilizadorDAO.getInstance();
        contadorParticipante = 0;
    }
    public Map<String,Campeonato> getCampeonatos() {
        Map<String,Campeonato> result =  new HashMap<>();

        for(Campeonato p : this.campeonatos.values()){
            result.put(p.getNomeCampeonato(),p.clone());
        }

        return result;
    }

    public Campeonato getCampeonato(String nomeCamp) {
        return this.campeonatos.get(nomeCamp);
    }

    public void addCampeonatos(Campeonato c) {
        this.campeonatos.put(c.getNomeCampeonato(),c.clone());
    }

    public Map<String,Carro> getCarros() {
        Map<String,Carro> result =  new HashMap<String,Carro>();

        for(Carro c : this.carros.values()){
            result.put(c.getId(),c.clone());
        }

        return result;
    }

    public Carro getCarro(String id){
        return this.carros.get(id);
    }

    public void addCarro(Carro c){
        this.carros.put(c.getId(),c.clone());
    }

    public Map<String,Circuito> getCircuitos() {
        Map<String,Circuito> result =  new HashMap<String,Circuito>();

        for(Circuito c : this.circuitos.values()){
            result.put(c.getNomeCircuito(),c.clone());
        }

        return result;
    }

    public Circuito getCircuito(String nome) {
        return this.circuitos.get(nome);
    }

    public void addCircuito(Circuito c){
        this.circuitos.put(c.getNomeCircuito(),c.clone());
    }


    public Map<String,Piloto> getPilotos() {
        Map<String,Piloto> result =  new HashMap<String,Piloto>();

        for(Piloto p : this.pilotos.values()){
            result.put(p.getNome(),p.clone());
        }

        return result;
    }

    public Piloto getPiloto(String nome){
        return this.pilotos.get(nome);
    }

    public void addPiloto(Piloto p){
        this.pilotos.put(p.getNome(),p.clone());
    }

    public Map<String,Utilizador> getUtilizadores(){
        Map<String,Utilizador> result =  new HashMap<String,Utilizador>();

        for(Utilizador u : this.utilizadores.values()){
            result.put(u.getNomeUtilizador(),u.clone());
        }
        return result;
    }
    public Utilizador getUtilizador(String nome){
        return utilizadores.get(nome);
    }
    public void addUtilizador(Utilizador u){
        utilizadores.put(u.getNomeUtilizador(),u.clone());
    }
    public int getContadorParticipante() {
        return this.contadorParticipante;
    }

    public void incrementaContador(){
        this.contadorParticipante++;
    }

    public Map<String,Participante> simularCorrida(String nomeCampeonato){
        Campeonato c = campeonatos.get(nomeCampeonato);
        c.simularCorrida();
        Corrida corrida = c.proximacorrida();
        return corrida.getParticipantes();
    }

    public List<Participante> classificacaoFinal(String nomeCampeonato){
        return campeonatos.get(nomeCampeonato).classificacaoFinal();
    }

    public List<String> infoCarros(Boolean allinfo){
        List<String> result =  new ArrayList<String>();
        for(Carro c : this.carros.values()){
            if (allinfo) {
                result.add(c.toString());
            }else {
                result.add(c.toSimpleString());
            }
        }
        return result;
    }

    public List<String> infoCampeonatos(Boolean allinfo){
        List<String> result =  new ArrayList<String>();
        for(Campeonato c : this.campeonatos.values()){
            if (allinfo) {
                result.add(c.toString());
            }else {
                result.add(c.toSimpleString());
            }
        }
        return result;
    }

    public List<String> infoPilotos(Boolean allinfo){
        List<String> result =  new ArrayList<String>();
        for(Piloto c : this.pilotos.values()){
            if (allinfo) {
                result.add(c.toString());
            }else {
                result.add(c.toSimpleString());
            }
        }
        return result;
    }

    public List<String> infoCircuitos(Boolean allinfo){
        List<String> result =  new ArrayList<String>();
        for(Circuito c : this.circuitos.values()){
            if (allinfo) {
                result.add(c.toString());
            }else {
                result.add(c.toSimpleString());
            }
        }
        return result;
    }

    public Boolean removeCampeonato(String key) {
        Boolean r = campeonatos.containsKey(key);
        if (r){
            campeonatos.remove(key);
        }
        return r;
    }

    public Boolean removePiloto(String key) {
        Boolean r = pilotos.containsKey(key);
        if (r){
            pilotos.remove(key);
        }
        return r;
    }

    public Boolean removeCircuito(String key) {
        Boolean r = circuitos.containsKey(key);
        if (r){
            circuitos.remove(key);
        }
        return r;
    }

    public Boolean removeCarro(String key) {
        Boolean r = carros.containsKey(key);
        if (r){
            carros.remove(key);
        }
        return r;
    }
}
