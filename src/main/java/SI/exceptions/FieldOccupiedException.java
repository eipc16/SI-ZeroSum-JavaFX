package SI.exceptions;

public class FieldOccupiedException extends Exception {

    public FieldOccupiedException(String fieldName) {
        super(String.format("Field '%s' is already occupied!", fieldName));
    }
}
