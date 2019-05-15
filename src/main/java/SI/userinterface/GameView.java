package SI.userinterface;

import SI.algorithms.Algorithm;
import SI.algorithms.AlphaBetaPruning;
import SI.algorithms.MinMax;
import SI.enums.Color;
import SI.enums.GamePhase;
import SI.logic.game.Game;
import SI.logic.game.GameInterface;
import SI.logic.heuristics.*;
import SI.models.GameModel;
import SI.models.NineMensMorris;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static SI.enums.GamePhase.*;

public class GameView implements Initializable {

    @FXML
    private CheckBox white_depth_checkbox, white_time_checkbox, black_depth_checkbox, black_time_checkbox;

    @FXML
    private ComboBox white_pick_algorithm, black_pick_algorithm, white_pick_heuristic, black_pick_heuristic;

    @FXML
    private TextField white_depth_value, white_time_value, black_depth_value, black_time_value;

    @FXML
    private VBox black_move_log, white_move_log;

    @FXML
    private AnchorPane mans;

    @FXML
    private Text player_label, phase_label, time_label, white_states_counter, black_states_counter, white_moves_counter, black_moves_counter;

    @FXML
    private Button button_reset, button_start;

    private javafx.scene.paint.Color blackColor, whiteColor, emptyColor, placeColor, removeColor, sourceColor, targetColor;

    private GameInterface game;
    private GameModel model;

    private boolean gameStarted;

    private GameTask task;

    private String userMove;
    private String twoManMove;

    public void handleResetClick() {
        initGame();

        if(gameStarted) {
            button_start.setDisable(false);
            button_reset.setDisable(true);
            this.gameStarted = false;
        }
    }

    @SuppressWarnings("unchecked")
    public void handleStartClick() {
        initGame();

        this.task = new GameTask(this, this.game);
        this.task.execute();
    }

    public void initGame() {
        initMans();

        this.model = new NineMensMorris();
        this.game = new Game(this.model);
        this.game.init();

        black_move_log.getChildren().clear();
        white_move_log.getChildren().clear();
        white_states_counter.setText("0");
        black_states_counter.setText("0");
        time_label.setText("0");
        white_moves_counter.setText("0");
        black_moves_counter.setText("0");
        button_start.setDisable(true);
        button_reset.setDisable(false);
        this.gameStarted = true;
        this.userMove = null;
        this.twoManMove = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initGame();
        this.whiteColor = new javafx.scene.paint.Color(1, 1, 1, 1);
        this.blackColor = new javafx.scene.paint.Color(0, 0, 0, 1);
        this.emptyColor = new javafx.scene.paint.Color(0, 0, 0, 0);
        this.placeColor = new javafx.scene.paint.Color(0, 0.7, 0.0, 1.0);
        this.removeColor = new javafx.scene.paint.Color(0.7, 0.0, 0.0, 1.0);
        this.sourceColor = new javafx.scene.paint.Color(0, 0.7, 0.0, 1.0);
        this.targetColor = new javafx.scene.paint.Color(0, 0.8, 0.0, 1.0);

        button_reset.setDisable(true);
    }

    private void updateMans(Set<String> possibleMoves, Color playerColor) {
        javafx.scene.paint.Color newColor;

        switch(playerColor) {
            case WHITE:
                newColor = whiteColor;
                break;
            case BLACK:
                newColor = blackColor;
                break;
            default:
                newColor = emptyColor;
        }

        for(Node node : this.mans.getChildren()) {
            if(possibleMoves.contains(node.getId())) {
                Circle c = (Circle) node;
                if(playerColor.equals(Color.NONE))
                    c.setOpacity(0);
                else {
                    c.setOpacity(1);
                    c.setFill(newColor);
                    c.setStroke(blackColor);
                }
            }
        }
    }

    void setFieldState(Map<Color, Set<String>> fieldState) {
        Platform.runLater(() -> {
            updateFields(fieldState);
        });
    }

    void updateFields(Map<Color, Set<String>> fieldState) {
        for(Map.Entry<Color, Set<String>> entry : fieldState.entrySet()) {
            updateMans(entry.getValue(), entry.getKey());
        }
    }

