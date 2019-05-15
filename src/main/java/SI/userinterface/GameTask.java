package SI.userinterface;

import SI.algorithms.Algorithm;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.logic.game.GameInterface;
import com.victorlaerte.asynctask.AsyncTask;

public class GameTask extends AsyncTask {

    GameView controller;
    GameInterface game;
    Long startTime;

    public GameTask(GameView controller, GameInterface game) {
        this.controller = controller;
        this.game = game;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public Object doInBackground(Object[] objects) {
        startTime = System.currentTimeMillis();
        String move = null;
        Algorithm whitePlayer = controller.getAlgorithm(Color.WHITE);
        Algorithm blackPlayer = controller.getAlgorithm(Color.BLACK);

        while(!game.isFinished() && controller.gameStarted()) {
            try {
                controller.setCurrentPlayer(game.getActivePlayer());
                controller.setCurrentPhase(game.getPhase());
                controller.setFieldState(game.getFieldsState());

                if(game.getActivePlayer().equals(Color.WHITE) && whitePlayer != null) {
                    move = whitePlayer.getNextBestMove();
                    controller.setStatesCounter(Color.WHITE, whitePlayer.getNumOfInstructions());
                } else if(game.getActivePlayer().equals(Color.BLACK) && blackPlayer != null) {
                    move = blackPlayer.getNextBestMove();
                    controller.setStatesCounter(Color.BLACK, blackPlayer.getNumOfInstructions());
                } else if((game.getActivePlayer().equals(Color.WHITE) && whitePlayer == null)
                        || (game.getActivePlayer().equals(Color.BLACK) && blackPlayer == null)) {
                    controller.showPossibleMoves();
                    move = getUserInput(game.getActivePlayer(), game.getPhase());
                }

                if(move == null)
                    break;

                controller.addMoveHistory(game.getActivePlayer(), (game.getTotalMoves() + 1) + ". " + move);
                controller.setMoveCounter(game.getActivePlayer(), game.getPlayerMoveCount(game.getActivePlayer()));

                game.move(move);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            } finally {
                controller.setTime((System.currentTimeMillis() - startTime) / 1000D);
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Object o) {
        controller.setWinnerPlayer(game.getWinner());
        controller.setCurrentPhase(game.getPhase());
        controller.setGameStarted(false);
    }

    @Override
    public void progressCallback(Object[] objects) {
    }

    private String getUserInput(Color playerColor, GamePhase gamePhase) {
        String move = null;

        while(move == null && controller.gameStarted())
            move = controller.getUserInput(playerColor, gamePhase);

        return move;
    }
}
