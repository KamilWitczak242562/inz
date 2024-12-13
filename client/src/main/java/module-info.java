module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires static lombok;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.kordamp.ikonli.javafx;
    requires java.logging;


    opens com.example.client.model.machine to com.fasterxml.jackson.databind;
    opens com.example.client.model.user to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.client.model.resource to javafx.base, com.fasterxml.jackson.databind;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.controller;
    opens com.example.client.controller to javafx.fxml;
    exports com.example.client.utils;
    opens com.example.client.utils to javafx.fxml;
}