package SI.logic.game;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.exceptions.NoSuchFieldException;
import SI.models.Field;
import SI.models.GameModel;
import SI.models.Move;

import java.util.List;
import java.util.Map;

public interface GameInterface {

    void init();
    void move(String command) throws MoveNotPossibleException, NoSuchFieldException, FieldOccupiedException, FieldEmptyException, RemovingOwnedFieldException, NotANeighbourException;

    GamePhase getState();

    String getStateName();
    String getActivePlayer();
    String getWinner();
    String getResult();

    Color getActivePlayerColor();

    List<Move> getPossibleMoves();

    int getPlacingMovesLeft();
    int getMovesSinceMill();
    int getRemovingMovesLeft();

    GameModel getGameModel();

    int currentStateIndex();
    void restoreGameState(int index) throws NoSuchPreviousStateException;
    void restorePreviousState() throws NoSuchPreviousStateException;
}
