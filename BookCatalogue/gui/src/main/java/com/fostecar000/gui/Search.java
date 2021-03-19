package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
// import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.function.BiConsumer;
import java.util.List;
import java.util.ArrayList;
import com.fostecar000.backend.Database;
import com.fostecar000.backend.Book;
import com.fostecar000.backend.BookQueryException;
import com.fostecar000.backend.BookQuery;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;

public class Search extends Application {

    private static final String[] FIELDS = new String[] {"Title", "Author's First Name", "Author's Last Name",
                                                         "Genre", "Series Name", "Number in Series", "Original Publication Date"};

    private static int WIDTH;
    private static int HEIGHT;
    private Database db;
    private SearchOperator root;
    private BorderPane mainPane, resultsPane;
    private Scene scene;


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

    private VBox getInitialSearchNodes() { //Group drawingGroup) {
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

        SearchElement tag = new SearchElement("Tag:");
        tag.addToPane(v, createReplacementFunction(3 + FIELDS.length));

        //SearchOperator blank = new SearchOperator(SearchOperator.Type.BLANK);
        //blank.addToPane(v, createReplacementFunction(3 + FIELDS.length));

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

        /*for (Node n : v.getChildren()) {
            ((SearchAtom)n).setDrawingGroup(drawingGroup);
        }*/

        return v;
    }

    private BorderPane getMainPane() { //Group drawingGroup) {
        // StackPane base = new StackPane();
        if (mainPane != null) return mainPane;
        mainPane = new BorderPane();
        VBox initials = getInitialSearchNodes(); // drawingGroup);

        ScrollPane center = new ScrollPane();
        center.setContent(root);
        center.setPrefSize(WIDTH - initials.getPrefWidth(), HEIGHT);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            BookQuery query = constructQuery();
            if (query == null) return;
            List<Book> results;
            try {
                results = query.query(false);
                resultsPane = getResultsPane(results);
                scene.setRoot(resultsPane);
            } catch (BookQueryException err) {
                Alert.error("Unable to create database query", "Unable to create database query from given conditions.");
                return;
            } catch (IllegalStateException err) {
                Alert.error("Failed to connect to database", "Could not open a session with the database.");
                return;
            } catch (Exception err) {
                Alert.error("Unknown exception occurred", "An unknown exception occurred.");
                err.printStackTrace();
                return;
            }
        });

        searchButton.setStyle("-fx-font-size: 15pt;");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(searchButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox centerBox = new VBox();
        centerBox.getChildren().addAll(center, buttonBox);
        VBox.setMargin(buttonBox, new Insets(10, 0, 10, 0));

        mainPane.setCenter(centerBox);
        mainPane.setRight(initials);
        BorderPane.setMargin(initials, new Insets(0, 20, 0, 20));
        
        // base.getChildren().add(pane);
        // base.getChildren().add(drawingGroup);
        
        return mainPane; // base;
    }

    private BorderPane getResultsPane(List<Book> results) {
        // must always construct a new one, in case the results argument was different
        List<VBox> elements = new ArrayList<>();
        for (Book b : results) {
            Label title = new Label(b.getTitle());
            title.setStyle("-fx-font-size: 15pt;");
            Label author = new Label(b.getAuthorFirst() + " " + b.getAuthorLast());
            
            VBox box = new VBox();
            box.getChildren().addAll(title, author);
            box.setSpacing(10);
            elements.add(box);
        }
        
        ScrollPane scroll = new ScrollPane();
        ListView<VBox> listView = new ListView<>(FXCollections.observableArrayList(elements));
        scroll.setContent(listView);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        
        Button back = new Button("Back");
        Button view = new Button("View");
        back.setStyle("-fx-font-size: 15pt;");
        view.setStyle("-fx-font-size: 15pt;");

        view.setOnAction(e -> {
            int i = listView.getSelectionModel().getSelectedIndex();
            if (i == -1) return;
            Book bookToView = results.get(i);
            scene.setRoot(View.getViewPane(bookToView, null));
        });

        back.setOnAction(e -> {
            scene.setRoot(mainPane);
        });
        
        VBox buttonBox = new VBox();
        buttonBox.getChildren().addAll(view, back);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        resultsPane = new BorderPane();
        resultsPane.setCenter(scroll);
        resultsPane.setRight(buttonBox);
        BorderPane.setMargin(buttonBox, new Insets(0, 10, 10, 10));

        return resultsPane;
    }

    private void interpret(BookQuery query, SearchAtom atom) throws NumberFormatException, BookQueryException {
        if (atom == null) return;
        if (atom.isOperator()) {
            SearchOperator op = (SearchOperator) atom;
            SearchAtom left = op.getLeft();
            SearchAtom right = op.getRight();
            switch (op.type()) {
                case AND:
                    query.and();
                    interpret(query, left);
                    interpret(query, right);
                    query.endAnd();
                    break;
                case OR:
                    query.or();
                    interpret(query, left);
                    interpret(query, right);
                    query.endOr();
                    break;
                case NOT:
                    query.not();
                    interpret(query, right);
                    break;
                case BLANK:
                    interpret(query, left);
                    break;
            }
        } else {
            SearchElement el = (SearchElement) atom;
            switch (el.getLabelText()) {
                case "Title:":
                    query.isTitle(el.getText());
                    break;
                case "Author's First Name:":
                    query.isAuthorFirst(el.getText());
                    break;
                case "Author's Last Name:":
                    query.isAuthorLast(el.getText());
                    break;
                case "Genre:":
                    query.isGenre(el.getText());
                    break;
                case "Series Name:":
                    query.isSeries(el.getText());
                    break;
                case "Number in Series:":
                    query.isNumberInSeries(Integer.valueOf(el.getText())); // already encased in try block, no need for more handling here
                    break;
                case "Original Publication Date:":
                    query.isOriginalPublicationDate(Integer.valueOf(el.getText())); // same here
                    break;
                case "Tag:":
                    query.hasTag(el.getText());
                    break;
            }
        }
    }

    private BookQuery constructQuery() {
        if (db == null) {
            Alert.error("No database connected", "There is no database to query from.");
            return null;
        }

        try {
            BookQuery query = new BookQuery();
            
            interpret(query, root);
            
            return query;
        } catch (IllegalStateException e) {
            Alert.error("Failed to connect to database", "Could not open a session with the database.");
            return null;
        } catch (BookQueryException e) {
            Alert.error("Failed to construct query", "The query could not be constructed properly.");
            return null;
        } catch (NumberFormatException e) {
            Alert.error("Illegal format", "'Number in Series' and 'Original Publication Date' must both be integers.");
            return null;
        } catch (Exception e) {
            Alert.error("Unknown exception occurred", "An unknown exception occurred.");
            return null;
        }
    }

    public void start(Stage stage) {
        stage.setTitle("Book Catalogue: Book Search");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        //ArrayList<Book> list = new ArrayList<>();
        //list.add(new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965));

        scene = new Scene(getMainPane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public void setDatabase(Database db) {
        this.db = db;
    }

    public Search() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int)(screenSize.getWidth() * 0.75);
        HEIGHT = (int) (screenSize.getHeight() * 0.75);
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