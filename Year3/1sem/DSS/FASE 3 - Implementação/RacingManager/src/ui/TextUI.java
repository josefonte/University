package ui;
import business.*;

import business.Model;

import java.util.*;

public class TextUI  {
    

    private Model model;
    private Menu menu;
    private  boolean Admin;
    private Scanner is;

    public TextUI() {
        
        this.model = new Model();

        this.menu = new Menu(new String[]{
                "Modo Admin",
                "Modo Jogador",
        });

        this.menu.setHandler(1,this::MenuPrincipal_Admin);
        this.menu.setHandler(2,this::MenuPrincipal_User);


        is = new Scanner(System.in);
    }

    public void MenuPrincipal_User() {

        Menu menuP = new Menu("Menu Principal",new String[]{
                "Jogar",
                "Consultar Circuitos",
                "Consultar Pilotos",
                "Consultar Carros"
        },true);

        menuP.setHandler(1, this::Jogar);
        menuP.setHandler(2, this::InfoCircuito);
        menuP.setHandler(3, this::InfoPiloto);
        menuP.setHandler(4, this::InfoCarro);

        menuP.run();
    }

    public void MenuPrincipal_Admin() {
        // Criar o menu
        Menu menuP = new Menu("Menu Principal",new String[]{
                "Editar Campeonatos",
                "Editar Circuitos",
                "Editar Pilotos",
                "Editar Carros"
        },true);

        menuP.setHandler(1, this::MenuCampeonatos_Admin);
        menuP.setHandler(2, this::MenuCircuitos_Admin);
        menuP.setHandler(3, this::MenuPilotos_Admin);
        menuP.setHandler(4, this::MenuCarros_Admin);
        menuP.run();
    }

    public void MenuCampeonatos_Admin() {
        // Criar o menu
        Menu menuP = new Menu("Menu dos Campeonatos" , new String[]{
                "Consultar Campeonatos",
                "Adicionar Campeonato",
                "Remover Campeonato"
        },true);

        menuP.setHandler(1, this::InfoCampeonato);
        menuP.setHandler(2, this::AdicionarCampeonato);
        menuP.setHandler(3, this::RemoverCampeonato);
        menuP.run();
    }


    public void MenuCircuitos_Admin() {
        // Criar o menu
        Menu menuP = new Menu("Menu dos Circuitos" , new String[]{
                "Consultar Circuitos",
                "Adicionar Circuito",
                "Remover Circuito",
        },true);

        menuP.setHandler(1, this::InfoCircuito);
        menuP.setHandler(2, this::AdicionarCircuito);
        menuP.setHandler(3, this::RemoverCircuito);

        menuP.run();
    }



    public void MenuPilotos_Admin() {
        // Criar o menu
        Menu menuP = new Menu("Menu dos Pilotos" , new String[]{
                "Consultar Pilotos",
                "Adicionar Piloto",
                "Remover Piloto"
        },true);

        menuP.setHandler(1, this::InfoPiloto);
        menuP.setHandler(2, this::AdicionarPiloto);
        menuP.setHandler(3, this::RemoverPiloto);
        menuP.run();
    }


    public void MenuCarros_Admin() {

        Menu menuP = new Menu("Menu de Carros",new String[]{
                "Consultar Carros",
                "Adicionar Carro",
                "Remover Carro",
        },true);

        menuP.setHandler(1, this::InfoCarro);
        menuP.setHandler(2, this::AdicionarCarro);
        menuP.setHandler(3, this::RemoverCarro);

        menuP.run();
    }

    // ###################### METODOS AUXILIARES ##############################

