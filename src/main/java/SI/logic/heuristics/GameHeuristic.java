package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public abstract class GameHeuristic {
    public abstract double getResultCoefficient(GameInterface game);

    protected double availableMoves(GameInterface game, Color playerColor) {
        double sum = 0;
        for(String fieldName : game.getGameModel().getFields(playerColor)) {
            for(String neighbour : game.getGameModel().getNeighbours(fieldName)) {
                if(game.getGameModel().getFieldColor(neighbour).equals(Color.NONE)) {
                    sum += 1;
                }
            }
        }
        return sum;
    }
}