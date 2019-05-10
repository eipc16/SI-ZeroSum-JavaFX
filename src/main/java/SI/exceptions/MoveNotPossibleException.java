package SI.exceptions;

public class MoveNotPossibleException extends Exception {

    public MoveNotPossibleException(String command) {
        super(String.format("Given move '%s' is not possible in current state", command));
    }
}
