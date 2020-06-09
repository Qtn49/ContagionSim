package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import fr.quentin.contagionsim.model.Individual;
import fr.quentin.contagionsim.model.State;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * The main main.java.fr.quentin.contagionsim.controller for the game.
 * @author Quentin Cld
 */
public class MainController implements Initializable {

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
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainCanvas.setCursor(Cursor.DEFAULT);

    }

    /**
     * Function used to run the game. The game update every 10ms. Changing this value
     * may cause unexpected behaviours.
     */
    public void runGame(int nbIndiv, double vIndiv, int tIndiv, int tauxIndiv, double tauxContag, double tauxMortal) {

        int personnesInfectesDepart = (int) (tauxIndiv / 100.0 * nbIndiv);

        int longueur;
        double squareRoot = Math.sqrt(nbIndiv);

//        mainCanvas.getScene().getWindow().setWidth();

        game = new Game((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight(), nbIndiv, vIndiv, tIndiv, personnesInfectesDepart, tauxContag, tauxMortal);

        System.out.println("max x : " + game.getWidth() + "\nmax y : " + game.getHeight() + "\nindivRadius : " + tIndiv + "\n");

//        System.out.println(((HBox) mainCanvas.getParent()).getWidth());
//        System.out.println(longueur);

        runGame();

    }
    public void runGame() {

//        showGraph();

        game = new Game(this.game);

        game.initialise();

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        if (updateGame != null)
            updateGame.stop();

//        gc.fillOval(game.getWidth() - game.getIndividuals().get(0).getRadius(), game.getHeight() - game.getIndividuals().get(0).getRadius(), game.getIndividuals().get(0).getRadius(), game.getIndividuals().get(0).getRadius());

//        game.update(gc);

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

        healthy.setText(healthy.getText().replaceAll("\\d+", "" + game.getStats(State.HEALTHY)));
        infected.setText(infected.getText().replaceAll("\\d+", "" + game.getStats(State.INFECTED)));
        diagnosed.setText(diagnosed.getText().replaceAll("\\d+", "" + game.getStats(State.DIAGNOSED)));
        immune.setText(immune.getText().replaceAll("\\d+", "" + game.getStats(State.IMMUNE)));
        dead.setText(dead.getText().replaceAll("\\d+", "" + game.getStats(State.DEAD)));


    }

    public void pause () {

        if (updateGame.getStatus() == Animation.Status.PAUSED)
            updateGame.play();
        else
            updateGame.pause();

    }

    public void backToMenu (ActionEvent event) throws IOException {

        if (updateGame != null)
            updateGame = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Accueil.fxml"));
        stage.setScene(new Scene(loader.load()));
        AccueilController controller = loader.getController();
        controller.setStage(stage);
        controller.init();

    }

    public void showGraph () {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Graph.fxml"));
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

        GraphController controller = loader.getController();
        controller.setGame(game);
        controller.chargerDonnees();

        stage.getScene().setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                controller.close();
                stage.close();
            }
        });

        stage.setOnCloseRequest(event -> controller.close());

    }

}
