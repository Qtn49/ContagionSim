package fr.quentin.contagionsim.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {

    private Stage stage;
    @FXML
    private HBox parameters;
    @FXML
    private Spinner<Integer> nbIndiv, tIndiv, nbInfect, tContag, tMortal;

    @FXML
    private Spinner<Double> vIndiv;

    @FXML
    private Button generer, run;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void init () {
        init(false);
    }

    public void init (boolean random) {

        for (Node node : parameters.getChildren()) {

            if (node instanceof VBox) {

                for (Node node1 : ((VBox) node).getChildren()) {

                    if (node1 instanceof HBox) {
                        for (Node subNode : ((HBox) node1).getChildren()) {

                            if (subNode instanceof Spinner) {

                                ((Spinner) subNode).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 0));

                                subNode.setOnKeyReleased(this::changeValue);
                            }

                        }
                    }
                }

            }

        }

        nbIndiv.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, 0));
        vIndiv.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 5, 0, 0.1));
        tIndiv.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, 0));

        generer.setTooltip(new Tooltip("touche (g) pour générer"));
        run.setTooltip(new Tooltip("touche (entrée) pour lancer"));

        stage.getScene().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    Platform.exit();
                    break;
                case ENTER:
                    run();
                    break;
                case G:
                    generate();
                    break;
            }
        });

    }

    public void generate () {
        double max, min, step, value;

        for (Node node : parameters.getChildren()) {

            if (node instanceof VBox) {

                for (Node node1 : ((VBox) node).getChildren()) {

                    if (node1 instanceof HBox) {
                        for (Node subNode : ((HBox) node1).getChildren()) {

                            if (subNode instanceof Spinner) {

                                if (((Spinner) subNode).getValueFactory() instanceof SpinnerValueFactory.IntegerSpinnerValueFactory) {
                                    max = ((SpinnerValueFactory.IntegerSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMax();
                                    min = ((SpinnerValueFactory.IntegerSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMin();
                                    step = ((SpinnerValueFactory.IntegerSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getAmountToStepBy();
                                    value = Math.random() * max + min;
                                    ((Spinner) subNode).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory((int) min, (int) max, (int) value, (int) step));
                                } else {
                                    max = ((SpinnerValueFactory.DoubleSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMax();
                                    min = ((SpinnerValueFactory.DoubleSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getMin();
                                    step = ((SpinnerValueFactory.DoubleSpinnerValueFactory) ((Spinner) subNode).getValueFactory()).getAmountToStepBy();
                                    value = Math.random() * max + min;
                                    ((Spinner) subNode).setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, value, step));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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

            int nbIndiv = Integer.parseInt(this.nbIndiv.getEditor().getText()), tIndiv = Integer.parseInt(this.tIndiv.getEditor().getText()), nbInfect = Integer.parseInt(this.nbInfect.getEditor().getText());
            double vIndiv = Double.parseDouble(this.vIndiv.getEditor().getText()), tContag = Double.parseDouble(this.tContag.getEditor().getText()) / 100, tMortal = Double.parseDouble(this.tMortal.getEditor().getText()) / 100;

            int longueur;
            double squareRoot = Math.sqrt(nbIndiv);

            Canvas mainCanvas = (Canvas) loader.getNamespace().get("mainCanvas");

            // check if nbIndiv is a perfect square (formula from Anouar Anefaoui)
            if (squareRoot - Math.floor(squareRoot) == 0) {
                longueur = (int) (squareRoot * (2 * tIndiv + 1) + 1);
            }else
                longueur = (int) (2 * (tIndiv + 1) + Math.floor(squareRoot) * (2 * tIndiv + 1));

            ((HBox) mainCanvas.getParent()).setPrefWidth(longueur);
            ((HBox) mainCanvas.getParent()).setPrefHeight(longueur);

            mainCanvas.setWidth(longueur);
            mainCanvas.setHeight(longueur);

            MainController controller = loader.getController();
            controller.setStage(stage);
            controller.runGame(nbIndiv, vIndiv, tIndiv, nbInfect, tContag, tMortal);

            stage.setScene(new Scene(parent));
            stage.show();

            stage.getScene().setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE)
                    Platform.exit();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
