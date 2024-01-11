module com.example.partjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.testng;
    requires org.junit.jupiter.api;


    opens com.example.partjava to javafx.fxml;
    exports com.example.partjava;
    exports DataTests;
}