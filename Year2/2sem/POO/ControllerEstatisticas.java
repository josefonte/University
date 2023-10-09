
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import ErrorHandling.FaturaException;

public class ControllerEstatisticas {
        public static void run(Estado current_estado, Estado old_estado) {
            
            boolean exit = false;

            while(!exit){
                int opcao = -1;
                while(opcao < 0 || opcao > 3) {
                    opcao = Menu.menuEstatisticas();
                }

                    switch(opcao) {
                        
                        case 1: 
                            CasaInteligente casa_mais_gastou = old_estado.casaMaisGastou();
                            System.out.println("-----------CASA QUE MAIS GASTOU-----------\n\n");
                            System.out.println(casa_mais_gastou.toString());
                            Menu.pressEnter();
                            break;
                        
                        case 2:
                            Fornecedor maior_faturação = old_estado.fornecedorMaisFaturou(old_estado.getDate(), current_estado.getDate());
                            System.out.println("-----------Fornecedor com maior volume de faturação-----------\n\n");
                            System.out.println(maior_faturação.toString());
                            Menu.pressEnter();
                            break;

                        case 3:
                            int top = Menu.escolherNtopCasas();
                            TreeSet<CasaInteligente> casas = old_estado.casasMaiorConsumo((int) old_estado.getDate().until(current_estado.getDate(), ChronoUnit.DAYS));
                            ArrayList<CasaInteligente> l = new ArrayList<>();
                            
                            l.addAll(casas);

                            if (top > l.size() || top < 1) {
                                System.out.println("Valor de N incorreto, Size = " + l.size());
                            }
                            
                            else {
                                for (int i = 0; i<top; i++) {
                                    try { System.out.println(l.get(i).faturaCasa(old_estado.getDate(), current_estado.getDate()));}
                                    catch (FaturaException | ParseException e) {System.out.print(e + "\n");}
                                }
                            }

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
