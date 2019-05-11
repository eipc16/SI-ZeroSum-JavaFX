package SI.models;

import SI.enums.Color;
import SI.exceptions.NotANeighbourException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Field implements Serializable {
    private String fieldName;
    private Set<String> neighbours;

    private Color color;
    private Color previousColor;

    public Field(String fieldName) {
        this.fieldName = fieldName;

        this.resetColors();
    }

    public Field(Field field) {
        this.fieldName = field.fieldName;
        this.neighbours = new HashSet<>(field.neighbours);
        this.color = field.color;
        this.previousColor = field.previousColor;
    }

    public Field getCopy() {
        return new Field(this);
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

    public String getColorName() {
        return color.name();
    }

    public String getName() {
        return fieldName;
    }

    public Set<String> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<String> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return fieldName;
    }

    @Override
    public int hashCode() {
        if(fieldName == null) {
            fieldName = "00";
        }
        return Objects.hash(fieldName);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Field)) {
            return false;
        }
        return fieldName.equals(((Field) other).fieldName);
    }
}
