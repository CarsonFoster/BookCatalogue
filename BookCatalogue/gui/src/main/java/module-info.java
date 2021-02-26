module gui {
    requires backend;
    requires javafx.controls;
    opens com.fostecar000.gui to javafx.graphics;
}