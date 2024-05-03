/*
Smart Offices - CA Project of Distributed Systems
TemperatureControlClientGUI.java
@author Muhammad Syamil (x23104660)
26/04/2024
*/


package com.example.smartoffice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class TemperatureControlClientGUI extends Application {

    private SmartTemperatureControlGrpc.SmartTemperatureControlBlockingStub stub;
    private ManagedChannel channel;
    private TextArea outputArea;
    private ComboBox<String> workAreaComboBox;


    @Override
    public void start(Stage primaryStage) {

        // Creates a channel to the server - 9092
        channel = ManagedChannelBuilder.forAddress("localhost", 9092)
                .usePlaintext()
                .build();

        // Creates a stub for the SmartTemperatureControl service
        stub = SmartTemperatureControlGrpc.newBlockingStub(channel);

        // Sets up the UI components
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Adds UI components for temperature control function
        addTemperatureControlOptions(grid);

        // Adds output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);
        grid.add(outputArea, 0, 4, 2, 1);

        // Adds exit button for the interface
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            channel.shutdown();
            System.exit(0);
        });
        grid.add(exitButton, 0, 5, 2, 1);

        // Creates the scene and sets it on the stage
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Temperature Control Service by Syamil");
        primaryStage.show();

        // Shutdowns the channel when the window of the interface is closed
        primaryStage.setOnCloseRequest(event -> {
            channel.shutdown();
            System.exit(0);
        });
    }

    // Method to add the temperature control options to the UI
    private void addTemperatureControlOptions(GridPane grid) {

        // Temperature Control Section
        Label temperatureControlLabel = new Label("Smart Temperature Control Service");
        temperatureControlLabel.setStyle("-fx-font-weight: bold");
        grid.add(temperatureControlLabel, 0, 0, 2, 1);

        // Adds a dropdown menu for the work area selection
        List<String> workAreas = Arrays.asList("Conference Room Dublin", "Conference Room Cork", "Conference Room Galway",
                "Meeting Room Limerick", "Meeting Room Waterford", "General Workstations", "Cafeteria");
        workAreaComboBox = new ComboBox<>();
        workAreaComboBox.getItems().addAll(workAreas);
        workAreaComboBox.setValue("Select Work Area");
        grid.add(new Label("Select Work Area:"), 0, 1);
        grid.add(workAreaComboBox, 1, 1);

        // Adds temperature input field
        TextField temperatureField = new TextField();
        temperatureField.setPromptText("Enter Temperature");
        grid.add(new Label("Set Temperature (°C):"), 0, 2);
        grid.add(temperatureField, 1, 2);

        // Adds buttons for setting and getting the temperature
        Button setTemperatureButton = new Button("Set Temperature");
        Button getTemperatureButton = new Button("Get Temperature");
        grid.add(setTemperatureButton, 0, 3);
        grid.add(getTemperatureButton, 1, 3);

        // Sets the action for setting temperature button
        setTemperatureButton.setOnAction(event -> {
            String workArea = workAreaComboBox.getValue();
            String temperatureStr = temperatureField.getText();
            if (workArea == null || workArea.isEmpty() || temperatureStr.isEmpty()) {
                showErrorDialog("Error", "Please select a work area and enter a temperature.");
                return;
            }
            try {
                float temperature = Float.parseFloat(temperatureStr);
                if (temperature < 16 || temperature > 30) {
                    showErrorDialog("Error", "Temperature must be between 16°C and 30°C.");
                    return;
                }
                setTemperature(workArea, temperature);
            } catch (NumberFormatException e) {
                showErrorDialog("Error", "Please enter a valid temperature.");
            }
        });


        // Sets the action for getting temperature button
        getTemperatureButton.setOnAction(event -> {
            String workArea = workAreaComboBox.getValue();
            if (workArea == null || workArea.isEmpty()) {
                showErrorDialog("Error", "Please select a work area.");
                return;
            }
            getTemperature(workArea);
        });
    }

    // Method to set the temperature for a work area
    private void setTemperature(String workArea, float temperature) {
        TemperatureRequest request = TemperatureRequest.newBuilder()
                .setWorkArea(workArea)
                .setTemperature(temperature)
                .build();
        try {
            TemperatureResponse response = stub.setTemperature(request);
            outputArea.appendText("Server Message: " + response.getMessage() + "\n");
        } catch (StatusRuntimeException e) {
            showErrorDialog("Error", "Failed to communicate with the server. Please try again.");
        }
    }

    // Method to get the temperature for a work area
    private void getTemperature(String workArea) {
        TemperatureRequest request = TemperatureRequest.newBuilder()
                .setWorkArea(workArea)
                .build();
        try {
            TemperatureResponse response = stub.getTemperature(request);
            outputArea.appendText("Server Message: " + response.getMessage() + "\n");
        } catch (StatusRuntimeException e) {
            showErrorDialog("Error", "Failed to communicate with the server. Please try again.");
        }
    }

    // Method to show error dialog
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}
