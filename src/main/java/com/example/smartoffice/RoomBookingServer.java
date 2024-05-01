package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RoomBookingServer {
    // Defines the port number
    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Creates a new gRPC server
        Server server = ServerBuilder.forPort(PORT)
                .addService(new RoomBookingServiceImpl()) // Registers service implementation
                .build();

        // Start the server
        server.start();

        // Print a message indicating that the server has started
        System.out.println("Room Booking Server started on port " + PORT);

        // Block until the server is terminated
        server.awaitTermination();
    }
}
