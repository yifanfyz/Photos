package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import java.util.ArrayList;



public class Photos extends Application implements EventHandler<ActionEvent> {
    public static ArrayList<User> userCollection = new ArrayList<>();
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/login.fxml"));
        primaryStage.setTitle("PrivateApp");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();

    }



    @Override
    public void handle(ActionEvent actionEvent) {

    }
}