    public void Jogar() {
        ListaCampeonatos();
        System.out.print("Escolha o campoenato:");
        String aux = is.nextLine();
        while (!model.getCampeonatos().containsKey(aux)){
            System.out.println("Campoenato inexistente ");
            System.out.print("Escolha o campoenato:");
            aux = is.nextLine();
        }
        Campeonato campeonato = model.getCampeonato(aux);
        System.out.println(campeonato);

        int afinacoesTotais = (int)((campeonato.getCorridas().size()*2)/3);
        aux = is.nextLine();

        int bot=0;

        Map<String,Participante> parts = new HashMap<>();
        while(true){
            System.out.println("# Adicionar Participante # ");

            Participante part = new Participante();
            part.setTempos(new ArrayList<>());
            part.setAfinacoesRestantes(afinacoesTotais);
            System.out.print("Tipo de Participante (Jogador,Convidado,Bot, STOP-cancelar): ");
            String tipo = is.nextLine();

            if (tipo.equals("STOP") || tipo.equals("stop")) break;
            else if(tipo.equals("BOT")){
                bot++;
                part.setPiloto(model.getPilotos().values().stream().toList().get(0));
                part.setCarro(model.getCarros().values().stream().toList().get(0));
                part.setUtilizador(new Utilizador("BOT"+ bot, TipoUtilizador.BOT));
                parts.put(part.getNome(),part);
                campeonato.addParticipante(part);
            }else {
                String car_nome, piloto_nome;

                if (!ListaCarros()) {System.out.println("Crie carros para prossegir");return;}
                do {
                    System.out.println("Escolha carro: ");
                    car_nome = is.nextLine();
                }while(!model.getCarros().containsKey(car_nome));

                part.setCarro(model.getCarro(car_nome));

                if (!ListaPilotos()) {System.out.println("Crie pilotos para prossegir");return;}
                do {
                    System.out.println("Escolha piloto: ");
                    piloto_nome = is.nextLine();
                }while(!model.getPilotos().containsKey(piloto_nome));

                part.setPiloto(model.getPiloto(piloto_nome));

                if(tipo.equals("Convidado") || tipo.equals("convidado")) {
                    System.out.print("Nome do Convidado: ");
                    String nome = is.nextLine();
                    part.setUtilizador(new Utilizador(nome, TipoUtilizador.CONVIDADO));
                    parts.put(part.getNome(),part);
                    campeonato.addParticipante(part);
                }
                else if (tipo.equals("Jogador") || tipo.equals("jogador")){
                    System.out.print("Nome do Jogador: ");
                    String nome = is.nextLine();
                    part.setUtilizador(new Utilizador(nome , TipoUtilizador.JOGADOR));
                    parts.put(part.getNome(),part);
                    campeonato.addParticipante(part);
                }
            }
        }

        campeonato.addParticipantes2Corridas(parts);

        for(Corrida corrida : campeonato.getCorridas()){
            System.out.println("Pretende Simular a Corrida - " + corrida.getCircuito().getNomeCircuito());
            campeonato.simularCorrida();
            System.out.println("# Tempos da Corrida" + corrida.listaClacificacao());
        }
        List<Participante> resultadoCampeonato = campeonato.classificacaoFinal();
        System.out.println("RESULTADOS FINAIS" + resultadoCampeonato);
    }

    public boolean ListaCampeonatos(){

        System.out.println("\n# Lista de Campeonatos #");
        if( model.getCampeonatos().isEmpty()) {System.out.println("Lista Vazia");return false;}
        for (String camp : model.infoCampeonatos(false)) System.out.println(camp);
        return true;
    }

    public void InfoCampeonato(){

        if (!ListaCampeonatos()) return;
        String nome = "";
        is = new Scanner(System.in);
        do {
            System.out.print("\nCampeonato em Detalhe [Nome?/STOP] ");
            nome = is.nextLine();
            if (!model.getCircuitos().containsKey(nome)) System.out.println("Campeonato não existe");
            else System.out.println(model.getCircuito(nome));
        } while (!nome.equals("STOP") && !nome.equals("stop"));

    }

