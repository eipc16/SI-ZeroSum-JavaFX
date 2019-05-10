package SI.logic;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.enums.GameResult;
import SI.exceptions.FieldEmptyException;
import SI.exceptions.FieldOccupiedException;
import SI.exceptions.MoveNotPossibleException;
import SI.exceptions.NoSuchFieldException;
import SI.models.Field;
import SI.models.GameModel;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.Logger;

import java.util.*;
import java.util.List;

public class Game implements GameInterface {

    public final static String MOVE_SEPARATOR = " ";
    public final static int MOVES_WITHOUT_MILL = 50;

    private GameModel model;

    private Color currentPlayerColor;
    private Color enemyColor;
    private Color winnerColor;

    private GameResult result;
    private GamePhase phase;

    private int placingMovesLeft;
    private int removingMovesLeft;
    private int movesWithoutMill;

    private Map<Color, Field[]> previousMove;
    private Map<Color,Set<Integer>> playerMills;

    private List<GameModel> gameStateHistory;
    private int currentGameStateIndex;

    public Game(GameModel gameModel) {
        this.model = gameModel;
        this.currentPlayerColor = Color.WHITE;
        this.enemyColor = Color.BLACK;
        this.previousMove = new HashMap<>();
    }

    @Override
    public void init() {
        this.currentPlayerColor = Color.WHITE;
        this.winnerColor = Color.NONE;
        this.result = GameResult.NONE;
        this.phase = GamePhase.PLACING;

        this.placingMovesLeft = model.getNumOfMans();
        this.removingMovesLeft = 0;
        this.movesWithoutMill = 0;

        model.resetFields();
    }

    @Override
    public void move(String command)
            throws MoveNotPossibleException, NoSuchFieldException,
                    FieldOccupiedException, FieldEmptyException {
        String[] fields = command.split(MOVE_SEPARATOR);

        if(fields.length < 1 || fields.length > 2) {
            throw new MoveNotPossibleException(command);
        }

        switch(phase) {
            case PLACING:
                if(fields.length != 1) {
                    throw new MoveNotPossibleException(command);
                }

                place(model.getField(fields[0]));
                movesWithoutMill++;
                placingMovesLeft--;
                removingMovesLeft = countActiveMills();

                break;

            case MOVING:
            case FLYING:
                if(fields.length != 2) {
                    throw new MoveNotPossibleException(command);
                }
                Field source = model.getField(fields[0]);
                Field target = model.getField(fields[1]);

                move(source, target);
                movesWithoutMill++;
                removingMovesLeft = countActiveMills();

                if(!model.backMovesAllowed()) {
                    previousMove.put(currentPlayerColor, new Field[]{source, target});
                }

                break;

            case REMOVING:
                if(fields.length != 1) {
                    throw new MoveNotPossibleException(command);
                }

                remove(model.getField(fields[0]));
                movesWithoutMill++;
                removingMovesLeft--;

                break;
        }

        updateState();

    }

    private void move(Field source, Field target) throws FieldOccupiedException, NoSuchFieldException {
        if(target.getColor() == Color.NONE) {
            model.setFieldColor(target, currentPlayerColor);
            model.setFieldColor(source, Color.NONE);
        } else {
            throw new FieldOccupiedException(target.getName());
        }
    }

    private void place(Field target) throws FieldOccupiedException, NoSuchFieldException {
        if(target.getColor() == Color.NONE) {
            model.setFieldColor(target, currentPlayerColor);
        } else {
            throw new FieldOccupiedException(target.getName());
        }
    }

    private void remove(Field target) throws FieldEmptyException, NoSuchFieldException {
        if(target.getColor() != Color.NONE) {
            model.setFieldColor(target, Color.NONE);
        } else {
            throw new FieldEmptyException(target.getName());
        }
    }

    @Override
    public String getState() {
        return phase.name();
    }

    @Override
    public String getActivePlayer() {
        return currentPlayerColor == Color.WHITE ? "White" : "Black";
    }

    @Override
    public String getWinner() {
        return winnerColor.name();
    }

    @Override
    public String getResult() {
        return result.name();
    }

    @Override
    public List<Field> getMovesHistory() {
        return null;
    }

    @Override
    public Map<String, String> getCurrentFieldTable() {
        Map<String, String> currentFieldTable = new HashMap<>();

        for(Field field: model.getFields().values()) {
            currentFieldTable.put(field.getName(), field.getColorName());
        }

        return currentFieldTable;
    }

    @Override
    public GameModel getGameModel() {
        return model;
    }

    @Override
    public List<Field> getPossibleMoves() {
        switch (phase) {
            case PLACING:
                return getPossiblePlacingMoves();
            case MOVING:
                return getPossibleMovingMoves();
            case FLYING:
                return getPossibleFlyingMoves();
            case REMOVING:
                return getPossibleRemovingMoves();
            default:
                return new ArrayList<>();
        }
    }

