package ErrorHandling;

public class SmartCameraException extends Exception{
    public SmartCameraException(String s){
        super("SmartCameraException: "+s);
    }
}