    public void AdicionarCampeonato(){

        is = new Scanner(System.in);
        System.out.print("Nome do Campeonato: ");
        String nome = is.nextLine();
        Campeonato campeonato = new Campeonato();

        while(model.getCampeonatos().containsKey(nome)){
                System.out.println("Nome do Campeonato já existe");
                System.out.print("Nome do Campeonato: ");
                nome = is.nextLine();
        }

        campeonato.setNomeCampeonato(nome);

        System.out.print("Categoria (C1-C2-GT-SC): ");
        String cat = is.nextLine();

        TipoCampeonato categoria = switch (cat) {
                case "C1", "c1" -> TipoCampeonato.C1;
                case "C2", "c2" -> TipoCampeonato.C2;
                case "GT", "gt" ->  TipoCampeonato.GT;
                case "SC","sc"->  TipoCampeonato.SC;
                default -> TipoCampeonato.C1;
        };
        int escolha = 0;

        do{
                Map<String,Participante> parts = new HashMap<>();
                System.out.println("## Adicionar Corridas ##");

                if (!ListaCircuitos()) {System.out.println("Crie Circuitos para prossegir");return;}

                System.out.print("Escolher Circuito: ");
                String circ = is.nextLine();
                while(!model.getCircuitos().containsKey(circ)){
                    System.out.println("# Circuito não existe");
                    System.out.print("Escolher Circuito: ");
                    circ = is.nextLine();
                }
                Circuito circuito = model.getCircuito(circ);

                System.out.print("Escolher Clima (0-Seco | 1-Chuva): ");
                int clima = is.nextInt();

                while(clima != 0 && clima !=1){
                    System.out.println("# Opção não existe");
                    System.out.print("Escolher Clima (0-Seco | 1-Chuva): ");
                    clima = is.nextInt();
                }

                System.out.print("Número Total de Voltas: ");
                int voltas = is.nextInt();

                Corrida corrida = new Corrida(circuito,parts,clima,voltas);
                campeonato.addCorrida(corrida);

                System.out.print("Adicionar outro Circuito (1-Sim | 0-Não) : ");
                escolha = is.nextInt();
            }while (escolha == 1);

            model.addCampeonatos(campeonato);

        System.out.print("Campeonato Adicionado com Sucesso - ");
        System.out.println(model.getCampeonato(campeonato.getNomeCampeonato()));

    }

    public void RemoverCampeonato(){

        ListaCampeonatos();
        System.out.print("Campeonato a Remover: [Nome?]");
        is = new Scanner(System.in);
        String nome = is.nextLine();
        if (!model.removeCampeonato(nome)) System.out.println("Campeonato não existe");
        else System.out.println("Campeonato Eliminado com Sucesso");
    }

    public boolean ListaCircuitos(){

        System.out.println("\n# Lista de Circuitos #");
        if( model.getCircuitos().isEmpty()) {System.out.println("Lista Vazia");return false;}
        for (String camp : model.infoCircuitos(false)) System.out.println(camp);
        return true;
    }

    public void InfoCircuito(){

        if(!ListaCircuitos()) return;
        String nome = "";
        is = new Scanner(System.in);

        do {
            System.out.print("\nCircuito em Detalhe :[Nome?/STOP]");
            nome = is.nextLine();

            if (!model.getCircuitos().containsKey(nome)) System.out.println("Circuito não existe");
            else System.out.println(model.getCircuito(nome));
        } while (!nome.equals("STOP") && !nome.equals("stop"));


    }

    public void AdicionarCircuito(){
            is = new Scanner(System.in);
            System.out.print("Nome do Circuito: ");
            String aux = is.nextLine();

            while(model.getCircuitos().containsKey(aux)){
                System.out.println("Nome do Circuito já existe");
                System.out.print("Nome do Circuito: ");
                aux = is.nextLine();
            }
            String nome = aux;

            System.out.print("Distância do Circuito: ");
            float dist = is.nextFloat();

            ArrayList<SegmentoDePista> segmentos = new ArrayList<>();
            is.nextLine();
            while(true) {
                System.out.print("Segmentos (Reta-Curva-Chicane/STOP) ");
                aux = is.nextLine();
                if(aux.equals("STOP")||aux.equals("stop")) break;
                TipoSegmento s = switch (aux) {
                    case "Reta", "reta" -> TipoSegmento.RETA ;
                    case "Curva", "curva" -> TipoSegmento.CURVA ;
                    case "Chicane", "chicane" -> TipoSegmento.CHICANE ;
                    default -> TipoSegmento.RETA;
                };
                segmentos.add(new SegmentoDePista(0,0,s));
            }

            Circuito circuito = new Circuito(dist,nome,segmentos);
            circuito.constroiCircuito();
            model.addCircuito(circuito);

        System.out.print("Circuito Adicionado com Sucesso - ");
        System.out.println(model.getCircuito(circuito.getNomeCircuito()));

    }

