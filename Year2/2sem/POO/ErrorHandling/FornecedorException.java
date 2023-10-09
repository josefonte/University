package ErrorHandling;

public class FornecedorException extends Exception{
    public FornecedorException(String s){
        super("FornecedorException: "+ s);
    }
}
