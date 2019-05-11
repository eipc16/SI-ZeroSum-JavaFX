package SI.models;

import java.io.Serializable;

public class Move implements Serializable {

    private String source;
    private String target;

    public Move(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public Move(Move move) {
        this.source = move.source;
        this.target = move.target;
    }

    public Move getCopy() {
        return new Move(this);
    }

    public String getSourceName() {
        return source;
    }

    public String getTargetName() {
        return target;
    }

    @Override
    public String toString() {
        return source == null ? target : String.format("%s %s", source, target);
    }
}