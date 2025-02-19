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
    requires javafx.swing;
    requires javafx.web;
    requires org.json;
    requires org.apache.pdfbox;
    requires org.apache.commons.logging;


    opens com.example.client.model.machine to com.fasterxml.jackson.databind;
    opens com.example.client.model.user to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.client.model.resource to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.client.model.planning to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.client.model.program to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.client.model.recipe to javafx.base, com.fasterxml.jackson.databind;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.controller;
    opens com.example.client.controller to javafx.fxml;
    exports com.example.client.utils;
    opens com.example.client.utils to javafx.fxml;
    exports com.example.client.controller.user;
    opens com.example.client.controller.user to javafx.fxml;
    exports com.example.client.controller.machine;
    opens com.example.client.controller.machine to javafx.fxml;
    exports com.example.client.controller.resources;
    opens com.example.client.controller.resources to javafx.fxml;
    opens com.example.client.controller.planning to javafx.fxml;
    exports com.example.client.controller.planning;
    opens com.example.client.controller.recipe to javafx.fxml;
    exports com.example.client.controller.recipe;
    opens com.example.client.controller.program to javafx.fxml;
    exports com.example.client.controller.program;

}