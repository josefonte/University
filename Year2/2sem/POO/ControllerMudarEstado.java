import java.util.*;

import ErrorHandling.EstadoException;
import ErrorHandling.SmartBulbException;

import java.io.*;

public class ControllerMudarEstado {
        public static void run(Estado estado) {
            
            boolean exit = false;
            boolean errorMessage = false;
            while(!exit){
                int opcao = -1;
                while(opcao < 0 || opcao > 4) {
                    opcao = Menu.menuMudarEstado();
                }
           
                switch(opcao) {
                    
                    case 1:
                        CasaInteligente casa = Menu.escolherCasa(estado);
                        Fornecedor forn1 = Menu.escolherFornecedor(estado, casa);
                        try {estado.mudaFornecedor(casa, forn1.getName());}
                        catch (EstadoException e) {System.out.print(e + "\n");}
                        break;
                        
                    case 2:
                        Fornecedor forn2 = Menu.escolherFornecedor(estado);
                        Menu.menuCriaAlteraFornecedor(estado, forn2, false);
                        break;

                    case 3:
                        ControllerOnOff.run(estado);
                        break;
                    
                    case 0:
                        exit = true;
                        Menu.clearWindow();
                        break;
                        
                }
            }
        }
    }
