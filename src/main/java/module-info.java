module de.sterzsolutions.calculatefx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.jfoenix;

    opens de.sterzsolutions.calculatefx to javafx.fxml;
    exports de.sterzsolutions.calculatefx;
}