    public void RemoverCircuito(){

            ListaCircuitos();
            System.out.print("Circuito a Remover: [Nome?]");
            is = new Scanner(System.in);
            String nome = is.nextLine();
            if (!model.removeCircuito(nome)) System.out.println("Circuito não Existe");
            else System.out.println("Circuito Eliminado com Sucesso");

    }

    public boolean ListaPilotos(){

        System.out.println("\n## Lista de Pilotos #");
        if( model.getPilotos().isEmpty()) {System.out.println("Lista Vazia");return false;}
        for (String pil : model.infoPilotos(false)) System.out.println(pil);

        return true;
    }

    public void InfoPiloto(){
        is = new Scanner(System.in);
        if(!ListaPilotos()) return;
        String nome = "";

        do {
            System.out.print("\nPiloto em Detalhe : [Nome?/STOP] ");
            nome = is.nextLine();
            if (!model.getPilotos().containsKey(nome)) System.out.println("Piloto não existe");
            else System.out.println(model.getPiloto(nome));
        } while (!nome.equals("STOP") && !nome.equals("stop"));
    }
    public void AdicionarPiloto(){

            is = new Scanner(System.in);
            System.out.print("Nome do Piloto: ");
            String nome = is.nextLine();

            while(model.getPilotos().containsKey(nome)){
                System.out.println("Nome do Piloto já existe");
                System.out.print("Nome do Piloto: ");
                nome = is.nextLine();
            }

            System.out.print("Ratio Suave vs Agressivo (0-100): ");
            int sva = is.nextInt();

            while(sva>100 || sva<0){
                System.out.println("Ratio SVA errado");
                System.out.print("Ratio Suave vs Agressivo (0-100): ");
                sva = is.nextInt();
            }

            System.out.print("Ratio Tempo Chuvoso vs Tempo Seco (0-100): ");
            int cts = is.nextInt();

            while(cts>100 || cts<0){
                System.out.println("Ratio CTS errado");
                System.out.print("Ratio Tempo Chuvoso vs Tempo Seco (0-100): ");
                cts = is.nextInt();
            }

            Piloto piloto = new Piloto(nome,sva,cts);
            model.addPiloto(piloto);


        System.out.print("Piloto Adicionado com Sucesso - ");
        System.out.println(model.getPiloto(piloto.getNome()));
    }

    public void RemoverPiloto(){

            ListaCircuitos();
            is = new Scanner(System.in);
            System.out.print("Piloto a Remover: [Nome?] ");
            String nome = is.nextLine();
            if (!model.removePiloto(nome)) System.out.println("Piloto não Existe");
            else System.out.println("# PILOTO REMOVIDO COM SUCESSO");
    }


    public boolean ListaCarros(){

        System.out.println("\n# Lista de Carros #");
        if( model.getCarros().isEmpty()) {System.out.println("Lista Vazia");return false;}
        for (Carro carro : model.getCarros().values()){
            System.out.println(carro.toSimpleString());
        }
        return true;
    }

    public void InfoCarro(){
            is = new Scanner(System.in);
            if(!ListaCarros()) return;
            String nome = "";

        do {
            System.out.print("\nCarro em Detalhe  : [Id?/STOP] ");
            nome = is.nextLine();
            if (!model.getCarros().containsKey(nome)) System.out.println("Carro não existe");
            else System.out.println(model.getCarro(nome));
        } while (!nome.equals("STOP") && !nome.equals("stop"));
    }

