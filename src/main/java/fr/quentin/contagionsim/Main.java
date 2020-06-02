package fr.quentin.contagionsim;

import fr.quentin.contagionsim.controller.AccueilController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The entry point of the program.
 * @author Quentin Cld
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Accueil.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();

        AccueilController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.init();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
