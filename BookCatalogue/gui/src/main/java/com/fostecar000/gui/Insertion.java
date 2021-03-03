package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import com.fostecar000.backend.Database;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.BookCatalogue;

public class Insertion extends Application {

    private static final String[] FIELDS = new String[] {"Title", "Author's First Name", "Author's Last Name",
                                                         "Genre", "Series Name", "Number in Series", "Original Publication Date"};

    private static int WIDTH;
    private static int HEIGHT;
    private Database db;
    private HashMap<String, TextField> textFields;
    private ObservableList<String> tags;
    private TextField tagField;
    private Label successMessage;

    public static void call(Database database) {
        try {
            new Insertion(database).start(new Stage());
        } catch (Exception e) {
            Alert.error("Could not start Insertion GUI", e);
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
        tagContainer.setVgrow(scroll, Priority.NEVER);

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

    BorderPane createPane() {
        BorderPane pane = new BorderPane();

        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
        center.getChildren().addAll(createFieldInputs(), createTagInput());

        Button insert = new Button("Insert Book");
        insert.setPrefHeight(50);
        insert.setPrefWidth(insert.getPrefHeight() * 2);

        successMessage = new Label();

        insert.setOnAction((ActionEvent e) -> {
            if (db == null) {
                Alert.error("Cannot connect to database", "Could not connect with the database.");
                return;
            }
            int number, publicationDate;
            try {
                String numberStr = textFields.get("Number in Series").getText();
                String pubStr = textFields.get("Original Publication Date").getText();
                if (numberStr != null && numberStr.length() > 0) number = Integer.valueOf(numberStr);
                else number = -1;
                if (pubStr != null && pubStr.length() > 0) publicationDate = Integer.valueOf(pubStr);
                else publicationDate = -1;
            } catch (NumberFormatException ex) {
                Alert.error("Invalid Input", "Both the 'Number in Series' and 'Original Publication Date' fields"
                     + " must be integers, if specified.");
                return;
            }
            Book b = new Book(textFields.get("Title").getText(),
                                  textFields.get("Author's First Name").getText(),
                                  textFields.get("Author's Last Name").getText(),
                                  textFields.get("Genre").getText(),
                                  textFields.get("Series Name").getText(),
                                  number,
                                  publicationDate);
            try {
                BookCatalogue.addBook(b, tags);
            } catch (Exception ex) {
                Alert.error("Could not insert book into database", ex);
            }
            successMessage.setText("Success! Inserted book '" + textFields.get("Title").getText() + "' into the database.");
            tags.clear();
            for (String field : FIELDS) textFields.get(field).clear();
        });

        HBox bottom = new HBox(20);
        bottom.getChildren().addAll(successMessage, insert);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        pane.setCenter(center);
        pane.setBottom(bottom);
        BorderPane.setAlignment(bottom, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(bottom, new Insets(20, 20, 20, 20));
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

    public void setDatabase(Database db) {
        this.db = db;
    }

    public Insertion() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.getWidth() * 5.0/14.0);
        HEIGHT = WIDTH;
    }

    public Insertion(Database db) {
        this();
        this.db = db;
    }

    public static void main(String[] args) {
        launch(args);
    }
}