package com.fostecar000.gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import java.util.function.Consumer;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.Database;

public class View {
    
    private Book b;
    private Consumer<Pane> setPaneFunction;
    private Pane previous;
    private Database db;

    public View(Book b, Consumer<Pane> setPaneFunction, Pane previous, Database db) {
        this.b = b;
        this.setPaneFunction = setPaneFunction;
        this.previous = previous;
        this.db = db;
    }

    public BorderPane getPane() {
        BorderPane pane = new BorderPane();

        VBox text = new VBox();
        text.setSpacing(5);
        Label title = new Label(b.getTitle());
        title.setStyle("-fx-font-size: 20pt;");
        Label author = new Label(b.getAuthorFirst() + " " + b.getAuthorLast());
        author.setStyle("-fx-font-size: 15pt;");
        text.getChildren().addAll(title, author);
        if (!b.getGenre().equals("")) {
            Label genre = new Label("Genre: " + b.getGenre());
            text.getChildren().add(genre);
        }
        if (!b.getSeries().equals("")) {
            Label series = new Label(b.getSeries() + (b.getNumberInSeries() >= 1 ? " (#" + b.getNumberInSeries() + ")" : ""));
            text.getChildren().add(series);
        }
        if (b.getOriginalPublicationDate() >= 1) {
            Label originalPublicationDate = new Label("Published in " + b.getOriginalPublicationDate());
            text.getChildren().add(originalPublicationDate);
        }

        ListView<String> tags = new ListView<>(FXCollections.observableArrayList(b.getTagNames()));
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(tags);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        Button delete = new Button("Delete");
        Button update = new Button("Update");
        Button back = new Button("Back");
        delete.setStyle("-fx-font-size: 15pt;");
        update.setStyle("-fx-font-size: 15pt;");
        back.setStyle("-fx-font-size: 15pt;");

        update.setOnAction(e -> {
            Update u = new Update(b, setPaneFunction, pane);
            setPaneFunction.accept(u.getPane());
        });

        back.setOnAction(e -> {
            setPaneFunction.accept(previous);
        });

        HBox backBox = new HBox();
        backBox.getChildren().add(back);
        backBox.setAlignment(Pos.CENTER_LEFT);

        HBox deleteAndUpdate = new HBox();
        deleteAndUpdate.getChildren().addAll(delete, update);
        deleteAndUpdate.setAlignment(Pos.CENTER_RIGHT);
        deleteAndUpdate.setSpacing(10);

        HBox buttonBoxesBox = new HBox(); // box to hold the button boxes
        buttonBoxesBox.getChildren().addAll(backBox, deleteAndUpdate);
        HBox.setHgrow(backBox, Priority.ALWAYS);
        HBox.setHgrow(deleteAndUpdate, Priority.ALWAYS);

        pane.setCenter(text);
        BorderPane.setMargin(text, new Insets(20, 0, 0, 20));
        pane.setBottom(buttonBoxesBox);
        BorderPane.setMargin(buttonBoxesBox, new Insets(10, 10, 10, 10));
        pane.setRight(scroll);
        BorderPane.setMargin(scroll, new Insets(20, 10, 0, 0));

        return pane;
    }
}