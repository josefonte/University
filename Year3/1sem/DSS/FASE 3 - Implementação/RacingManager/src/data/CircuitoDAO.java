package data;


import business.*;

import java.sql.*;
import java.util.*;

public class CircuitoDAO implements Map<String,Circuito>{
    private static CircuitoDAO singleton = null;

    private CircuitoDAO() {
        CampeonatoDAO.getInstance();
    }

    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static CircuitoDAO getInstance() {
        if (CircuitoDAO.singleton == null) {
            CircuitoDAO.singleton = new CircuitoDAO();
        }
        return CircuitoDAO.singleton;
    }

    @Override
    public int size() {

        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM circuitos")) {
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
             ResultSet rs = stm.executeQuery("SELECT nome FROM circuitos WHERE nome='"+key.toString()+"'")) {
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
        Circuito t = (Circuito) value;
        return this.containsKey(t.getNomeCircuito());
    }

    private ArrayList<SegmentoDePista> getSegmentosCircuito(String key, Statement stm) throws SQLException{
        ArrayList<SegmentoDePista> r = new ArrayList<>();
        // preencher com o conteúdo da base de dados
        try (ResultSet rsa = stm.executeQuery("SELECT * FROM segmentos WHERE nomecircuito='"+key+"'")) {
            while (rsa.next()) {
                SegmentoDePista seg = new SegmentoDePista(rsa.getInt("gdu"), rsa.getFloat("distancia"), Enum.valueOf(TipoSegmento.class, rsa.getString("nome")));
                r.add(seg);
            }
        }
        return r;
    }


    @Override
    public Circuito get(Object key) {
        Circuito t = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM circuitos WHERE nome='"+key+"'")) {
            if (rs.next()) {  // A chave existe na tabela
                // Reconstruir a os segmentos de pista
                ArrayList<SegmentoDePista> segmentos = getSegmentosCircuito(key.toString(), stm);
                //reconstruir circuito
                t = new Circuito(rs.getFloat("distancia"),
                        rs.getString("nome"),
                        segmentos);
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;

    }

    @Override
    public Circuito put(String key, Circuito value) {
        Circuito res;
        List<SegmentoDePista> s = value.getSegmentosdepista();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            // Actualizar a circuito
            stm.executeUpdate(
                    "INSERT INTO circuitos (nome, distancia)" +
                            "VALUES ('"+ value.getNomeCircuito()+ "', '"+
                            value.getDistancia()+"') "+
                            "ON DUPLICATE KEY UPDATE distancia=Values(distancia)");

            // Actualizar os segmentos do circuito
            //Eliminar segmentos cujo nome do circuito corresponde
            stm.executeUpdate("DELETE FROM segmentos WHERE nomecircuito='"+key+"'");
            //Adicionar segmentos
            for (int i=0; i<s.size();i++){
                SegmentoDePista seg = s.get(i);
                stm.executeUpdate("INSERT INTO segmentos (gdu, distancia, nome, nomecircuito)" +
                        "VALUES ('"+
                        seg.getGdu()+"', '"+
                        seg.getDistancia()+"', '"+
                        seg.getNome()+"', '"+
                        value.getNomeCircuito()+"') ");
            }
            res = get(key);



        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Circuito remove(Object key) {
        Circuito t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            // apagar a segmentos
            stm.executeUpdate("DELETE FROM segmentos WHERE nomecircuito='"+key+"'");
            // apagar circuito
            stm.executeUpdate("DELETE FROM circuitos WHERE nome='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Circuito> m) {
        for(Circuito t : m.values()) this.put(t.getNomeCircuito(), t);
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE segmentos");
            stm.executeUpdate("TRUNCATE circuitos");
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
    public Collection<Circuito> values() {
        Collection<Circuito> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nome FROM circuitos")) {
            while (rs.next()) {
                String idc = rs.getString("nome");
                Circuito c = this.get(idc);
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
    public Set<Entry<String, Circuito>> entrySet() {
        Map<String,Circuito> res = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()){
            ResultSet rs = stm.executeQuery("SELECT nome FROM circuitos");
            while (rs.next()) {
                String idc = rs.getString("nome");
                Circuito c = get(idc);
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