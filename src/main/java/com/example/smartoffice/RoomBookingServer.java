/*
Smart Offices - CA Project of Distributed Systems
RoomBookingServer.java
@author Muhammad Syamil (x23104660)
24/04/2024
*/


package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class RoomBookingServer {

    // Initializes a gRPC server on port 9091
    private static final int PORT = 9091;


    public static void main(String[] args) throws IOException, InterruptedException {

        // Creates a new gRPC server
        Server server = ServerBuilder.forPort(PORT)

                // Registers the service implementation class
                .addService(new RoomBookingServiceImpl())
                .build();

        // Starts the server
        server.start();

        // Prints a message indicating that the server has started
        System.out.println("The Room Booking Server has started on port " + PORT);

        // Blocks until the server is terminated
        server.awaitTermination();
    }
}
