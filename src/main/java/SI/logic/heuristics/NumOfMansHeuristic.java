package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;
import SI.models.GameModel;

public class NumOfMansHeuristic extends GameHeuristic {

    @Override
    public double getResultCoefficient(GameInterface game, Color playerColor) {
        GameModel model = game.getGameModel();
        return playerTurn(playerColor)
                * (game.getPlayerMans(Color.WHITE) - game.getPlayerMans(Color.BLACK))
                + (availableMoves(game, Color.WHITE) - availableMoves(game, Color.BLACK));
    }

    private double availableMoves(GameInterface game, Color playerColor) {
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
