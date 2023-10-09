
import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.util.*;
import java.util.stream.Collectors;

public class TextUI {

    private Client cliente;
    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;


    /**
     * Construtor.
     *
     * Cria os menus e a camada de negócio.
     */
    public TextUI() throws IOException{

        this.menu = new Menu("Login",new String[]{
                "Login",
                "Registo"
        });
        this.menu.setHandler(1, this::login);
        this.menu.setHandler(2, this::criaUser);
        scin = new Scanner(System.in);
        cliente = new Client();
    }

    public void login() throws IOException {
        System.out.println("Username:");
        String user = scin.next();
        System.out.println("Password");
        String pass = scin.next();
        try{
            cliente.authentication(user,pass);
        }
        catch (AuthenticationFailedException e){
            System.out.println("Dados inválidos");
            new TextUI().run();
        }
        Menu menucliente = new Menu("Menu Cliente", new String[]{
                "Mapa das trotinetes",
                "Trotinetes num dado raio",
                "Reservar trotinete",
                "Estacionar trotinete",
                "Recompensas num dado raio",
                "Receber notificações de recompensas",
                "Cancelar notificações"
        },true);
        menucliente.setHandler(1, this::mapaTrotinetes);
        menucliente.setHandler(2, this::trotinetesRaio);
        menucliente.setHandler(3, this::reservarTrotinetes);
        menucliente.setHandler(4, this::estacionarTrotinetes);
        menucliente.setHandler(5, this::recompensasRaio);
        menucliente.setHandler(6, this::recompensas);
        menucliente.setHandler(7, this::cancelRecompensas);
        //scin = new Scanner(System.in);
        menucliente.run();
    }


    public void criaUser() throws IOException{
        System.out.println("Username:");
        String user = scin.next();
        System.out.println("Password");
        String pass = scin.next();
        try {
            cliente.createAccount(user,pass);
        }
        catch (UserAlreadyExistsException e){
            System.out.println("Nome de user já existe");
        }
    }

    public void mapaTrotinetes() throws IOException{
        List<Coordinate> lista = cliente.listTrotinetes();
        if (lista.isEmpty()) System.out.println("Não existem trotinetes disponíveis");
        else System.out.println(lista.toString());

    }

    public void trotinetesRaio() throws IOException {
        System.out.println("Indique o x:");
        int x = scin.nextInt();
        System.out.println("Indique o y:");
        int y = scin.nextInt();
        Coordinate coordinate = new Coordinate(x,y);
        List<Coordinate> cordTrotinetes = cliente.freeScooters(coordinate);
        if (cordTrotinetes.isEmpty()) System.out.println("Não existem trotinetes disponíveis no raio pré-definido");
        else  System.out.println(cordTrotinetes.toString());

    }

    public void reservarTrotinetes() throws IOException {
        System.out.println("Indique o x:");
        int x = scin.nextInt();
        System.out.println("Indique o y:");
        int y = scin.nextInt();
        Coordinate coordinate = new Coordinate(x,y);
        List<Coordinate> cordTrotinetes = cliente.freeScooters(coordinate);
        if (! cordTrotinetes.isEmpty()){
            Reserve reserve = cliente.reserveScooter(coordinate);
            System.out.println(reserve.toString());
        }
        else System.out.println("Não existem trotinetes disponíveis no raio pré-definido");
    }

    public void estacionarTrotinetes() throws IOException {
        System.out.println("Indique o id da reserva:");
        String idReserve = scin.next();
        System.out.println("Indique o x:");
        int x = scin.nextInt();
        System.out.println("Indique o y:");
        int y = scin.nextInt();
        Coordinate coordinate = new Coordinate(x,y);
        TripInfo infoReserve = cliente.parkScooter(idReserve,coordinate);
        if (infoReserve==null) System.out.println("ID Inexistente");
        else System.out.println(infoReserve.toString());
    }

    public void recompensasRaio() throws IOException{
        System.out.println("Indique o x:");
        int x = scin.nextInt();
        System.out.println("Indique o y:");
        int y = scin.nextInt();
        Coordinate coordinate = new Coordinate(x,y);
        Map<Coordinate,Coordinate> cordTrotinetes = cliente.rewardsRaio(coordinate);
        if (cordTrotinetes.isEmpty()) System.out.println("Não existem recompensas disponíveis no raio pré-definido");
        else  System.out.println(cordTrotinetes.toString());

    }

    public void recompensas() throws  IOException{
        System.out.println("Indique o x:");
        int x = scin.nextInt();
        System.out.println("Indique o y:");
        int y = scin.nextInt();
        Coordinate coordinate = new Coordinate(x,y);
        boolean b = cliente.activateRewards(coordinate);
        if (b) System.out.println("Notificações ativadas");
        else System.out.println("Ocorreu um erro");

    }

    public void cancelRecompensas() throws  IOException{
        boolean b = cliente.deactivateRewards();
        if (b) System.out.println("Notificações desativadas");
        else System.out.println("Ocorreu um erro");
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() throws IOException {
        this.menu.run();
        System.out.println("Até breve!...");
    }


}
