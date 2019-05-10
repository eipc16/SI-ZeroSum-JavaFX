package SI.models;

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

    public static void main(String[] args) {
        NineMensMorris game = new NineMensMorris(false);
        game.initFields();
        game.initNeighbours();
        game.initMills();
    }

    public NineMensMorris(boolean backMoves) {
        super(18, backMoves);
    }

    private void initFields() {
        String fieldName;
        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                fieldName = GAME_BOARD[i][j];
                if(correctField(fieldName)) {
                    fields.put(fieldName, new Field(fieldName));
                }
            }
        }
    }

    private void initMills() {
        Set<Field> millHorizontal = new HashSet<>();
        Set<Field> millVertical = new HashSet<>();
        int fieldCounter = 0;

        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                String fieldNameHorizontal = GAME_BOARD[i][j];
                String fieldNameVertical = GAME_BOARD[j][i];

                if(correctField(fieldNameHorizontal)) {
                    Field field = this.fields.get(fieldNameHorizontal);
                    millHorizontal.add(field);
                }

                if(correctField(fieldNameVertical)) {
                    Field field = this.fields.get(fieldNameVertical);
                    millVertical.add(field);
                }

                if(millHorizontal.size() == 3) {
                    Set<Field> addedSet = new HashSet<>(millHorizontal);
                    mills.add(addedSet);
                    millHorizontal.clear();
                }

                if(millVertical.size() == 3) {
                    Set<Field> addedSet = new HashSet<>(millVertical);
                    mills.add(addedSet);
                    millVertical.clear();
                }
            }
        }
//
//        mills.forEach(m -> {
//            System.out.print("Mill: ");
//            System.out.println(m.stream().map(Field::getName).collect(Collectors.joining(" ")));
//        });
    }

    private boolean correctField(String fieldName) {
        return fieldName != null && !fieldName.equals("|") && !fieldName.equals("-");
    }

    private Set<Field> getNeighbours(int fieldX, int fieldY) {
        Set<Field> neighbours = new HashSet<>();
        String fieldName;

        for(int i = fieldX + 1; i < GAME_BOARD.length; i++) {
            fieldName = GAME_BOARD[i][fieldY];

            if(fieldName == null) {
                break;
            }

            if(correctField(fieldName)) {
                neighbours.add(this.fields.get(fieldName));
                break;
            }
        }

        for(int i = fieldY + 1; i < GAME_BOARD[fieldX].length; i++) {
            fieldName = GAME_BOARD[fieldX][i];

            if(fieldName == null) {
                break;
            }

            if(correctField(fieldName)) {
                neighbours.add(this.fields.get(fieldName));
                break;
            }
        }

        for(int i = fieldX - 1; i >= 0; i--) {
            fieldName = GAME_BOARD[i][fieldY];

            if(fieldName == null) {
                break;
            }

            if(correctField(fieldName)) {
                neighbours.add(this.fields.get(fieldName));
                break;
            }
        }

        for(int i = fieldY - 1; i >= 0; i--) {
            fieldName = GAME_BOARD[fieldX][i];

            if(fieldName == null) {
                break;
            }

            if(correctField(fieldName)) {
                neighbours.add(this.fields.get(fieldName));
                break;
            }
        }

        return neighbours;
    }

    private void initNeighbours() {
        for(int i = 0; i < GAME_BOARD.length; i++) {
            for(int j = 0; j < GAME_BOARD[i].length; j++) {
                String fieldName = GAME_BOARD[i][j];
                if(correctField(fieldName)) {
                    Field field = this.fields.get(GAME_BOARD[i][j]);
                    field.setNeighbours(getNeighbours(i, j));
//                    String neighbours = getNeighbours(i, j).stream().map(Field::getName).collect(Collectors.joining(" "));
//                    String output = String.format("Field %s: Neighbours: %s", field.getName(), neighbours);
//                    System.out.println(output);
                }
            }
        }
    }

    @Override
    public String[][] getBoard() {
        return GAME_BOARD;
    }
}
