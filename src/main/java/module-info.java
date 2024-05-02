/*
Smart Offices - CA Project of Distributed Systems
module-info.java
@author Muhammad Syamil (x23104660)
24/04/2024
*/


module SmartOfficeV2 {
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.netty;
    requires protobuf.java;
    requires io.grpc.protobuf;
    requires java.annotation;
    requires com.google.common;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;

    exports com.example.smartoffice;

}