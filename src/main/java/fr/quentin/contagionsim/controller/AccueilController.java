package fr.quentin.contagionsim.controller;

import fr.quentin.contagionsim.model.Game;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class AccueilController implements Initializable {

    private Stage stage;
    @FXML
    private HBox parameters;

    @FXML
    private Spinner<Integer> nbIndiv, tIndiv, nbInfect;

    @FXML
    private CheckBox randomSpeed;

    @FXML
    private Spinner<Double> vIndiv, tContag, tMortal;

    @FXML
    private Button generer, run;

    private static int nbIndivVal, tIndivVal, nbInfectVal;
    private static double vIndivVal, tContagVal, tMortalVal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void init() {

        nbIndiv.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, nbIndivVal));
        vIndiv.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 5, 0, vIndivVal));
        tIndiv.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, tIndivVal));
        nbInfect.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 190, nbInfectVal));
        tContag.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(5, 90, tContagVal));
        tMortal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(5, 90, tMortalVal));

        generer.setTooltip(new Tooltip("touche (G) pour générer"));
        run.setTooltip(new Tooltip("touche (V) pour lancer"));

        randomSpeed.setOnAction(event -> {
            vIndiv.setDisable(randomSpeed.isSelected());
        });

        stage.getScene().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    Platform.exit();
                    break;
                case V:
                    run();
                    break;
                case G:
                    generate();
                    break;
            }
        });

    }

    public void generate () {
        double max, min, value;

        for (Node node : parameters.getChildren()) {

            if (node instanceof VBox) {

                for (Node node1 : ((VBox) node).getChildren()) {

                    if (node1 instanceof HBox) {
                        for (Node subNode : ((HBox) node1).getChildren()) {

                            if (subNode instanceof Spinner) {

                                if (((Spinner) subNode).getValueFactory() instanceof SpinnerValueFactory.IntegerSpinnerValueFactory) {
                                    max = ((SpinnerValueFactory.IntegerSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMax();
                                    min = ((SpinnerValueFactory.IntegerSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMin();
                                    value = Math.random() * max + min;
                                    ((Spinner) subNode).getValueFactory().setValue((int) value);
                                } else {
                                    max = ((SpinnerValueFactory.DoubleSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMax();
                                    min = ((SpinnerValueFactory.DoubleSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMin();
                                    value = Math.random() * max + min;
                                    ((Spinner) subNode).getValueFactory().setValue(value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void changeValue (KeyEvent event) {

        Spinner<Integer> spinner = (Spinner<Integer>) event.getSource();

        switch (event.getCode()) {
            case UP:
                event.consume();
                spinner.increment();
                break;
            case DOWN:
                event.consume();
                spinner.decrement();
                break;
        }
    }

    public void run() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Game.fxml"));
            Parent parent = loader.load();

            vIndiv.getValueFactory().setValue(Math.random() * 5 + 1);

            nbIndivVal = nbIndiv.getValue();
            tIndivVal = tIndiv.getValue();
            vIndivVal = vIndiv.getValue();
            nbInfectVal = nbInfect.getValue();
            tContagVal = tContag.getValue();
            tMortalVal = tMortal.getValue();

            int longueur;
            double squareRoot = Math.sqrt(nbIndivVal);

            Canvas mainCanvas = (Canvas) loader.getNamespace().get("mainCanvas");

            // check if nbIndiv is a perfect square (formula from Anouar Anefaoui)
            if (squareRoot - Math.floor(squareRoot) == 0) {
                longueur = (int) (squareRoot * (2 * tIndivVal + 1) + 1);
            }else
                longueur = (int) (2 * (tIndivVal + 1) + Math.floor(squareRoot) * (2 * tIndivVal + 1));

            ((HBox) mainCanvas.getParent()).setPrefWidth(longueur);
            ((HBox) mainCanvas.getParent()).setPrefHeight(longueur);

            mainCanvas.setWidth(longueur);
            mainCanvas.setHeight(longueur);

            MainController controller = loader.getController();
            controller.setStage(stage);

            Game game = new Game((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight(), nbIndivVal, vIndivVal, tIndivVal, nbInfectVal, tContagVal / 100, tMortalVal / 100);

            controller.runGame(game);

            stage.setScene(new Scene(parent));
//            stage.show();

            stage.getScene().setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE)
                    Platform.exit();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
