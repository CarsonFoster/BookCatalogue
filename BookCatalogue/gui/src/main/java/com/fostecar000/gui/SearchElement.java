package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.beans.binding.Bindings;

public class SearchElement extends SearchAtom {
    private TextField text;
    private Label label;
    private Rectangle r;

    private static final Color COLOR = Color.SLATEBLUE;

    public SearchElement(String labelText) {
        super();
        text = new TextField();
        text.prefColumnCountProperty().bind(Bindings.min(8, Bindings.max(1, text.lengthProperty())));
        label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        
        HBox box = new HBox();
        box.getChildren().addAll(label, text);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        box.setMaxWidth(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);

        r = new Rectangle();
        r.setStroke(COLOR);
        r.setFill(COLOR);
        r.arcHeightProperty().bind(r.heightProperty());
        r.arcWidthProperty().bind(r.heightProperty());
        
        r.widthProperty().bind(box.widthProperty().add(20));
        r.heightProperty().bind(box.heightProperty().add(10));

        getChildren().addAll(r, box);
        StackPane.setAlignment(r, Pos.CENTER);
        StackPane.setAlignment(box, Pos.CENTER);
    }

    public String getText() {
        return text.getText();
    }

    public String getLabelText() {
        return label.getText();
    }

    public boolean isOperator() {
        return false;
    }
}