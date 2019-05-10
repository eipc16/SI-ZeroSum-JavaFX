package SI.models;

import SI.enums.Color;
import SI.exceptions.NotANeighbourException;

import java.io.Serializable;
import java.util.Set;

public class Field implements Serializable {
    private String fieldName;
    private Set<Field> neighbours;

    private Color color;
    private Color previousColor;

    public Field(String fieldName) {
        this.fieldName = fieldName;

        this.resetColors();
    }

    public Color getColor() {
        return color;
    }

    public Color getPreviousColor() {
        return previousColor;
    }

    public void setColor(Color color) {
        this.previousColor = this.color;
        this.color = color;
    }

    void resetColors() {
        this.previousColor = Color.NONE;
        this.color = Color.NONE;
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

    public String getColorName() {
        return color.name();
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
