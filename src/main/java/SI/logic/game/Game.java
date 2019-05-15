package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.enums.GameResult;
import SI.exceptions.*;
import SI.models.GameModel;

import java.util.*;
import java.util.List;

public class Game implements GameInterface {

    private final static String MOVE_DIVIDER = " ";
    private final static int MOVES_WITHOUT_MILL = 50;
    private final static int ALLOWED_STATE_REPETITIONS = 3;

    private GameModel model;

    private Color currentPlayerColor, enemyColor, winnerColor;

    private GameResult result;
    private GamePhase phase;

    private int placingMovesLeft, removingMovesLeft, movesWithoutMill, moveCount;

    private Map<Color, String> previousMove;
    private Map<Color, Set<Integer>> playerMills;
    private Map<Color, Integer> playerMoves;
    private List<String> possibleMoves;

    private List<Game> gameStateHistory;
    private Map<Color, List<String>> moveHistory;

    public Game(GameModel gameModel) {
        this.model = gameModel;

        this.playerMoves = new HashMap<>();
        this.playerMoves.put(Color.WHITE, 0);
        this.playerMoves.put(Color.BLACK, 0);
        this.placingMovesLeft = 0;
        this.removingMovesLeft = 0;
        this.movesWithoutMill = 0;
        this.moveCount = 0;
    }

    public Game(Game game) {
        this.model = game.model.getCopy();
        this.currentPlayerColor = game.currentPlayerColor;
        this.enemyColor = game.enemyColor;
        this.winnerColor = game.winnerColor;
        this.result = game.result;
        this.phase = game.phase;
        this.placingMovesLeft = game.placingMovesLeft;
        this.removingMovesLeft = game.removingMovesLeft;
        this.movesWithoutMill = game.movesWithoutMill;
        this.moveCount = game.moveCount;
        this.previousMove = new HashMap<>(game.previousMove);
        this.possibleMoves = game.possibleMoves == null ? null : new ArrayList<>(game.possibleMoves);

        this.playerMills = new HashMap<>();
        this.playerMills.put(Color.BLACK, new HashSet<>(game.playerMills.get(Color.BLACK)));
        this.playerMills.put(Color.WHITE, new HashSet<>(game.playerMills.get(Color.WHITE)));

        this.moveHistory = new HashMap<>();
        this.moveHistory.put(Color.BLACK, new ArrayList<>(game.moveHistory.get(Color.BLACK)));
        this.moveHistory.put(Color.WHITE, new ArrayList<>(game.moveHistory.get(Color.WHITE)));

        this.gameStateHistory = new ArrayList<>(game.gameStateHistory);

        this.playerMoves = new HashMap<>(game.playerMoves);
    }

    @Override
    public Game getCopy() {
        return new Game(this);
    }

    @Override
    public void init() {
        this.currentPlayerColor = Color.WHITE;
        this.winnerColor = Color.NONE;
        this.result = GameResult.NONE;
        this.phase = GamePhase.PLACING;

        this.placingMovesLeft = model.getNumOfFields();
        this.removingMovesLeft = 0;
        this.movesWithoutMill = 0;

        this.playerMills = new HashMap<>();
        playerMills.put(Color.WHITE, new HashSet<>());
        playerMills.put(Color.BLACK, new HashSet<>());

        this.moveHistory = new HashMap<>();
        this.moveHistory.put(Color.WHITE, new ArrayList<>());
        this.moveHistory.put(Color.BLACK, new ArrayList<>());

        this.gameStateHistory = new ArrayList<>();
        this.previousMove = new HashMap<>();
        this.previousMove.put(Color.WHITE, null);
        this.previousMove.put(Color.BLACK,null);

        initPlayers();
        model.resetFields();
    }

    @Override
    public void move(String command) throws MoveNotPossibleException {
        String[] fields = command.split(MOVE_DIVIDER);

        if(fields.length < 1 || fields.length > 2) {
            throw new MoveNotPossibleException(command);
        }

        switch(phase) {
            case PLACING:
                if(fields.length != 1
                        || !model.getFields().containsKey(fields[0])
                        || !model.getFieldColor(fields[0]).equals(Color.NONE)) {
                    throw new MoveNotPossibleException(command);
                }

                place(fields[0]);
                movesWithoutMill++;
                placingMovesLeft--;
                removingMovesLeft = countActiveMills();

                break;

            case MOVING:
            case FLYING:
                if(fields.length != 2
                        || !model.getFields().containsKey(fields[0])
                        || !model.getFields().containsKey(fields[1])
                        || !model.getFieldColor(fields[1]).equals(Color.NONE)
                        || !model.getFieldColor(fields[0]).equals(currentPlayerColor)
                        || isBackMove(fields[0], fields[1])
                        || phase.equals(GamePhase.MOVING) && !model.getNeighbours(fields[0]).contains(fields[1])) {
                    throw new MoveNotPossibleException(command);
                }

                move(fields[0], fields[1]);
                movesWithoutMill++;
                removingMovesLeft = countActiveMills();
                this.previousMove.put(currentPlayerColor, fields[0] + MOVE_DIVIDER + fields[1]);

                break;

            case REMOVING:
                if(fields.length != 1
                        || !model.getFields().containsKey(fields[0])
                        || !model.getFieldColor(fields[0]).equals(enemyColor)) {
                    throw new MoveNotPossibleException(command);
                }

                remove(fields[0]);
                removingMovesLeft--;

                break;
        }

        moveHistory.get(currentPlayerColor).add(moveCount + ". " + command);
        this.possibleMoves = null;

        this.moveCount++;
        this.playerMoves.put(currentPlayerColor, this.playerMoves.get(currentPlayerColor) + 1);

        updateState();
        updateGameResult();
        saveGameState();
    }

