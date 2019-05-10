package SI.logic;

import SI.exceptions.FieldEmptyException;
import SI.exceptions.FieldOccupiedException;
import SI.exceptions.MoveNotPossibleException;
import SI.exceptions.NoSuchFieldException;
import SI.models.Field;
import SI.models.GameModel;

import java.util.List;
import java.util.Map;

public interface GameInterface {

    void init();
    void move(String command) throws MoveNotPossibleException, NoSuchFieldException, FieldOccupiedException, FieldEmptyException;

    String getState();
    String getActivePlayer();
    String getWinner();
    String getResult();

    List<Field> getMovesHistory();
    List<Field> getPossibleMoves();

    Map<String, String> getCurrentFieldTable();

    GameModel getGameModel();
}
