package ErrorHandling;

public class SmartDeviceException extends Exception{

    public SmartDeviceException(String s){
        super("SmartDeviceException: "+s);
    }
}
