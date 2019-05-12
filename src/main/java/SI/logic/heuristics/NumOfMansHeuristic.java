package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;
import SI.models.GameModel;

public class NumOfMansHeuristic extends GameHeuristic {

    @Override
    public double getResultCoefficient(GameInterface game) {
        GameModel model = game.getGameModel();
        return (game.getPlayerMans(game.getActivePlayer()) - game.getPlayerMans(game.getEnemyPlayer()))
                + (availableMoves(game, game.getActivePlayer()) - availableMoves(game, game.getEnemyPlayer()));
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
