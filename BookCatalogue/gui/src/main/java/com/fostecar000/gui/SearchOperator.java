package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

public class SearchOperator implements SearchAtom {
    private StackPane pane;
    private Label label;
    private Rectangle background, left, right;
    private SearchAtom leftElement, rightElement;
    private HBox box;
    private Type type;

    private static final Color COLOR = Color.rgb(189, 205, 90);

    public static enum Type {
        AND, OR, NOT;
    }

    public SearchOperator(Type type) {
        this.type = type;
        pane = new StackPane();
        label = new Label((isAnd ? "AND" : "OR"));
        //label.setTextFill(Color.WHITE);
        
        left = new Rectangle();
        left.setStroke(Color.WHITE);
        left.setFill(Color.WHITE);
        left.arcHeightProperty().bind(left.heightProperty());
        left.arcWidthProperty().bind(left.heightProperty());
        left.setWidth(50);
        left.heightProperty().bind(label.heightProperty());

        right = new Rectangle();
        right.setStroke(Color.WHITE);
        right.setFill(Color.WHITE);
        right.arcHeightProperty().bind(right.heightProperty());
        right.arcWidthProperty().bind(right.heightProperty());
        right.setWidth(50);
        right.heightProperty().bind(label.heightProperty());

        box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        box.setMaxWidth(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.getChildren().addAll(left, label, right);
        

        background = new Rectangle();
        background.setStroke(COLOR);
        background.setFill(COLOR);
        background.arcHeightProperty().bind(background.heightProperty());
        background.arcWidthProperty().bind(background.heightProperty());
        background.widthProperty().bind(box.widthProperty().add(20));
        background.heightProperty().bind(box.heightProperty().add(10));

        pane.getChildren().addAll(background, box);
        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(box, Pos.CENTER);
    }

    public StackPane getPane() {
        return pane;
    }

    public boolean isOperator() {
        return true;
    }

    public Type type() {
        return type;
    }

    public void setLeft(SearchAtom newLeft) {
        leftElement = newLeft;
        resetChildren();
    }

    public void setRight(SearchAtom newRight) {
        rightElement = newRight;
        resetChildren();
    }

    private void resetChildren() {
        box.getChildren().clear();
        box.getChildren().addAll((leftElement == null ? left : leftElement.getPane()), label, (rightElement == null ? right : rightElement.getPane()));
    }
}