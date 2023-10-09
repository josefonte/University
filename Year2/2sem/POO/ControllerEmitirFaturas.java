import java.util.*;

import ErrorHandling.FaturaException;
import ErrorHandling.SmartBulbException;

import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;

public class ControllerEmitirFaturas {
        public static void run(Estado old, Estado current) {


            boolean exit = false;
            boolean errorMessage = false;
            while(!exit){
                int opcao = -1;
                while(opcao < 0 || opcao > 7) {
                    opcao = Menu.menuEmitirFaturas();
                }
           
                switch(opcao) {
                    
                    case 1:
                        Fornecedor forn = Menu.escolherFornecedor(current);
                        TreeSet<Fatura> faturasForn = current.faturasFornecedor(forn, old.getDate(), current.getDate());    
                        System.out.print(faturasForn);
                        Menu.pressEnter();
                        break;
                        
                    case 2:
                        Fatura fat = null;
                        CasaInteligente casa = Menu.escolherCasa(current);
                        try {fat = new Fatura(casa, old.getDate(), current.getDate());}
                        catch (FaturaException | ParseException e) {System.out.print(e + "\n");}
                        
                        System.out.print(fat.toString());
                        Menu.pressEnter();

                        break;

                    case 3:
                        HashMap<String,CasaInteligente> l = current.getCasas();
        
                        for(String name: l.keySet()) {  
                            try {Fatura fat1 = new Fatura(l.get(name), old.getDate(), current.getDate()); System.out.print(fat1.toString());}
                            catch (FaturaException | ParseException e) {System.out.print(e + "\n");}
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