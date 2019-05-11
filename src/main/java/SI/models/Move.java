package SI.models;

import java.io.Serializable;

public class Move implements Serializable {
    private Field source;
    private Field target;

    public Move(Field source, Field target) {
        this.source = source;
        this.target = target;
    }

    public Field getSource() {
        return source;
    }

    public Field getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return source == null ? target.toString() : String.format("%s -> %s", source, target);
    }
}