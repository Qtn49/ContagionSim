package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import fr.quentin.contagionsim.model.Individual;
import javafx.animation.Animation;
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
    private Game game;
    private Timeline updateGame;

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

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            mainCanvas.widthProperty().bind(((AnchorPane) mainCanvas.getParent()).widthProperty());
            mainCanvas.heightProperty().bind(((AnchorPane) mainCanvas.getParent()).heightProperty());
            if (game != null) {
                game.setWidth((int) mainCanvas.getWidth());
                game.setHeight((int) mainCanvas.getHeight());
            }
        });

        mainCanvas.setCursor(Cursor.DEFAULT);

    }

    /**
     * Function used to run the game. The game update every 10ms. Changing this value
     * may cause unexpected behaviours.
     */
    public void runGame(int nbIndiv, double vIndiv, int tIndiv, int tauxIndiv, int tauxContag, int tauxMortal) {

        int personnesInfectesDepart = (int) (tauxIndiv / 100.0 * nbIndiv);

        game = new Game((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight(), nbIndiv, vIndiv, tIndiv, personnesInfectesDepart);

        runGame();

    }
    public void runGame() {

        Game game = new Game(this.game);

        game.initialise();

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        updateGame = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                game.update(gc);

                int healthy = 0, dead = 0, infected = 0, diagnosed = 0, immunise = 0;

                for (Individual individual : game.getIndividuals()) {

                   switch (individual.getState()) {
                       case HEALTHY:
                           healthy++;
                           break;
                       case DEAD:
                           dead++;
                           break;
                       case INFECTED:
                           infected++;
                           break;
                       case DIAGNOSED:
                           diagnosed++;
                           break;
                       default:
                           immunise++;
                   }

                }

                System.out.println("*******************");
                System.out.println("---------------");
                System.out.println("healthy : " + healthy);
                System.out.println("---------------");
                System.out.println("diagnosed : " + diagnosed);
                System.out.println("---------------");
                System.out.println("immunise : " + immunise);
                System.out.println("---------------");
                System.out.println("infected : " + infected);
                System.out.println("---------------");
                System.out.println("dead : " + dead);

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

    public void pause () {

        if (updateGame.getStatus() == Animation.Status.PAUSED)
            updateGame.play();
        else
            updateGame.pause();

    }

    public void backToMenu (ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Accueil.fxml"));
        stage.setScene(new Scene(loader.load()));
        AccueilController controller = loader.getController();
        controller.setStage(stage);
        controller.init();

    }

}
