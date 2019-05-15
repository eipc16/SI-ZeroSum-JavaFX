package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

import static java.lang.Double.MAX_VALUE;

public class FinishedGameHeuristic extends GameHeuristic {

    @Override
    public double getResultCoefficient(GameInterface game) {
        if(game.isFinished()) {
            if(game.getWinner().equals(Color.WHITE))
                return MAX_VALUE;
            else if(game.getWinner().equals(Color.BLACK))
                return -MAX_VALUE;
            else
                return 0;
        }

        return (game.getPlayerFields(Color.WHITE) - game.getPlayerFields(Color.BLACK))
                + (availableMoves(game, Color.WHITE) - availableMoves(game, Color.BLACK));
    }
}
