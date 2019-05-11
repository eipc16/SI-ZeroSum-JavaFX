package SI.logic;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.enums.GameResult;
import SI.models.Field;
import SI.models.GameModel;
import SI.models.Move;

import java.io.Serializable;
import java.util.*;

class GameState implements Serializable {

    protected GameModel model;

    protected Color currentPlayerColor;
    protected Color enemyColor;
    protected Color winnerColor;

    protected GameResult result;
    protected GamePhase phase;

    protected int placingMovesLeft = 0;
    protected int removingMovesLeft = 0;
    protected int movesWithoutMill = 0;

    protected Map<Color, Move> previousMove;
    protected Map<Color, Set<Integer>> playerMills;

    public GameState(GameModel gameModel) {
        this.model = gameModel;
    }

    public GameState(GameModel model, Color currentPlayerColor, Color enemyColor,
                     Color winnerColor, GameResult result, GamePhase phase,
                     int placingMovesLeft, int removingMovesLeft, int movesWithoutMill,
                     Map<Color, Move> previousMove, Map<Color, Set<Integer>> playerMills) {
        this(model);
        this.currentPlayerColor = currentPlayerColor;
        this.enemyColor = enemyColor;
        this.winnerColor = winnerColor;
        this.result = result;
        this.phase = phase;
        this.placingMovesLeft = placingMovesLeft;
        this.removingMovesLeft = removingMovesLeft;
        this.movesWithoutMill = movesWithoutMill;
        this.previousMove = previousMove;
        this.playerMills = playerMills;
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

        for(Field field: model.getFields().values()) {
            currentFieldTable.put(field.getName(), field.getColorName());
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
}
