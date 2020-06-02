package fr.quentin.contagionsim.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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

        vIndiv.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 5, 0, 0.1));

        stage.getScene().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    Platform.exit();
                    break;
                case ENTER:
                    run();
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
            stage.setScene(new Scene(parent));
            stage.show();

            stage.getScene().setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE)
                    Platform.exit();
            });

            MainController controller = loader.getController();
            controller.setStage(stage);
                controller.init();
            controller.runGame(Integer.parseInt(nbIndiv.getEditor().getText()), Double.parseDouble(vIndiv.getEditor().getText()), Integer.parseInt(tIndiv.getEditor().getText()), Integer.parseInt(nbInfect.getEditor().getText()), Integer.parseInt(tContag.getEditor().getText()), Integer.parseInt(tMortal.getEditor().getText()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
