package SI.logic.heuristics;

import SI.logic.game.GameState;

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
    public double getResultCoefficient(GameState gameState) {
        return (millFactor * gameState.getPossibleMillsFill()
                + availableMovesFactor * gameState.getPossibleMovesDifference());
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
