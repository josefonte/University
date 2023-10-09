package ErrorHandling;

public class FaturaException extends Exception{
    public FaturaException(String s){
        super("FaturaException: "+s);
    }
}
