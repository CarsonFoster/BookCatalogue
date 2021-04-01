package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import java.awt.Toolkit;
import java.awt.Dimension;
import com.fostecar000.backend.Database;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.BookCatalogue;

public class Main extends Application {

    private static int WIDTH;
    private static int HEIGHT;
    private static final Database db = getDatabaseHandle();

    private VBox createPane() {
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Region.USE_PREF_SIZE);

        Button insert = new Button("Insert a Book");
        Button search = new Button("Search for a Book");
        Button help = new Button("Help");
        Button[] buttons = new Button[] {insert, search, help};
        for (Button b : buttons) {
            b.setStyle("-fx-font-size: 15pt;");
            b.setMaxWidth(Double.MAX_VALUE);
        }

        insert.setOnAction(e -> {
            Insertion.call(db);
        });

        search.setOnAction(e -> {
            Search.call(db);
        });

        help.setOnAction(e -> {
            showHelp();
        });
        
        box.getChildren().addAll(insert, search, help);
        VBox tmp = new VBox();
        tmp.setAlignment(Pos.CENTER);
        tmp.getChildren().add(box);
        return tmp;
    }

    public void showHelp() {
        Alert.info("Helpful Information for BookCatalogue", "");
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(createPane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public Main() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.getWidth() * 0.25);
        HEIGHT = WIDTH;
    }

    private static Database getDatabaseHandle() {
        try {
            return new Database();
        } catch (Exception e) {
            Alert.error("Failed to connect to database; check database.properties", e);
            return null;
        }
    }

    public static void main(String[] args) {
        if (db == null) return;
        try (db) {
            launch(args);
        } catch (Exception e) {
            Alert.error("Exception in program", e);
        }
    }
}