import business.*;
//import data.*;
import data.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalTime;
import java.util.*;

public class TesteModel {
    public static Map<String,Campeonato> campeonatos;
    public static Map<String,Carro> carros;
    public static Map<String,Circuito> circuitos;
    public static Map<String,Piloto> pilotos;
    public static Map<String,Utilizador> utilizadores;
    public int contadorParticipante;

    public TesteModel(){
        campeonatos = CampeonatoDAO.getInstance();
        carros = CarroDAO.getInstance();
        circuitos = CircuitoDAO.getInstance();
        pilotos = PilotoDAO.getInstance();
        utilizadores = UtilizadorDAO.getInstance();
        //contadorParticipante = 0;
    }
    public static void main(String[] args) {
        TesteModel testeModel = new TesteModel();
        DAOconfig DAOconfig = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD)){
            Piloto p1 = new Piloto("Rafael", 2,3);
            Piloto p2 = new Piloto("Ines", 4,3);
            Piloto p3 = new Piloto("Raposo", 5,3);
            Piloto p4 = new Piloto("Ze", 1,3);
            Piloto p5 = new Piloto("Joao", 1,3);
            Piloto p6 = new Piloto("Rafael", 12, 2);
            pilotos.put("Rafael", p1);
            pilotos.put("Ines",p2);
            pilotos.put("Raposo",p3);
            pilotos.put("Ze",p4);
            pilotos.put("Joao",p5);
            pilotos.put("Rafael",p6);
            Piloto pget = pilotos.get("Rafael");
            System.out.println("pilotos antes : ");
            System.out.println(p1.toString());
            System.out.println(p2.toString());
            System.out.println(p3.toString());
            System.out.println(p4.toString());
            System.out.println(p5.toString());
            System.out.println(p6.toString());
            System.out.println("pilotos depois : ");
            System.out.println(pget.toString());
            TipoSegmento curva = TipoSegmento.CURVA;
            TipoSegmento chicane = TipoSegmento.CHICANE;
            TipoSegmento reta = TipoSegmento.RETA;
            SegmentoDePista s1 = new SegmentoDePista(1,2,reta);
            SegmentoDePista s2 = new SegmentoDePista(1,2,curva);
            SegmentoDePista s3 = new SegmentoDePista(1,2,reta);
            SegmentoDePista s4 = new SegmentoDePista(1,2,chicane);
            ArrayList<SegmentoDePista> l = new ArrayList<SegmentoDePista>();
            l.add(s1);
            l.add(s2);
            l.add(s3);
            l.add(s4);
            Circuito c1 = new Circuito(2,"Industrial",l);
            circuitos.put("Industrial",c1);
            Circuito c2 = circuitos.get("Industrial");
            System.out.println(c1.toString());
            System.out.println(c2.toString());

            l.add(s1);
            Circuito c3 = new Circuito(4, "Industrial", l);
            circuitos.put("Industrial",c3);

            Circuito c4 = circuitos.get("Industrial");
            System.out.println(c4.toString());

            Utilizador u1 = new Utilizador("Ze",TipoUtilizador.JOGADOR,15);
            Utilizador u2 = new Utilizador("Rafa",TipoUtilizador.JOGADOR,24);
            utilizadores.put("Ze",u1);
            utilizadores.put("Rafa",u2);
            Utilizador u3 = utilizadores.get("Ze");
            Utilizador u4 = utilizadores.get("Rafa");
            System.out.println(u3.toString());
            System.out.println(u4.toString());

            Carro carro = new C2("bmw","A5",6000,1,2,4,"as",11);
            Carro carro1 = new C2("audi","A2",4000,1,2,4,"us",11);
            carro.setPneus(TipoPneus.MACIO);
            carro.setMotor(ModoMotor.NORMAL);
            carro1.setPneus(TipoPneus.MACIO);
            carro1.setMotor(ModoMotor.NORMAL);
            Carro car1 = carros.put("as",carro);
            System.out.println(car1.toString());
            Carro car2 =carros.put("us",carro1);
            System.out.println(car2.toString());
            Carro carro2 = carros.get("as");
            Carro carro3 = carros.get("us");
            System.out.println(carro2.toString());
            System.out.println(carro3.toString());

            List<LocalTime> tempos =new ArrayList<LocalTime>();

            Participante par = new Participante(1,12,tempos,3,5,7,carro,u1,p1);
            Participante par2 = new Participante(2,12,tempos,3,5,7,carro1,u2,p1);
            Map <String, Participante> part = new HashMap<>();
            part.put("1",par);
            part.put("2",par2);

            Corrida cor = new Corrida(c1,part,4,2);
            List<Corrida> corri = new ArrayList<Corrida>();
            corri.add(cor);

            Campeonato camp = new Campeonato("Camp",3,corri,part,TipoCampeonato.C1);
            System.out.println("AQUI");
            campeonatos.put("Camp",camp);
            Campeonato camp2 = campeonatos.get("Camp");
            System.out.println(camp2.toString());



        } catch (Exception e) {
            System.out.println("Não foi possível arrancar: " + e.getMessage());
        }
    }
}
