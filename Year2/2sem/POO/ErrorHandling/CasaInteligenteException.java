package ErrorHandling;

public class CasaInteligenteException extends Exception {
    public CasaInteligenteException(String s){
        super("CasaInteligenteException: " + s);
    }
}