    private String getPlayerName(Color playerColor) {
        switch(playerColor) {
            case WHITE:
                return "BIAŁY";
            case BLACK:
                return "CZARNY";
            default:
                return "NIEZNANY";
        }
    }

    private String getPhaseName(GamePhase phase) {
        switch(phase) {
            case PLACING:
                return "USTAWIANIE";
            case MOVING:
                return "PRZESTAWIANIE";
            case FLYING:
                return "LATANIE";
            case REMOVING:
                return "USUWANIE";
            case FINISHED:
                return "ZAKOŃCZONA";
            default:
                return "NIEZNANY";
        }
    }

    private void initMans() {
        for(Node node : this.mans.getChildren()) {
            Circle c = (Circle) node;
            c.setOpacity(0);

            c.setOnMouseClicked(mouseEvent -> handleManClick(c.getId()));
        }
    }

    void setCurrentPlayer(Color playerColor) {
        Platform.runLater(() -> {
            player_label.setText("Gracz: " + getPlayerName(playerColor));
        });
    }

    void setWinnerPlayer(Color playerColor) {
        Platform.runLater(() -> {
            String winner = "Wynik: REMIS";
            if(!playerColor.equals(Color.NONE))
                winner = "Zwycięzca: " + getPlayerName(playerColor);
            player_label.setText(winner);
        });
    }

    void setCurrentPhase(GamePhase phase) {
        Platform.runLater(() -> {
            phase_label.setText("Faza gry: " + getPhaseName(phase));
        });
    }

    void addMoveHistory(Color playerColor, String move) {
        Platform.runLater(() -> {
            Label label = new Label(move);
            label.setFont(new Font(16));
            if(playerColor.equals(Color.WHITE)) {
                white_move_log.getChildren().add(0, label);
            } else {
                black_move_log.getChildren().add(0, label);
            }
        });
    }

    void setGameStarted(boolean gameStarted) {
        Platform.runLater(()->{
            button_start.setDisable(gameStarted);
            button_reset.setDisable(!gameStarted);
            this.gameStarted = gameStarted;
        });
    }

    void setTime(double time) {
        Platform.runLater(() -> {
            time_label.setText(time + "s");
        });
    }

    void setStatesCounter(Color playerColor, Integer value) {
        Platform.runLater(() -> {
           if(playerColor.equals(Color.WHITE))
               white_states_counter.setText(value.toString());
           else
               black_states_counter.setText(value.toString());
        });
    }

    void setMoveCounter(Color playerColor, Integer value) {
        Platform.runLater(() -> {
            if(playerColor.equals(Color.WHITE))
                white_moves_counter.setText(value.toString());
            else
                black_moves_counter.setText(value.toString());
        });
    }

    boolean gameStarted() {
        return gameStarted;
    }

    private GameHeuristic getHeuristic(Color playerColor) {
        int index = playerColor.equals(Color.WHITE)
                ? white_pick_heuristic.getSelectionModel().getSelectedIndex()
                : black_pick_heuristic.getSelectionModel().getSelectedIndex();
        switch(index) {
            case 0:
                return new NumOfMansHeuristic();
            case 1:
                return new PossibleMovesHeuristic();
            case 2:
                return new MansAndPossibleMovesHeuristic();
            case 3:
                return new FinishedGameHeuristic();
            case 4:
                return new WeightedFieldValueHeuristic(20, 8, 10);
            default:
                return null;
        }
    }

