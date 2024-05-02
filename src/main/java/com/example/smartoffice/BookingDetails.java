/*
Smart Offices - CA Project of Distributed Systems
BookingDetails.java
@author Muhammad Syamil (x23104660)
24/04/2024
*/


package com.example.smartoffice;

/*
Represents the details of a room booking.
*/
public class BookingDetails {
    private final String roomType; // Type of room to be booked (e.g., Conference Room, Meeting Room)
    private final String date; // Date of the booking
    private final String startTime; // Start time of the booking
    private final String endTime; // End time of the booking
    private final String employeeName; // Name of the employee making the booking
    private final String employeeId; // ID of the employee making the booking

    /**
     * Constructs a new BookingDetails object with the given parameters.
     * @param roomType The type of the room to be booked (e.g. Conference Room, Meeting Room)
     * @param date The date of the booking
     * @param startTime The start time of the booking
     * @param endTime The end time of the booking
     * @param employeeName The name of the employee making the booking
     * @param employeeId The ID of the employee making the booking
     */


    public BookingDetails(String roomType, String date, String startTime, String endTime, String employeeName, String employeeId) {

        // Initializes the booking details
        this.roomType = roomType;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
    }

    /**
     * Gets the type of room.
     * @return The type of the room
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Gets the date of the booking.
     * @return The date of the booking
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the start time of the booking.
     * @return The start time of the booking
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the booking.
     * @return The end time of the booking
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Gets the name of the employee making the booking.
     * @return The name of the employee making the booking
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * Gets the ID of the employee making the booking.
     * @return The ID of the employee making the booking
     */
    public String getEmployeeId() {
        return employeeId;
    }
}
