package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.enums.GameResult;
import SI.models.Field;
import SI.models.GameModel;
import SI.models.Move;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GameState implements Serializable {

    protected GameModel model;

    protected Color currentPlayerColor;
    protected Color enemyColor;
    protected Color winnerColor;

    protected GameResult result;
    protected GamePhase phase;

    protected int placingMovesLeft = 0;
    protected int removingMovesLeft = 0;
    protected int movesWithoutMill = 0;
    protected int moveCount = 0;

    protected Map<Color, Move> previousMove;
    protected Map<Color, Set<Integer>> playerMills;

    protected List<Move> possibleMoves;

    public GameState(GameModel gameModel) {
        this.model = gameModel;
    }

    public GameState(GameState gameState) {
        this.model = gameState.model.getCopy();
        this.currentPlayerColor = gameState.currentPlayerColor;
        this.enemyColor = gameState.enemyColor;
        this.winnerColor = gameState.winnerColor;
        this.result = gameState.result;
        this.phase = gameState.phase;
        this.placingMovesLeft = gameState.placingMovesLeft;
        this.removingMovesLeft = gameState.removingMovesLeft;
        this.movesWithoutMill = gameState.movesWithoutMill;
        this.moveCount = gameState.moveCount;

        this.previousMove = new HashMap<>();
        for(Color c : gameState.previousMove.keySet()) {
            this.previousMove.put(c, new Move(gameState.previousMove.get(c)));
        }

        this.playerMills = new HashMap<>();
        for(Color c : gameState.playerMills.keySet()) {
            this.playerMills.put(c, new HashSet<>(gameState.playerMills.get(c)));
        }
        this.playerMills = new HashMap<>(gameState.playerMills);

        this.possibleMoves = null;

//        if(gameState.possibleMoves != null) {
//            this.possibleMoves = new ArrayList<>();
//            for(Move move : gameState.possibleMoves) {
//                this.possibleMoves.add(new Move(move));
//            }
//        }
//
//        System.out.println(this.possibleMoves);
    }

    public GameState getCopy() {
        return new GameState(this);
    }

    public String getStateName() {
        return phase.name();
    }

    public GamePhase getState() {
        return phase;
    }

    public String getActivePlayer() {
        return currentPlayerColor.name();
    }

    public String getWinner() {
        return winnerColor.name();
    }

    public String getResult() {
        return result.name();
    }

    public Map<String, String> getCurrentFieldTable() {
        Map<String, String> currentFieldTable = new HashMap<>();

        for(String fieldName: model.getFields().keySet()) {
            currentFieldTable.put(fieldName, model.getFields().get(fieldName).name());
        }

        return currentFieldTable;
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

    public int getPlayerManCountDifference() {
        return model.getFields(currentPlayerColor).size() - model.getFields(enemyColor).size();
    }

    private int getPlayerPossibleMoves(Color color) {
        int possibleMoves = 0;

        for(String fieldName : model.getFields(color)) {
            possibleMoves += model.getNeighbours(fieldName)
                    .stream().filter(line -> model.getFieldColor(line).equals(Color.NONE)).collect(Collectors.toList()).size();
        }

        return possibleMoves;
    }

    public int getPossibleMovesDifference() {
        if(!phase.equals(GamePhase.MOVING)) {
            return 0;
        }

        return getPlayerPossibleMoves(currentPlayerColor) - getPlayerPossibleMoves(enemyColor);
    }

    private int getMillOccupiedFields(Set<String> mill) {
        int result = 0;

        for(String fieldName: mill) {
            if(model.getFieldColor(fieldName).equals(currentPlayerColor)) {
                result++;
            } else if (model.getFieldColor(fieldName).equals(enemyColor)) {
                return 0;
            }
        }

        return result;
    }

    public int getPossibleMillsFill() {
        int filledFields = 0;

        for(Set<String> mill : model.getMills()) {
            filledFields += getMillOccupiedFields(mill);
        }

        return filledFields;
    }
}
