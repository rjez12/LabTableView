module com.desarrollo.papeleriacampus {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.desarrollo.papeleriacampus to javafx.fxml;
    exports com.desarrollo.papeleriacampus;
}