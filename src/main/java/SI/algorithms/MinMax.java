package SI.algorithms;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.logic.heuristics.GameHeuristicInterface;
import SI.logic.game.GameInterface;

public class MinMax implements AlgorithmInterface {

    private GameInterface game;
    private GameHeuristicInterface gameAnalytics;
    private int depthLimit;

    public MinMax(GameInterface game, GameHeuristicInterface gameAnalytics, int depthLimit) {
        this.game = game;
        this.gameAnalytics = gameAnalytics;
        this.depthLimit = depthLimit;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;

        double moveCoeff;
        double bestCoeff = -Double.MAX_VALUE;

        for(String move : game.getPossibleMoves()) {
            moveCoeff = calculateCoeff(game.getCopy(), move, 0, game.getActivePlayerColor());

            if(bestMove == null || moveCoeff > bestCoeff) {
                bestMove = move;
                bestCoeff = moveCoeff;
            }
        }

        return bestMove;
    }

    private double calculateCoeff(GameInterface game, String move, int depth, Color startPlayerColor) {
        double bestResult;

        try {
            game.move(move);

            if(depth >= depthLimit) {
                bestResult = gameAnalytics.getResultCoefficient(game, startPlayerColor);

            } else if(!game.getState().equals(GamePhase.FINISHED)) {

                double result;
                boolean maximize = game.getActivePlayerColor().equals(startPlayerColor);

                bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

                for(String nextMove : game.getPossibleMoves()) {
                    result = calculateCoeff(game.getCopy(), nextMove, depth + 1, startPlayerColor);

                    if(maximize) {
                        if (result > bestResult) {
                            bestResult = result;
                        }
                    } else {
                        if (result < bestResult) {
                            bestResult = result;
                        }
                    }
                }

            } else {

                if(game.getWinner().equals(startPlayerColor)) {
                    bestResult = Double.MAX_VALUE;
                } else if (game.getWinner().equals(Color.NONE.name())) {
                    bestResult = 0;
                } else {
                    bestResult = -Double.MAX_VALUE;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            bestResult = -Double.MAX_VALUE;
        }

        return bestResult;

    }
}
