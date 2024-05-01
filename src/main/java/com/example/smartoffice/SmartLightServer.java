package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SmartLightServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize a gRPC server on port 9090
        Server server = ServerBuilder.forPort(9090)
                .addService(new SmartLightServiceImpl()) // Add SmartLight service implementation
                .build();

        // Start the server
        server.start();

        // Print a message indicating that the server has started
        System.out.println("Smart Light Server started on port 9090");

        // Wait for the server to shutdown (optional)
        server.awaitTermination();
    }
}
