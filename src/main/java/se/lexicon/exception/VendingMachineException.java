package se.lexicon.exception;

/** Thrown when a purchase cannot be completed. The message is suitable for showing to the user. */
public class VendingMachineException extends RuntimeException {

    /**
     * @param message user-friendly reason, e.g. "Cola is out of stock."
     */
    public VendingMachineException(String message) {
        super(message);
    }
}
