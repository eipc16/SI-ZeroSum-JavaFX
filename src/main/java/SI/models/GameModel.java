package SI.models;

import SI.enums.Color;
import SI.errors.NoSuchFieldException;

import java.util.*;

public class GameModel {

    protected Map<String, Field> fields;
    protected List<Set<Field>> mills;

    protected int numOfMans;
    protected boolean backMoves;

    public GameModel(int numOfMans, boolean backMoves) {
        this.fields = new HashMap<>();
        this.mills = new ArrayList<>();
        this.numOfMans = numOfMans;
        this.backMoves = backMoves;
    }

    public Field getField(String fieldName) throws NoSuchFieldException {
        if(fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        throw new NoSuchFieldException(fieldName);
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Map<Color, Set<Field>> getFieldsByColor() {
        Map<Color, Set<Field>> fieldsByColor = new HashMap<>();

        Arrays.asList(Color.values()).forEach(value -> {
           fieldsByColor.put(value, new HashSet<>());
        });

        fields.forEach((fieldName, field) -> {
            Color fieldColor = field.getColor();
            fieldsByColor.get(fieldColor).add(field);
        });

        return fieldsByColor;
    }

    public List<Set<Field>> getMills() {
        return mills;
    }

    public int getNumOfManss() {
        return numOfMans;
    }

    public boolean backMovesAllowed() {
        return backMoves;
    }
}
