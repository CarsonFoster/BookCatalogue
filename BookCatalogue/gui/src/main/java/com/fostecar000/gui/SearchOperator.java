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
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;

public class SearchOperator extends SearchAtom {
    private Label label;
    private Rectangle background, left, right;
    private SearchAtom leftElement, rightElement;
    private HBox box;
    private Type type;

    private static final Color COLOR_AND = Color.rgb(189, 205, 90);
    private static final Color COLOR_OR = Color.rgb(104, 69, 143);
    private static final Color COLOR_NOT = Color.rgb(176, 77, 124);
    private static final Color COLOR_BLANK = Color.rgb(0, 0, 0, 0); // completely transparent
    private static final Color COLOR_BLANK_INNER = Color.rgb(100, 100, 100);
    private static final Color DRAG_OUTLINE = Color.FORESTGREEN;

    public static enum Type {
        AND, OR, NOT, BLANK;
    }

    public SearchOperator(Type type) {
        super();
        this.type = type;
        if (type != Type.BLANK)
            label = new Label((type == Type.AND ? "AND" : (type == Type.OR ? "OR" : "NOT")));
        else setOnDragDetected(null); // cannot drag blank SearchAtoms
        
        if (type != Type.NOT) {
            left = new Rectangle();
            if (type != Type.BLANK) {
                left.setStroke(Color.WHITE);
                left.setFill(Color.WHITE);
                left.heightProperty().bind(label.heightProperty());
            } else {
                left.setStroke(COLOR_BLANK_INNER);
                left.setFill(COLOR_BLANK_INNER);
                left.setHeight(new Text("").getLayoutBounds().getHeight());
            }
            left.arcHeightProperty().bind(left.heightProperty());
            left.arcWidthProperty().bind(left.heightProperty());
            left.setWidth(50);
        }

        if (type != Type.BLANK) {
            right = new Rectangle();
            right.setStroke(Color.WHITE);
            right.setFill(Color.WHITE);
            right.arcHeightProperty().bind(right.heightProperty());
            right.arcWidthProperty().bind(right.heightProperty());
            right.setWidth(50);
            right.heightProperty().bind(label.heightProperty());
        }

        box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        box.setMaxWidth(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        if (type != Type.NOT) box.getChildren().add(left);
        if (type != Type.BLANK) box.getChildren().addAll(label, right);
        

        background = new Rectangle();
        switch (type) {
            case AND:
                background.setStroke(COLOR_AND);
                background.setFill(COLOR_AND);
                break;
            case OR:
                background.setStroke(COLOR_OR);
                background.setFill(COLOR_OR);
                label.setTextFill(Color.WHITE);
                break;
            case NOT:
                background.setStroke(COLOR_NOT);
                background.setFill(COLOR_NOT);
                label.setTextFill(Color.WHITE);
                break;
            case BLANK:
                background.setStroke(COLOR_BLANK);
                background.setFill(COLOR_BLANK);
                break;
        }
        background.arcHeightProperty().bind(background.heightProperty());
        background.arcWidthProperty().bind(background.heightProperty());
        background.widthProperty().bind(box.widthProperty().add(10));
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
                } else if (type != Type.NOT) left.setStroke(Color.WHITE);
                
                if (type != Type.BLANK && !inLeft && rightElement == null && dropSpotContainsPoint(right, right.sceneToLocal(x, y))) right.setStroke(DRAG_OUTLINE); 
                else if (type != Type.BLANK) right.setStroke(Color.WHITE);
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
                
                    double x = e.getSceneX(), y = e.getSceneY();
                    if (type != Type.NOT && leftElement == null && dropSpotContainsPoint(left, left.sceneToLocal(x, y))) {
                        source.removeFromParent();
                        setLeft(source);
                        source.setParent(box, this, true);
                        success = true;
                    } else if (type != Type.BLANK && rightElement == null && dropSpotContainsPoint(right, right.sceneToLocal(x, y))) {
                        source.removeFromParent();
                        setRight(source);
                        source.setParent(box, this, false);
                        success = true;
                    }
                } catch (Exception err) {
                    Alert.error("Unable to complete operation", "BookCatalogue was unable to complete the drag-and-drop operation");
                    //err.printStackTrace();
                    return;
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
        if (type != type.BLANK) box.getChildren().addAll(label, (rightElement == null ? right : rightElement));
    }

    protected SearchAtom deepCopy() {
        return new SearchOperator(type());
    }
}