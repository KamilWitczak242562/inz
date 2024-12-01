module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;


    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.controller;
    opens com.example.client.controller to javafx.fxml;
    exports com.example.client.utils;
    opens com.example.client.utils to javafx.fxml;
}