package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class AlternativeGameHeuristic implements GameHeuristicInterface {

    public static final double MEN_IN_GAME_FACTOR = 8;
    public static final double MAN_FACTOR = 16;
    public static final double FREE_NEIGHBOUR_FACTOR = 4;
    public static final double ALLY_NEIGHBOUR_FACTOR = 2;
    public static final double ENEMY_NEIGHBOUR_FACTOR = 1;

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
        return (availableMoves(game, game.getActivePlayer()) - availableMoves(game, game.getEnemyPlayer()));
    }

    private double menInGameCeofficient(GameInterface game) {
        return game.getPlacingMovesLeft() + MEN_IN_GAME_FACTOR;
    }

    @Override
    public double getResultCoefficient(GameInterface game) {
        return availableMovesCoefficient(game) / menInGameCeofficient(game);
    }
}
