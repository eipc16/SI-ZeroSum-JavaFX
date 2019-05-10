package SI.errors;

public class NotANeighbourException extends Exception {

    public NotANeighbourException(String fieldName, String neighbourName) {
        super(String.format("Field %s is not a neighbour of field %s", neighbourName, fieldName));
    }
}
