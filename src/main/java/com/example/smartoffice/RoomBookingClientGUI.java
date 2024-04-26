package com.example.smartoffice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RoomBookingClientGUI extends Application {

    private RoomBookingGrpc.RoomBookingBlockingStub stub;
    private ManagedChannel channel;
    private TextField roomTypeField;
    private TextField dateField;
    private TextField startTimeField;
    private TextField endTimeField;
    private TextField employeeNameField;
    private TextField employeeIdField;
    private TextField cancelBookingIdField;
    private TextField viewBookingIdField;
    private TextArea outputArea;

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

        // Room Booking Section
        Label roomBookingLabel = new Label("Room Booking Service");
        roomBookingLabel.setStyle("-fx-font-weight: bold");
        grid.add(roomBookingLabel, 0, 0, 2, 1);

        roomTypeField = new TextField();
        roomTypeField.setPromptText("Room Type");
        grid.add(new Label("Room Type:"), 0, 1);
        grid.add(roomTypeField, 1, 1);

        dateField = new TextField();
        dateField.setPromptText("Date (YYYY-MM-DD)");
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateField, 1, 2);

        startTimeField = new TextField();
        startTimeField.setPromptText("Start Time (HH:MM)");
        grid.add(new Label("Start Time:"), 0, 3);
        grid.add(startTimeField, 1, 3);

        endTimeField = new TextField();
        endTimeField.setPromptText("End Time (HH:MM)");
        grid.add(new Label("End Time:"), 0, 4);
        grid.add(endTimeField, 1, 4);

        employeeNameField = new TextField();
        employeeNameField.setPromptText("Your Name");
        grid.add(new Label("Your Name:"), 0, 5);
        grid.add(employeeNameField, 1, 5);

        employeeIdField = new TextField();
        employeeIdField.setPromptText("Your Employee ID");
        grid.add(new Label("Your Employee ID:"), 0, 6);
        grid.add(employeeIdField, 1, 6);

        Button bookRoomButton = new Button("Book Room");
        bookRoomButton.setOnAction(e -> bookRoom());
        grid.add(bookRoomButton, 0, 7, 2, 1);

        // Cancel Booking Section
        Label cancelBookingLabel = new Label("Cancel Booking");
        cancelBookingLabel.setStyle("-fx-font-weight: bold");
        grid.add(cancelBookingLabel, 0, 8, 2, 1);

        cancelBookingIdField = new TextField();
        cancelBookingIdField.setPromptText("Booking ID to Cancel");
        grid.add(new Label("Booking ID to Cancel:"), 0, 9);
        grid.add(cancelBookingIdField, 1, 9);

        Button cancelBookingButton = new Button("Cancel Booking");
        cancelBookingButton.setOnAction(e -> cancelBooking());
        grid.add(cancelBookingButton, 0, 10, 2, 1);

        // View Booking Details Section
        Label viewBookingLabel = new Label("View Booking Details");
        viewBookingLabel.setStyle("-fx-font-weight: bold");
        grid.add(viewBookingLabel, 0, 11, 2, 1);

        viewBookingIdField = new TextField();
        viewBookingIdField.setPromptText("Booking ID to View");
        grid.add(new Label("Booking ID to View:"), 0, 12);
        grid.add(viewBookingIdField, 1, 12);

        Button viewBookingButton = new Button("View Booking");
        viewBookingButton.setOnAction(e -> viewBookingDetails());
        grid.add(viewBookingButton, 0, 13, 2, 1);

        // Output Area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);
        grid.add(outputArea, 0, 14, 2, 1);

        // Create the scene and set it on the stage
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Room Booking Service");
        primaryStage.show();

        // Shutdown the channel when the window is closed
        primaryStage.setOnCloseRequest(event -> channel.shutdown());
    }

    private void bookRoom() {
        // Get input values
        String roomType = roomTypeField.getText().trim();
        String date = dateField.getText().trim();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        String employeeName = employeeNameField.getText().trim();
        String employeeId = employeeIdField.getText().trim();

        // Create a request message
        BookRoomRequest request = BookRoomRequest.newBuilder()
                .setRoomType(roomType)
                .setDate(date)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setEmployeeName(employeeName)
                .setEmployeeId(employeeId)
                .build();

        try {
            // Call the RPC and get the response
            BookRoomResponse response = stub.bookRoom(request);
            outputArea.appendText("Booking ID: " + response.getBookingId() + "\n");
            outputArea.appendText("Message: " + response.getMessage() + "\n");
        } catch (StatusRuntimeException e) {
            outputArea.appendText("RPC failed: " + e.getStatus() + "\n");
        }
    }

    private void cancelBooking() {
        // Get booking ID to cancel
        String bookingId = cancelBookingIdField.getText().trim();

        // Create a request message
        CancelBookingRequest request = CancelBookingRequest.newBuilder()
                .setBookingId(bookingId)
                .build();

        try {
            // Call the RPC and get the response
            CancelBookingResponse response = stub.cancelBooking(request);
            outputArea.appendText("Message: " + response.getMessage() + "\n");
        } catch (StatusRuntimeException e) {
            outputArea.appendText("RPC failed: " + e.getStatus() + "\n");
        }
    }

    private void viewBookingDetails() {
        // Get booking ID to view details
        String bookingId = viewBookingIdField.getText().trim();

        // Create a request message
        ViewBookingRequest request = ViewBookingRequest.newBuilder()
                .setBookingId(bookingId)
                .build();

        try {
            // Call the RPC and get the response
            ViewBookingResponse response = stub.viewBooking(request);
            outputArea.appendText("Room Type: " + response.getRoomType() + "\n");
            outputArea.appendText("Date: " + response.getDate() + "\n");
            outputArea.appendText("Start Time: " + response.getStartTime() + "\n");
            outputArea.appendText("End Time: " + response.getEndTime() + "\n");
            outputArea.appendText("Employee Name: " + response.getEmployeeName() + "\n");
            outputArea.appendText("Employee ID: " + response.getEmployeeId() + "\n");
            outputArea.appendText("Message: " + response.getMessage() + "\n");
        } catch (StatusRuntimeException e) {
            outputArea.appendText("RPC failed: " + e.getStatus() + "\n");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
