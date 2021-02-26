package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class Insertion extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    protected static void error(String title, String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Book Catalogue");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void call() {
        try {
            new Insertion().start(new Stage());
        } catch (Exception e) {
            StringBuilder trace = new StringBuilder();
            for (StackTraceElement el : e.getStackTrace()) trace.append(el.toString());
            error("Could not start Insertion GUI", trace.toString());
        }
    }

    private BorderPane createPane() {
        BorderPane pane = new BorderPane();
        VBox center = new VBox();
        return pane;
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue: Insert Book");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(createPane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}