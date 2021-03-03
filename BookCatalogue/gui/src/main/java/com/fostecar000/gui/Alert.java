package com.fostecar000.gui;

import javafx.scene.control.Alert.AlertType;

public abstract class Alert {
    protected static void alert(AlertType type, String title, String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle("Book Catalogue");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    protected static void error(String title, String msg) {
        alert(AlertType.ERROR, title, msg);
    }

    protected static void error(String title, Throwable e) {
        StringBuilder trace = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) trace.append(el.toString() + "\n");
        error(title, trace.toString());
    }

    protected static void info(String title, String msg) {
        alert(AlertType.INFORMATION, title, msg);
    }
}