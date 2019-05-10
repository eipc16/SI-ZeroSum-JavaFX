package SI.models;

import SI.enums.Color;
import SI.errors.NotANeighbourException;

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

    public Field getNeighbourByFieldName(String neighbourFieldName) throws NotANeighbourException {
        for(Field field: neighbours) {
            if(field.getName().equals(neighbourFieldName)) {
                return field;
            }
        }
        throw new NotANeighbourException(fieldName, neighbourFieldName);
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
