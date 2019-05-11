package SI.logic;

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
    void move(String command) throws MoveNotPossibleException, NoSuchFieldException, FieldOccupiedException, FieldEmptyException, RemovingOwnedFieldException;

    GamePhase getState();

    String getStateName();
    String getActivePlayer();
    String getWinner();
    String getResult();

    List<Field> getMovesHistory();
    List<Move> getPossibleMoves();

    int getPlacingMovesLeft();
    int getRemovingMovesLeft();

    Map<String, String> getCurrentFieldTable();

    GameModel getGameModel();

    void restoreGameState(int index) throws NoSuchPreviousStateException;
    void restorePreviousState() throws NoSuchPreviousStateException;
}
