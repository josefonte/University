package ErrorHandling;

public class SmartSpeakerException extends Exception {
    public SmartSpeakerException(String s){
        super("SmartSpeakerException: "+s);
    }
}
