package com.fostecar000.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import java.util.function.BiConsumer;

public abstract class SearchAtom extends StackPane {
    //protected StackPane pane;
    protected Pane parent;
    protected SearchOperator parentAtom;
    protected boolean isLeft;
    protected boolean replace;
    protected BiConsumer<Pane, SearchAtom> replaceFunction;

    public abstract boolean isOperator();
    protected abstract SearchAtom deepCopy();
    
    public SearchAtom() {
        super();

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
        addToPane(parent, null);
    }

    public void addToPane(Pane parent, BiConsumer<Pane, SearchAtom> replaceFunction) {
        if (this.parent != null) throw new RuntimeException("already has a parent");
        this.parent = parent;
        parent.getChildren().add(this);
        if (replaceFunction != null) {
            replace = true;
            this.replaceFunction = replaceFunction;
        } else replace = false;
    }

    public void setParent(Pane parent) {
        setParent(parent, null);
    }

    public void setParent(Pane parent, BiConsumer<Pane, SearchAtom> replaceFunction) {
        if (this.parent != null) throw new RuntimeException("already has a parent");
        this.parent = parent;
        if (replaceFunction != null) {
            replace = true;
            this.replaceFunction = replaceFunction;
        } else replace = false;
    }

    public void setParent(Pane parent, SearchOperator parentAtom, boolean isLeft) {
        if (this.parent != null) throw new RuntimeException("already has a parent");
        this.parent = parent;
        this.parentAtom = parentAtom;
        this.isLeft = isLeft;
        replace = false; // never replace an atom inside of another atom
    }

    public void removeFromParent() {
        if (parent == null) throw new RuntimeException("no parent to remove");
        parent.getChildren().remove(this);
        if (parentAtom != null) {
            if (isLeft) parentAtom.removeLeft();
            else parentAtom.removeRight();
            parentAtom = null;
        }
        if (replace) {
            replaceFunction.accept(parent, deepCopy());
        }
        parent = null;
    }
}