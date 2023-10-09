import ErrorHandling.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * O interpreter recebe um ficheiro config.csv e cria a configuração inicial a partir do mesmo.
 * Esta configuração inicial significa que através do metodo houseConfig é criada um hashmap com as keys sendo os nomes da casa associados às suas casas inteligentes
 *
 * @author josefonte
 * @version 0.1
 */

public class Parser {
    private  File configfile;

    public Parser(){
        this.configfile =  new File("");
    }

    public Parser(String filepath){
        this.configfile =  new File(filepath);
    }

    public Parser(Parser myfile){
        this.configfile = myfile.getFile();
    }

    public File getFile(){
        return this.configfile;
    }

    public void setFile(File new_file){
        this.configfile = new_file;
    }

    public boolean equals(Object o){
        if (this==o) return true;
        if (this.getClass()!=o.getClass()) return false;
        Parser file = (Parser) o;
        return true;
        /*try {
           // return FileUtils.contentEquals(configfile, file);
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }*/
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        try{
            Scanner sc = new Scanner(this.configfile);
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occurred. File not located or not open correctly");
        }

        return sb.toString();
    }

    public Parser clone(){
        return new Parser(this);
    }

    //------------------------------------------------------------------------------------------

    public HashMap<String,CasaInteligente> housesConfig() throws CasaInteligenteException, SmartDeviceException, FornecedorException, SmartBulbException, ResolutionException, SmartCameraException, SmartSpeakerException, FileNotFoundException {
        List<String> linhas = lerFicheiro();
        String[] linhaPartida;

        HashMap<String,Fornecedor> fornecedores = new HashMap<>();
        HashMap<String,CasaInteligente> lista_casas = new HashMap<>();

        CasaInteligente casaMaisRecente = null;
        String divisao = null;
        Random num= new Random();
        int cont=0;

        for (String linha : linhas) {
            linhaPartida = linha.split(":", 2);
            switch(linhaPartida[0]){
                case "Fornecedor" -> {
                    if (!fornecedores.containsKey(linhaPartida[1])) {
                        Fornecedor fn = null;
                        String nome_fornecedor = linhaPartida[1];
                        int num_random = num.nextInt(3);

                        switch (num_random){
                            case(0) -> fn = new FornecedorA(nome_fornecedor,0.1f,6,5);
                            case(1) -> fn = new FornecedorB(nome_fornecedor,0.1f,10,15);
                            case(2) -> fn = new FornecedorC(nome_fornecedor,0.1f,13,20);
                        }
                        fornecedores.put(nome_fornecedor,fn);
                    }
                }
                case "Casa" -> {
                   // String key = "casa" + cont;

                    //cont ++;
                    casaMaisRecente = createCasa(linhaPartida[1], fornecedores);
                    lista_casas.put(casaMaisRecente.getID(), casaMaisRecente);
                }
                case "Divisao" -> {
                    if (casaMaisRecente == null) throw new CasaInteligenteException("Casa Não Identificada");

                    divisao = linhaPartida[1];
                    if (!casaMaisRecente.hasRoom(divisao))
                        casaMaisRecente.addRoom(divisao);
                }
                case "SmartBulb" -> {
                    if (divisao == null) throw new CasaInteligenteException("Divisão Não Identificada");

                    String[] campos = linhaPartida[1].split(",");
                    StringBuilder nomesb = new StringBuilder();
                    int rand_num = num.nextInt(999999);
                    nomesb.append("smtblb-").append(rand_num);
                    String nome= nomesb.toString();

                    int tone = -1;
                    switch (campos[0]){
                        case "Warm" -> tone = 2;
                        case "Neutral" -> tone = 1;
                        case "Cold" -> tone = 0;
                    }
                    float valor_fixo = 0.005f;
                    float dimensao = Float.parseFloat(campos[1]);

                    boolean state = false;
                    if (rand_num % 3 == 1) state = true;

                    SmartDevice sd = new SmartBulb(nome, state, 0.05f, tone, dimensao, valor_fixo);

                    casaMaisRecente.addDevice(sd, divisao);
                }
                case "SmartCamera" -> {
                    if (divisao == null) throw new CasaInteligenteException("Divisão Não Identificada");

                    String[] campos = linhaPartida[1].split("[(,x)]");

                    StringBuilder nomesb = new StringBuilder();
                    int rand_num = num.nextInt(999999);
                    nomesb.append("smtcmr-").append(rand_num);
                    String nome = nomesb.toString();

                    float width = Float.parseFloat(campos[1]);
                    float heigth = Float.parseFloat(campos[2]);

                    Resolution res = new Resolution(width,heigth);

                    float file_size = Float.parseFloat(campos[4]);
                    float consumo = Float.parseFloat(campos[5]);

                    boolean state = false;
                    if (rand_num % 3 == 1) state=true;

                    SmartDevice sd = new SmartCamera(nome, state, 0.1f,res,file_size,consumo);

                    casaMaisRecente.addDevice(sd, divisao);
                }
                case "SmartSpeaker" -> {
                    if (divisao == null) throw new CasaInteligenteException("Divisão Não Identificada");

                    String[] campos = linhaPartida[1].split(",");

                    StringBuilder nomesb = new StringBuilder();
                    int rand_num = num.nextInt(999999);
                    nomesb.append("smtspk-").append(rand_num);
                    String nome = nomesb.toString();

                    int volume = Integer.parseInt(campos[0]);
                    String channel = campos[1];
                    String brand = campos[2];
                    float consumo = Float.parseFloat(campos[3]);

                    boolean state = false;
                    if (rand_num%3 == 1) state=true;

                    SmartDevice sd = new SmartSpeaker(nome, state, 0.15f, volume, channel, brand, consumo);

                    casaMaisRecente.addDevice(sd, divisao);
                }
            }
        }
        return lista_casas;
    }

