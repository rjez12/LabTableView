module com.desarrollo.papeleriacampus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.desarrollo.papeleriacampus to javafx.fxml;
    opens com.desarrollo.papeleriacampus.controller to javafx.fxml;
    exports com.desarrollo.papeleriacampus;
}