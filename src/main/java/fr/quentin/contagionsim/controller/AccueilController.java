package fr.quentin.contagionsim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
    private Spinner<Integer> nbIndiv, vIndiv, tIndiv, nbInfect, tContag, tMortal;

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

                                int defaultValue = 0;

                                if (random) {
                                    defaultValue = (int) (Math.random() * 101);
                                }

                                ((Spinner) subNode).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, defaultValue));
                                subNode.setOnKeyReleased(this::changeValue);
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

    public void run (ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentin/contagionsim/fxml/Game.fxml"));
            Parent parent = loader.load();
            stage.setScene(new Scene(parent));
            stage.show();

            MainController controller = loader.getController();
            controller.setStage(stage);
            controller.init();
            controller.runGame(nbIndiv.getValue(), vIndiv.getValue(), tIndiv.getValue(), nbInfect.getValue(), tContag.getValue(), tMortal.getValue());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generate (ActionEvent event) {

        init(true);

    }

}
