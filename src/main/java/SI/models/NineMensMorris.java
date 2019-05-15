package SI.models;

import SI.enums.Color;

import java.util.*;

public class NineMensMorris extends GameModel {

    private static final String[][] GAME_BOARD =
            {
                    {"A7",  "-",    "-",    "D7",   "-",    "-",    "G7"},
                    {"|",   "B6",   "-",    "D6",   "-",    "F6",   "|"},
                    {"|",   "|",    "C5",   "D5",   "E5",   "|",    "|"},
                    {"A4",  "B4",   "C4",   null,   "E4",   "F4",   "G4"},
                    {"|",   "|",    "C3",   "D3",   "E3",   "|",    "|"},
                    {"|",   "B2",   "-",    "D2",   "-",    "F2",   "|"},
                    {"A1",  "-",    "-",    "D1",   "-",    "-",    "G1"}
            };

    public NineMensMorris() {
        super(18);

        initFields();
        initNeighbours();
        initMills();
    }

    public NineMensMorris(NineMensMorris model) {
        super(model);
    }

    @Override
    public GameModel getCopy() {
        return new NineMensMorris(this);
    }

    private void initFields() {
        String fieldName;
        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                fieldName = GAME_BOARD[i][j];
                if(correctField(fieldName)) {
                    fieldColors.put(fieldName, Color.NONE);
                }
            }
        }

        fieldsByColor.put(Color.NONE, new HashSet<>(fieldColors.keySet()));
    }

    private void initMills() {
        Set<String> millHorizontal = new HashSet<>();
        Set<String> millVertical = new HashSet<>();

        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                String fieldNameHorizontal = GAME_BOARD[i][j];
                String fieldNameVertical = GAME_BOARD[j][i];

                if(correctField(fieldNameHorizontal)) {
                    millHorizontal.add(fieldNameHorizontal);
                }

                if(correctField(fieldNameVertical)) {
                    millVertical.add(fieldNameVertical);
                }

                if(millHorizontal.size() == 3) {
                    Set<String> addedSet = new HashSet<>(millHorizontal);
                    mills.add(addedSet);
                    millHorizontal.clear();
                }

                if(millVertical.size() == 3) {
                    Set<String> addedSet = new HashSet<>(millVertical);
                    mills.add(addedSet);
                    millVertical.clear();
                }
            }
        }
    }

    private boolean correctField(String fieldName) {
        return fieldName != null && !fieldName.equals("|") && !fieldName.equals("-");
    }

    private Set<String> getNeighbours(int fieldX, int fieldY) {
        Set<String> neighbours = new HashSet<>();

        for(int i = fieldX + 1; i < GAME_BOARD.length; i++) {
            if (addNeighbours(fieldY, neighbours, i)) break;
        }

        for(int i = fieldY + 1; i < GAME_BOARD[fieldX].length; i++) {
            if (addNeighbours(i, neighbours, fieldX)) break;
        }

        for(int i = fieldX - 1; i >= 0; i--) {
            if (addNeighbours(fieldY, neighbours, i)) break;
        }

        for(int i = fieldY - 1; i >= 0; i--) {
            if (addNeighbours(i, neighbours, fieldX)) break;
        }

        return neighbours;
    }

    private boolean addNeighbours(int fieldY, Set<String> neighbours, int i) {
        String fieldName;
        fieldName = GAME_BOARD[i][fieldY];

        if(fieldName == null) {
            return true;
        }

        if(correctField(fieldName)) {
            neighbours.add(fieldName);
            return true;
        }
        return false;
    }

    private void initNeighbours() {
        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                String fieldName = GAME_BOARD[i][j];
                if(correctField(fieldName)) {
                    this.fieldNeighbours.put(fieldName, getNeighbours(i, j));
                }
            }
        }
    }

    @Override
    public String[][] getBoard() {
        return GAME_BOARD;
    }

    @Override
    public String toString() {
        return String.format("7  %s-----------%s-----------%s\n", getSign("A7"), getSign("D7"), getSign("G7")) +
                "   |           |           |\n" +
                String.format("6  |   %s-------%s-------%s   |\n", getSign("B6"), getSign("D6"), getSign("F6")) +
                "   |   |       |       |   |\n" +
                String.format("5  |   |   %s---%s---%s   |   |\n", getSign("C5"), getSign("D5"), getSign("E5")) +
                "   |   |   |       |   |   |\n" +
                String.format("4  %s---%s---%s       %s---%s---%s\n", getSign("A4"), getSign("B4"), getSign("C4"), getSign("E4"), getSign("F4"), getSign("G4")) +
                "   |   |   |       |   |   |\n" +
                String.format("3  |   |   %s---%s---%s   |   |\n", getSign("C3"), getSign("D3"), getSign("E3")) +
                "   |   |       |       |   |\n" +
                String.format("2  |   %s-------%s-------%s   |\n", getSign("B2"), getSign("D2"), getSign("F2")) +
                "   |           |           |\n" +
                String.format("1  %s-----------%s-----------%s\n\n", getSign("A1"), getSign("D1"), getSign("G1")) +
                "   A---B---C---D---E---F---G\n";

    }

    private String getSign(String fieldName) {
        switch(fieldColors.get(fieldName)) {
            case WHITE:
                return "W";
            case BLACK:
                return "B";
            default:
                return " ";
        }
    }
}
