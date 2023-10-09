package data;

import business.*;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CampeonatoDAO implements Map<String , Campeonato> {
    private static CampeonatoDAO singleton = null;

    public CampeonatoDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            String campeonato = "CREATE TABLE IF NOT EXISTS campeonatos ("+
                    "nome varchar(16) NOT NULL PRIMARY KEY, "+
                    "corridaAtual INT NOT NULL, "+
                    "categoria varchar(3))";
            stm.executeUpdate(campeonato);
            String circuito ="CREATE TABLE IF NOT EXISTS circuitos ("+
                    "nome varchar(45) NOT NULL PRIMARY KEY,"+
                    "distancia float(10) NOT NULL)";
            stm.executeUpdate(circuito);
            String corridas ="CREATE TABLE IF NOT EXISTS corridas ("+
                    "id INT NOT NULL PRIMARY KEY, "+
                    "clima INT NOT NULL, "+
                    "voltas INT NOT NULL, "+
                    "circuito varchar(45) NOT NULL,"+
                    "ncampeonato varchar(16) NOT NULL,"+
                    "FOREIGN KEY (circuito) REFERENCES circuitos(nome),"+
                    "FOREIGN KEY (ncampeonato) REFERENCES campeonatos(nome))";
            stm.executeUpdate(corridas);
            String segmentos = "CREATE TABLE IF NOT EXISTS segmentos ("+
                    "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"+
                    "gdu INT NOT NULL,"+
                    "distancia float NOT NULL,"+
                    "nome VARCHAR(17) NOT NULL,"+
                    "nomecircuito varchar(45) NOT NULL,"+
                    "FOREIGN KEY (nomecircuito) REFERENCES circuitos(nome))";
            stm.executeUpdate(segmentos);
            String carros = "CREATE TABLE IF NOT EXISTS carros (" +
                    "id varchar(10) NOT NULL PRIMARY KEY," +
                    "categoria varchar(10) NOT NULL,"+
                    "modelo varchar(45) NOT NULL," +
                    "marca varchar(45) NOT NULL,"+
                    "celindrada INT NOT NULL," +
                    "potencia INT NOT NULL," +
                    "fiabilidade FLOAT DEFAULT NULL," +
                    "pac INT NOT NULL," +
                    "TipoPneus VARCHAR(17) DEFAULT NULL ," +
                    "ModoMotor VARCHAR(17) DEFAULT NULL," +
                    "potenciaHibrido INT DEFAULT NULL," +
                    "taxaDeteorizacao INT DEFAULT NULL)";
            stm.executeUpdate(carros);
            String pilotos = "CREATE TABLE IF NOT EXISTS pilotos (" +
                    "nome varchar(45) NOT NULL PRIMARY KEY," +
                    "sva INT NOT NULL,"+
                    "cts INT NOT NULL)";
            stm.executeUpdate(pilotos);
            String utilizadores="CREATE TABLE IF NOT EXISTS utilizadores (" +
                    "nomeUtilizador varchar(45) NOT NULL PRIMARY KEY," +
                    "pontosRanking INT NOT NULL,"+
                    "tipoUtilizador VARCHAR(17) NOT NULL)";
            stm.executeUpdate(utilizadores);
            String tempos = "CREATE TABLE IF NOT EXISTS tempos (" +
                    "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "dt_value varchar(45) NOT NULL)";
            stm.executeUpdate(tempos);
            String participantes = "CREATE TABLE IF NOT EXISTS participantes (" +
                    "idParticipante int NOT NULL PRIMARY KEY," +
                    "pontuacao INT NOT NULL,"+
                    "afinacoesRestantes INT NOT NULL," +
                    "voltasTotais INT NOT NULL,"+
                    "localizacaoPista INT NOT NULL," +
                    "tempos INT NOT NULL," +
                    "campeonato_nome VARCHAR(16) NOT NULL," +
                    "utilizador_nome VARCHAR(45) NOT NULL," +
                    "carro_id varchar(10) NOT NULL ," +
                    "piloto_nome VARCHAR(45) NOT NULL,"+
                    "corrida_id INT NOT NULL, "+
                    "corrida_circuito_nome VARCHAR(45),"+
                    "FOREIGN KEY (tempos) REFERENCES tempos(id),"+
                    "FOREIGN KEY (campeonato_nome) REFERENCES campeonatos(nome),"+
                    "FOREIGN KEY (utilizador_nome) REFERENCES utilizadores(nomeUtilizador),"+
                    "FOREIGN KEY (carro_id) REFERENCES carros(id),"+
                    "FOREIGN KEY (piloto_nome) REFERENCES pilotos(nome)," +
                    "FOREIGN KEY (corrida_id) REFERENCES corridas(id)," +
                    "FOREIGN KEY (corrida_circuito_nome) REFERENCES circuitos(nome))";
            stm.executeUpdate(participantes);



        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static CampeonatoDAO getInstance() {
        if (CampeonatoDAO.singleton == null) {
            CampeonatoDAO.singleton = new CampeonatoDAO();
        }
        return CampeonatoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM campeonatos")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nome FROM campeonatos WHERE nome='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        Campeonato c = (Campeonato) value;
        return this.containsKey(c.getNomeCampeonato());
    }

    @Override
    public Campeonato get(Object key) {
        Campeonato c = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM campeonatos WHERE nome='"+key+"'")) {
            if (rs.next()) {  // A chave existe na tabela
                // Reconstruir a os segmentos de pista
                //reconstruir circuito
                String nomeCampeonato=rs.getString("nome");
                int corridaAtual=rs.getInt("corridaAtual");
                List<Corrida> corridas = getCorridas(key.toString(), stm);
                //Map<String, Participante> campParticipantes = getParticipantes(key.toString(),stm);
                Map<String, Participante> campParticipantes = new HashMap<>();
                //TipoCampeonato tipo =
                c = new Campeonato(nomeCampeonato,corridaAtual,corridas, campParticipantes, Enum.valueOf(TipoCampeonato.class, rs.getString("categoria")));
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    private Map<String, Participante> getParticipantes(String key, Statement stm) throws SQLException {
        Map<String,Participante> r = new HashMap<>();
        // preencher com o conteúdo da base de dados
        try (ResultSet rsa = stm.executeQuery("SELECT * FROM participantes WHERE campeonatoNome='"+key+"'")) {
            while (rsa.next()) {
                List<LocalTime> tempos = getTemposParticipante(rsa.getInt("tempos"), stm);
                Carro carro = getCarroParticipante(rsa.getInt("carro_id"), stm);
                Utilizador utilizador = getUtilizadorParticipante(rsa.getString("utilizador_nome"),stm);
                Piloto piloto = getPilotoParticipante(rsa.getString("piloto_nome"),stm);
                Participante participante = new Participante(rsa.getInt("idParticipante"), rsa.getInt("pontuacao"), tempos, rsa.getInt("afinacoesRestantes"), rsa.getInt("voltasTotais"), rsa.getInt("localizacaoPista"),carro,utilizador,piloto);
                r.put((Integer.toString(rsa.getInt("idParticipante"))),participante);
            }
        }
        return r;
    }

    private Piloto getPilotoParticipante(String pilotoNome, Statement stm) throws SQLException{
        Piloto p;
        try (ResultSet rsa = stm.executeQuery("SELECT * FROM pilotos WHERE nome='"+pilotoNome+"'")){
            p = new Piloto(rsa.getString("nome"), rsa.getInt("sva"), rsa.getInt("cts"));
        }
        return p;
    }

    private Utilizador getUtilizadorParticipante(String utilizadorNome, Statement stm) throws SQLException{
        Utilizador u;
        try (ResultSet rsa = stm.executeQuery("SELECT * FROM utilizadores WHERE nomeUtilizaodr='"+utilizadorNome+"'")){
            u = new Utilizador(rsa.getString("nomeUtilizador"),  Enum.valueOf(TipoUtilizador.class, rsa.getString("tipoUtilizador")), rsa.getInt("pontosRanking"));
        }
        return u;
    }

    private Carro getCarroParticipante(int carroId, Statement stm) throws SQLException{
        Carro c = null;
        try(ResultSet rs = stm.executeQuery("SELECT * FROM carros WHERE id = '"+carroId+"'")){
            String categoria = rs.getString("categoria");
            switch (categoria) {
                case "C1" :
                    c = new C1(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"), rs.getInt("potencia"), rs.getFloat("fiabilidade"), rs.getInt("pac"), rs.getString("id"), rs.getInt("potenciaHibrida"));
                    break;
                case "C2" :
                    c = new C2(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"), rs.getInt("potencia"), rs.getFloat("fiabilidade"), rs.getInt("pac"), rs.getString("id"), rs.getInt("potenciaHibrida"));
                    break;
                case "SC" :
                    c = new SC(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"), rs.getInt("potencia"), rs.getFloat("fiabilidade"), rs.getInt("pac"), rs.getString("id"));
                    break;
                case "GT" :
                    c = new GT(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"), rs.getInt("potencia"), rs.getFloat("fiabilidade"), rs.getInt("pac"), rs.getString("id"), rs.getInt("potenciaHibrida"), rs.getInt("taxadeteorizacao"));
                    break;
                default:
                    break;
            }
        }
        return c;
    }

    private List<LocalTime> getTemposParticipante(int tempos, Statement stm) throws SQLException{
        List<LocalTime> r = new ArrayList<>();
        try (ResultSet rs = stm.executeQuery("SELECT * FROM tempos WHERE id = '"+ tempos + "'")){
            while(rs.next()){
                String timeS = rs.getString("dt_value");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime time = LocalTime.parse(timeS, formatter);
                r.add(time);
            }
        }
        return r;
    }

    private List<Corrida> getCorridas(String key, Statement stm) throws SQLException {
        List<Corrida> r = new ArrayList<>();
        try(ResultSet rs = stm.executeQuery("SELECT * FROM corridas WHERE id='"+key+"'")) {
            while (rs.next()){
                Utilizador u = getUtilizadorParticipante(String.valueOf(rs.getInt("id")),stm);
                //Participante p = null;
                Circuito circuito = getCircuitoCorrida(rs.getString("circuito"),stm);
                Map<String, Participante> participantes = new HashMap<>();
                Corrida c = new Corrida(circuito, participantes, rs.getInt("clima"), rs.getInt("voltas"));
                r.add(c);
            }
        }
        return r;
    }

    private Circuito getCircuitoCorrida(String circuito, Statement stm) throws SQLException{
        Circuito c;
        try(ResultSet rs = stm.executeQuery("SELECT * FROM circuitos WHERE nome = '"+circuito+"´")){
            ArrayList<SegmentoDePista> segmentos = getSegmentos(rs.getString("nome"),stm);
            c = new Circuito(rs.getFloat("distancia"), rs.getString("nome"),segmentos);
        }
        return c;
    }

    private ArrayList<SegmentoDePista> getSegmentos(String nome, Statement stm) throws SQLException{
        ArrayList<SegmentoDePista> segs = new ArrayList<>();
        try(ResultSet rs = stm.executeQuery("SELECT * FROM segmentos WHERE nomecircuito = '"+nome+"´")){
            SegmentoDePista seg = new SegmentoDePista(rs.getInt("gdu"), rs.getFloat("distancia"),Enum.valueOf(TipoSegmento.class, rs.getString("TipoSegmento")));
            segs.add(seg);
        }
        return segs;
    }
    @Override
    public Campeonato put(String s, Campeonato campeonato) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             PreparedStatement stm = conn.prepareStatement("INSERT INTO campeonatos (nome, corridaAtual, categoria) VALUES (?,?,?) ON DUPLICATE KEY UPDATE corridaAtual = ?, categoria = ?")) {
            stm.setString(1, campeonato.getNomeCampeonato());
            stm.setInt(2, campeonato.getCorridaAtual());
            stm.setString(3, campeonato.getTipoCampeonato().toString());
            stm.setInt(4, campeonato.getCorridaAtual());
            stm.setString(5, campeonato.getTipoCampeonato().toString());

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campeonato;
    }

    /*

    @Override
    public Campeonato put(String s, Campeonato campeonato) {
        Campeonato t = null;
        ArrayList<Corrida> c = (ArrayList<Corrida>) campeonato.getCorridas();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate(
                    "INSERT INTO campeonatos " +
                            "VALUES ('"+ campeonato.getNomeCampeonato()+ "', '"+
                            campeonato.getCorridaAtual()+"', '"+
                            campeonato.getTipoCampeonato()+"') "+
                            "ON DUPLICATE KEY UPDATE corridaAtual = values(corridaAtual),"+
                            "categoria = Values(categoria)"
            );
            stm.executeUpdate("DELETE FROM corridas WHERE ncampeonato='"+s+"'");

            for(Corrida key : c) {

                //stm.executeUpdate("DELETE FROM corridas WHERE id='" + j + "'");
                //key.getCircuito().getNomeCircuito() ?
                //j++;
                for (int i=0; i<c.size();i++){
                    Corrida corrida = c.get(i);
                    stm.executeUpdate("INSERT INTO corridas " +
                            "VALUES ("+i+"', '"+
                            corrida.getClima()+"', '"+
                            corrida.getVoltas()+"', '"+
                            corrida.getCircuito()+") ");
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }
*/
    private void putCorrida(String key,Corrida c, int index, Statement stm) throws SQLException{
        Circuito circuito  = c.getCircuito();
        stm.executeUpdate("INSERT INTO corridas (Indice,NomeCampeonato,Circuito) " +
                "VALUES ("+ index+ ", '"+
                key+"', '"+
                circuito.getNomeCircuito()+
                "') " +
                "ON DUPLICATE KEY UPDATE Indice=Values(Indice), " +
                " NomeCampeonato=Values(NomeCampeonato), " +
                " Circuito=Values(Circuito)");
    }

    @Override
    public Campeonato remove(Object key) {
        Campeonato c = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            // apagar a segmentos
            stm.executeUpdate("DELETE FROM participantes WHERE nome_campeonato='"+key+"'");
            // apagar circuito
            stm.executeUpdate("DELETE FROM campeonatos WHERE nome='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Campeonato> map) {
        for(Campeonato c: map.values()) this.put(c.getNomeCampeonato(),c);
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE participantes");
            stm.executeUpdate("TRUNCATE campeonatos");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

    }

    @Override
    public Set<String> keySet() {
        Set<String> res= new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            ResultSet rs = stm.executeQuery("SELECT nome FROM circuitos");
            while (rs.next()) {
                String idc = rs.getString("nome");
                res.add(idc);
            }
        }
        catch (SQLException e){
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Collection<Campeonato> values() {
        Collection<Campeonato> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nome FROM campeonatos")) {
            while (rs.next()) {
                String idc = rs.getString("nome");
                Campeonato c = this.get(idc);
                res.add(c);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Campeonato>> entrySet() {
        Map<String,Campeonato> res = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            ResultSet rs = stm.executeQuery("SELECT nome FROM campeonatos");
            while (rs.next()) {
                String idc = rs.getString("nome");
                Campeonato c = get(idc);
                res.put(idc,c);
            }
        }
        catch (SQLException e){
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res.entrySet();
    }

}
