package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class NumOfMansHeuristic extends GameHeuristic {
    @Override
    public double getResultCoefficient(GameInterface game) {
        return game.getGameModel().getFields(Color.WHITE).size() - game.getGameModel().getFields(Color.BLACK).size();
    }
}
