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
import java.nio.file.Files;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class GraphController {

    public static final int TIME_MAX = 3000;
    private Game game;

    private int compteur = 0;

    @FXML
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> courbeHealthy, courbeInfected, courbeDiagnosed, courbeImmune, courbeDead;

    public void setGame(Game game) {
        this.game = game;
    }

    public void init () {

        courbeHealthy = new XYChart.Series<>();
        courbeInfected = new XYChart.Series<>();
        courbeDiagnosed = new XYChart.Series<>();
        courbeImmune = new XYChart.Series<>();
        courbeDead = new XYChart.Series<>();

        courbeHealthy.setName("En bonne santé");
        courbeInfected.setName("Infecté(s)");
        courbeDiagnosed.setName("Diagnostiqué(s)");
        courbeImmune.setName("Immunisé(s)");
        courbeDead.setName("Décédé(s)");

        lineChart.getData().addAll(courbeHealthy, courbeInfected, courbeDiagnosed, courbeImmune, courbeDead);

    }

    public void run () {

        if (end())
            return;

        courbeHealthy.getData().add(new XYChart.Data<>(compteur, game.getStats(State.HEALTHY)));
        courbeInfected.getData().add(new XYChart.Data<>(compteur, game.getStats(State.INFECTED)));
        courbeDiagnosed.getData().add(new XYChart.Data<>(compteur, game.getStats(State.DIAGNOSED)));
        courbeImmune.getData().add(new XYChart.Data<>(compteur, game.getStats(State.IMMUNE)));
        courbeDead.getData().add(new XYChart.Data<>(compteur, game.getStats(State.DEAD)));

        courbeHealthy.getNode().getStyleClass().add("healthy");

        compteur += 50;



    }

    public boolean end () {

        return game.getStats(State.INFECTED) == 0 && game.getStats(State.DIAGNOSED) == 0;

    }

}
