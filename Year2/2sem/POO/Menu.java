import java.util.AbstractMap;
import java.util.Scanner;

import ErrorHandling.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Menu {
    public static int MenuInicial() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------MENU INICIAL-----------\n\n");
        sb.append("1) Simulação.\n");
        sb.append("2) Criar/Alterar Estado.\n");
        sb.append("3) Carregar Estado.\n");
        sb.append("4) Guardar Estado.\n");
        sb.append("0) Sair.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    
    public static String pressEnter(){
        System.out.println("Pressione enter para continuar...");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    
    //Tentar encontrar outra forma mais elegante
    public static void clearWindow() {
        
        for (int i = 0;i<100;i++){
            System.out.println();
        }
    }


    public static int MenuCriarEstado() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------Menu Criar Estado -----------\n\n");
        sb.append("1) Criar nova Casa.\n");
        sb.append("2) Criar novo Dispositivo.\n");
        sb.append("3) Criar novo Fornecedor.\n");
        sb.append("4) Carregar ficheiro csv.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }


    public static int MenuSimulacao(Estado current) {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------Menu Simulação -----------\n\n");
        sb.append("Data atual: " + current.getDate() + "\n\n");
        sb.append("1) Avançar no tempo.\n");
        sb.append("2) Mudar Estado.\n");
        sb.append("3) Estatísticas.\n");
        sb.append("4) Emitir Faturas.\n");
        sb.append("5) Visualizar casa.\n");
        sb.append("6) Visualizar Fornecedor.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static CasaInteligente MenuCriarCasa(Estado estado) {
        clearWindow();
        
        CasaInteligente smarthouse = new CasaInteligente();
        

        System.out.print("-----------Menu Criar Casa -----------\n\n");
                
        
        Scanner scanner = new Scanner(System.in);
        boolean i;
        String owner;

        do {
            System.out.print("Defina o proprietário da casa: ");
            owner = scanner.nextLine();
        
            try { smarthouse.setOwner(owner); i=false;}
            catch (CasaInteligenteException e) {System.out.print(e + "\n"); i = true;}
        } while(i);
        
        Random num = new Random();
        StringBuilder houseid = new StringBuilder();
        int rand_num = num.nextInt(999999);
        houseid.append(owner + "-").append(rand_num);
        String id = houseid.toString();
        smarthouse.setID(id);

        do {
            System.out.print("Defina o NIF do proprietário: ");
            int nif = scanner.nextInt();
        
            try { smarthouse.setNif(nif); i=false;}
            catch (CasaInteligenteException e) {System.out.print(e + "\n"); i = true;}
        } while(i);
        
        boolean continuar = true;    

        while(continuar) {

            do {
                String room = scanner.nextLine();
                System.out.println("Escolha a divisão que pretende criar: ");
                room = scanner.nextLine();

                try {smarthouse.addRoom(room); i = false;}
                catch (CasaInteligenteException e) {System.out.print(e + "\n"); i = true;}
            } while(i);
            
            
            System.out.println("Pretende criar mais divisões?");
            System.out.println("1) Sim");
            System.out.println("2) Não");
            int opcao = scanner.nextInt();

            if(opcao == 2) continuar = false;

        }
            return smarthouse;
    }

    public static Fornecedor escolherFornecedor(Estado estado, CasaInteligente casa) {
        
    
            System.out.print("Lista de Fornecedores: \n");      
 
            Scanner scanner = new Scanner(System.in);

            HashMap<String,Fornecedor> l = estado.getFornecedores();

            for(String name: l.keySet()) {
                String key = name.toString();
                System.out.println(" - " + key);
            }

        boolean i;
        String option;
        
        do { 
            
            System.out.println("Escreva o nome do Fornecedor pretendido: ");
            option = scanner.nextLine();

                    
            if(l.containsKey(option)) {
                i=false;
            }

            else {
                i=true;
                System.out.println("Fornecedor inválido, tente novamente");
                Menu.pressEnter();
            }
        } while(i);

        return l.get(option);
    }
    

    public static Fornecedor menuCriaAlteraFornecedor(Estado estado, Fornecedor forn, boolean criar) {
                
        Scanner scanner = new Scanner(System.in);
        String name;
        Float valor_base, imposto, desconto;
        boolean i; 

        if(criar) {
            do { 
                System.out.print("Introduza o nome do Fornecedor: ");
                name = scanner.nextLine();
                        
                try { forn.setName(name); i=false;}
                catch (FornecedorException e) {System.out.print(e + "\n"); i = true;}
            }   while(i); 
        }

        else name = forn.getName();
        
        do {
            System.out.print("Defina o preço de energia (€ por kwh): ");
            valor_base = scanner.nextFloat();
        
            try { forn.setValor_base(valor_base); i=false;}
            catch (FornecedorException e) {System.out.print(e + "\n"); i = true;}
        } while(i);
        
        
        do {
            System.out.print("Defina o imposto: ");
            imposto = scanner.nextFloat();
        
            try { forn.setImposto(imposto); i=false;}
            catch (FornecedorException e) {System.out.print(e + "\n"); i = true;}
        } while(i);

        do {
            System.out.print("Defina o desconto: ");
            desconto = scanner.nextFloat();
            System.out.print("\n");

            try { forn.setDesconto(desconto); i=false;}
            catch (FornecedorException e) {System.out.print(e + "\n"); i = true;}
        
        } while(i);


        if(!criar) {
            try { estado.mudaValoresForn(name, valor_base, imposto, desconto); i=false;}
            catch (FornecedorException | CasaInteligenteException | EstadoException e) {System.out.print(e + "\n"); i = true;}
        }

        else {
            try {estado.adicionaFornecedor(forn);}
            catch(EstadoException e) {System.out.print(e + "\n"); i=true;}
            }

       return forn;
            
    }

    public static DeviceType escolherDispositivo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Selecione o Dispositivo que pretende criar.\n\n");
        sb.append("1) SmartBulb\n");
        sb.append("2) SmartCamera\n");
        sb.append("3) SmartSpeaker\n");
        sb.append("\nIntroduza a opção pretendida: ");

        System.out.println(sb.toString());

        Scanner scanner = new Scanner(System.in);
        int opção = scanner.nextInt();

        switch(opção) {
            case 1: return DeviceType.SmartBulb;
            case 2: return DeviceType.SmartCamera;
            case 3: return DeviceType.SmartSpeaker;
            default: return null; 
        }
    }

    public static SmartBulb menuSmartBulb() {
        SmartBulb smartbulb = null;
        
        Random num= new Random();
        StringBuilder nomesb = new StringBuilder();
        int rand_num = num.nextInt(999999);
        nomesb.append("smtblb-").append(rand_num);
        String nome= nomesb.toString();

        StringBuilder sb = new StringBuilder();
        
        System.out.print("------------Menu SmartBulb---------\n\n");
        
        boolean i;
        do {
        sb.append("Opções de tonalidade: \n");
        sb.append("0) Cold\n");
        sb.append("1) Neutral\n");
        sb.append("2) Warm\n");
        sb.append("Escolha uma opção: ");

        System.out.println(sb.toString());

        Scanner scanner = new Scanner(System.in);
        int tone = scanner.nextInt();

        
        System.out.print("Defina a dimensão (float): ");
        float dimensao = scanner.nextFloat();
 
        System.out.print("Defina o valor fixo (inteiro): ");
        int valor_fixo = scanner.nextInt();
  
        System.out.print("Pretende deixar o dispositivo ligado?\n");
        System.out.print("1) Sim\n");
        System.out.print("2) Não\n");
        int opcao = scanner.nextInt();
        boolean bool = false;
        if (opcao == 1) bool = true;

        System.out.print("Defina o custo de instalação: ");
        Float cust_inst = scanner.nextFloat();

        try {smartbulb = new SmartBulb(nome, bool, cust_inst, tone, dimensao, valor_fixo); i=false;}
        catch (SmartBulbException e) {System.out.print(e + "\n"); i=true;}
        catch (SmartDeviceException e) {System.out.print(e + "\n"); i=true;}
        } while(i);   
        
        return smartbulb;

    } 

    public static SmartCamera menuSmartCamera() {
        
        SmartCamera smartcamera = null;
        Resolution res = new Resolution();
        
        Random num= new Random();
        StringBuilder nomesb = new StringBuilder();
        int rand_num = num.nextInt(999999);
        nomesb.append("smtcam-").append(rand_num);
        String nome= nomesb.toString();

        StringBuilder sb = new StringBuilder();
        
        System.out.println("------------Menu Smart Camera---------\n\n");
        
        Scanner scanner = new Scanner(System.in);
        boolean i;

        do {
        System.out.println("Defina a resolução da camera.");
        System.out.print("Defina a largura (width): ");
        float width = scanner.nextFloat();
        res.setWidth(width);

        System.out.print("Defina a altura (height): ");
        float height = scanner.nextFloat();
        res.setHeight(height);

        System.out.print("Defina o tamanho do ficheiro (float): ");
        float file_size = scanner.nextFloat();
         
        System.out.print("Pretende deixar o dispositivo ligado?\n");
        System.out.print("1) Sim\n");
        System.out.print("2) Não\n");
        int opcao = scanner.nextInt();
        boolean bool = false;
        if (opcao == 1) bool = true;

        System.out.print("Defina o custo de instalação: ");
        Float cust_inst = scanner.nextFloat();
        
        float consumo = width * height * file_size / 10000;

        try {smartcamera = new SmartCamera(nome, bool, cust_inst, res, file_size, consumo); i=false;}
        catch (SmartCameraException e) {System.out.print(e + "\n"); i=true;}
        catch (SmartDeviceException e) {System.out.print(e + "\n"); i=true;}
        catch (ResolutionException e) {System.out.print(e + "\n"); i=true;}
        } while(i);   

        return smartcamera;
    }
    
    public static SmartSpeaker menuSmartSpeaker() {
        
        SmartSpeaker smartspeaker = null;

        Random num= new Random();
        StringBuilder nomesb = new StringBuilder();
        int rand_num = num.nextInt(999999);
        nomesb.append("smtspk-").append(rand_num);
        String id= nomesb.toString();
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("------------Menu Smart Speaker---------\n\n");
        System.out.println(sb.toString());

        boolean i;
        do {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Defina o volume do Speaker: ");
        int volume = scanner.nextInt();
       

        System.out.print("Defina a rádio online que está a tocar: ");
        String channel = scanner.nextLine();     
        channel = scanner.nextLine();

        System.out.print("Defina a marca do Speaker: ");
        String brand = scanner.nextLine();
        
        System.out.print("Defina o consumo da respetiva marca: ");
        Float brand_comsuption = scanner.nextFloat();

        System.out.print("Pretende deixar o dispositivo ligado?\n");
        System.out.print("1) Sim\n");
        System.out.print("2) Não\n");
        int opcao = scanner.nextInt();
        boolean bool = false;
        if (opcao == 1) bool = true;

        System.out.print("Defina o custo de instalação: ");
        Float cust_inst = scanner.nextFloat();

        try {smartspeaker = new SmartSpeaker(id, bool, cust_inst, volume, channel, brand, brand_comsuption); i=false;}
        catch (SmartSpeakerException e) {System.out.print(e + "\n"); i=true;}
        catch (SmartDeviceException e) {System.out.print(e + "\n"); i=true;}
        } while(i);   
        
        return smartspeaker;
    }
    
    public static int menuMudarEstado() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------MENU MUDAR ESTADO-----------\n\n");
        sb.append("1) Mudar de Fornecedor de uma Casa.\n");
        sb.append("2) Mudar Valores de um Fornecedor.\n");
        sb.append("3) Ligar e Desligar Dispositivos de uma Casa.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static int menuLigarDesligar() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------Menu ON/OFF-----------\n\n");
        sb.append("1) ON/OFF Dispositivo.\n");
        sb.append("2) ON/OFF Todos os Dispositivos da Divisão.\n");
        sb.append("3) ON/OFF Todos os Dispositivos da Casa.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static CasaInteligente escolherCasa(Estado estado) {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Lista de casas: ");

        HashMap<String,CasaInteligente> l = estado.getCasas();
        
        for(String name: l.keySet()) {  
            System.out.println(" - " + name);
        }

        String id;
        boolean continuar;
        
        do {
            System.out.println("Escolha o id da casa: ");
            id = scanner.nextLine();        
            if(!l.containsKey(id)) {continuar = true; System.out.println("ID inválido, tente novamente");}
            else continuar = false;
        } while (continuar);

        return l.get(id);
        
    }

    public static String escolherDivisão(CasaInteligente casa) {
        
        Scanner scanner = new Scanner(System.in);
        
        Map<String, List<String>> locations = casa.getLocations();

        for(String name: locations.keySet()) {  
            System.out.println(" - " + name);
        }
        
        boolean continuar;
        String divisao;

        do {
            System.out.println("Escolha a divisão da casa: ");
            divisao = scanner.nextLine();        
            if(!casa.hasRoom(divisao)) {continuar = true; System.out.println("Divisão inválida, tente novamente");}
            else continuar = false;
        } while (continuar);

        return divisao;
        
    }

    public static void ONOFFDispositivo(Estado estado, CasaInteligente casa, String room) {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Lista de dispositivos na divisão: ");

        List<SmartDevice> l = casa.getDevicesinRoom(room);
        
        for(SmartDevice name: l) {  
            System.out.println(" - " + name.getID());
        }

        String device_id;
        boolean continuar;
        SmartDevice smtd = null;
        
        do {
            System.out.println("Escolha o id do dispositivo: ");
            device_id = scanner.nextLine();        
            
            try {smtd = casa.getDevice(device_id); continuar = false;}
            catch (CasaInteligenteException e) {System.out.println(e + "\n"); continuar = true;}
        
        } while (continuar);

        StringBuilder sb = new StringBuilder("O que pretende fazer com o dispositivo " + device_id + "\n\n");
        sb.append("1) Ligar.\n");
        sb.append("2) Desligar.\n");
        System.out.println(sb.toString());
        int option = scanner.nextInt();

        switch(option) {
            case 1:
                try {estado.turnDeviceON(casa, smtd.getID());}
                catch (EstadoException | CasaInteligenteException e) {System.out.print(e + "\n");}
                break;
            case 2:
                try {estado.turnDeviceOFF(casa, smtd.getID());}
                catch (EstadoException | CasaInteligenteException e) {System.out.print(e + "\n");}
                break;
        }
    }

    public static void ONOFFDivisão(Estado estado, CasaInteligente casa, String room) {
        
        Scanner scanner = new Scanner(System.in);

        StringBuilder sb = new StringBuilder("O que pretende fazer com os dispositivos da divisão " + room + "\n\n");
        sb.append("1) Ligar todos os dispositivos da divisão.\n");
        sb.append("2) Desligar todos os dispositivos da divisão.\n");
        System.out.println(sb.toString());
        int option = scanner.nextInt();

        switch(option) {
            case 1:
                try {estado.turnRoomOn(casa, room);}
                catch (CasaInteligenteException | EstadoException e) {System.out.println(e + "\n");}
                break;
            case 2:
                try {estado.turnRoomOFF(casa, room);}
                catch (CasaInteligenteException | EstadoException e) {System.out.println(e + "\n");}
                break;
        }
    }

    public static void ONOFFCasa(Estado estado, CasaInteligente casa) {
        
        Scanner scanner = new Scanner(System.in);

        StringBuilder sb = new StringBuilder("O que pretende fazer com os dispositivos da casa \n\n");
        sb.append("1) Ligar tudo.\n");
        sb.append("2) Desligar tudo.\n");
        System.out.println(sb.toString());
        int option = scanner.nextInt();

        switch(option) {
            case 1:
                try {estado.turnALLON(casa);}
                catch (CasaInteligenteException | EstadoException e) {System.out.println(e + "\n");}
                break;
            case 2:
                try {estado.turnALLOFF(casa);;}
                catch (CasaInteligenteException | EstadoException e) {System.out.println(e + "\n");}
                break;
        }
    }
     
    public static Fornecedor escolherFornecedor(Estado estado) {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Lista de fornecedores: ");

        HashMap<String,Fornecedor> l = estado.getFornecedores();
        
        for(String name: l.keySet()) {  
            System.out.println(" - " + name);
        }

        String forn;
        boolean continuar;
        
        do {
            System.out.println("Escolha um fornecedor: ");
            forn = scanner.nextLine();        
            
            if(l.containsKey(forn)) {continuar = false;}
            else {System.out.println("Fornecedor inválido, tente novamente"); continuar = true;}
        
        } while (continuar);

        return l.get(forn);
    }


    public static LocalDate avancarTempo() {
        
        int dia,mes,ano;
        LocalDate date = null;
        Scanner scanner = new Scanner(System.in);
        boolean i = false;
        
        do {
            System.out.println("Escolha a data a que pretende avançar. ");
            System.out.println("Dia: ");
            dia = scanner.nextInt();
        
            System.out.println("Mês: ");
            mes = scanner.nextInt();
            
            System.out.println("Ano: ");
            ano = scanner.nextInt();
            
            try {date = LocalDate.of(ano, mes, dia); i=false;}
            catch (DateTimeException e) {System.out.print(e + "\n"); i=true;}
        } while(i);

        
        return date;
    }


    public static int menuEstatisticas() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------MENU ESTATISTICAS-----------\n\n");
        sb.append("1) Casa que mais gastou.\n");
        sb.append("2) Comercializador com maior volume de faturação.\n");
        sb.append("3) Casas com maior consumo.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static int menuEmitirFaturas() {
        clearWindow();
        StringBuilder sb = new StringBuilder("-----------MENU EMITIR FATURAS-----------\n\n");
        sb.append("1) Todas as faturas por fornecedor.\n");
        sb.append("2) Emitir fatura de uma casa.\n");
        sb.append("3) Emitir faturas de todas as casas.\n");
        sb.append("0) Retroceder.\n\n");
        sb.append("Selecione a opção pretendida: ");
        System.out.println(sb.toString());
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }


    public static int escolherNtopCasas() {
        System.out.print("Escolha o top N de casa: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }


    

}




