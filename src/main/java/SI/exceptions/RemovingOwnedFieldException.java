package SI.exceptions;

public class RemovingOwnedFieldException extends Exception {

    public RemovingOwnedFieldException(String fieldName) {
        super(String.format("Cannot remove field '%s' because it's occupied by one of your mans!", fieldName));
    }
}
