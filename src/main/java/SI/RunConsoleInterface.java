package SI;

import SI.algorithms.AlgorithmInterface;
import SI.algorithms.AlphaBetaPruning;
import SI.algorithms.MinMax;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.exceptions.NoSuchFieldException;
import SI.logic.heuristics.GameHeuristic;
import SI.logic.heuristics.GameHeuristicInterface;
import SI.logic.game.Game;
import SI.logic.game.GameInterface;
import SI.logic.game.GameState;
import SI.models.GameModel;
import SI.models.Move;
import SI.models.NineMensMorris;

import java.util.Scanner;

public class RunConsoleInterface {
    public static void main(String[] args) {

        GameModel model = new NineMensMorris(false);
        GameInterface game = new Game(model);
        GameHeuristicInterface gameAnalytics = new GameHeuristic(0.2, 0.8, 0.6);

        AlgorithmInterface white = new AlphaBetaPruning(game, gameAnalytics, 2);
        AlgorithmInterface black = new AlphaBetaPruning(game, gameAnalytics, 4);
        //AlgorithmInterface black = new MinMax(game, gameAnalytics, 2);
        AlgorithmInterface algorithm;
        game.init();

        Scanner scanner = new Scanner(System.in);
        String instruction = "";
        Long startTime, startMoveTime;
        Double moveTime = null;
        Double totalTime;

        startTime = System.currentTimeMillis();

        do {
            System.out.println("\n" + game.getGameModel());
            System.out.println("---------------");
            System.out.print(String.format("Phase: %s | Current player: %s\n", game.getStateName(), game.getActivePlayer()));
            System.out.println(String.format("Moves since last mill: %d | Total moves: %d", game.getMovesSinceMill(), ((Game) game).getTotalMoves()));

            if(!game.getState().equals(GamePhase.FINISHED)) {
                if(game.getActivePlayerColor().equals(Color.WHITE)) {
                    algorithm = white;
                } else {
                    algorithm = black;
                }

                startMoveTime = System.currentTimeMillis();
                System.out.print(String.format("Placing moves: %d | Removing moves: %s\n", game.getPlacingMovesLeft(), game.getRemovingMovesLeft()));
                System.out.println(String.format("Possible moves: %s", game.getPossibleMoves()));
                Move best = algorithm.getNextBestMove();
                System.out.println(String.format("Best move: %s", best));
                System.out.println(String.format("Current coefficient: %f", gameAnalytics.getResultCoefficient((GameState) game)));
                if(moveTime != null) {
                    System.out.println(String.format("Move time: %.6f seconds", moveTime));
                }
                //System.out.print("\nNext move: ");
                //instruction = scanner.nextLine();

                instruction = String.format("%s", best);
                if(instruction.equals("b")) {
                    try {
                        game.restorePreviousState();
                    } catch (NoSuchPreviousStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        game.move(instruction);
                    } catch (MoveNotPossibleException | NoSuchFieldException | FieldOccupiedException | FieldEmptyException | RemovingOwnedFieldException | NotANeighbourException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                moveTime = (System.currentTimeMillis() - startMoveTime) / 1000D;

                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else {
                totalTime = (System.currentTimeMillis() - startTime) / 1000D;
                System.out.println("----------[FINISHED]----------");
                System.out.println("Result: " + game.getResult());
                System.out.println("Winner: " + game.getWinner());
                System.out.println("Moves: " + ((Game) game).getTotalMoves());
                System.out.println(String.format("Total time elapsed: %.4f seconds", totalTime));
                System.out.println("------------------------------");
                break;
            }
        } while(!instruction.equals("exit"));
    }
}
