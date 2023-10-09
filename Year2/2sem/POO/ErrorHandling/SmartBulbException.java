package ErrorHandling;

public class SmartBulbException extends Exception {

    public SmartBulbException(String s){
        super("SmartBulbException: "+s);
    }
}
