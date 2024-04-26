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
// import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class RoomBookingClientGUI extends Application {

    private RoomBookingGrpc.RoomBookingBlockingStub stub;
    private ManagedChannel channel;
    private TextArea outputArea;

    // Room types
    private enum RoomType {CONFERENCE_ROOM, MEETING_ROOM}

    @Override
    public void start(Stage primaryStage) {
        // Create a channel to the server
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // Create a stub for the RoomBooking service
        stub = RoomBookingGrpc.newBlockingStub(channel);

        // Set up the UI components
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Add UI components for room booking options
        addRoomBookingOptions(grid);

        // Add output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);
        grid.add(outputArea, 0, 6, 2, 1);

        // Create the scene and set it on the stage
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Room Booking Service");
        primaryStage.show();

        // Shutdown the channel when the window is closed
        primaryStage.setOnCloseRequest(event -> channel.shutdown());
    }

    private void addRoomBookingOptions(GridPane grid) {
        // Room Booking Section
        Label roomBookingLabel = new Label("Room Booking Service");
        roomBookingLabel.setStyle("-fx-font-weight: bold");
        grid.add(roomBookingLabel, 0, 0, 2, 1);

        // Create a dropdown menu for room booking options
        ComboBox<String> bookingOptions = new ComboBox<>();
        bookingOptions.getItems().addAll("Book a Room", "View Booking Details", "Exit the Service");
        bookingOptions.setValue("Select an option");
        grid.add(new Label("Select Option:"), 0, 1);
        grid.add(bookingOptions, 1, 1);

        // Add action listener to the dropdown menu
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
                        // Close the application
                        channel.shutdown();
                        System.exit(0);
                        break;
                }
            }
        });
    }

    private void bookRoom() {
        // Room type selection
        List<String> roomTypes = Arrays.asList("Conference Room", "Meeting Room");
        ChoiceDialog<String> roomTypeDialog = new ChoiceDialog<>(roomTypes.get(0), roomTypes);
        roomTypeDialog.setTitle("Room Type Selection");
        roomTypeDialog.setHeaderText("Select Room Type:");
        roomTypeDialog.setContentText("Choose room type:");

        // Get the selected room type
        String selectedRoomType = roomTypeDialog.showAndWait().orElse(null);
        if (selectedRoomType == null) {
            return; // User canceled the dialog
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

        // Create a dialog for date and time selection
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

        // Add buttons to the dialog
        ButtonType bookButton = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dateTimeDialog.getDialogPane().getButtonTypes().addAll(bookButton, ButtonType.CANCEL);

        // Wait for user input
        dateTimeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButton) {
                LocalDate selectedDate = datePicker.getValue();
                String startTime = startTimeComboBox.getValue();
                String endTime = endTimeComboBox.getValue();

                if (startTime == null || endTime == null || selectedDate == null) {
                    showErrorDialog("Invalid Selection", "Please select a date and time.");
                    return null;
                }

                // Perform validation
                return selectedDate.toString() + ";" + startTime + ";" + endTime;
            }
            return null;
        });

        String dateTimeResult = dateTimeDialog.showAndWait().orElse(null);
        if (dateTimeResult == null) {
            return; // User canceled the dialog or input was invalid
        }

        String[] dateTimeParts = dateTimeResult.split(";");
        LocalDate selectedDate = LocalDate.parse(dateTimeParts[0]);
        String startTime = dateTimeParts[1];
        String endTime = dateTimeParts[2];

        // Employee details input
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

        // Create a request message
        BookRoomRequest request = BookRoomRequest.newBuilder()
                .setRoomType(selectedRoomType)
                .setDate(selectedDate.toString())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setEmployeeName(employeeName)
                .setEmployeeId(employeeId)
                .build();

        try {
            // Call the RPC and get the response
            BookRoomResponse response = stub.bookRoom(request);
            if (response != null) {
                outputArea.appendText("Booking ID: " + response.getBookingId() + "\n");
                outputArea.appendText("Message: " + response.getMessage() + "\n");
            } else {
                showErrorDialog("Booking Failed", "Failed to book the room. Please try again later.");
            }
        } catch (StatusRuntimeException e) {
            showErrorDialog("Server Error", "Failed to communicate with the server. Please provide a valid booking ID (case sensitive).");
        }
    }

    private void viewBookingDetails() {
        // Get booking ID from user input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Booking Details");
        dialog.setHeaderText("Enter Booking ID:");
        dialog.setContentText("Booking ID (case sensitive):");

        String bookingId = dialog.showAndWait().orElse(null);
        if (bookingId == null || bookingId.isEmpty()) {
            return; // User canceled the dialog or input was empty
        }

        // Create a request message
        ViewBookingRequest request = ViewBookingRequest.newBuilder()
                .setBookingId(bookingId)
                .build();

        try {
            // Call the RPC and get the response
            ViewBookingResponse response = stub.viewBooking(request);
            if (response != null) {
                outputArea.appendText("Room Type: " + response.getRoomType() + "\n");
                outputArea.appendText("Date: " + response.getDate() + "\n");
                outputArea.appendText("Start Time: " + response.getStartTime() + "\n");
                outputArea.appendText("End Time: " + response.getEndTime() + "\n");
                outputArea.appendText("Employee Name: " + response.getEmployeeName() + "\n");
                outputArea.appendText("Employee ID: " + response.getEmployeeId() + "\n");
                outputArea.appendText("Message: " + response.getMessage() + "\n");
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
