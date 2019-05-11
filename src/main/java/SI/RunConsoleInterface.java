package SI;

import SI.enums.GamePhase;
import SI.exceptions.*;
import SI.exceptions.NoSuchFieldException;
import SI.logic.Game;
import SI.logic.GameInterface;
import SI.models.GameModel;
import SI.models.NineMensMorris;

import java.util.Scanner;

public class RunConsoleInterface {
    public static void main(String[] args) {

        GameModel model = new NineMensMorris(false);
        GameInterface game = new Game(model);

        game.init();

        Scanner scanner = new Scanner(System.in);
        String instruction = "";

        do {
            System.out.println(game.getGameModel());
            System.out.println("---------------");
            System.out.print(String.format("Phase: %s | Current player: %s\n", game.getStateName(), game.getActivePlayer()));
            System.out.print(String.format("Placing moves: %d | Removing moves: %s\n", game.getPlacingMovesLeft(), game.getRemovingMovesLeft()));
            System.out.println(String.format("Possible moves: %s", game.getPossibleMoves()));

            if(!game.getState().equals(GamePhase.FINISHED)) {
                System.out.print("\nNext move: ");
                instruction = scanner.nextLine();
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
                    }
                }

                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else {
                System.out.println("----------[FINISHED]----------");
                System.out.println("Result: " + game.getResult());
                System.out.println("Winner: " + game.getWinner());
                System.out.println("------------------------------");
                break;
            }
        } while(!instruction.equals("exit"));
    }
}
