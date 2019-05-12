package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public abstract class GameHeuristic {
    public abstract double getResultCoefficient(GameInterface game, Color playerColor);

    double playerTurn(Color player) {
        return player.equals(Color.WHITE) ? 1 : -1;
    }
}