package SI.errors;

public class NoSuchFieldException extends Exception {

    public NoSuchFieldException(String fieldName) {
        super(String.format("There is no field named: %s", fieldName));
    }
}
