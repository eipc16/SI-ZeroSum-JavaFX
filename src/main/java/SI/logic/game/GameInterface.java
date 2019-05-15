package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.models.GameModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GameInterface {

    void init();
    void move(String command) throws MoveNotPossibleException;

    GamePhase getPhase();

    String getStateName();
    String getResult();

    Color getWinner();
    Color getActivePlayer();

    int getPlacingMovesLeft();
    int getMovesSinceMill();
    int getRemovingMovesLeft();
    int getTotalMoves();
    int getPlayerMoveCount(Color playerColor);
    int getPlayerFields(Color playerColor);

    GameModel getGameModel();
    Game getCopy();

    boolean isFinished();

    Map<Color, Set<String>> getFieldsState();
    List<String> getPossibleMoves();
}
