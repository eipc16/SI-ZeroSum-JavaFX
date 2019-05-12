package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;

public class GameHeuristic implements GameHeuristicInterface {

    private double manFactor;
    private double millFactor;
    private double availableMovesFactor;

    public GameHeuristic(double man_factor, double millFactor, double availableMovesFactor) {
        this.manFactor = man_factor;
        this.millFactor = millFactor;
        this.availableMovesFactor = availableMovesFactor;
    }

    @Override
    public double getResultCoefficient(GameInterface game) {
//        return (millFactor * game.getPossibleMillsFill()
//                + availableMovesFactor * game.getPossibleMovesDifference());
        return 0.0;
    }

    public double getManFactor() {
        return manFactor;
    }

    public void setManFactor(double manFactor) {
        this.manFactor = manFactor;
    }

    public double getMillFactor() {
        return millFactor;
    }

    public void setMillFactor(double millFactor) {
        this.millFactor = millFactor;
    }

    public double getAvailableMovesFactor() {
        return availableMovesFactor;
    }

    public void setAvailableMovesFactor(double availableMovesFactor) {
        this.availableMovesFactor = availableMovesFactor;
    }
}
