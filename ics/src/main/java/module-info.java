module com.example.ics {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ics to javafx.fxml;
    exports com.example.ics;
}