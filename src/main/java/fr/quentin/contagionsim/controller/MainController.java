package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;

/**
 * The main main.java.fr.quentin.contagionsim.controller for the game.
 * @author Quentin Cld
 */
public class MainController {

    /**
     * The canvas used to draw the game
     */
    @FXML
    private Canvas mainCanvas;

    @FXML
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * init the length of the canvas
     */
    public void init() {

        mainCanvas.widthProperty().bind(((AnchorPane) mainCanvas.getParent()).widthProperty());
        mainCanvas.heightProperty().bind(((AnchorPane) mainCanvas.getParent()).heightProperty());

        mainCanvas.setCursor(Cursor.DEFAULT);

    }

    /**
     * Function used to run the game. The game update every 10ms. Changing this value
     * may cause unexpected behaviours.
     */
//    public void runGame(int nbIndiv, int vIndiv, int tIndiv, int tauxIndiv, int tauxContag, int tauxMortal) {
    public void runGame() {
        Game game = new Game((int)mainCanvas.getWidth(), (int)mainCanvas.getHeight());

        game.initialise();

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        Timeline updateGame = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                game.update(gc);
            }
        }));

        updateGame.setCycleCount(Timeline.INDEFINITE);
        updateGame.play();

        // ---------------------------------------------------------------------------------------------------
        // This way of updating seems to cause Java to think that he can dispose of some data,
        // see https://stackoverflow.com/questions/52918195/java-game-with-javafx-canvas-canvas-stops-updating
        // and https://stackoverflow.com/questions/9966136/javafx-periodic-background-task/9966213#9966213
        // ---------------------------------------------------------------------------------------------------

        /*Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                game.update(gc);
            }
        }, 0, 10);*/
    }

    public void backToMenu (ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Accueil.fxml"));
        stage.setScene(new Scene(loader.load()));
        AccueilController controller = loader.getController();
        controller.setStage(stage);
        controller.init();

    }

}
