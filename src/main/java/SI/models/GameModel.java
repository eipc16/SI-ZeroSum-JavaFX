package SI.models;

import SI.enums.Color;

import java.io.Serializable;
import java.util.*;

public abstract class GameModel implements Serializable {

    Map<String, Color> fieldColors;
    List<Set<String>> mills;
    Map<Color, Set<String>> fieldsByColor;
    Map<String, Set<String>> fieldNeighbours;

    private int numOfMans;

    GameModel(int numOfMans) {
        this.fieldColors = new HashMap<>();
        this.mills = new ArrayList<>();
        this.numOfMans = numOfMans;

        this.fieldsByColor = new HashMap<>();

        for (Color c : Color.values()) {
            fieldsByColor.put(c, new HashSet<>());
        }

        this.fieldNeighbours = new HashMap<>();
    }

    GameModel(GameModel gameModel) {
        this.numOfMans = gameModel.numOfMans;

        this.mills = new ArrayList<>(gameModel.getMills());
        this.fieldColors = new HashMap<>(gameModel.getFields());

        this.fieldsByColor = new HashMap<>();

        for (Color c : gameModel.fieldsByColor.keySet()) {
            this.fieldsByColor.put(c, new HashSet<>());
        }

        for (String fieldName : this.fieldColors.keySet()) {
            this.fieldsByColor.get(getFieldColor(fieldName)).add(fieldName);
        }

        this.fieldNeighbours = new HashMap<>();
        for (String fieldName : gameModel.fieldNeighbours.keySet()) {
            this.fieldNeighbours.put(fieldName, gameModel.fieldNeighbours.get(fieldName));
        }
    }

    public abstract GameModel getCopy();

    public Color getFieldColor(String fieldName) {
        return fieldColors.get(fieldName);
    }

    public void setFieldColor(String fieldName, Color color) {
        Color fieldColor = fieldColors.get(fieldName);

        fieldsByColor.get(fieldColor).remove(fieldName);
        fieldsByColor.get(color).add(fieldName);
        fieldColors.put(fieldName, color);
    }

    public List<Set<String>> getPossibleMills(String fieldName) {
        List<Set<String>> possibleMills = new ArrayList<>();

        for (Set<String> mill : mills) {
            if (mill.contains(fieldName)) {
                possibleMills.add(mill);
            }
        }

        return possibleMills;
    }

    public void resetFields() {
        for (String fieldName : fieldColors.keySet()) {
            Color fieldColor = fieldColors.get(fieldName);

            if (fieldColor != Color.NONE) {
                fieldsByColor.get(fieldColor).remove(fieldName);
                fieldsByColor.get(Color.NONE).add(fieldName);
                fieldColors.put(fieldName, Color.NONE);
            }
        }
    }

    public boolean isSamePlacing(GameModel gameModel) {
        Map<String, Color> previousFields = gameModel.getFields();

        for (String fieldName : previousFields.keySet()) {
            if (!previousFields.get(fieldName).equals(fieldColors.get(fieldName))) {
                return false;
            }
        }

        return true;
    }

    public Set<String> getNeighbours(String fieldName) {
        return fieldNeighbours.get(fieldName);
    }

    public Map<String, Color> getFields() {
        return fieldColors;
    }

    public Set<String> getFields(Color color) {
        return fieldsByColor.get(color);
    }

    public List<Set<String>> getMills() {
        return mills;
    }

    public Set<String> getMill(int index) {
        return mills.get(index);
    }

    public int getNumOfFields() {
        return numOfMans;
    }

    public abstract String[][] getBoard();
}
