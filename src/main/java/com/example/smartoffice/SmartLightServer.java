/*
Smart Offices - CA Project of Distributed Systems
SmartLightServer.java
@author Muhammad Syamil (x23104660)
21/04/2024
*/


package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SmartLightServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Initializes a gRPC server on port 9090
        Server server = ServerBuilder.forPort(9090)

                // Adds the SmartLight service implementation
                .addService(new SmartLightServiceImpl())
                .build();

        // Starts the server
        server.start();

        // Prints a message indicating that the server has started
        System.out.println("Smart Light Server started on port 9090");

        // Waits for the server to shutdown (optional)
        server.awaitTermination();
    }
}
