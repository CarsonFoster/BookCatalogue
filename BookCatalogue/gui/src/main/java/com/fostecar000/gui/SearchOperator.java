package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;

public class SearchOperator extends SearchAtom {
    private Label label;
    private Rectangle background, left, right;
    private SearchAtom leftElement, rightElement;
    private HBox box;
    private Type type;

    private static final Color COLOR_STROKE = Color.GREY;
    private static final Color COLOR_AND = Color.rgb(189, 205, 90);
    private static final Color COLOR_OR = Color.rgb(104, 69, 143);
    private static final Color COLOR_NOT = Color.rgb(176, 77, 124);
    private static final Color COLOR_BLANK = Color.rgb(0, 0, 0, 0); // completely transparent
    private static final Color COLOR_BLANK_INNER = Color.rgb(100, 100, 100);
    private static final Color DRAG_OUTLINE = Color.FORESTGREEN;
    private static final double TRIANGLE_HEIGHT_WIDTH = 10;

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
        /*if (type == Type.BLANK) {
            box.setSpacing(0);
            Polygon triangle = new Polygon(0, 0, TRIANGLE_HEIGHT_WIDTH, TRIANGLE_HEIGHT_WIDTH/2, TRIANGLE_HEIGHT_WIDTH, -TRIANGLE_HEIGHT_WIDTH/2);
            triangle.setFill(COLOR_BLANK_INNER);
            triangle.setStroke(COLOR_BLANK_INNER);

            triangle.setOnDragDetected(e -> {
                Dragboard db = triangle.startDragAndDrop(TransferMode.COPY);

                ClipboardContent content = new ClipboardContent();
                content.putString(Search.dragboardIdentifier);
                db.setContent(content);
                e.consume();
                System.out.println("started triangle");
            });

            box.getChildren().add(triangle);
        }*/
        if (type != Type.NOT) box.getChildren().add(left);
        if (type != Type.BLANK) box.getChildren().addAll(label, right);
        

        background = new Rectangle();
        if (type != Type.BLANK) {
            background.setStrokeWidth(1);
            background.setStroke(COLOR_STROKE);
        }
        
        switch (type) {
            case AND:
                background.setFill(COLOR_AND);
                break;
            case OR:
                background.setFill(COLOR_OR);
                label.setTextFill(Color.WHITE);
                break;
            case NOT:
                background.setFill(COLOR_NOT);
                label.setTextFill(Color.WHITE);
                break;
            case BLANK:
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
                e.acceptTransferModes(TransferMode.MOVE);//, TransferMode.COPY);
                boolean inLeft = false;
                double x = e.getSceneX(), y = e.getSceneY();
                
                if (type != Type.NOT && leftElement == null && dropSpotContainsPoint(left, left.sceneToLocal(x, y))) {
                    left.setStroke(DRAG_OUTLINE);
                    inLeft = true;
                    //drawLine(e, left);
                } else if (type != Type.NOT) left.setStroke(Color.WHITE);
                
                if (type != Type.BLANK && !inLeft && rightElement == null && dropSpotContainsPoint(right, right.sceneToLocal(x, y))) {
                    right.setStroke(DRAG_OUTLINE); 
                    //drawLine(e, right);
                } else if (type != Type.BLANK) right.setStroke(Color.WHITE);

                
            }
            e.consume();
        });

        setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString() && db.getString().equals(Search.dragboardIdentifier)) {
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
                    e.setDropCompleted(false);
                    //err.printStackTrace();
                    return;
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    /*private void drawLine(DragEvent e, Rectangle rect) {
        if (e.getAcceptedTransferMode() == TransferMode.COPY) {
            System.out.println("here");
            // from a Polygon in a blank node
            Polygon triangle;
            try {
                triangle = (Polygon) e.getGestureSource();
            } catch (Exception err) { 
                err.printStackTrace();
                return; }
            Point2D triangleOrigin = triangle.localToScene(0, 0);
            Point2D rectCenter = rect.localToScene(rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
            Line line = new Line(triangleOrigin.getX(), triangleOrigin.getY(), rectCenter.getX(), rectCenter.getY());
            drawingGroup.getChildren().add(line);
        }
    }*/

    private static boolean isDragAcceptable(DragEvent e, StackPane target) {
        if (e.getGestureSource() == target) return false;
        Dragboard db = e.getDragboard();
        return db.hasString() && db.getString().equals(Search.dragboardIdentifier);
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
        if (type == Type.BLANK) throw new RuntimeException("there is no right element to set when it is a blank operator");
        rightElement = newRight;
        resetChildren();
    }

    public void removeLeft() {
        setLeft(null);
    }

    public void removeRight() {
        setRight(null);
    }

    public SearchAtom getLeft() {
        return leftElement;
    }

    public SearchAtom getRight() {
        return rightElement;
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