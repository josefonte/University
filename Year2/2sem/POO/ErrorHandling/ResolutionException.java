package ErrorHandling;

public class ResolutionException extends Exception{
    public ResolutionException(String s){
        super("ResolutionException: " + s);
    }
}