    // Todo: Change error throwing for previous move exception
    private void move(String source, String target) {
        model.setFieldColor(target, currentPlayerColor);
        model.setFieldColor(source, Color.NONE);
        updateMills(target);
    }

    private void place(String target) {
        model.setFieldColor(target, currentPlayerColor);
        updateMills(target);
    }

    private void remove(String target) {
        model.setFieldColor(target, Color.NONE);
        updateMills(target);
    }

    private boolean isBackMove(String from, String to) {
        if (previousMove.get(currentPlayerColor) == null) {
            return false;
        }

        String[] previousFields = previousMove.get(currentPlayerColor).split(MOVE_DIVIDER);

        if(previousFields.length < 2) {
            return true;
        }

        return from.equals(previousFields[1])
                && to.equals(previousFields[0]);
    }

    @Override
    public List<String> getPossibleMoves() {
        if(possibleMoves == null) {
            possibleMoves = getPhasePossibleMoves();
        }
        return possibleMoves;
    }

    private List<String> getPhasePossibleMoves() {
        switch(phase) {
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

    @Override
    public int getMovesSinceMill() {
        return movesWithoutMill;
    }


    private List<String> getPossiblePlacingMoves() {
        return new ArrayList<>(model.getFields(Color.NONE));
    }

    private List<String> getPossibleMovingMoves() {
        List<String> movingMoves = new ArrayList<>();
        Set<String> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(String fieldName : fieldsWithPlayerColor) {
            for(String neighbour : model.getNeighbours(fieldName)) {
                if(!isBackMove(fieldName, neighbour) && model.getFieldColor(neighbour).equals(Color.NONE)) {
                    movingMoves.add(fieldName + MOVE_DIVIDER + neighbour);
                }
            }
        }

        return movingMoves;
    }

    private List<String> getPossibleFlyingMoves() {
        List<String> flyingMoves = new ArrayList<>();

        Set<String> fieldsWithoutColor = model.getFields(Color.NONE);
        Set<String> fieldsWithPlayerColor = model.getFields(currentPlayerColor);

        for(String field : fieldsWithPlayerColor) {
            for(String target : fieldsWithoutColor) {
                if(!isBackMove(field, target) && model.getFieldColor(target).equals(Color.NONE)) {
                    flyingMoves.add(field + MOVE_DIVIDER + target);
                }
            }
        }

        return flyingMoves;
    }

    private List<String> getPossibleRemovingMoves() {
        List<String> removingMoves = new ArrayList<>();

        Set<String> fieldsWithEnemyColor = model.getFields(enemyColor);

        for(String fieldName : fieldsWithEnemyColor) {
            if (!isPartOfMill(fieldName, enemyColor)) {
                removingMoves.add(fieldName);
            }
        }

        if(removingMoves.isEmpty()) {
            removingMoves.addAll(fieldsWithEnemyColor);
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

    private boolean isPartOfMill(String field, Color color) {
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

        for(Game gameState : gameStateHistory) {
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

    private void saveGameState() {
        this.gameStateHistory.add(this.getCopy());
    }

    private void initPlayers() {
        this.currentPlayerColor = Color.WHITE;
        this.enemyColor = Color.BLACK;
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

    public int getPlayerMoveCount(Color playerColor) {
        return this.playerMoves.get(playerColor);
    }

    public String getStateName() {
        return phase.name();
    }

    public GamePhase getPhase() {
        return phase;
    }

    @Override
    public Color getActivePlayer() {
        return currentPlayerColor;
    }

    public Color getWinner() {
        return winnerColor;
    }

    public String getResult() {
        return result.name();
    }

    public GameModel getGameModel() {
        return model;
    }

    public int getPlacingMovesLeft() {
        return placingMovesLeft;
    }

    public int getRemovingMovesLeft() {
        return removingMovesLeft;
    }

    public boolean isFinished() {
        return phase.equals(GamePhase.FINISHED);
    }

    public int getPlayerFields(Color color) {
        return model.getFields(color).size();
    }

    public Map<Color, Set<String>> getFieldsState() {
        Map<Color, Set<String>> fieldState = new HashMap<>();

        for(Color c : Color.values()) {
            fieldState.put(c, model.getFields(c));
        }

        return fieldState;
    }
}
