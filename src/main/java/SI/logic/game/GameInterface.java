package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.logic.heuristics.GameHeuristicInterface;
import SI.models.GameModel;

import java.util.List;

public interface GameInterface {

    void init();
    void move(String command) throws MoveNotPossibleException;
    void setBlackPlayerHeuristic(GameHeuristicInterface heuristic);
    void setWhitePlayerHeuristic(GameHeuristicInterface heuristic);

    GamePhase getState();

    String getStateName();
    String getWinner();
    String getResult();

    Color getActivePlayer();
    Color getEnemyPlayer();

    List<String> getPossibleMoves();

    int getPlacingMovesLeft();
    int getMovesSinceMill();
    int getRemovingMovesLeft();
    int getTotalMoves();

    GameModel getGameModel();

    Game getCopy();

    double evaluate();
    boolean isFinished();
}
