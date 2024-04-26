package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RoomBookingServer {
    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Create a new server
        Server server = ServerBuilder.forPort(PORT)
                .addService(new RoomBookingServiceImpl())
                .build();

        // Start the server
        server.start();

        // Print a message indicating that the server has started
        System.out.println("Room Booking Server started on port " + PORT);

        // Block until the server is terminated
        server.awaitTermination();
    }
}
