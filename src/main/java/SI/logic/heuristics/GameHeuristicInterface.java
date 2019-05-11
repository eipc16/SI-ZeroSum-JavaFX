package SI.logic.heuristics;

import SI.logic.game.GameState;

public interface GameHeuristicInterface {
    double getResultCoefficient(GameState gameState);
}