module gui {
    requires backend;
    requires genre;
    requires javafx.controls;
    requires java.desktop;
    opens com.fostecar000.gui to javafx.graphics;
}