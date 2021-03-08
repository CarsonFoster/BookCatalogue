package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
    private SearchOperator root;
    public static String dragboardIdentifier = "f0S";

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

    private VBox getInitialSearchNodes(Group drawingGroup) {
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

        SearchOperator blank = new SearchOperator(SearchOperator.Type.BLANK);
        blank.addToPane(v, createReplacementFunction(3 + FIELDS.length));

        v.setOnDragOver(e -> {
            if (e.getGestureSource() != this) {
                Dragboard db = e.getDragboard();
                if (db.hasString() && db.getString().equals(dragboardIdentifier)) {
                    e.acceptTransferModes(TransferMode.MOVE);
                }
            }
            e.consume();
        });

        v.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString() && db.getString().equals(dragboardIdentifier)) {
                SearchAtom source;
                try {
                    source = (SearchAtom) e.getGestureSource();
                    source.removeFromParent(); // remove nodes dropped on the vbox
                    success = true;
                } catch (Exception err) {
                    Alert.error("Unable to complete operation", "BookCatalogue was unable to complete the drag-and-drop operation");
                    e.setDropCompleted(false);
                    return;
                }
            }

            e.setDropCompleted(success);
            e.consume();
        });

        for (Node n : v.getChildren()) {
            ((SearchAtom)n).setDrawingGroup(drawingGroup);
        }

        return v;
    }

    private StackPane getPane(Group drawingGroup) {
        StackPane base = new StackPane();
        
        BorderPane pane = new BorderPane();
        VBox initials = getInitialSearchNodes(drawingGroup);

        ScrollPane center = new ScrollPane();
        center.setContent(root);
        center.setPrefSize(WIDTH - initials.getPrefWidth(), HEIGHT);

        pane.setCenter(center);
        pane.setRight(initials);
        BorderPane.setMargin(initials, new Insets(0, 20, 0, 20));
        
        base.getChildren().add(pane);
        base.getChildren().add(drawingGroup);
        
        return base;
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue: Book Search");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(getPane(new Group()), WIDTH, HEIGHT);
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
        WIDTH = (int)(screenSize.getWidth() * 0.5);
        HEIGHT = WIDTH;
        root = new SearchOperator(SearchOperator.Type.BLANK);
    }

    public Search(Database db) {
        this();
        this.db = db;
    }

    public static void main(String[] args) {
        launch(args);
    }
}