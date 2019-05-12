package SI.algorithms;

import SI.utils.CustomUtils;
import SI.enums.Color;
import SI.exceptions.*;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristicInterface;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruning implements AlgorithmInterface {
    private GameInterface game;
    private int depthLimit;

    public AlphaBetaPruning(GameInterface game, GameHeuristicInterface gameHeuristic, int depthLimit) {
        this.game = game;
        this.depthLimit = depthLimit;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;
        double bestResult = -Double.MAX_VALUE;

        double alpha = -Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;

        Color playerColor = game.getActivePlayer();
        List<String> possibleMoves = game.getPossibleMoves();

        try {
            sortPossibleMoves(possibleMoves, playerColor);
        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
        }

        for(String move : possibleMoves) {
            alpha = calculateCoeff(game.getCopy(), move, 0, true, alpha, beta);

            if (bestMove == null || alpha > bestResult) {
                bestMove = move;
                bestResult = alpha;
            }
        }

        return bestMove;
    }

    private void sortPossibleMoves(List<String> possibleMoves, Color playerColor) throws MoveNotPossibleException {
        List<Double> coeffs = new ArrayList<>();
        GameInterface game = this.game.getCopy();
        for(String move : possibleMoves) {
            game.move(String.format("%s", move));
            coeffs.add(game.evaluate());
        }

        CustomUtils.sortArray(possibleMoves, coeffs);
    }

    private double calculateCoeff(GameInterface game, String move, int depth, boolean maximize, double alpha, double beta) {
        double bestResult, result;

        try {
            game.move(move);


            if(game.isFinished() || depth < 1 || game.getPossibleMoves().size() == 0) {
                return game.evaluate();
            }

            bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

            for(String nextMove : game.getPossibleMoves()) {
                result = calculateCoeff(game.getCopy(), nextMove, depth - 1, !maximize, alpha, beta);
                bestResult = newBestResult(bestResult, result, maximize);

                if(maximize && result > bestResult) {
                    alpha = result;
                } else if(!maximize && result < bestResult) {
                    beta = result;
                }

                if(alpha >= beta) {
                    break;
                }
            }

            return bestResult;

        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
            return -Double.MAX_VALUE;
        }
    }

    private double newBestResult(double currentBest, double newResult, boolean maximize) {
        if(maximize) {
            return Math.max(currentBest, newResult);
        } else {
            return Math.min(currentBest, newResult);
        }
    }


}
