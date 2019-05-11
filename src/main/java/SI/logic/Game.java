package SI.logic;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.enums.GameResult;
import SI.exceptions.*;
import SI.exceptions.NoSuchFieldException;
import SI.models.Field;
import SI.models.GameModel;
import SI.models.Move;
import SI.utils.CustomUtils;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Game extends GameState implements GameInterface {

    public final static String MOVE_SEPARATOR = " ";
    public final static int MOVES_WITHOUT_MILL = 50;
    public final static int ALLOWED_STATE_REPETITIONS = 3;

    private List<GameState> gameStateHistory;
    private int currentGameStateIndex;

    public Game(GameModel gameModel) {
        super(gameModel);
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

        playerMills = new HashMap<>();
        playerMills.put(Color.WHITE, new HashSet<>());
        playerMills.put(Color.BLACK, new HashSet<>());
        this.gameStateHistory = new ArrayList<>();

        model.resetFields();
    }

    @Override
    public void move(String command)
            throws MoveNotPossibleException, NoSuchFieldException,
            FieldOccupiedException, FieldEmptyException, RemovingOwnedFieldException {
        String[] fields = command.split(MOVE_SEPARATOR);
        Field source, target;

        if(fields.length < 1 || fields.length > 2) {
            throw new MoveNotPossibleException(command);
        }

        switch(phase) {
            case PLACING:
                if(fields.length != 1) {
                    throw new MoveNotPossibleException(command);
                }
                target = model.getField(fields[0]);
                place(target);
                movesWithoutMill++;
                placingMovesLeft--;
                removingMovesLeft = countActiveMills();

                break;

            case MOVING:
            case FLYING:
                if(fields.length != 2) {
                    throw new MoveNotPossibleException(command);
                }
                source = model.getField(fields[0]);
                target = model.getField(fields[1]);

                move(source, target);
                movesWithoutMill++;
                removingMovesLeft = countActiveMills();

                if(!model.backMovesAllowed()) {
                    previousMove.put(currentPlayerColor, new Move(source, target));
                }

                break;

            case REMOVING:
                if(fields.length != 1) {
                    throw new MoveNotPossibleException(command);
                }

                if(placingMovesLeft > 0) {
                    placingMovesLeft--;
                }

                remove(model.getField(fields[0]));
                movesWithoutMill++;
                removingMovesLeft--;

                break;
        }

        updateState();
        updateGameResult();
        saveCurrentState();
    }

    private void move(Field source, Field target) throws FieldOccupiedException, NoSuchFieldException, MoveNotPossibleException {
        if(!target.getColor().equals(Color.NONE) || !source.getColor().equals(currentPlayerColor)) {
            throw new FieldOccupiedException(target.getName());
        } else if ((!source.getNeighbours().contains(target) && !phase.equals(GamePhase.FLYING))
                    || isForbidden(source, target)) {
            throw new MoveNotPossibleException(String.format("%s -> %s", source, target));
        } else {
            model.setFieldColor(target, currentPlayerColor);
            model.setFieldColor(source, Color.NONE);
        }
    }

    private void place(Field target) throws FieldOccupiedException, NoSuchFieldException {
        if(!target.getColor().equals(Color.NONE)) {
            throw new FieldOccupiedException(target.getName());
        } else {
            model.setFieldColor(target, currentPlayerColor);
        }
    }

    private void remove(Field target) throws FieldEmptyException, NoSuchFieldException, RemovingOwnedFieldException {
        if(target.getColor().equals(Color.NONE)) {
            throw new FieldEmptyException(target.getName());
        } else if (target.getColor() == currentPlayerColor) {
            throw new RemovingOwnedFieldException(target.getName());
        } else {
            model.setFieldColor(target, Color.NONE);
        }
    }

    @Override
    public List<Field> getMovesHistory() {
        return null;
    }

    private boolean isForbidden(Field from, Field to) {
        if (to.getColor() != Color.NONE) {
            return true;
        }

        Move previousFields = previousMove.get(currentPlayerColor);

        if (previousFields == null) {
            return false;
        }

        return from.equals(previousFields.getTarget())
                && to.equals(previousFields.getSource());
    }

    @Override
    public List<Move> getPossibleMoves() {
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

    private List<Move> getPossiblePlacingMoves() {
        Set<Field> fieldsWithoutColor = model.getFields(Color.NONE);

        return fieldsWithoutColor
                .stream()
                .map(field -> new Move(null, field))
                .collect(Collectors.toList());
    }

    private List<Move> getPossibleMovingMoves() {
        List<Move> movingMoves = new ArrayList<>();
        Set<Field> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(Field field : fieldsWithPlayerColor) {
            for(Field neighbour : field.getNeighbours()) {
                if(!isForbidden(field, neighbour)) {
                    movingMoves.add(new Move(field, neighbour));
                }
            }
        }

        return movingMoves;
    }

    private List<Move> getPossibleFlyingMoves() {
        List<Move> flyingMoves = new ArrayList<>();

        Set<Field> fieldsWithoutColor = model.getFields(Color.NONE);
        Set<Field> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(Field field : fieldsWithPlayerColor) {
            for(Field target : fieldsWithoutColor) {
                if(!isForbidden(field, target)) {
                    flyingMoves.add(new Move(field, target));
                }
            }
        }

        return flyingMoves;
    }

    private List<Move> getPossibleRemovingMoves() {
        List<Move> removingMoves = new ArrayList<>();

        Set<Field> fieldsWithEnemyColor = model.getFields(enemyColor);

        for(Field field : fieldsWithEnemyColor) {
            try {
                if (!isPartOfMill(field, enemyColor)) {
                    removingMoves.add(new Move(null, field));
                }
            } catch (NoSuchFieldException e) {
                System.out.println(String.format("Encountered error in getPossibleRemovingMoves:\n%s", e.toString()));
            }

        }

        if(removingMoves.isEmpty()) {
            fieldsWithEnemyColor.forEach(field -> removingMoves.add(new Move(null, field)));
        }

        return removingMoves;
    }

    private int countActiveMills() {
        int activeMills = 0;

        for(int i = 0; i < model.getMills().size(); i++) {
            Set<Field> mill = model.getMills().get(i);
            System.out.println(String.format("Mill nr %d: %s", i, mill));
            if(!playerMills.get(currentPlayerColor).contains(i)
                    && mill.stream().allMatch(f -> f.getColor().equals(currentPlayerColor))) {
                System.out.println("SUCCESS");
                playerMills.get(currentPlayerColor).add(i);
                this.movesWithoutMill = 0;
                activeMills++;
            } else {
                playerMills.get(currentPlayerColor).remove(i);
            }
        }
        return activeMills;
    }

    private boolean isPartOfMill(Field field, Color color) throws NoSuchFieldException {
        for(Set<Field> mill : model.getPossibleMills(field)) {
            if(mill.stream()
                    .allMatch(f -> f.getColor().equals(color))) {
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
        } else if (countStateRepetitions() > ALLOWED_STATE_REPETITIONS) {
            this.winnerColor = Color.NONE;
            result = GameResult.REPEATED_STATE;
            phase = GamePhase.FINISHED;
        }
    }

    private int countStateRepetitions() {
        int repetitions = 0;

        for(GameState gameState : gameStateHistory) {
            if(model.isSamePlacing(gameState.model)) {
                repetitions++;
            }
        }

        return repetitions;
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

    private void saveCurrentState() {
        GameState currentState = new GameState( model, currentPlayerColor, enemyColor,
                                                winnerColor, result, phase,
                                                placingMovesLeft, removingMovesLeft,
                                                movesWithoutMill, previousMove, playerMills);

        gameStateHistory.add((GameState) CustomUtils.deepCopy(currentState));
        currentGameStateIndex++;
    }

    @Override
    public void restoreGameState(int index) throws NoSuchPreviousStateException {
        if(index > 0 && index < gameStateHistory.size()) {
            GameState gameState = (GameState) CustomUtils.deepCopy(gameStateHistory.get(index));

            this.model = gameState.model;

            this.currentPlayerColor = gameState.currentPlayerColor;
            this.enemyColor = gameState.enemyColor;
            this.winnerColor = gameState.winnerColor;

            this.result = gameState.result;
            this.phase = gameState.phase;

            this.placingMovesLeft = gameState.placingMovesLeft;
            this.removingMovesLeft = gameState.removingMovesLeft;
            this.movesWithoutMill = gameState.movesWithoutMill;

            this.previousMove = gameState.previousMove;
            this.playerMills = gameState.playerMills;

            this.currentGameStateIndex = index;
            this.gameStateHistory = gameStateHistory.subList(0, index + 1);
        } else {
            throw new NoSuchPreviousStateException(index);
        }
    }

    public void restorePreviousState() throws NoSuchPreviousStateException {
        restoreGameState(currentGameStateIndex - 1);
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
