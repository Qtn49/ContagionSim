package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import fr.quentin.contagionsim.model.State;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class GraphController implements Initializable {

    public static final int TIME_MAX = 30;
    public static final int PAUSE = 300;
    private Game game;

    @FXML
    private VBox container;

    private Timer timer;

    private boolean stop;

    private int compteur = 0;

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chargerDonnees();
    }

    public void chargerDonnees () {

        if (game == null)
            return;

        NumberAxis temps = new NumberAxis(0, TIME_MAX, 1);
        NumberAxis indiv = new NumberAxis(0, game.getIndividuals().size(), game.getIndividuals().size() / 10);

        temps.setLabel("Temps");
        indiv.setLabel("Nombre d'individu");
        container.getChildren().clear();

        LineChart<Number, Number> lineChart = new LineChart<>(temps, indiv);

        lineChart.setTitle("Évolution de l'épidémie");

        XYChart.Series courbeHealthy = new XYChart.Series<>();
        XYChart.Series courbeInfected = new XYChart.Series<>();
        XYChart.Series courbeDiagnosed = new XYChart.Series<>();
        XYChart.Series courbeImmune = new XYChart.Series<>();
        XYChart.Series courbeDead = new XYChart.Series<>();

        courbeHealthy.setName("En bonne santé");
        courbeInfected.setName("Infecté(s)");
        courbeDiagnosed.setName("Diagnostiqué(s)");
        courbeImmune.setName("Immunisé(s)");
        courbeDead.setName("Décédé(s)");

        // runnable for that thread
        new Thread(new Runnable () {

            @Override
            public void run() {

                while (compteur < TIME_MAX && !stop) {
                    try {
                        // imitating work
                        Thread.sleep(new Random().nextInt(PAUSE));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    // update ProgressIndicator on FX thread
                    Platform.runLater(() -> {
                        courbeHealthy.getData().add(new XYChart.Data<>(compteur, game.getStats(State.HEALTHY)));
                        courbeInfected.getData().add(new XYChart.Data<>(compteur, game.getStats(State.INFECTED)));
                        courbeDiagnosed.getData().add(new XYChart.Data<>(compteur, game.getStats(State.DIAGNOSED)));
                        courbeImmune.getData().add(new XYChart.Data<>(compteur, game.getStats(State.IMMUNE)));
                        courbeDead.getData().add(new XYChart.Data<>(compteur, game.getStats(State.DEAD)));

                        courbeHealthy.getNode().getStyleClass().add("healthy");

                    });

                    compteur++;
                }

            }
        }).start();

        lineChart.getData().addAll(courbeHealthy, courbeInfected, courbeDiagnosed, courbeImmune, courbeDead);

        container.getChildren().add(lineChart);

    }

    public void close() {

        stop = true;

    }
}
