package SI.algorithms;

import SI.enums.Color;
import SI.exceptions.MoveNotPossibleException;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;
import SI.utils.CustomUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {

    protected GameInterface game;
    protected GameHeuristic gameHeuristic;
    protected int numOfInstructions;

    private boolean sorting;

    public Algorithm(GameInterface game, GameHeuristic gameHeuristic, boolean sorting) {
        this.game = game;
        this.gameHeuristic = gameHeuristic;
        this.numOfInstructions = 0;
        this.sorting = sorting;
    }

    List<String> getPossibleMoves() {
        List<String> possibleMoves = game.getPossibleMoves();

        if(sorting) {
            try {
                sortPossibleMoves(possibleMoves);
            } catch (MoveNotPossibleException e) {
                e.printStackTrace();
            }
        }

        return possibleMoves;
    }

    private void sortPossibleMoves(List<String> possibleMoves) throws MoveNotPossibleException {
        List<Double> coeffs = new ArrayList<>();

        for(String move : possibleMoves) {
            GameInterface tempGame = game.getCopy();
            tempGame.move(move);
            coeffs.add(gameHeuristic.getResultCoefficient(tempGame));
        }

        CustomUtils.sortArray(possibleMoves, coeffs);
    }

    public abstract String getNextBestMove();

    public int getNumOfInstructions() {
        return this.numOfInstructions;
    }

    double evaluate() {
        return gameHeuristic.getResultCoefficient(game);
    }
}
