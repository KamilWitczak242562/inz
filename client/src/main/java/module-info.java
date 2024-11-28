module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.client to javafx.fxml;
    exports com.example.client;
}