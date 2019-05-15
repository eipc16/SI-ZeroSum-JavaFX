package SI;

import SI.algorithms.Algorithm;
import SI.algorithms.AlphaBetaPruning;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.logic.heuristics.*;
import SI.logic.game.Game;
import SI.logic.game.GameInterface;
import SI.models.GameModel;
import SI.models.NineMensMorris;

import java.util.Scanner;

public class RunConsoleInterface {
    public static void main(String[] args) {

        GameModel model = new NineMensMorris();
        GameInterface game = new Game(model);

        GameHeuristic heuristicWhite = new WeightedFieldValueHeuristic(20, 8, 10);
        GameHeuristic heuristicBlack = new WeightedFieldValueHeuristic(20, 8, 10);

        Algorithm white = new AlphaBetaPruning(game, 8, heuristicWhite, false, false, Double.MAX_VALUE);
        //Algorithm black = new AlphaBetaPruning(game, 1, heuristicBlack, false);
        Algorithm black = new AlphaBetaPruning(game, 8, heuristicBlack, false,false, Double.MAX_VALUE);
        //Algorithm black = new MinMax(game,  3);
        Algorithm algorithm;
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
            System.out.println(String.format("Moves since last mill: %d | Total moves: %d", game.getMovesSinceMill(), (game.getTotalMoves())));
            System.out.print(String.format("Placing moves: %d | Removing moves: %s\n", game.getPlacingMovesLeft(), game.getRemovingMovesLeft()));
            System.out.println(String.format("Possible moves: %s", game.getPossibleMoves()));

            if(!game.getPhase().equals(GamePhase.FINISHED)) {
                startMoveTime = System.currentTimeMillis();
                String best;
                if(game.getActivePlayer().equals(Color.WHITE)) {
                    best = white.getNextBestMove();
//                    System.out.print("Next move: ");
//                    best = scanner.nextLine();
                } else {
                    best = black.getNextBestMove();
//                    System.out.print("Next move: ");
//                    best = scanner.nextLine();
                }
                System.out.println(String.format("Best move: %s", best));
                System.out.print("\nNext move: ");

                try {
                    game.move(best);
                    moveTime = (System.currentTimeMillis() - startMoveTime) / 1000D;
                    System.out.println(String.format("Move time: %.6f seconds", moveTime));
                } catch (MoveNotPossibleException e) {
                    e.printStackTrace();
                }

                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else {
                totalTime = (System.currentTimeMillis() - startTime) / 1000D;
                System.out.println("----------[FINISHED]----------");
                System.out.println("Result: " + game.getResult());
                System.out.println("Winner: " + game.getWinner());
                System.out.println("Moves: " + game.getTotalMoves());
                System.out.println(String.format("Total time elapsed: %.4f seconds", totalTime));
                System.out.println(String.format("Black moves: %s", (game.getPlayerMoveCount(Color.BLACK))));
                System.out.println(String.format("White moves: %s", (game.getPlayerMoveCount(Color.WHITE))));
                System.out.println(String.format("Black searched nodes: %s", (black.getNumOfInstructions())));
                System.out.println(String.format("White searched nodes: %s", (white.getNumOfInstructions())));
                System.out.println("------------------------------");
                break;
            }
        } while(!instruction.equals("exit"));
    }
}
