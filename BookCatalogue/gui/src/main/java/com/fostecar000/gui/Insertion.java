package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import java.util.HashMap;
import java.awt.Toolkit;
import java.awt.Dimension;

public class Insertion extends Application {

    private static final String[] FIELDS = new String[] {"Title", "Author's First Name", "Author's Last Name",
                                                         "Genre", "Series Name", "Number in Series", "Original Publication Date"};

    private static int WIDTH;
    private static int HEIGHT;
    private HashMap<String, TextField> textFields;
    private ObservableList<String> tags;
    private TextField tagField;

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

    private GridPane createFieldInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setHalignment(HPos.RIGHT);
        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().addAll(leftColumn, rightColumn);

        textFields = new HashMap<>();

        int y = 0;
        for (String field : FIELDS) {
            Label label = new Label(field + ": ");
            TextField textField = new TextField();
            textFields.put(field, textField);
            grid.add(label, 0, y);
            grid.add(textField, 1, y++);
        }
        return grid;
    }

    private VBox createTagInput() {
        VBox tagContainer = new VBox();

        tags = FXCollections.observableArrayList();
        ListView<String> list = new ListView<>(tags);

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(list);

        HBox controls = new HBox();
        Label tagLabel = new Label("Add Tag:");
        tagField = new TextField();
        Button add = new Button("Add");
        controls.getChildren().addAll(tagLabel, tagField, add);
        controls.setSpacing(10);
        controls.setAlignment(Pos.CENTER);

        tagContainer.getChildren().addAll(scroll, controls);
        tagContainer.setAlignment(Pos.CENTER);

        Runnable addTag = () -> {
            tags.add(tagField.getText());
            tagField.clear();
        };

        add.setOnAction((ActionEvent e) -> {
            addTag.run();
        });

        tagField.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER)
                addTag.run();
        });

        return tagContainer;
    }

    private BorderPane createPane() {
        BorderPane pane = new BorderPane();

        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
        center.getChildren().addAll(createFieldInputs(), createTagInput());

        pane.setCenter(center);
        return pane;
    }

    public void start(Stage stage) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.getWidth() * 5.0/14.0);
        HEIGHT = WIDTH;
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