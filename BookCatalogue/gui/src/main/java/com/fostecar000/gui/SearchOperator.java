package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.DragEvent;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;

public class SearchOperator extends SearchAtom {
    private Label label;
    private Rectangle background, left, right;
    private SearchAtom leftElement, rightElement;
    private HBox box;
    private Type type;

    private static final Color COLOR = Color.rgb(189, 205, 90);
    private static final Color DRAG_OUTLINE = Color.FORESTGREEN;

    public static enum Type {
        AND, OR, NOT;
    }

    public SearchOperator(Type type) {
        super();
        this.type = type;
        label = new Label((type == Type.AND ? "AND" : (type == Type.OR ? "OR" : "NOT")));
        //label.setTextFill(Color.WHITE);
        
        if (type != Type.NOT) {
            left = new Rectangle();
            left.setStroke(Color.WHITE);
            left.setFill(Color.WHITE);
            left.arcHeightProperty().bind(left.heightProperty());
            left.arcWidthProperty().bind(left.heightProperty());
            left.setWidth(50);
            left.heightProperty().bind(label.heightProperty());
        }

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
        if (type != Type.NOT) box.getChildren().add(left);
        box.getChildren().addAll(label, right);
        

        background = new Rectangle();
        background.setStroke(COLOR);
        background.setFill(COLOR);
        background.arcHeightProperty().bind(background.heightProperty());
        background.arcWidthProperty().bind(background.heightProperty());
        background.widthProperty().bind(box.widthProperty().add(20));
        background.heightProperty().bind(box.heightProperty().add(10));

        getChildren().addAll(background, box);
        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(box, Pos.CENTER);

        setOnDragOver(e -> {
            if (isDragAcceptable(e, this)) {
                e.acceptTransferModes(TransferMode.MOVE);
                boolean inLeft = false;
                double x = e.getSceneX(), y = e.getSceneY();
                
                if (type != Type.NOT && leftElement == null && dropSpotContainsPoint(left, left.sceneToLocal(x, y))) {
                    left.setStroke(DRAG_OUTLINE);
                    inLeft = true;
                } else left.setStroke(Color.WHITE);

                if (!inLeft && rightElement == null && dropSpotContainsPoint(right, right.sceneToLocal(x, y))) right.setStroke(DRAG_OUTLINE); 
                else right.setStroke(Color.WHITE);
            }
            e.consume();
        });

        setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString() && db.getString().equals("f0S")) {
                SearchAtom source;
                try {
                    source = (SearchAtom) e.getGestureSource();
                } catch (Exception err) {
                    Alert.error("Unable to complete operation", "BookCatalogue was unable to complete the drag-and-drop operation");
                    return;
                }
                
                double x = e.getSceneX(), y = e.getSceneY();
                if (type != Type.NOT && leftElement == null && dropSpotContainsPoint(left, left.sceneToLocal(x, y))) {
                    source.removeFromParent();
                    setLeft(source);
                    source.setParent(box, this, true);
                    success = true;
                } else if (rightElement == null && dropSpotContainsPoint(right, right.sceneToLocal(x, y))) {
                    source.removeFromParent();
                    setRight(source);
                    source.setParent(box, this, false);
                    success = true;
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    private static boolean isDragAcceptable(DragEvent e, StackPane target) {
        if (e.getGestureSource() == target) return false;
        Dragboard db = e.getDragboard();
        return db.hasString() && db.getString().equals("f0S");
    }

    private static double square(double num) {
        return num * num;
    }

    private static double distanceSquared(double centerX, double centerY, double x, double y) {
        return square(centerX - x) + square(centerY - y);
    }

    private static boolean dropSpotContainsPoint(Rectangle target, Point2D point) {
        double x = point.getX(), y = point.getY();
        double rectW = target.getWidth(), rectH = target.getHeight(), rectDiameter = rectH;
        if (x >= rectDiameter/2 && x <= rectW - rectDiameter/2 && y >= 0 && y <= rectH) return true; // check the rectangular part of the drop spot
        return ((distanceSquared(rectDiameter/2, rectH/2, x, y) <= square(rectDiameter)/4) || 
                (distanceSquared(rectW - rectDiameter/2, rectH/2, x, y) <= square(rectDiameter)/4));
    }

    public boolean isOperator() {
        return true;
    }

    public Type type() {
        return type;
    }

    public void setLeft(SearchAtom newLeft) {
        if (type == Type.NOT) throw new RuntimeException("there is no left element to set when the operator is a NOT");
        leftElement = newLeft;
        resetChildren();
    }

    public void setRight(SearchAtom newRight) {
        rightElement = newRight;
        resetChildren();
    }

    public void removeLeft() {
        setLeft(null);
    }

    public void removeRight() {
        setRight(null);
    }

    private void resetChildren() {
        box.getChildren().clear();
        if (type != Type.NOT && leftElement == null) box.getChildren().add(left);
        else if (type != Type.NOT) box.getChildren().add(leftElement);
        box.getChildren().addAll(label, (rightElement == null ? right : rightElement));
    }
}