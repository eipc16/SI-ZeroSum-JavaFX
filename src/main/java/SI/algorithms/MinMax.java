package SI.algorithms;

import SI.enums.Color;
import SI.enums.Sorting;
import SI.exceptions.MoveNotPossibleException;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;

import java.util.List;

@SuppressWarnings("Duplicates")
public class MinMax extends Algorithm {

    private int depthLimit;
    private double timeLimit;
    private boolean sortAllDepths;

    public MinMax(GameInterface game, int depthLimit, GameHeuristic gameHeuristic, boolean sorting, boolean sortAllDepths, double timeLimit) {
        super(game, gameHeuristic, sorting);
        this.depthLimit = depthLimit;
        this.timeLimit = timeLimit;
        this.sortAllDepths = sortAllDepths;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;

        double result;
        double bestResult = -Double.MAX_VALUE;

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
            result = calculateCoeff(game.getCopy(), move, 0, playerColor, startTime, subTimeLimit);
            if(bestMove == null || result > bestResult) {
                bestMove = move;
                bestResult = result;
            }
        }

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, Color playerColor, long startTime, double subTimeLimit) {
        double bestResult, result;

        try {
            game.move(move);
            this.numOfInstructions++;

            if(depth >= depthLimit || game.isFinished() || ((System.currentTimeMillis() - startTime) / 1000D) > subTimeLimit) {
                return evaluate(game, playerColor);

            }

            boolean maximize = game.getActivePlayer().equals(playerColor);
            bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;


            List<String> possibleMoves;
            if(sortAllDepths) {
                if(maximize)
                    possibleMoves = getPossibleMoves(game.getActivePlayer(), game.getCopy(), Sorting.DESCENDING);
                else
                    possibleMoves = getPossibleMoves(game.getActivePlayer(), game.getCopy(), Sorting.ASCENDING);
            } else {
                possibleMoves = game.getPossibleMoves();
            }

            for(String nextMove : possibleMoves) {
                result = calculateCoeff(game.getCopy(), nextMove, depth + 1, playerColor, startTime, subTimeLimit);

                if(maximize) {
                    if(result > bestResult)
                        bestResult = result;
                } else {
                    if(result < bestResult)
                        bestResult = result;
                }
            }

            return bestResult;

        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
            return -Double.MAX_VALUE;
        }
    }
}
