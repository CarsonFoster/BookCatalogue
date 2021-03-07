package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Dimension;
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

    private VBox getInitialSearchNodes() {
        VBox v = new VBox();
        v.setSpacing(10);
        v.setAlignment(Pos.CENTER);

        SearchOperator and = new SearchOperator(SearchOperator.Type.AND);
        SearchOperator or = new SearchOperator(SearchOperator.Type.OR);
        SearchOperator not = new SearchOperator(SearchOperator.Type.NOT);

        and.addToPane(v);
        or.addToPane(v);
        not.addToPane(v);

        for (String field : FIELDS) {
            SearchElement el = new SearchElement(field + ":");
            el.addToPane(v);
        }

        return v;
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue: Book Search");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(getInitialSearchNodes(), WIDTH, HEIGHT);
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
        WIDTH = (int) (screenSize.getWidth() * 5.0/14.0);
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