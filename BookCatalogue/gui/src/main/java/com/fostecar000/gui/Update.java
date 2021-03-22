package com.fostecar000.gui;

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
import java.util.HashMap;
import com.fostecar000.backend.Database;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.BookCatalogue;

public class Update {

    private static final String[] FIELDS = new String[] {"Title", "Author's First Name", "Author's Last Name",
                                                         "Genre", "Series Name", "Number in Series", "Original Publication Date"};

    private Database db;
    private Book b;
    private HashMap<String, TextField> textFields;
    private ObservableList<String> tags;
    private TextField tagField;
    private Label successMessage;

    private void addField(String fieldName, String defaultValue, HashMap<String, TextField> fieldMap, GridPane grid, int y) {
        Label label = new Label(fieldName + ": ");
        TextField textField = new TextField(defaultValue);
        fieldMap.put(fieldName, textField);
        grid.add(label, 0, y);
        grid.add(textField, 1, y);
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
        addField(FIELDS[0], b.getTitle(), textFields, grid, y++);
        addField(FIELDS[1], b.getAuthorFirst(), textFields, grid, y++);
        addField(FIELDS[2], b.getAuthorLast(), textFields, grid, y++);
        addField(FIELDS[3], b.getGenre(), textFields, grid, y++);
        addField(FIELDS[4], b.getSeries(), textFields, grid, y++);
        addField(FIELDS[5], Integer.toString(b.getNumberInSeries()), textFields, grid, y++);
        addField(FIELDS[6], Integer.toString(b.getOriginalPublicationDate()), textFields, grid, y++);

        return grid;
    }

    private VBox createTagInput() {
        VBox tagContainer = new VBox();

        tags = FXCollections.observableArrayList(b.getTagNames());
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

        add.setOnAction(e -> {
            addTag.run();
        });

        tagField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                addTag.run();
        });

        return tagContainer;
    }

    public BorderPane getPane() {
        BorderPane pane = new BorderPane();

        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
        center.getChildren().addAll(createFieldInputs(), createTagInput());

        Button update = new Button("Update Book");
        update.setPrefHeight(50);
        update.setPrefWidth(update.getPrefHeight() * 2);

        successMessage = new Label();

        update.setOnAction(e -> {
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
            // update book object
            Alert.info("update book", ":)");
            try {
                // send to database
            } catch (Exception ex) {
                Alert.error("Could not insert book into database", ex);
            }
            successMessage.setText("Success! Updated book '" + textFields.get("Title").getText() + "' in the database.");
        });

        HBox bottom = new HBox(20);
        bottom.getChildren().addAll(successMessage, update);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        pane.setCenter(center);
        pane.setBottom(bottom);
        BorderPane.setAlignment(bottom, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(bottom, new Insets(20, 20, 20, 20));
        return pane;
    }

    public void setDatabase(Database db) {
        this.db = db;
    }

    public Update() {

    }

    public Update(Book b) {
        this.b = b;
    }

    public Update(Database db) {
        this();
        this.db = db;
    }

    public Update(Book b, Database db) {
        this.b = b;
        this.db = db;
    }
}