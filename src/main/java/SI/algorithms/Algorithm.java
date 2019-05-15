package SI.algorithms;

import SI.enums.Color;
import SI.enums.Sorting;
import SI.exceptions.MoveNotPossibleException;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.GameHeuristic;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public abstract class Algorithm {

    protected GameInterface game;
    private GameHeuristic gameHeuristic;
    int numOfInstructions;

    boolean sorting;

    Algorithm(GameInterface game, GameHeuristic gameHeuristic, boolean sorting) {
        this.game = game;
        this.gameHeuristic = gameHeuristic;
        this.numOfInstructions = 0;
        this.sorting = sorting;
    }

    List<String> getPossibleMoves(Color playerColor, GameInterface game) {
        return game.getPossibleMoves();
    }

    List<String> getPossibleMoves(Color playerColor, GameInterface game, Sorting sorting) {
        List<String> possibleMoves = game.getPossibleMoves();

        try {
            return sortPossibleMoves(game, possibleMoves, playerColor, sorting);
        } catch (Exception e) {
            return possibleMoves;
        }
    }

    private List<String> sortPossibleMoves(GameInterface game, List<String> possibleMoves, Color playerColor, Sorting sorting) throws MoveNotPossibleException {
        Map<String, Double> moveMap = new HashMap<>();
        for(String move : possibleMoves) {
            GameInterface tempGame = game.getCopy();
            tempGame.move(move);
            moveMap.put(move, evaluate(tempGame, playerColor));
        }

        if(sorting.equals(Sorting.ASCENDING))
            return moveMap
                    .entrySet()
                    .stream()
                    .sorted(comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        else
            return moveMap
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }

    public abstract String getNextBestMove();

    public int getNumOfInstructions() {
        return this.numOfInstructions;
    }

    double evaluate(GameInterface tempGame, Color playerColor) {
        return playerTurn(playerColor) * gameHeuristic.getResultCoefficient(tempGame);
    }

    private double playerTurn(Color playerColor) {
        return playerColor.equals(Color.WHITE) ? 1 : -1;
    }
}
