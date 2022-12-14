package Model;
public class InstrumentException extends Exception {

    public InstrumentException(String failureMsg, Throwable e) {
        super(failureMsg, e);
    }

    public InstrumentException(String failureMsg) {
        super(failureMsg);
    }
}