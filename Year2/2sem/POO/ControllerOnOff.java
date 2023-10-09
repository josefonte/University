import java.util.*;

import ErrorHandling.SmartBulbException;

import java.io.*;

public class ControllerOnOff {
        public static void run(Estado estado) {
            
            boolean exit = false;
            boolean errorMessage = false;
            while(!exit){
                int opcao = -1;
                while(opcao < 0 || opcao > 3) {
                    opcao = Menu.menuLigarDesligar();
                }

                if (opcao == 1 || opcao == 2 || opcao == 3) {
                    
                    CasaInteligente casa = Menu.escolherCasa(estado);
                    
                    if (opcao == 1 || opcao == 2) {
                        String room = Menu.escolherDivisão(casa);

                        switch(opcao) {
                        
                            case 1: 
                                Menu.ONOFFDispositivo(estado, casa, room);
                                break;
                        
                            case 2:
                                Menu.ONOFFDivisão(estado, casa, room);
                                break;
                        }
                    }

                    else {
                        Menu.ONOFFCasa(estado, casa);
                    }
                
                }
                
                else {
                    exit = true;
                    Menu.clearWindow();
                    break;
                }     
                
            }
        }
    }
