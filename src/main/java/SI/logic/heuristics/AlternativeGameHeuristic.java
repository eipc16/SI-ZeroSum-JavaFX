package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class AlternativeGameHeuristic implements GameHeuristicInterface {

    public static final double MEN_IN_GAME_FACTOR = 8;
    public static final double MAN_FACTOR = 16;
    public static final double FREE_NEIGHBOUR_FACTOR = 4;
    public static final double ALLY_NEIGHBOUR_FACTOR = 2;
    public static final double ENEMY_NEIGHBOUR_FACTOR = 1;

    private double availableMovesCoefficient(GameInterface game) {
        double sum = 0;
        for(String fieldName : game.getGameModel().getFields(game.getActivePlayer())) {
            sum += MAN_FACTOR;
            for(String neighbour : game.getGameModel().getNeighbours(fieldName)) {
                if(game.getGameModel().getFieldColor(neighbour).equals(Color.NONE)) {
                    sum += FREE_NEIGHBOUR_FACTOR;
                } else if (game.getGameModel().getFieldColor(neighbour).equals(game.getActivePlayer())) {
                    sum += ALLY_NEIGHBOUR_FACTOR;
                } else {
                    sum += ENEMY_NEIGHBOUR_FACTOR;
                }
            }
        }
        return sum;
    }

    private double menInGameCeofficient(GameInterface game) {
        return game.getPlacingMovesLeft() + MEN_IN_GAME_FACTOR;
    }

    private double playerTurn(Color playerColor) {
        return playerColor == Color.WHITE ? 1 : -1;
    }

    @Override
    public double getResultCoefficient(GameInterface game, Color playerColor) {
        return playerTurn(playerColor) * availableMovesCoefficient(game) / menInGameCeofficient(game);
    }
}
