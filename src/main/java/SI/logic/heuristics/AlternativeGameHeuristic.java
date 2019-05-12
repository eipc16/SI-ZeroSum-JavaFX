package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class AlternativeGameHeuristic extends GameHeuristic {

    public static final double MIN_MEN_FACTOR = 4;
    public static final double MAN_FACTOR = 20;
    public static final double FREE_NEIGHBOUR_FACTOR = 4;
    public static final double ALLY_NEIGHBOUR_FACTOR = 0.9;
    public static final double ENEMY_NEIGHBOUR_FACTOR = 1.1;

    private double availableMoves(GameInterface game, Color playerColor) {
        double sum = 0;
        for(String fieldName : game.getGameModel().getFields(playerColor)) {
            sum += MAN_FACTOR;
            for(String neighbour : game.getGameModel().getNeighbours(fieldName)) {
                if(game.getGameModel().getFieldColor(neighbour).equals(Color.NONE)) {
                    sum += FREE_NEIGHBOUR_FACTOR;
                } else if (game.getGameModel().getFieldColor(neighbour).equals(playerColor)) {
                    sum += ALLY_NEIGHBOUR_FACTOR;
                } else {
                    sum += ENEMY_NEIGHBOUR_FACTOR;
                }
            }
        }
        return sum;
    }

    private double availableMovesCoefficient(GameInterface game) {
        return (availableMoves(game, Color.WHITE) - availableMoves(game, Color.BLACK));
    }

    private double menInGameCeofficient(GameInterface game) {
        return game.getPlacingMovesLeft() + MIN_MEN_FACTOR;
    }

    private double menDifferenceCoefficient(GameInterface game) {
        return MAN_FACTOR * (Math.max(game.getGameModel().getFields(Color.WHITE).size(), MIN_MEN_FACTOR) - Math.max(game.getGameModel().getFields(Color.BLACK).size(), MIN_MEN_FACTOR));
    }

    @Override
    public double getResultCoefficient(GameInterface game, Color playerColor) {
        return playerTurn(playerColor) * (menDifferenceCoefficient(game) + availableMovesCoefficient(game)) / menInGameCeofficient(game);
    }
}
