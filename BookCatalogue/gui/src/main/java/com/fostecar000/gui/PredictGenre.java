package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import java.awt.Toolkit;
import java.awt.Dimension;

public class PredictGenre extends Application {

    private static int WIDTH;
    private static int HEIGHT;
    private Stage stage;
    private TextArea blurb;
    private TextField prediction;

    public static void call() {
        try {
            new PredictGenre().start(new Stage());
        } catch (Exception e) {
            Alert.error("Could not start genre prediction GUI", e);
        }
    }

    BorderPane createPane() {
        BorderPane pane = new BorderPane();

        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
        center.setMaxWidth(Region.USE_PREF_SIZE);

        blurb = new TextArea();
        blurb.setPrefRowCount(5);
        blurb.maxWidthProperty().bind(center.prefWidthProperty());

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);        

        Button predict = new Button("Predict:");
        prediction = new TextField();
        prediction.setEditable(false);
        prediction.setPrefColumnCount(15);
        Button use = new Button("Use");
        Button cancel = new Button("Cancel");
        buttons.getChildren().addAll(predict, prediction, use, cancel);

        center.getChildren().addAll(blurb, buttons);
        pane.setCenter(center);

        cancel.setOnAction(e -> {
            stage.close();
        });
        
        return pane;
    }

    public void start(Stage passedStage) {
        stage = passedStage;
        stage.setTitle("Book Catalogue: Predict Genre");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(createPane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public PredictGenre() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.getWidth() * 5.0/16.0);
        HEIGHT = WIDTH;
    }

    public static void main(String[] args) {
        launch(args);
    }
}