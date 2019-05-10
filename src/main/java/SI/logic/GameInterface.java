package SI.logic;

import SI.models.Field;
import SI.models.GameModel;

import java.util.List;
import java.util.Map;

public interface GameInterface {

    void init();
    void start();
    void move(String command);

    String getState();
    String getActivePlayer();
    String getWinner();
    String getResult();

    List<Field> getMovesHistory();
    List<Field> getPossibleMoves();

    Map<String, Field> getFieldTable();

    GameModel getGameModel();
}
