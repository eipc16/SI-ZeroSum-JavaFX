package SI.exceptions;

public class FieldEmptyException extends Exception {

    public FieldEmptyException(String fieldName) {
        super(String.format("Field '%s' is empty!", fieldName));
    }
}
