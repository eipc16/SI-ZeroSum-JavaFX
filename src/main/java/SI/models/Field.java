package SI.models;

import SI.enums.Color;

import java.io.Serializable;
import java.util.Set;

public class Field implements Serializable {
    private String fieldName;
    private Set<Field> neighbours;
    private Color color;

    public Field(String fieldName) {
        this.fieldName = fieldName;
        this.color = Color.NONE;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getSymbolByColor() {
        switch(color) {
            case WHITE:
                return "W";
            case BLACK:
                return "B";
            default:
                return " ";
        }
    }

    public String getName() {
        return fieldName;
    }

    public Set<Field> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<Field> neighbours) {
        this.neighbours = neighbours;
    }
}
