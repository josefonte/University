package ErrorHandling;

public class EstadoException extends Exception{
    public EstadoException(String s){
        super("EstadoException: "+s);
    }
}