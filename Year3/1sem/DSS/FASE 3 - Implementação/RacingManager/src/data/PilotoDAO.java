package data;

import business.*;

import java.sql.*;
import java.util.*;

public class PilotoDAO implements Map<String, Piloto> {
    private static PilotoDAO singleton = null;

    private PilotoDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS pilotos (" +
                    "nome varchar(45) NOT NULL PRIMARY KEY," +
                    "sva INT NOT NULL,"+
                    "cts INT NOT NULL)";
            stm.executeUpdate(sql);
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
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static PilotoDAO getInstance(){
        if(PilotoDAO.singleton == null){
            PilotoDAO.singleton=new PilotoDAO();
        }
        return PilotoDAO.singleton;
    }
    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM pilotos")) {
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
             ResultSet rs =
                     stm.executeQuery("SELECT nome FROM pilotos WHERE nome='"+key.toString()+"'")) {
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
        Piloto p = (Piloto) value;
        return this.containsKey(p.getNome());
    }

    @Override
    public Piloto get(Object key) {
        Piloto p = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM pilotos WHERE nome='"+key+"'")) {
            if (rs.next()) {  // A chave existe na tabela

                p = new Piloto(rs.getString("nome"), rs.getInt("sva"), rs.getInt("cts"));
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    @Override
    public Piloto put(String s, Piloto piloto) {
        Piloto res = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            // Actualizar a Sala
            stm.executeUpdate(
                    "INSERT INTO pilotos " +
                            "VALUES ('"+ piloto.getNome()+ "', '"+
                            piloto.getSva()+"', "+
                            piloto.getCts()+") " +
                            "ON DUPLICATE KEY UPDATE sva=Values(sva), " +
                            "cts=Values(cts)");


            res = get(s);
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Piloto remove(Object o) {
        Piloto p = this.get(o);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            // apagar a turma
            stm.executeUpdate("DELETE FROM pilotos WHERE nome='" + p.getNome() + "'");
            stm.executeUpdate("DELETE FROM participantes WHERE piloto_nome='"+p.getNome()+"'");

        }catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return p;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Piloto> map) {
        for(Piloto p: map.values()) this.put(p.getNome(), p);
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            for(Piloto p: this.values()) stm.executeUpdate("TRUNCATE participantes WHERE piloto_nome='"+p.getNome()+"'");
            stm.executeUpdate("TRUNCATE pilotos");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("SELECT nome from pilotos");
            ResultSet rs = stm.executeQuery("SELECT nome FROM pilotos");
            while(rs.next()){
                String idt = rs.getString("nome");
                res.add(idt);
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Collection<Piloto> values() {
        Collection<Piloto> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nome FROM pilotos")) { // ResultSet com os ids de todas as turmas
            while (rs.next()) {
                String idt = rs.getString("nome"); // Obtemos um id de turma do ResultSet
                Piloto p = this.get(idt);                    // Utilizamos o get para construir as turmas uma a uma
                res.add(p);                                 // Adiciona a turma ao resultado.
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Piloto>> entrySet() {
        Map<String,Piloto> res = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nome FROM pilotos")) { // ResultSet com os ids de todas as turmas
            while (rs.next()) {
                String idt = rs.getString("nome"); // Obtemos um id de turma do ResultSet
                Piloto p = this.get(idt);                    // Utilizamos o get para construir as turmas uma a uma
                res.put(idt,p);                                 // Adiciona a turma ao resultado.
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res.entrySet();
    }
}
