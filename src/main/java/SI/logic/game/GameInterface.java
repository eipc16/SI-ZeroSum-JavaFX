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
    String getWinner();
    String getResult();

    Color getActivePlayer();
    Color getActivePlayerColor();

    List<String> getPossibleMoves();

    int getPlacingMovesLeft();
    int getMovesSinceMill();
    int getRemovingMovesLeft();

    GameModel getGameModel();

    Game getCopy();
}
