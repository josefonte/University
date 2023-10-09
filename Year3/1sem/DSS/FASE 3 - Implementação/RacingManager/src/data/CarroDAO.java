package data;
import business.*;

import java.sql.*;
import java.util.*;

import static business.TipoPneus.CHUVA;

public class CarroDAO implements Map<String, Carro> {
    private static CarroDAO singleton = null;
    private CarroDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS carros (" +
                    "id varchar(10) NOT NULL PRIMARY KEY," +
                    "categoria varchar(10) NOT NULL,"+
                    "modelo varchar(45) NOT NULL," +
                    "marca varchar(45) NOT NULL,"+
                    "celindrada int NOT NULL," +
                    "potencia int NOT NULL," +
                    "fiabilidade float DEFAULT NULL," +
                    "pac int NOT NULL," +
                    "TipoPneus varchar(17)," +//ENUM ('Duro','Macio','Chuva')
                    "ModoMotor varchar(17)," + //ENUM('Conversador','Normal', 'Agressivo')," +
                    "potenciaHibrido int DEFAULT NULL," +
                    "taxaDeteorizacao int DEFAULT NULL)";
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
    public static CarroDAO getInstance() {
        if (CarroDAO.singleton == null) {
            CarroDAO.singleton = new CarroDAO();
        }
        return CarroDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM carros")) {
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
                     stm.executeQuery("SELECT id FROM carros WHERE id='"+key.toString()+"'")) {
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
        Carro c = (Carro) value;
        return this.containsKey(c.getId());
    }

    @Override
    public Carro get(Object key) {
        Carro c = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM carros WHERE id='" + key + "'")) {
            if (rs.next()) {  // A chave existe na tabela
                // Reconstruir a colecção de alunos da turma
                String id = rs.getString("id");
                String categoria = rs.getString("categoria");
                switch (categoria) {
                    case "C1":
                        c = new C1(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"),rs.getInt("potencia"),rs.getFloat("fiabilidade"),rs.getInt("pac"),rs.getString("id"),rs.getInt("potenciaHibrido"));
                        break;
                    case "C2":
                        c = new C2(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"),rs.getInt("potencia"),rs.getFloat("fiabilidade"),rs.getInt("pac"),rs.getString("id"),rs.getInt("potenciaHibrido"));
                        break;
                    case "SC":
                        c = new SC(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"),rs.getInt("potencia"),rs.getFloat("fiabilidade"),rs.getInt("pac"),rs.getString("id"));
                        break;
                    case "GT":
                        c = new GT(rs.getString("marca"), rs.getString("modelo"), rs.getInt("celindrada"),rs.getInt("potencia"),rs.getFloat("fiabilidade"),rs.getInt("pac"),rs.getString("id"),rs.getInt("potenciaHibrido"), rs.getInt("taxadeteorizacao"));
                        break;
                    default:
                        break;
                }
                // Reconstruir a Sala
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    @Override
    public Carro put(String s, Carro carro) {
        Carro res;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String id = carro.getId();
            String categoria = carro.getCategoria();
            String modelo = carro.getModelo();
            String marca = carro.getMarca();
            int cilindradaint = carro.getCelindrada();
            String celindrada = Integer.toString(cilindradaint);
            String potencia = Integer.toString(carro.getPotencia());
            String fiabilidade = Float.toString(carro.getFiabilidade());
            String pac = Integer.toString(carro.getPac());
            TipoPneus tipoPneusT = carro.getPneus();
            String tipoPneusS = "";
            switch (tipoPneusT.toString()){
                case("CHUVA"):
                    tipoPneusS="CHUVA";
                    break;
                case("DURO"):
                    tipoPneusS="DURO";
                    break;
                case("MACIO"):
                    tipoPneusS="MACIO";
                    break;
                default:
                    tipoPneusS="MACIO";
                    break;
            }
            String tipoPneus=tipoPneusT.name();

            ModoMotor modoMotorM = carro.getMotor();
            String modoMotor = modoMotorM.name();
            // Actualizar a Sala
            String potenciaHibrida = "0";
            String taxaDeteorizacao = "0";

            switch(categoria){
                case "C1":
                    potenciaHibrida = Integer.toString(((C1) carro).getPotenciaHibrida());
                    break;
                case "C2":
                    potenciaHibrida = Integer.toString(((C2) carro).getPotenciaHibrida());
                    break;
                case "SC":
                    break;
                case "GT":
                    potenciaHibrida = Integer.toString(((C1) carro).getPotenciaHibrida());
                    taxaDeteorizacao= Integer.toString(((GT) carro).getTaxaDeteorizacao());
                    break;
                default:
                    break;
            }
            String str = "INSERT INTO carros (id, categoria, modelo, marca, celindrada, potencia, fiabilidade, pac, TipoPneus, ModoMotor, potenciaHibrido, taxaDeteorizacao)" +
                    "VALUES ('"+ s+ "', '"+
                    categoria+"', '"+
                    modelo+"', '"+
                    marca+"', '"+
                    celindrada+"', '"+
                    potencia+"', '"+
                    fiabilidade+"', '"+
                    pac+"', '"+
                    tipoPneus+"', '"+
                    modoMotor+"', '"+
                    potenciaHibrida+"', '"+
                    taxaDeteorizacao +"')"+
                    "ON DUPLICATE KEY UPDATE categoria=Values(categoria), " +
                    "modelo = Values(modelo), "+
                    "marca = Values(marca), "+
                    "celindrada = Values(celindrada), "+
                    "potencia = Values(potencia), "+
                    "fiabilidade = Values(fiabilidade), "+
                    "pac = Values(pac), "+
                    "TipoPneus = Values(TipoPneus), "+
                    "ModoMotor = Values(ModoMotor), "+
                    "potenciaHibrido = Values(potenciaHibrido), "+
                    "taxaDeteorizacao = Values(taxaDeteorizacao)";
            stm.executeUpdate(
                    "INSERT INTO carros (id, categoria, modelo, marca, celindrada, potencia, fiabilidade, pac, TipoPneus, ModoMotor, potenciaHibrido, taxaDeteorizacao)" +
                            "VALUES ('"+ s+ "', '"+
                            categoria+"', '"+
                            modelo+"', '"+
                            marca+"', '"+
                            celindrada+"', '"+
                            potencia+"', '"+
                            fiabilidade+"', '"+
                            pac+"', '"+
                            tipoPneus+"', '"+
                            modoMotor+"', '"+
                            potenciaHibrida+"', '"+
                            taxaDeteorizacao +"')"+
                            "ON DUPLICATE KEY UPDATE categoria=Values(categoria), " +
                            "modelo = Values(modelo), "+
                            "marca = Values(marca), "+
                            "celindrada = Values(celindrada), "+
                            "potencia = Values(potencia), "+
                            "fiabilidade = Values(fiabilidade), "+
                            "pac = Values(pac), "+
                            "TipoPneus = Values(TipoPneus), "+
                            "ModoMotor = Values(ModoMotor), "+
                            "potenciaHibrido = Values(potenciaHibrido), "+
                            "taxaDeteorizacao = Values(taxaDeteorizacao)");

            return get(s);
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Carro remove(Object o) {
        Carro c = this.get(o);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            // apagar a turma
            stm.executeUpdate("DELETE FROM carros WHERE id='" + c.getId() + "'");
            stm.executeUpdate("DELETE FROM participantes WHERE carro_id='"+c.getId()+"'");


        }catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Carro> map) {
        for(Carro c: map.values()) this.put(c.getId(), c);
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            for(Carro c: this.values()) stm.executeUpdate("TRUNCATE participantes WHERE carro_id='"+c.getId()+"'");
            stm.executeUpdate("TRUNCATE carros");
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
            stm.executeUpdate("SELECT id from carros");
            ResultSet rs = stm.executeQuery("SELECT id FROM carros");
            while(rs.next()){
                String idt = rs.getString("id");
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
    public Collection<Carro> values() {
        Collection<Carro> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM carros")) { // ResultSet com os ids de todas as turmas
            while (rs.next()) {
                String idt = rs.getString("id"); // Obtemos um id de turma do ResultSet
                Carro c = this.get(idt);                    // Utilizamos o get para construir as turmas uma a uma
                res.add(c);                                 // Adiciona a turma ao resultado.
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Carro>> entrySet() {
        Map<String,Carro> res = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM carros")) { // ResultSet com os ids de todas as turmas
            while (rs.next()) {
                String idt = rs.getString("id"); // Obtemos um id de turma do ResultSet
                Carro c = this.get(idt);                    // Utilizamos o get para construir as turmas uma a uma
                res.put(idt,c);                                 // Adiciona a turma ao resultado.
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res.entrySet();
    }
}


