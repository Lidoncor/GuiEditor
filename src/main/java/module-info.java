module com.editor.guieditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires jfxtras.labs;
    requires java.desktop;
    requires javafx.swing;

    opens com.editor.guieditor to javafx.fxml;
    exports com.editor.guieditor;
}