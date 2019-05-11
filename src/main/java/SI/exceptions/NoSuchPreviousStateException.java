package SI.exceptions;

public class NoSuchPreviousStateException extends Exception {

    public NoSuchPreviousStateException(int index) {
        super(String.format("No such state with index '%d'", index));
    }
}
