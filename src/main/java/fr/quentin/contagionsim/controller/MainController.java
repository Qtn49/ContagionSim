package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import fr.quentin.contagionsim.model.Individual;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    private Label healthy, infected, diagnosed, immune, dead;
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

        mainCanvas.setCursor(Cursor.DEFAULT);

    }

    /**
     * Function used to run the game. The game update every 10ms. Changing this value
     * may cause unexpected behaviours.
     */
    public void runGame(int nbIndiv, double vIndiv, int tIndiv, int tauxIndiv, double tauxContag, double tauxMortal) {

        int personnesInfectesDepart = (int) (tauxIndiv / 100.0 * nbIndiv);

        game = new Game((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight(), nbIndiv, vIndiv, tIndiv, personnesInfectesDepart, tauxContag, tauxMortal);

        runGame();

    }
    public void runGame() {

        game = new Game(this.game);

        game.initialise();

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        if (updateGame != null)
            updateGame.stop();

        updateGame = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            game.update(gc);

            updateValues();

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

    private void updateValues() {

        int nbHealthy = 0, nbInfected = 0, nbDiagnosed = 0, nbImmunise = 0;

        for (Individual individual : game.getIndividuals()) {

            switch (individual.getState()) {
                case HEALTHY:
                    nbHealthy++;
                    break;
                case INFECTED:
                    nbInfected++;
                    break;
                case DIAGNOSED:
                    nbDiagnosed++;
                    break;
                default:
                    nbImmunise++;
            }

        }

        healthy.setText(healthy.getText().replaceAll("\\d+", "" + nbHealthy));
        infected.setText(infected.getText().replaceAll("\\d+", "" + nbInfected));
        diagnosed.setText(diagnosed.getText().replaceAll("\\d+", "" + nbDiagnosed));
        immune.setText(immune.getText().replaceAll("\\d+", "" + nbImmunise));
        dead.setText(dead.getText().replaceAll("\\d+", "" + game.getDeadIndividuals().size()));


    }

    public void pause () {

        if (updateGame.getStatus() == Animation.Status.PAUSED)
            updateGame.play();
        else
            updateGame.pause();

    }

    public void backToMenu (ActionEvent event) throws IOException {

        updateGame.stop();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Accueil.fxml"));
        stage.setScene(new Scene(loader.load()));
        AccueilController controller = loader.getController();
        controller.setStage(stage);
        controller.init();

    }

}