    public CasaInteligente createCasa(String input, HashMap<String,Fornecedor> forns) throws CasaInteligenteException {
        String[] campos = input.split(",");

        String nome = campos[0];
        int nif = Integer.parseInt(campos[1]);
        Fornecedor fornecedor = forns.get(campos[2]);

        Random num = new Random();
        StringBuilder houseid = new StringBuilder();
        int rand_num = num.nextInt(999999);
        houseid.append(nome + "-").append(rand_num);
        String id = houseid.toString();
        
        if (fornecedor == null) throw new CasaInteligenteException("Fornecedor não listado");

        return new CasaInteligente(id, nome, nif,fornecedor);
    }

    public HashMap<String,Fornecedor> energyConfig() throws FileNotFoundException, FornecedorException {

        List<String> linhas = lerFicheiro();
        String[] linhaPartida;
        HashMap<String,Fornecedor> fornecedores = new HashMap<>();

        Random num= new Random();

        for (String linha : linhas) {
            linhaPartida = linha.split(":", 2);
            if (linhaPartida[0].equals("Fornecedor")) {
                if (!fornecedores.containsKey(linhaPartida[1])) {
                    Fornecedor fn = null;
                    String nome_fornecedor = linhaPartida[1];
                    int num_random = num.nextInt(3);

                    switch (num_random) {
                        case (0) -> fn = new FornecedorA(nome_fornecedor, 1, 10, 5);
                        case (1) -> fn = new FornecedorB(nome_fornecedor, 2, 15, 10);
                        case (2) -> fn = new FornecedorC(nome_fornecedor, 3, 10, 15);
                    }
                    fornecedores.put(nome_fornecedor, fn);
                }
            }
        }
        return  fornecedores;
    }

    public List<String> lerFicheiro() throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        try {
            Scanner sc = new Scanner(this.configfile);
            String file_line;
            while (sc.hasNextLine()){
                file_line= sc.nextLine();
                lines.add(file_line);
            }
        } catch (FileNotFoundException e) {
           throw  new FileNotFoundException("An error occurred. File not located or not open correctly");
        }

        return lines;
    }

    public Set<String> casas() throws CasaInteligenteException, ResolutionException, SmartBulbException, SmartSpeakerException, SmartCameraException, SmartDeviceException, FornecedorException, FileNotFoundException {return housesConfig().keySet();}

    public CasaInteligente getCasaInteligente(String housename) throws CasaInteligenteException, ResolutionException, SmartBulbException, SmartSpeakerException, SmartCameraException, SmartDeviceException, FornecedorException, FileNotFoundException {
        return housesConfig().get(housename).clone();
    }

    public Fornecedor getFornecedor(String housename) throws FileNotFoundException, FornecedorException {
        return energyConfig().get(housename);
    }

    public int getNumberCasas() throws CasaInteligenteException, ResolutionException, SmartBulbException, SmartSpeakerException, SmartCameraException, SmartDeviceException, FornecedorException, FileNotFoundException {
        return housesConfig().keySet().size();
    }

}
