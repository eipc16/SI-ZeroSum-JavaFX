package SI.algorithms;

import SI.enums.Color;
import SI.exceptions.MoveNotPossibleException;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;

import java.util.List;

public class MinMax extends Algorithm {

    protected int depthLimit;

    public MinMax(GameInterface game, int depthLimit, GameHeuristic gameHeuristic, boolean sorting) {
        super(game, gameHeuristic, sorting);
        this.depthLimit = depthLimit;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;

        double moveCoeff;
        double bestCoeff = -Double.MAX_VALUE;

        Color playerColor = game.getActivePlayer();
        List<String> possibleMoves = getPossibleMoves();

        for(String move : possibleMoves) {
            moveCoeff = calculateCoeff(game.getCopy(), move, 0, playerColor);

            if(bestMove == null || moveCoeff > bestCoeff) {
                bestMove = move;
                bestCoeff = moveCoeff;
            }
        }

        System.out.println(game.getActivePlayer() + " Result: " + bestCoeff);

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, Color playerColor) {
        double bestResult, result;

        try {
            game.move(move);
            this.numOfInstructions++;

            if(depth >= depthLimit) {
                return evaluate();

            }

            if(game.isFinished()) {
                if(game.getWinner().equals(playerColor)) {
                    return Double.MAX_VALUE / depth;
                } else if (game.getWinner().equals(Color.NONE)) {
                    return 0;
                } else {
                    return -Double.MAX_VALUE;
                }
            }

            boolean maximize = game.getActivePlayer().equals(playerColor);

            bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

            for(String nextMove : game.getPossibleMoves()) {
                result = calculateCoeff(game.getCopy(), nextMove, depth + 1, playerColor);
                if(maximize) {
                    bestResult = Math.max(bestResult, result);
                } else {
                    bestResult = Math.min(bestResult, result);
                }
            }

            return bestResult;

        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
            return -Double.MAX_VALUE;
        }
    }
}
