package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.function.BiConsumer;
import com.fostecar000.backend.Database;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.BookCatalogue;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;

public class Search extends Application {

    private static final String[] FIELDS = new String[] {"Title", "Author's First Name", "Author's Last Name",
                                                         "Genre", "Series Name", "Number in Series", "Original Publication Date"};

    private static int WIDTH;
    private static int HEIGHT;
    private Database db;

    public static void call(Database database) {
        try {
            new Search(database).start(new Stage());
        } catch (Exception e) {
            Alert.error("Could not start Search GUI", e);
        }
    }

    private BiConsumer<Pane, SearchAtom> createReplacementFunction(int index) {
        final int indexFinal = index;
        return new BiConsumer<Pane, SearchAtom>() {
            public void accept(Pane p, SearchAtom replacement) {
                p.getChildren().add(indexFinal, replacement);
                replacement.setParent(p, this);
            }
        };
    }

    private VBox getInitialSearchNodes() {
        VBox v = new VBox();
        v.setSpacing(10);
        v.setAlignment(Pos.CENTER);

        SearchOperator and = new SearchOperator(SearchOperator.Type.AND);
        SearchOperator or = new SearchOperator(SearchOperator.Type.OR);
        SearchOperator not = new SearchOperator(SearchOperator.Type.NOT);

        and.addToPane(v, createReplacementFunction(0));
        or.addToPane(v, createReplacementFunction(1));
        not.addToPane(v, createReplacementFunction(2));

        for (int i = 0; i < FIELDS.length; i++) {
            String field = FIELDS[i];
            SearchElement el = new SearchElement(field + ":");
            el.addToPane(v, createReplacementFunction(3 + i));
        }

        return v;
    }

    private BorderPane getPane() {
        BorderPane pane = new BorderPane();

        //pane.setCenter();
        pane.setLeft(getInitialSearchNodes());
        return pane;
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue: Book Search");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(getPane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        /*try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
        and.setRight(authorFirst);*/
    }

    public void setDatabase(Database db) {
        this.db = db;
    }

    public Search() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = Math.rint(screenSize.getWidth() * 0.5);
        HEIGHT = WIDTH;
    }

    public Search(Database db) {
        this();
        this.db = db;
    }

    public static void main(String[] args) {
        launch(args);
    }
}