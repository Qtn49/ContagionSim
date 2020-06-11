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
import javafx.scene.Parent;
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

    private FXMLLoader graph;
    private Stage graphStage;

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

    public void runGame () {
        runGame(game);
    }

    public void runGame(Game game) {

        this.game = game;

        game.initialise();

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        graph = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Graph.fxml"));
        graphStage = new Stage();
        try {
            graphStage.setScene(new Scene(graph.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GraphController graphController = graph.getController();
        graphController.setGame(game);
        graphController.init();

        graphStage.show();

        graphStage.getScene().setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE)
                graphStage.close();
        });

        if (updateGame != null)
            updateGame.stop();

//        gc.fillOval(game.getWidth() - game.getIndividuals().get(0).getRadius(), game.getHeight() - game.getIndividuals().get(0).getRadius(), game.getIndividuals().get(0).getRadius(), game.getIndividuals().get(0).getRadius());

//        game.update(gc);

        Timeline updateGraph = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            graphController.run();
        }));

        updateGame = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            game.update(gc);

            updateValues();

        }));

        updateGame.setCycleCount(Timeline.INDEFINITE);
        updateGame.play();
        updateGraph.setCycleCount(Timeline.INDEFINITE);
        updateGraph.play();

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

        graphStage.show();

    }

}
