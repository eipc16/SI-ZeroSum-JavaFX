package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class MansAndPossibleMovesHeuristic extends GameHeuristic {

    @Override
    public double getResultCoefficient(GameInterface game) {
        return (game.getPlayerFields(Color.WHITE) - game.getPlayerFields(Color.BLACK))
                + (availableMoves(game, Color.WHITE) - availableMoves(game, Color.BLACK));
    }
}
