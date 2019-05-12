package SI.algorithms;

import SI.exceptions.MoveNotPossibleException;
import SI.logic.game.GameInterface;

public class MinMax implements AlgorithmInterface {

    private GameInterface game;
    private int depthLimit;

    public MinMax(GameInterface game, int depthLimit) {
        this.game = game;
        this.depthLimit = depthLimit;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;

        double moveCoeff;
        double bestCoeff = -Double.MAX_VALUE;

        for(String move : game.getPossibleMoves()) {
            moveCoeff = calculateCoeff(game.getCopy(), move, depthLimit, true);

            if(bestMove == null || moveCoeff > bestCoeff) {
                bestMove = move;
                bestCoeff = moveCoeff;
            }
        }

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, boolean maximize) {
        double bestResult, result;

        try {
            game.move(move);


            if(game.isFinished() || depth < 1 || game.getPossibleMoves().size() == 0) {
                return game.evaluate();
            }

            bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

            for(String nextMove : game.getPossibleMoves()) {
                result = calculateCoeff(game.getCopy(), nextMove, depth - 1, !maximize);
                bestResult = newBestResult(bestResult, result, maximize);
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
