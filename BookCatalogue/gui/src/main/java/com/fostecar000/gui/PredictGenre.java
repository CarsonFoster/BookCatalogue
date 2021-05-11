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
import java.io.IOException;

import com.fostecar000.genre.GenreIdentifier;

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
        blurb.setWrapText(true);
        blurb.maxWidthProperty().bind(center.prefWidthProperty());

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);        

        Button predict = new Button("Predict:");
        prediction = new TextField();
        prediction.setEditable(false);
        prediction.setPrefColumnCount(15);
        Button cancel = new Button("Cancel");
        buttons.getChildren().addAll(predict, prediction, cancel);

        center.getChildren().addAll(blurb, buttons);
        pane.setCenter(center);

        cancel.setOnAction(e -> {
            stage.close();
        });

        predict.setOnAction(e -> {
            String blurbText = blurb.getText();
            System.out.println(blurbText);
            try {
                String predictedGenre = GenreIdentifier.predictGenre("glove.6b.100d.bin", "neuralNet.bin", blurbText); // change for packaging: will be in base directory
                //String predictedGenre = GenreIdentifier.predictGenre("C:\\Users\\cwf\\Documents\\BookCatalogue\\glove.6b\\glove.6b.100d.bin", "ai_data\\bestGraph120min.bin", blurbText);
                prediction.setText(predictedGenre);
            } catch (IOException err) {
                Alert.error("Could not predict genre", err);
            }
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