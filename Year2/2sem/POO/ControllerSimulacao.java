import java.util.*;

import ErrorHandling.SmartBulbException;

import java.io.*;
import java.time.LocalDate;

public class ControllerSimulacao {
        public static void run(Estado estado) {
            
            Estado old_estado = estado.clone();

            boolean exit = false;
            while(!exit){
                int opcao = -1;
                while(opcao < 0 || opcao > 6) {
                    opcao = Menu.MenuSimulacao(estado);
                }

                switch(opcao) {
                    
                    case 1:
                        LocalDate date = Menu.avancarTempo();
                        if(date.isAfter(estado.getDate()) && !date.isEqual(estado.getDate())) {
                            old_estado = estado.clone();
                            estado.setDate(date);
                        }
                        else {
                            System.out.print("Data inválida\n");
                            Menu.pressEnter();
                        }
                        break;
                        
                    case 2:
                        ControllerMudarEstado.run(estado);
                        break;

                    case 3:
                        ControllerEstatisticas.run(estado, old_estado);
                        break;
                    
                    case 4:
                        ControllerEmitirFaturas.run(old_estado,estado);
                        break;
                    
                    case 5:
                        CasaInteligente casa = Menu.escolherCasa(estado);
                        System.out.print(casa.toString());
                        Menu.pressEnter();
                        break;

                    case 6:
                        Fornecedor forn = Menu.escolherFornecedor(estado);
                        System.out.print(forn);
                        Menu.pressEnter();
                        break;
                    case 0:
                        exit = true;
                        Menu.clearWindow();
                        break;
                        
                }
            }
        }
    }