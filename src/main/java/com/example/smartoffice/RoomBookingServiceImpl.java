package com.example.smartoffice;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class RoomBookingServiceImpl extends RoomBookingGrpc.RoomBookingImplBase {

    // Map to store room bookings
    private final Map<String, BookingDetails> bookings = new HashMap<>();

    @Override
    public void bookRoom(BookRoomRequest request, StreamObserver<BookRoomResponse> responseObserver) {
        // Get booking details from the request
        String roomType = request.getRoomType();
        String date = request.getDate();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        String employeeName = request.getEmployeeName();
        String employeeId = request.getEmployeeId();

        // Validate the requested time
        if (!isValidTime(startTime, endTime)) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid booking time. Please book within working hours (9am - 6pm).")
                    .asRuntimeException());
            return;
        }

        // Check room availability
        if (isRoomAvailable(roomType, date, startTime, endTime)) {
            // Generate a unique booking ID
            String bookingId = generateBookingId();

            // Create booking details object
            BookingDetails bookingDetails = new BookingDetails(roomType, date, startTime, endTime, employeeName, employeeId);

            // Store booking details
            bookings.put(bookingId, bookingDetails);

            // Send response with booking ID
            BookRoomResponse response = BookRoomResponse.newBuilder()
                    .setBookingId(bookingId)
                    .setMessage("Room booked successfully.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // Room not available
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Room is not available for the requested time.")
                    .asRuntimeException());
        }
    }

    @Override
    public void cancelBooking(CancelBookingRequest request, StreamObserver<CancelBookingResponse> responseObserver) {
        // Get booking ID from request
        String bookingId = request.getBookingId();

        // Check if booking ID exists
        if (bookings.containsKey(bookingId)) {
            // Remove booking
            bookings.remove(bookingId);

            // Send response
            CancelBookingResponse response = CancelBookingResponse.newBuilder()
                    .setMessage("Booking canceled successfully.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // Booking ID not found
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Booking not found for ID: " + bookingId)
                    .asRuntimeException());
        }
    }



    @Override
    public void viewBooking(ViewBookingRequest request, StreamObserver<ViewBookingResponse> responseObserver) {
        // Get booking ID from request
        String bookingId = request.getBookingId();

        // Check if booking ID exists
        if (bookings.containsKey(bookingId)) {
            // Get booking details
            BookingDetails bookingDetails = bookings.get(bookingId);

            // Build response
            ViewBookingResponse response = ViewBookingResponse.newBuilder()
                    .setRoomType(bookingDetails.getRoomType())
                    .setDate(bookingDetails.getDate())
                    .setStartTime(bookingDetails.getStartTime())
                    .setEndTime(bookingDetails.getEndTime())
                    .setEmployeeName(bookingDetails.getEmployeeName())
                    .setEmployeeId(bookingDetails.getEmployeeId())
                    .setMessage("Booking details retrieved successfully.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // Booking ID not found
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Booking not found for ID: " + bookingId)
                    .asRuntimeException());
        }
    }

    // Method to generate a unique booking ID
    private String generateBookingId() {
        return "B" + (bookings.size() + 1);
    }

    // Method to validate the booking time
    private boolean isValidTime(String startTime, String endTime) {
        // Implement your validation logic here
        // For now, assuming all times are valid
        return true;
    }

    // Method to check room availability
    private boolean isRoomAvailable(String roomType, String date, String startTime, String endTime) {
        // Implement your room availability logic here
        // For now, assuming room is always available
        return true;
    }
}
