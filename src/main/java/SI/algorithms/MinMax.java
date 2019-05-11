package SI.algorithms;

import SI.enums.Color;
import SI.enums.GamePhase;
import SI.logic.heuristics.GameHeuristicInterface;
import SI.logic.game.GameInterface;
import SI.logic.game.GameState;
import SI.models.Move;

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
    public Move getNextBestMove() {
        Move bestMove = null;

        double moveCoeff;
        double bestCoeff = -Double.MAX_VALUE;

        for(Move move : game.getPossibleMoves()) {
            moveCoeff = calculateCoeff(move, 0, game.getActivePlayerColor());

            if(bestMove == null || moveCoeff > bestCoeff) {
                bestMove = move;
                bestCoeff = moveCoeff;
            }
        }

        return bestMove;
    }

    private double calculateCoeff(Move move, int depth, Color startPlayerColor) {
        double bestResult;

        int currentStateIndex = game.currentStateIndex();
        String command;

        if(move.getSourceName() != null) {
            command = String.format("%s %s", move.getSourceName(), move.getTargetName());
        } else {
            command = String.format("%s", move.getTargetName());
        }

        try {
            game.move(command);

            if(depth >= depthLimit) {
                bestResult = gameAnalytics.getResultCoefficient((GameState) game);

            } else if(!game.getState().equals(GamePhase.FINISHED)) {

                double result;
                boolean maximize = game.getActivePlayerColor().equals(startPlayerColor);

                bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

                for(Move nextMove : game.getPossibleMoves()) {
                    result = calculateCoeff(nextMove, depth + 1, startPlayerColor);

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

                if(game.getWinner().equals(game.getActivePlayer())) {
                    bestResult = Double.MAX_VALUE;
                } else if (game.getWinner().equals(Color.NONE.name())) {
                    bestResult = 0;
                } else {
                    bestResult = -Double.MAX_VALUE;
                }

            }

            game.restoreGameState(currentStateIndex);

        } catch (Exception e) {
            e.printStackTrace();
            bestResult = -Double.MAX_VALUE;
        }

        return bestResult;

    }
}
