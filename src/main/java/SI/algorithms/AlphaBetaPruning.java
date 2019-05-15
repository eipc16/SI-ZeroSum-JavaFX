package SI.algorithms;

import SI.enums.Color;
import SI.enums.Sorting;
import SI.exceptions.*;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;

import java.util.List;

import static java.lang.Double.MAX_VALUE;

@SuppressWarnings("Duplicates")
public class AlphaBetaPruning extends Algorithm {

    private int depthLimit;
    private boolean sortAllDepths;
    private double timeLimit;

    public AlphaBetaPruning(GameInterface game, int depthLimit, GameHeuristic gameHeuristic, boolean sorting, boolean sortAllDepths, double timeLimit) {
        super(game, gameHeuristic, sorting);
        this.depthLimit = depthLimit;
        this.timeLimit = timeLimit;
        this.sortAllDepths = sortAllDepths;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;
        double bestResult = -MAX_VALUE;
        double result = -MAX_VALUE;

        Color playerColor = game.getActivePlayer();
        List<String> possibleMoves;

        long startTime;
        double subTimeLimit = timeLimit / game.getPossibleMoves().size();

        if(sorting)
            possibleMoves = getPossibleMoves(playerColor, game, Sorting.DESCENDING);
        else
            possibleMoves = getPossibleMoves(playerColor, game);

        if(possibleMoves.size() == 1)
            return possibleMoves.get(0);

        for(String move : possibleMoves) {
            startTime = System.currentTimeMillis();
            result = calculateCoeff(game.getCopy(), move, 0, playerColor, result, MAX_VALUE, startTime, subTimeLimit);

            if (bestMove == null || result > bestResult) {
                bestMove = move;
                bestResult = result;
            }

            if(((System.currentTimeMillis() - startTime) / 1000D) > timeLimit)
                return bestMove;
        }

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, Color playerColor, double alpha, double beta, long startTime, double subTimeLimit) {
        double bestResult, result;

        try {
            game.move(move);
            this.numOfInstructions++;

            if(depth >= depthLimit || game.isFinished() || ((System.currentTimeMillis() - startTime) / 1000D) > subTimeLimit) {
                return evaluate(game, playerColor);

            }

            depth += 1;

            boolean maximize = game.getActivePlayer().equals(playerColor);
            bestResult = maximize ? alpha : beta;

            List<String> possibleMoves;
            if(sortAllDepths) {
                if(maximize)
                    possibleMoves = getPossibleMoves(game.getActivePlayer(), game.getCopy(), Sorting.DESCENDING);
                else
                    possibleMoves = getPossibleMoves(game.getActivePlayer(), game.getCopy(), Sorting.DESCENDING);
            } else {
                possibleMoves = game.getPossibleMoves();
            }

            for(String nextMove : possibleMoves) {
                result = calculateCoeff(game.getCopy(), nextMove, depth + 1, playerColor, alpha, beta, startTime, subTimeLimit);

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

                if (alpha >= beta) {
                    return bestResult;
                }
            }

            return bestResult;

        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
            return -MAX_VALUE;
        }
    }
}
