package com.fostecar000.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import com.fostecar000.backend.Database;

public class TestGui extends Application {
    private static int WIDTH;
    private static int HEIGHT;
    private static List<Runnable> todo;

    public static void doInGuiThread(Runnable r) {
        if (todo == null) todo = new ArrayList<>();
        todo.add(r);
    }

    public void start(Stage stage) {
        stage.setTitle("Testing GUI");
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        Scene scene = new Scene(new Pane(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        if (todo != null)
            for (Runnable r : todo) r.run();
    }

    public TestGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.getWidth() * 5.0/14.0);
        HEIGHT = WIDTH;
    }

    public static void main(String[] args) {
        launch(args);
    }
}