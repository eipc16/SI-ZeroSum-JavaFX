package SI.logic.heuristics;

import SI.logic.game.GameInterface;

public abstract class GameHeuristic {
    public abstract double getResultCoefficient(GameInterface game);
}