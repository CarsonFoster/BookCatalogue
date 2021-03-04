package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

public abstract class SearchAtom extends StackPane {
    //protected StackPane pane;
    protected Pane parent;

    public abstract boolean isOperator();
    
    public SearchAtom() {
        super();
        //pane = new StackPane();

        setOnDragDetected((MouseEvent e) -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("f0S"); // for [f]ostecar00[0] [S]earching
            db.setContent(content);
            e.consume();
        });

        setOnDragDone(e -> {
            // TODO
            e.consume();
        });
    }

    public void addToPane(Pane parent) {
        if (this.parent != null) throw new RuntimeException("already has a parent");
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void setParent(Pane parent) {
        if (this.parent != null) throw new RuntimeException("already has a parent");
        this.parent = parent;
    }

    public void removeFromParent() {
        if (parent == null) throw new RuntimeException("no parent to remove");
        parent.getChildren().remove(this);
        parent = null;
    }

    /*public StackPane getPane() {
        return pane;
    }*/
}