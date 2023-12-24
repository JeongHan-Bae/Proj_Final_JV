module com.example.partjava {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.partjava to javafx.fxml;
    exports com.example.partjava;
}