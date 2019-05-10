package SI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Label l = new Label("TEXT");
        scene = new Scene(new StackPane(l), 800, 800);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        //launch();

    }

}