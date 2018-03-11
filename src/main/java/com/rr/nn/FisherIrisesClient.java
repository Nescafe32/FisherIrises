package com.rr.nn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FisherIrisesClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/irises.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Fisher's irises");
        primaryStage.setScene(new Scene(root, 773, 470));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}