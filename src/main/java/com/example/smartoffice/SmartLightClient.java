/*
Smart Offices - CA Project of Distributed Systems
SmartLightClient.java
@author Muhammad Syamil (x23104660)
21/04/2024
*/


package com.example.smartoffice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;




public class SmartLightClient extends Application {

    private SmartLightGrpc.SmartLightBlockingStub stub;
    private ManagedChannel channel;
    private Label responseLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Creates a channel for the server
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // Creates a stub for the SmartLight service
        stub = SmartLightGrpc.newBlockingStub(channel);

        primaryStage.setTitle("Smart Light Client by Syamil");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);


        // For Workspace Selection
        ComboBox<String> workspaceComboBox = new ComboBox<>();
        workspaceComboBox.getItems().addAll(
                "Conference Room Dublin",
                "Conference Room Cork",
                "Conference Room Galway",
                "Meeting Room Limerick",
                "Meeting Room Waterford",
                "Server Room",
                "Cafeteria"
        );
        workspaceComboBox.setPromptText("Select Workspace");
        GridPane.setConstraints(workspaceComboBox, 0, 0);


        // For Action Selection
        Button turnOnButton = new Button("Turn On Lights");
        Button turnOffButton = new Button("Turn Off Lights");
        Button exitButton = new Button("Exit Client");
        VBox actionButtons = new VBox(10);
        actionButtons.getChildren().addAll(turnOnButton, turnOffButton, exitButton);
        GridPane.setConstraints(actionButtons, 0, 1);


        // Response labels
        responseLabel = new Label();
        GridPane.setConstraints(responseLabel, 0, 2);

        gridPane.getChildren().addAll(workspaceComboBox, actionButtons, responseLabel);

        turnOnButton.setOnAction(e -> performTurnOn(workspaceComboBox.getValue()));
        turnOffButton.setOnAction(e -> performTurnOff(workspaceComboBox.getValue()));
        exitButton.setOnAction(e -> stopClient());

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void performTurnOn(String workspace) {

        // Sends a request to turn on the lights for the selected workspace
        LightRequest request = LightRequest.newBuilder()
                .setWorkspaceId(workspace)
                .build();
        LightResponse response = stub.turnOn(request);
        updateResponse(response.getStatus());
    }

    private void performTurnOff(String workspace) {

        // Sends a request to turn off the lights for the selected workspace
        LightRequest request = LightRequest.newBuilder()
                .setWorkspaceId(workspace)
                .build();
        LightResponse response = stub.turnOff(request);
        updateResponse(response.getStatus());
    }

    // Updates response label with the given message
    private void updateResponse(String message) {
        responseLabel.setText(message);
    }

    private void stopClient() {

        // Shutdowns the channel when the smart light client is closed
        channel.shutdown();
        System.exit(0);
    }

    @Override
    public void stop() {

        // Shutdowns the channel when the application is closed
        channel.shutdown();
    }
}
