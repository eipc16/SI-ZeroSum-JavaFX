package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.models.GameModel;

import java.util.List;

public interface GameInterface {

    void init();
    void move(String command) throws MoveNotPossibleException;

    GamePhase getState();

    String getStateName();
    Color getWinner();
    String getResult();

    Color getActivePlayer();
    Color getEnemyPlayer();

    List<String> getPossibleMoves();
    List<String> getPlayerMovesHistory(Color color);

    int getPlacingMovesLeft();
    int getMovesSinceMill();
    int getRemovingMovesLeft();
    int getTotalMoves();

    GameModel getGameModel();

    Game getCopy();

    boolean isFinished();

    int getPlayerMoveCount(Color playerColor);
    int getPlayerMans(Color playerColor);
}
