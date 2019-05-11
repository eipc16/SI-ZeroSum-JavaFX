package SI.logic.game;

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

        this.playerMills = new HashMap<>();
        this.possibleMoves = null;

        playerMills.put(Color.WHITE, new HashSet<>());
        playerMills.put(Color.BLACK, new HashSet<>());
        this.gameStateHistory = new ArrayList<>();
        this.previousMove = new HashMap<>();

        initPlayers();
        model.resetFields();
        saveCurrentState();
    }

    @Override
    public void move(String command)
            throws MoveNotPossibleException, NoSuchFieldException,
            FieldOccupiedException, FieldEmptyException, RemovingOwnedFieldException, NotANeighbourException {
        String[] fields = command.split(MOVE_SEPARATOR);
        String source, target;

        if(fields.length < 1 || fields.length > 2) {
            throw new MoveNotPossibleException(command);
        }

        switch(phase) {
            case PLACING:
                if(fields.length != 1) {
                    throw new MoveNotPossibleException(command);
                }

                place(fields[0]);
                this.moveCount++;
                movesWithoutMill++;
                placingMovesLeft--;
                removingMovesLeft = countActiveMills();

                break;

            case MOVING:
            case FLYING:
                if(fields.length != 2) {
                    throw new MoveNotPossibleException(command);
                }
                source = fields[0];
                target = fields[1];

                move(source, target);
                this.moveCount++;
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

                remove(fields[0]);
                this.moveCount++;
                removingMovesLeft--;

                break;
        }


        this.possibleMoves = null;

        updateState();
        updateGameResult();
        saveCurrentState();
    }

    @Override
    public Color getActivePlayerColor() {
        return currentPlayerColor;
    }

    // Todo: Change error throwing for previous move exception
    private void move(String source, String target) throws FieldOccupiedException, NoSuchFieldException, MoveNotPossibleException, NotANeighbourException {
        if(!model.getFieldColor(target).equals(Color.NONE)) {
            throw new FieldOccupiedException(target);
        } else if (!model.getFieldColor(source).equals(currentPlayerColor)) {
            throw new FieldOccupiedException(source);
        } else if ((!model.getNeighbours(source).contains(target) && !phase.equals(GamePhase.FLYING))) {
            throw new NotANeighbourException(source, target);
        } else if (isForbidden(source, target)) {
            throw new MoveNotPossibleException(String.format("%s -> %s", source, target));
        } else {
            model.setFieldColor(target, currentPlayerColor);
            model.setFieldColor(source, Color.NONE);
            updateMills(target);
        }
    }

    private void place(String target) throws FieldOccupiedException, NoSuchFieldException {
        if(!model.getFieldColor(target).equals(Color.NONE)) {
            throw new FieldOccupiedException(target);
        } else {
            model.setFieldColor(target, currentPlayerColor);
            updateMills(target);
        }
    }

    private void remove(String target) throws FieldEmptyException, NoSuchFieldException, RemovingOwnedFieldException {
        if(model.getFieldColor(target).equals(Color.NONE)) {
            throw new FieldEmptyException(target);
        } else if (model.getFieldColor(target) == currentPlayerColor) {
            throw new RemovingOwnedFieldException(target);
        } else {
            model.setFieldColor(target, Color.NONE);
            updateMills(target);
        }
    }

    private boolean isForbidden(String from, String to) {
        if (model.getFieldColor(to) != Color.NONE) {
            return true;
        }

        Move previousFields = previousMove.get(currentPlayerColor);

        if (previousFields == null) {
            return false;
        }

        return from.equals(previousFields.getTargetName())
                && to.equals(previousFields.getSourceName());
    }

    @Override
    public List<Move> getPossibleMoves() {
//        if(possibleMoves != null) {
//            return possibleMoves;
//        }

        switch (phase) {
            case PLACING:
                this.possibleMoves = getPossiblePlacingMoves();
                break;
            case MOVING:
                this.possibleMoves = getPossibleMovingMoves();
                break;
            case FLYING:
                this.possibleMoves = getPossibleFlyingMoves();
                break;
            case REMOVING:
                this.possibleMoves = getPossibleRemovingMoves();
                break;
            default:
                return new ArrayList<>();
        }

        return this.possibleMoves;
    }

    @Override
    public int getMovesSinceMill() {
        return movesWithoutMill;
    }

    @Override
    public int currentStateIndex() {
        return currentGameStateIndex;
    }

    private List<Move> getPossiblePlacingMoves() {
        Set<String> fieldsWithoutColor = model.getFields(Color.NONE);

        return fieldsWithoutColor
                .stream()
                .map(field -> new Move(null, field))
                .collect(Collectors.toList());
    }

    private List<Move> getPossibleMovingMoves() {
        List<Move> movingMoves = new ArrayList<>();
        Set<String> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(String fieldName : fieldsWithPlayerColor) {
            for(String neighbour : model.getNeighbours(fieldName)) {
                if(!isForbidden(fieldName, neighbour) && model.getFieldColor(neighbour).equals(Color.NONE)) {
                    movingMoves.add(new Move(fieldName, neighbour));
                }
            }
        }

        return movingMoves;
    }

    private List<Move> getPossibleFlyingMoves() {
        List<Move> flyingMoves = new ArrayList<>();

        Set<String> fieldsWithoutColor = model.getFields(Color.NONE);
        Set<String> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(String field : fieldsWithPlayerColor) {
            for(String target : fieldsWithoutColor) {
                if(!isForbidden(field, target)) {
                    flyingMoves.add(new Move(field, target));
                }
            }
        }

        return flyingMoves;
    }

    private List<Move> getPossibleRemovingMoves() {
        List<Move> removingMoves = new ArrayList<>();

        Set<String> fieldsWithEnemyColor = model.getFields(enemyColor);

        for(String fieldName : fieldsWithEnemyColor) {
            try {
                if (!isPartOfMill(fieldName, enemyColor)) {
                    removingMoves.add(new Move(null, fieldName));
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }

        if(removingMoves.isEmpty()) {
            fieldsWithEnemyColor.forEach(field -> removingMoves.add(new Move(null, field)));
        }

        return removingMoves;
    }

    public void updateMills(String target) {
        Set<Integer> newPlayerMills = new HashSet<>();

        for(int i = 0; i < model.getMills().size(); i++) {
            Set<String> mill = model.getMill(i);

            if(target != null && mill.contains(target)
                    && mill.stream().allMatch(f -> model.getFieldColor(f).equals(currentPlayerColor))) {
                newPlayerMills.add(i);
                movesWithoutMill = 0;
            }
        }

        newPlayerMills.removeAll(playerMills.get(currentPlayerColor));

        playerMills.get(currentPlayerColor).clear();
        playerMills.get(currentPlayerColor).addAll(newPlayerMills);
    }

    private int countActiveMills() {
        return playerMills.get(currentPlayerColor).size();
    }

    private boolean isPartOfMill(String field, Color color) throws NoSuchFieldException {
        for(Set<String> mill : model.getPossibleMills(field)) {
            if(mill.stream()
                    .allMatch(f -> model.getFieldColor(f).equals(color))) {
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
        //gameStateHistory.add((GameState) CustomUtils.deepCopy(new GameState(this)));
        gameStateHistory.add(this.getCopy());
        currentGameStateIndex = gameStateHistory.size() - 1;
    }

    @Override
    public void restoreGameState(int index) throws NoSuchPreviousStateException {
        if(index >= 0 && index < gameStateHistory.size()) {
            //GameState gameState = (GameState) CustomUtils.deepCopy(gameStateHistory.get(index));
            GameState gameState = gameStateHistory.get(index).getCopy();

            this.model = gameState.model;

            this.currentPlayerColor = gameState.currentPlayerColor;
            this.enemyColor = gameState.enemyColor;
            this.winnerColor = gameState.winnerColor;

            this.moveCount = gameState.moveCount;

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

    private void initPlayers() {
        if(Math.random() < 0.5) {
            this.currentPlayerColor = Color.WHITE;
            this.enemyColor = Color.BLACK;
        } else {
            this.currentPlayerColor = Color.BLACK;
            this.enemyColor = Color.WHITE;
        }
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

    public int getTotalMoves() {
        return this.moveCount;
    }
}
