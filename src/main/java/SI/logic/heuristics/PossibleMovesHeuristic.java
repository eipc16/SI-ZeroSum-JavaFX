package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class PossibleMovesHeuristic extends GameHeuristic {
    @Override
    public double getResultCoefficient(GameInterface game) {
        return availableMoves(game, Color.WHITE) - availableMoves(game, Color.BLACK);
    }
}
