package SI.algorithms;

import SI.enums.Color;
import SI.exceptions.*;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;

import java.util.List;

public class AlphaBetaPruning extends Algorithm {

    private int depthLimit;

    public AlphaBetaPruning(GameInterface game, int depthLimit, GameHeuristic gameHeuristic, boolean sorting) {
        super(game, gameHeuristic, sorting);
        this.depthLimit = depthLimit;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;
        double bestResult = -Double.MAX_VALUE;

        double alpha = -Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;

        Color playerColor = game.getActivePlayer();
        List<String> possibleMoves = getPossibleMoves(playerColor);

        for(String move : possibleMoves) {
            alpha = calculateCoeff(game.getCopy(), move, 0, playerColor, alpha, beta);

            if (bestMove == null || alpha > bestResult) {
                bestMove = move;
                bestResult = alpha;
            }
        }

        System.out.println(game.getActivePlayer() + " Result: " + bestResult);

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, Color playerColor, double alpha, double beta) {
        double bestResult, result;

        try {
            game.move(move);
            this.numOfInstructions++;

            if(depth >= depthLimit) {
                return evaluate(playerColor);

            }

            if(game.isFinished()) {
                if(game.getWinner().equals(playerColor)) {
                    return Double.MAX_VALUE / depth;
                } else if (game.getWinner().equals(Color.NONE)) {
                    return 0;
                } else {
                    return -Double.MAX_VALUE / depth;
                }
            }

            boolean maximize = game.getActivePlayer().equals(playerColor);
            bestResult = maximize ? alpha : beta;

            for(String nextMove : game.getPossibleMoves()) {
                result = calculateCoeff(game.getCopy(), nextMove, depth + 1, playerColor, alpha, beta);

                if(maximize) {
                    if (result > bestResult) {
                        alpha = result;
                        bestResult = result;
                    }
                } else {
                    if(result < bestResult) {
                        beta = result;
                        bestResult = result;
                    }
                }

                if(alpha >= beta) {
                    return bestResult;
                }
            }

            return bestResult;

        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
            return -Double.MAX_VALUE;
        }
    }
}
