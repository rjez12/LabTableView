package com.desarrollo.papeleriacampus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductoApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ProductoApplication.class.getResource("producto-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Papelería Campus - Productos");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
