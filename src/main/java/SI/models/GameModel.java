package SI.models;

import SI.enums.Color;
import SI.exceptions.NoSuchFieldException;

import java.io.Serializable;
import java.util.*;

public abstract class GameModel implements Serializable {

    protected Map<String, Field> fields;
    protected List<Set<Field>> mills;
    protected Map<Color, Set<Field>> fieldsByColor;

    protected int numOfMans;
    protected boolean backMoves;

    GameModel(int numOfMans, boolean backMoves) {
        this.fields = new HashMap<>();
        this.mills = new ArrayList<>();
        this.numOfMans = numOfMans;
        this.backMoves = backMoves;

        this.fieldsByColor = new HashMap<>();

        for(Color c : Color.values()) {
            fieldsByColor.put(c, new HashSet<>());
        }
    }

    public Field getField(String fieldName) throws NoSuchFieldException {
        if(fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }

        throw new NoSuchFieldException(fieldName);
    }

    public void setFieldColor(String fieldName, Color color) throws NoSuchFieldException {
        if(fields.containsKey(fieldName)) {
            Field field = fields.get(fieldName);
            Color fieldColor = field.getColor();

            fieldsByColor.get(fieldColor).remove(field);
            field.setColor(color);
            fieldsByColor.get(color).add(field);
        } else {
            throw new NoSuchFieldException(fieldName);
        }
    }

    public void setFieldColor(Field field, Color color) throws NoSuchFieldException {
        if(fields.containsValue(field)) {
            Color fieldColor = field.getColor();

            fieldsByColor.get(fieldColor).remove(field);
            field.setColor(color);
            fieldsByColor.get(color).add(field);
        } else {
            throw new NoSuchFieldException(field.getName());
        }
    }

    public List<Set<Field>> getPossibleMills(Field field) throws NoSuchFieldException {
        if(fields.containsValue(field)) {
            List<Set<Field>> possibleMills = new ArrayList<>();

            for(Set<Field> mill : mills) {
                if(mill.contains(field)) {
                    possibleMills.add(mill);
                }
            }

            return possibleMills;
        } else {
            throw new NoSuchFieldException(field.getName());
        }
    }

    public void resetFields() {
        for(Field field : fields.values()) {
            Color fieldColor = field.getColor();

            if (fieldColor != Color.NONE) {
                fieldsByColor.get(fieldColor).remove(field);
                fieldsByColor.get(Color.NONE).add(field);
            }

            field.resetColors();
        }
    }

    public boolean isSamePlacing(GameModel gameModel) {
        Map<String, Field> previousFields = gameModel.getFields();

        for(String fieldName : previousFields.keySet()) {
            if(previousFields.get(fieldName).getColor() != fields.get(fieldName).getColor()) {
                return false;
            }
        }

        return true;
    }

    public Map<Color, Set<Field>> getFieldsByColor() {
        return fieldsByColor;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Set<Field> getFields(Color color) {
        return fieldsByColor.get(color);
    }

    public List<Set<Field>> getMills() {
        return mills;
    }

    public Set<Field> getMill(int index) {
        return mills.get(index);
    }

    public int getNumOfMans() {
        return numOfMans;
    }

    public boolean backMovesAllowed() {
        return backMoves;
    }

    public abstract String[][] getBoard();
}
