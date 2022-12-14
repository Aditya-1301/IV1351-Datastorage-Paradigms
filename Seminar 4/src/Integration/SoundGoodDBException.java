package Integration;

public class SoundGoodDBException extends Exception {

    /**
     * Create a new instance thrown because of the specified reason and exception.
     *
     * @param failureMsg Why the exception was thrown.
     * @param cause The exception that caused this exception to be thrown.
     */
    public SoundGoodDBException(String failureMsg, Throwable cause) {
        super(failureMsg, cause);
    }

    /**
     * Create a new instance thrown because of the specified reason.
     *
     * @param failureMsg Why the exception was thrown.
     */
    public SoundGoodDBException(String failureMsg) {
        super(failureMsg);
    }
}