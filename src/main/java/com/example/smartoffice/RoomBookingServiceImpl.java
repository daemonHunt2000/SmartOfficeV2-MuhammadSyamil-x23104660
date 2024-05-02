/*
Smart Offices - CA Project of Distributed Systems
RoomBookingServiceImpl.java
@author Muhammad Syamil (x23104660)
24/04/2024
*/


package com.example.smartoffice;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;


public class RoomBookingServiceImpl extends RoomBookingGrpc.RoomBookingImplBase {

    // Map to store the room bookings
    private final Map<String, BookingDetails> bookings = new HashMap<>();

    @Override
    public void bookRoom(BookRoomRequest request, StreamObserver<BookRoomResponse> responseObserver) {

        // Gets the booking details from the requests
        String roomType = request.getRoomType();
        String date = request.getDate();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        String employeeName = request.getEmployeeName();
        String employeeId = request.getEmployeeId();

        // Validates the requested time
        if (!isValidTime(startTime, endTime)) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid booking time. Please book within working hours (9am - 6pm).")
                    .asRuntimeException());
            return;
        }

        // Checks for room availability
        if (isRoomAvailable(roomType, date, startTime, endTime)) {

            // Generates a unique booking ID
            String bookingId = generateBookingId();

            // Creates booking details object
            BookingDetails bookingDetails = new BookingDetails(roomType, date, startTime, endTime, employeeName, employeeId);

            // Stores booking details
            bookings.put(bookingId, bookingDetails);

            // Sends response with the booking ID
            BookRoomResponse response = BookRoomResponse.newBuilder()
                    .setBookingId(bookingId)
                    .setMessage("Room booked successfully.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {

            // 'Room is not available' response
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Room is not available for the requested time.")
                    .asRuntimeException());
        }
    }

    @Override
    public void cancelBooking(CancelBookingRequest request, StreamObserver<CancelBookingResponse> responseObserver) {

        // Gets booking ID from requests
        String bookingId = request.getBookingId();

        // Checks if a booking ID exists
        if (bookings.containsKey(bookingId)) {

            // Removes booking
            bookings.remove(bookingId);

            // Sends response
            CancelBookingResponse response = CancelBookingResponse.newBuilder()
                    .setMessage("Booking canceled successfully.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {

            // Booking ID not found response
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Booking not found for ID: " + bookingId)
                    .asRuntimeException());
        }
    }



    @Override
    public void viewBooking(ViewBookingRequest request, StreamObserver<ViewBookingResponse> responseObserver) {

        // Gets booking ID from requests
        String bookingId = request.getBookingId();

        // Checks if booking ID exists
        if (bookings.containsKey(bookingId)) {

            // Gets booking details
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
            // Booking ID is not found response
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
        return true;
    }

    // Method to check the room availability
    private boolean isRoomAvailable(String roomType, String date, String startTime, String endTime) {
        return true;
    }
}
