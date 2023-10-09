import java.util.*;

import java.io.*;

public class Controller {
    public static void run() {
        
        Estado estado = new Estado();
        
        while(true){
            int opcao = -1;
            while(opcao < 0 || opcao > 4) {
                opcao = Menu.MenuInicial();
            }
       
            switch(opcao) {
                
                case 1:
                    if(!estado.getCasas().isEmpty() && !estado.getFornecedores().isEmpty()) ControllerSimulacao.run(estado);
                    else {
                        System.out.println("Estado inválido, por favor carregue um novo estado");
                        Menu.pressEnter();
                        Menu.clearWindow();
                    }          
                    break;
                    
                case 2:
                    ControllerCriarDados.run(estado);
                    break;
                    
                case 3:
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Introduza o nome do ficheiro objeto que pretende carregar: ");
                    String filename = scanner.nextLine();
                
                    try{estado = estado.carregaEstado(filename);System.out.println("Ficheiros carregados com sucesso!!!\n");}
                    catch (FileNotFoundException e) {System.out.println("Ficheiro não encontrado");}
                    catch (IOException e) {System.out.println("Não foi possivel carregar o Estado");}
                    catch(ClassNotFoundException e) {System.out.println("Erro ao ler para as estruturas de dados");}
                    Menu.pressEnter();
                    break;
                
                case 4:
                    scanner = new Scanner(System.in);
                    System.out.println("Introduza o nome do novo ficheiro .obj que deseja guardar: ");
                    String file_name = scanner.nextLine();

                    try{estado.guardaEstado(file_name);System.out.println("Ficheiros salvos com sucesso!!!\n");}
                    catch (FileNotFoundException e) {System.out.println("Ficheiro não encontrado");}
                    catch (IOException e) {System.out.println("Não foi possivel guardar o Estado");}
                    Menu.pressEnter();
                    break;

                case 0:
                    System.exit(0);
                    break;
                    
            }
        }
    }
}