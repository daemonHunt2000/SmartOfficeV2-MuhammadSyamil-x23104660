/*
Smart Offices - CA Project of Distributed Systems
RoomBookingClientGUI.java
@author Muhammad Syamil (x23104660)
24/04/2024
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;



public class RoomBookingClientGUI extends Application {

    private RoomBookingGrpc.RoomBookingBlockingStub stub;
    private ManagedChannel channel;
    private TextArea outputArea;
    private ComboBox<String> roomTypeComboBox;
    private GridPane grid;

    // Room Types
    private enum RoomType {CONFERENCE_ROOM, MEETING_ROOM}


    @Override
    public void start(Stage primaryStage) {

        // Creates a channel for the server
        channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .build();

        // Create a stub for the RoomBooking service
        stub = RoomBookingGrpc.newBlockingStub(channel);

        // Sets up the components of the UI
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Adds UI components for room booking options
        addRoomBookingOptions(grid);

        // Adds the output area after a function is executed
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);
        grid.add(outputArea, 0, 6, 2, 1);

        // Creates the scene and sets it onto the stage
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Room Booking Service by Syamil");
        primaryStage.show();

        // Shutdowns the channel when the window is closed
        primaryStage.setOnCloseRequest(event -> channel.shutdown());
    }


    private void addRoomBookingOptions(GridPane grid) {

        // Room Booking Section
        Label roomBookingLabel = new Label("Room Booking Service");
        roomBookingLabel.setStyle("-fx-font-weight: bold");
        grid.add(roomBookingLabel, 0, 0, 2, 1);

        // Creates a dropdown menu for room booking options
        ComboBox<String> bookingOptions = new ComboBox<>();
        bookingOptions.getItems().addAll("Book a Room", "View Booking Details", "Exit the Service");
        bookingOptions.setValue("Select an option");
        grid.add(new Label("Select Option:"), 0, 1);
        grid.add(bookingOptions, 1, 1);

        // Adds action listener to the dropdown menu
        bookingOptions.setOnAction(event -> {
            String selectedOption = bookingOptions.getSelectionModel().getSelectedItem();
            if (selectedOption != null) {
                switch (selectedOption) {
                    case "Book a Room":
                        bookRoom();
                        break;
                    case "View Booking Details":
                        viewBookingDetails();
                        break;
                    case "Exit the Service":

                        // Closes the application
                        channel.shutdown();
                        System.exit(0);
                        break;
                }
            }
        });

        // Adds room type selection for booking
        List<String> roomTypes = Arrays.asList("Conference Room Dublin", "Conference Room Cork", "Conference Room Galway", "Meeting Room Limerick", "Meeting Room Waterford");
        roomTypeComboBox = new ComboBox<>();
        roomTypeComboBox.getItems().addAll(roomTypes);
        roomTypeComboBox.setValue("Choose room type");
        grid.add(new Label("Select Room:"), 0, 2);
        grid.add(roomTypeComboBox, 1, 2);
    }


    private void bookRoom() {

        // Gets the selected room type
        String selectedRoomType = roomTypeComboBox.getValue();
        if (selectedRoomType.equals("Choose room type")) {
            showErrorDialog("Notice", "Please select the room that you want to book.");
            return;
        }

        // Date and time selection
        LocalDate currentDate = LocalDate.now();
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(currentDate);
        datePicker.setDayCellFactory(datePicker1 -> new DateCell() {

            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY
                        || item.isBefore(currentDate));
            }
        });

        ComboBox<String> startTimeComboBox = new ComboBox<>();
        ComboBox<String> endTimeComboBox = new ComboBox<>();
        for (int i = 9; i <= 18; i++) {
            startTimeComboBox.getItems().add(i + ":00");
            endTimeComboBox.getItems().add(i + ":00");
        }

        // Creates a dialog for date and time selection
        GridPane dateTimeGrid = new GridPane();
        dateTimeGrid.setHgap(10);
        dateTimeGrid.setVgap(10);
        dateTimeGrid.add(new Label("Date:"), 0, 0);
        dateTimeGrid.add(datePicker, 1, 0);
        dateTimeGrid.add(new Label("Start Time:"), 0, 1);
        dateTimeGrid.add(startTimeComboBox, 1, 1);
        dateTimeGrid.add(new Label("End Time:"), 0, 2);
        dateTimeGrid.add(endTimeComboBox, 1, 2);

        Dialog<String> dateTimeDialog = new Dialog<>();
        dateTimeDialog.setTitle("Date and Time Selection");
        dateTimeDialog.getDialogPane().setContent(dateTimeGrid);

        // Adds buttons to the dialog
        ButtonType bookButton = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dateTimeDialog.getDialogPane().getButtonTypes().addAll(bookButton, ButtonType.CANCEL);

        // Waits for the user input
        dateTimeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButton) {
                LocalDate selectedDate = datePicker.getValue();
                String startTime = startTimeComboBox.getValue();
                String endTime = endTimeComboBox.getValue();

                if (startTime == null || endTime == null || selectedDate == null) {
                    showErrorDialog("Invalid Selection", "Please select a date and time.");
                    return null;
                }

                // Performs validation
                return selectedDate.toString() + ";" + startTime + ";" + endTime;
            }
            return null;
        });

        String dateTimeResult = dateTimeDialog.showAndWait().orElse(null);
        if (dateTimeResult == null) {
            return; // <-- User canceled the dialog or the input was invalid
        }

        String[] dateTimeParts = dateTimeResult.split(";");
        LocalDate selectedDate = LocalDate.parse(dateTimeParts[0]);
        String startTime = dateTimeParts[1];
        String endTime = dateTimeParts[2];

        // Employee Details input
        TextInputDialog employeeNameDialog = new TextInputDialog();
        employeeNameDialog.setTitle("Employee Name");
        employeeNameDialog.setHeaderText("Enter Your Name:");
        employeeNameDialog.setContentText("Name:");

        String employeeName = employeeNameDialog.showAndWait().orElse(null);
        if (employeeName == null || !employeeName.matches("[a-zA-Z ]+")) {
            showErrorDialog("Invalid Input", "Please enter a valid name (letters and spaces only).");
            return;
        }

        TextInputDialog employeeIdDialog = new TextInputDialog();
        employeeIdDialog.setTitle("Employee ID");
        employeeIdDialog.setHeaderText("Enter Your Employee ID:");
        employeeIdDialog.setContentText("Employee ID:");

        String employeeId = employeeIdDialog.showAndWait().orElse(null);
        if (employeeId == null || !employeeId.matches("[A-Za-z]\\d{8}")) {
            showErrorDialog("Invalid Input", "Please enter a valid employee ID (e.g., x01234567).");
            return;
        }

        // Creates a request message
        BookRoomRequest request = BookRoomRequest.newBuilder()
                .setRoomType(selectedRoomType)
                .setDate(selectedDate.toString())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setEmployeeName(employeeName)
                .setEmployeeId(employeeId)
                .build();


        try {

            // Calls the RPC and gets the response
            BookRoomResponse response = stub.bookRoom(request);
            if (response != null) {
                outputArea.appendText("Booking ID: " + response.getBookingId() + "\n");
                outputArea.appendText("Server Message: " + response.getMessage() + "\n");
            } else {
                showErrorDialog("Booking Failed", "Failed to book the room. Please try again later.");
            }
        } catch (StatusRuntimeException e) {
            showErrorDialog("Server Error", "Failed to communicate with the server. Please try again.");
        }
    }


    private void viewBookingDetails() {

        // Gets booking ID from the user input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Booking Details");
        dialog.setHeaderText("Enter Booking ID:");
        dialog.setContentText("Booking ID (case sensitive):");

        String bookingId = dialog.showAndWait().orElse(null);
        if (bookingId == null || bookingId.isEmpty()) {
            return; // <-- User canceled the dialog or input was null
        }

        // Creates a request message
        ViewBookingRequest request = ViewBookingRequest.newBuilder()
                .setBookingId(bookingId)
                .build();


        try {

            // Calls the RPC and gets the response
            ViewBookingResponse response = stub.viewBooking(request);
            if (response != null) {
                outputArea.appendText("Room Type: " + response.getRoomType() + "\n");
                outputArea.appendText("Date: " + response.getDate() + "\n");
                outputArea.appendText("Start Time: " + response.getStartTime() + "\n");
                outputArea.appendText("End Time: " + response.getEndTime() + "\n");
                outputArea.appendText("Employee Name: " + response.getEmployeeName() + "\n");
                outputArea.appendText("Employee ID: " + response.getEmployeeId() + "\n");
                outputArea.appendText("Server Message: " + response.getMessage() + "\n");
            } else {
                showErrorDialog("Booking Details Not Found", "No booking found with the provided ID.");
            }
        } catch (StatusRuntimeException e) {
            showErrorDialog("Server Error", "Failed to communicate with the server. Please provide a valid booking ID (case sensitive).");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