    private List<Field> getPossiblePlacingMoves() {
        Set<Field> fieldsWithoutColor = model.getFields(Color.NONE);
        return new ArrayList<>(fieldsWithoutColor);
    }

    private boolean isForbidden(Field from, Field to) {
        if (to.getColor() != Color.NONE) {
            return true;
        }

        Field[] previousFields = previousMove.get(currentPlayerColor);

        if (previousFields == null) {
            return false;
        }

        return from.equals(previousFields[1]) && to.equals(previousFields[0]);
    }

    private List<Field> getPossibleMovingMoves() {
        List<Field> movingMoves = new ArrayList<>();
        Set<Field> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(Field field : fieldsWithPlayerColor) {
            for(Field neighbour : field.getNeighbours()) {
                if(!isForbidden(field, neighbour)) {
                    movingMoves.add(neighbour);
                }
            }
        }

        return movingMoves;
    }

    private List<Field> getPossibleFlyingMoves() {
        List<Field> flyingMoves = new ArrayList<>();

        Set<Field> fieldsWithoutColor = model.getFields(Color.NONE);
        Set<Field> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(Field field : fieldsWithPlayerColor) {
            for(Field target : fieldsWithoutColor) {
                if(!isForbidden(field, target)) {
                    flyingMoves.add(target);
                }
            }
        }

        return flyingMoves;
    }

    private List<Field> getPossibleRemovingMoves() {
        List<Field> removingMoves = new ArrayList<>();

        Set<Field> fieldsWithEnemyColor = model.getFields(enemyColor);

        for(Field field : fieldsWithEnemyColor) {
            try {
                if (!isPartOfMill(field)) {
                    removingMoves.add(field);
                }
            } catch (NoSuchFieldException e) {
                Logger.log(LogTag.JFR_EVENT, LogLevel.ERROR, e.toString());
            }

        }

        if(removingMoves.isEmpty()) {
            removingMoves.addAll(fieldsWithEnemyColor);
        }

        return removingMoves;
    }

    private int countActiveMills() {
        int activeMills = 0;

        for(int i = 0; i < model.getMills().size(); i++) {
            Set<Field> mill = model.getMills().get(i);

            if(playerMills.get(currentPlayerColor).contains(i)
                    && mill.stream().allMatch(f -> f.getColor().equals(currentPlayerColor))) {

                playerMills.get(currentPlayerColor).add(i);
                this.movesWithoutMill = 0;
                activeMills++;
            } else {
                playerMills.get(currentPlayerColor).remove(i);
            }
        }
        return activeMills;
    }

    private boolean isPartOfMill(Field field) throws NoSuchFieldException {
        for(Set<Field> mill : model.getPossibleMills(field)) {
            if(mill.stream()
                    .allMatch(f -> f.getColor().equals(field.getColor()))) {
                return true;
            }
        }
        return false;
    }

    private void updateGameResult() {
        if (getPossibleMoves().size() == 0) {
            this.winnerColor = enemyColor;
            result = GameResult.NO_POSSIBLE_MOVES;
            phase = GamePhase.FINISHED;
        } else if (model.getFields(currentPlayerColor).size() + placingMovesLeft < 3) {
            this.winnerColor = enemyColor;
            result = GameResult.NOT_ENOUGH_MEN_LEFT;
            phase = GamePhase.FINISHED;
        } else if (movesWithoutMill >= MOVES_WITHOUT_MILL) {
            this.winnerColor = Color.NONE;
            result = GameResult.TOO_MANY_MOVES_WITHOUT_MILL;
            phase = GamePhase.FINISHED;
        }
    }

    private void updateState() {
        if(phase != GamePhase.FINISHED) {
            if(removingMovesLeft > 0) {
                phase = GamePhase.REMOVING;
            } else {
                swapPlayers();



                if (placingMovesLeft > 0) {
                    phase = GamePhase.PLACING;
                } else if (model.getFields(currentPlayerColor).size() == 3) {
                    phase = GamePhase.FLYING;
                } else {
                    phase = GamePhase.MOVING;
                }
            }
        }
    }


    private boolean validatePlayerMill(Set<Field> fields) {
        for(Field field: fields) {
            if(field.getColor() != currentPlayerColor) {
                return false;
            }

        }
        return true;
    }

    private void swapPlayers() {
        if(this.currentPlayerColor == Color.WHITE) {
            this.currentPlayerColor = Color.BLACK;
            this.enemyColor = Color.WHITE;
        } else {
            this.currentPlayerColor = Color.WHITE;
            this.enemyColor = Color.BLACK;
        }
    }
}
