/*
 * @author Cristian Zazo Millán
 */
package electionresults;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ElectionResults extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(root);   
        stage.setScene(scene);
        stage.show();
        stage.setTitle("RESULTADOS ELECTORALES");
        stage.setMinHeight(730);
        stage.setMinWidth(730);
    }

    public static void main(String[] args) {
        launch(args);
    }   
}