    public void AdicionarCarro(){

            is = new Scanner(System.in);
            System.out.println("Categoria (C1-C2-GT-SC): ");
            String cat = is.nextLine();

            String categoria = switch (cat) {
                case "C1", "c1" -> "C1";
                case "C2", "c2" -> "C2";
                case "GT", "gt" -> "GT";
                case "SC","sc"-> "SC";
                default -> "C1";
            };

            System.out.print("id: ");
            String id = is.nextLine();

            System.out.print("Marca: ");
            String marca = is.nextLine();
            System.out.print("Modelo: ");
            String modelo = is.nextLine();
            int cilindrada;

            if (categoria.equals("C1")){
                cilindrada = 6000;
            } else if (categoria.equals("C2")) {
                System.out.print("Cilindrada (entre 3000 e 5000) : ");
                int i = is.nextInt();
                while(i<3000 || i>5000){
                    System.out.println("Número incorreto ");
                    System.out.print("Cilindrada (entre 3000 e 5000): ");
                    i = is.nextInt();
                }
                cilindrada = i;
            } else if (categoria.equals("GT")){
                System.out.print("Cilindrada (entre 2000 e 4000): ");
                int i = is.nextInt();
                while(i<2000 || i>4000){
                    System.out.println("Número incorreto ");
                    System.out.print("Cilindrada (entre 2000 e 4000): ");
                    i = is.nextInt();
                }
                cilindrada = i;
            } else{
                cilindrada = 2500;
            }
            int potencia;
            int potenciaH = 0;
            if (categoria.equals("C1") || categoria.equals("C2") || categoria.equals("GT")){
                System.out.print("Potencia Híbrida: (0 - se não for um hibrido)");
                potenciaH = is.nextInt();
            }
            System.out.print("Potencia: ");
            potencia = is.nextInt();

            float fiabilidade = 0;

            System.out.print("PAC (0-100): ");
            int i = is.nextInt();
            while(i<0 || i>100){
                System.out.println("Número incoreto ");
                System.out.print("PAC (0-100): ");
                i = is.nextInt();
            }
            int pac = i;
            is.nextLine();

            System.out.print("Pneus (Macio-Duro-Chuva): ");
            String p = is.nextLine();

            TipoPneus pneus = switch (p){//TipoPneus pneus = switch (p) {
                case "MACIO", "macio", "Macio" -> TipoPneus.MACIO;
                case "DURO", "duro", "Duro" -> TipoPneus.DURO;
                case "CHUVA", "chuva", "Chuva" -> TipoPneus.CHUVA;
                default -> TipoPneus.MACIO;
            };

            System.out.print("Modo Motor (Conservador-Normal-Agressivo): ");
            String mot = is.nextLine();
            ModoMotor motor = switch (mot) {//ModoMotor motor =
                case "CONSERVADOR", "conservador", "Conservador" -> ModoMotor.CONSERVADOR;
                case "NORMAL", "normal", "Normal" -> ModoMotor.NORMAL;
                case "AGRESSIVO", "agressivo", "Agressivo" -> ModoMotor.AGRESSIVO;
                default -> ModoMotor.NORMAL;
            };
            int taxa = 0;
            if (categoria.equals("GT")) {
                System.out.println("Taxa de Deteorização: ");
                taxa = is.nextInt();
            }
            Carro carro = null;
            switch (categoria) {
                case "C1" -> carro = new C1(marca,  modelo, cilindrada, potencia, fiabilidade, pac, id, potenciaH);
                case "C2"-> carro = new C2(marca,  modelo, cilindrada, potencia, fiabilidade, pac, id, potenciaH);
                case "GT" -> carro = new GT(marca,  modelo, cilindrada, potencia, fiabilidade, pac, id,potenciaH, taxa);
                case "SC" -> carro = new SC(marca,  modelo, cilindrada, potencia, fiabilidade, pac, id);
                default -> new C1(marca,  modelo, cilindrada, potencia, fiabilidade, pac, id, potenciaH);
            }

            assert carro != null;
            carro.setPneus(pneus);
            carro.calculaFiabilidade();

            model.addCarro(carro);


            System.out.println(model.getCarro(carro.getId()));
            System.out.println("Carro Adicionado Com Sucesso");


    }

    public void RemoverCarro(){

        ListaCarros();
        System.out.print("Carro a Remover: [Id?]");
        is = new Scanner(System.in);
        String nome = is.nextLine();
        if (!model.removeCarro(nome)) System.out.println("Carro não Existe");
        else System.out.println("Carro Eliminado com Sucesso");

    }


    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        this.menu.run();
        System.out.println("\nPrograma Encerrado. Até breve!...");
    }
}