    @SuppressWarnings("Duplicates")
    Algorithm getAlgorithm(Color playerColor) {

        int depthLimit;
        double timeLimit;
        Algorithm algorithm = null;

        int index = playerColor.equals(Color.WHITE)
                ? white_pick_algorithm.getSelectionModel().getSelectedIndex()
                : black_pick_algorithm.getSelectionModel().getSelectedIndex();

        GameHeuristic heuristic = getHeuristic(playerColor);

        if(playerColor.equals(Color.WHITE)) {

            depthLimit = white_depth_checkbox.isSelected() ? Integer.parseInt(white_depth_value.getText()) : Integer.MAX_VALUE;
            timeLimit = white_time_checkbox.isSelected() ? Double.parseDouble(white_time_value.getText()) : Double.MAX_VALUE;

            if(index == 1)
                algorithm = new MinMax(this.game, depthLimit, heuristic, false, false, true, timeLimit);
            else if(index == 2)
                algorithm = new AlphaBetaPruning(this.game, depthLimit, heuristic, false, false, true, timeLimit);
        } else {

            depthLimit = black_depth_checkbox.isSelected() ? Integer.parseInt(black_depth_value.getText()) : Integer.MAX_VALUE;
            timeLimit = black_time_checkbox.isSelected() ? Double.parseDouble(black_time_value.getText()) : Double.MAX_VALUE;

            if(index == 1) {
                algorithm = new MinMax(this.game, depthLimit, heuristic, false, false, true, timeLimit);
            } else if(index == 2) {
                algorithm = new AlphaBetaPruning(this.game, depthLimit, heuristic, false, false, true, timeLimit);
            }
        }

        return algorithm;
    }

    void showPossibleMoves() {
        Platform.runLater(() -> {
            switch(game.getPhase()) {
                case PLACING:
                    showSingleMoves(placeColor);
                    break;
                case MOVING:
                case FLYING:
                    showDoubleMoves(sourceColor);
                    break;
                case REMOVING:
                    showSingleMoves(removeColor);
                    break;
                default:
            }
        });
    }

    void handleManClick(String id) {
        if((game.getPhase().equals(REMOVING) || game.getPhase().equals(PLACING))
            && game.getPossibleMoves().contains(id)) {
            this.userMove = id;
        } else if((game.getPhase().equals(MOVING) || game.getPhase().equals(FLYING))) {
            if(this.twoManMove != null && !id.equals(this.twoManMove)) {
                String completeMove = this.twoManMove + " " + id;

                if(game.getPossibleMoves().contains(completeMove)) {
                    this.userMove = completeMove;
                } else {
                    updateFields(game.getFieldsState());
                    showDoubleMoves(sourceColor);
                    this.twoManMove = null;
                }

            }
            for(String possibleMove : game.getPossibleMoves()) {
                String[] move = possibleMove.split(" ");
                if(id.equals(move[0]) && game.getGameModel().getFields(game.getActivePlayer()).contains(id)) {
                    this.twoManMove = id;
                    showTargetMoves(move[0], sourceColor);
                    break;
                }
            }
        }
    }

    void showTargetMoves(String source, javafx.scene.paint.Color color) {
        Circle c;

        Set<String> targets = new HashSet<>();
        for(String possibleMove : game.getPossibleMoves()) {
            String[] move = possibleMove.split(" ");
            if(source.equals(move[0]))
                targets.add(move[1]);
        }

        for(Node node : this.mans.getChildren()) {
            if(node.getId().equals(source)) {
                c = (Circle) node;
                c.setOpacity(0.7);
                c.setFill(sourceColor);
            }
            if(targets.contains(node.getId())) {
                c = (Circle) node;
                c.setOpacity(0.5);
                c.setFill(color);
            }
        }
    }

    void showDoubleMoves(javafx.scene.paint.Color color) {
        List<String> possibleMoves = game.getPossibleMoves();
        Set<String> sources = possibleMoves.stream().map(f -> f.split(" ")[0]).collect(Collectors.toSet());
        Circle c;
        for(Node node : this.mans.getChildren()) {
            if(sources.contains(node.getId())) {
                c = (Circle) node;
                c.setStroke(color);
            }
        }
    }

    void showSingleMoves(javafx.scene.paint.Color color) {
        Circle c;
        for(Node node : this.mans.getChildren()) {
            if(game.getPossibleMoves().contains(node.getId())) {
                c = (Circle) node;
                c.setOpacity(0.5);
                c.setFill(color);
            }
        }
    }

    String getUserInput(Color playerColor, GamePhase phase) {
        String move = this.userMove;
        this.userMove = null;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return move;
    }
}
