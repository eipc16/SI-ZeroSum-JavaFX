package SI.algorithms;

import SI.utils.CustomUtils;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.exceptions.NoSuchFieldException;
import SI.logic.game.GameInterface;
import SI.logic.game.GameState;
import SI.logic.heuristics.GameHeuristicInterface;
import SI.models.Move;

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
    public Move getNextBestMove() {
        Move bestMove = null;
        double bestResult = -Double.MAX_VALUE;

        double alpha = -Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;

        Color playerColor = game.getActivePlayerColor();
        List<Move> possibleMoves = game.getPossibleMoves();

        try {
            sortPossibleMoves(possibleMoves);
        } catch (MoveNotPossibleException | FieldEmptyException | FieldOccupiedException | NotANeighbourException | RemovingOwnedFieldException | NoSuchFieldException | NoSuchPreviousStateException e) {
            e.printStackTrace();
        }

        for(Move move : possibleMoves) {
            alpha = calculateCoeff(move, 0, playerColor, alpha, beta);

            if (bestMove == null || alpha > bestResult) {
                bestMove = move;
                bestResult = alpha;
            }
        }

        return bestMove;
    }

    private void sortPossibleMoves(List<Move> possibleMoves) throws MoveNotPossibleException, FieldEmptyException, FieldOccupiedException, NotANeighbourException, RemovingOwnedFieldException, NoSuchFieldException, NoSuchPreviousStateException {
        List<Double> coeffs = new ArrayList<>();
        int currentStateIndex;

        for(Move move : possibleMoves) {
            currentStateIndex = game.currentStateIndex();
            game.move(String.format("%s", move));
            coeffs.add(gameHeuristic.getResultCoefficient((GameState) game));
            game.restoreGameState(currentStateIndex);
        }

        CustomUtils.sortArray(possibleMoves, coeffs);
    }

    private double calculateCoeff(Move move, int depth, Color startPlayerColor, double alpha, double beta) {
        double bestResult;

        int currentStateIndex = game.currentStateIndex();
        String command = move.toString();

        try {
            game.move(command);

            if(depth >= depthLimit) {
                bestResult = gameHeuristic.getResultCoefficient((GameState) game);

            } else if(!game.getState().equals(GamePhase.FINISHED)) {

                double result;
                boolean maximize = game.getActivePlayerColor().equals(startPlayerColor);

                bestResult = maximize ? -Double.MAX_VALUE : Double.MAX_VALUE;

                for(Move nextMove : game.getPossibleMoves()) {
                    result = calculateCoeff(nextMove, depth + 1, startPlayerColor, alpha, beta);

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

            game.restoreGameState(currentStateIndex);

        } catch (Exception e) {
            e.printStackTrace();
            bestResult = -Double.MAX_VALUE;
        }

        return bestResult;

    }

}
