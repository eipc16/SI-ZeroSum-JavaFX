package SI.algorithms;

import SI.utils.CustomUtils;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristicInterface;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruning implements AlgorithmInterface {
    private GameInterface game;
    private GameHeuristicInterface gameHeuristic;
    private int depthLimit;

    public AlphaBetaPruning(GameInterface game, GameHeuristicInterface gameHeuristic, int depthLimit) {
        this.game = game;
        this.depthLimit = depthLimit;
        this.gameHeuristic = gameHeuristic;
    }

    @Override
    public String getNextBestMove() {
        String bestMove = null;
        double bestResult = -Double.MAX_VALUE;

        double alpha = -Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;

        Color playerColor = game.getActivePlayerColor();
        List<String> possibleMoves = game.getPossibleMoves();

        try {
            sortPossibleMoves(possibleMoves, playerColor);
        } catch (MoveNotPossibleException e) {
            e.printStackTrace();
        }

        for(String move : possibleMoves) {
            alpha = calculateCoeff(move, 0, playerColor, alpha, beta);

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
            coeffs.add(gameHeuristic.getResultCoefficient(game, playerColor));
            //
        }

        CustomUtils.sortArray(possibleMoves, coeffs);
    }

    private double calculateCoeff(String move, int depth, Color playerColor, double alpha, double beta) {
        double bestResult;

        try {
            game.move(move);

            if(depth >= depthLimit) {
                bestResult = gameHeuristic.getResultCoefficient(game, playerColor);

            } else if(!game.getState().equals(GamePhase.FINISHED)) {

                double result;
                boolean maximize = game.getActivePlayerColor().equals(playerColor);

                bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

                for(String nextMove : game.getPossibleMoves()) {
                    result = calculateCoeff(nextMove, depth + 1, playerColor, alpha, beta);

                    if(maximize) {
                        alpha = result;
                        if (result > bestResult) {
                            bestResult = result;
                        }
                    } else {
                        beta = result;
                        if (result < bestResult) {
                            bestResult = result;
                        }
                    }

                    if (alpha >= beta)
                        break;
                }

            } else {

                if(game.getWinner().equals(game.getActivePlayer())) {
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
