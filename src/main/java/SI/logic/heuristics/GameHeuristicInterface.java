package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public interface GameHeuristicInterface {
    double getResultCoefficient(GameInterface game, Color playerColor);
}