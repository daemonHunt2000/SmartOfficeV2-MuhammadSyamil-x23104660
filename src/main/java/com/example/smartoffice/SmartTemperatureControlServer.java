/*
Smart Offices - CA Project of Distributed Systems
SmartTemperatureControlServer.java
@author Muhammad Syamil (x23104660)
26/04/2024
*/


package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmartTemperatureControlServer {
    private final int port;
    private final Server server;


    // Constructor to initialize the server with the specified port
    public SmartTemperatureControlServer(int port) {
        this.port = port;

        // Builds the gRPC server with the specified port and registers the TemperatureControlImpl service
        this.server = ServerBuilder.forPort(port)
                .addService(new TemperatureControlImpl())
                .build();
    }

    // Method to start the server
    public void start() throws IOException {
        server.start();
        System.out.println("Smart Temperature Control Server started, listening on " + port);

        // Adds a shutdown hook to gracefully shut down the server when the Java Virtual Machine (JVM) is shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down Smart Temperature Control server since JVM is shutting down");
            SmartTemperatureControlServer.this.stop();
            System.err.println("*** Smart Temperature Control server shut down");
        }));
    }

    // Method to stop the server
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // Method to block until the server is terminated
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    // Main method to start the server
    public static void main(String[] args) throws IOException, InterruptedException {

        // Creates an instance of SmartTemperatureControlServer with port 9092
        SmartTemperatureControlServer server = new SmartTemperatureControlServer(9092);

        // Starts the server
        server.start();

        // Block until the server is terminated
        server.blockUntilShutdown();
    }


    // Implementation of the SmartTemperatureControl service
    static class TemperatureControlImpl extends SmartTemperatureControlGrpc.SmartTemperatureControlImplBase {

        // Map to store the temperature of each work area
        private final Map<String, Float> temperatureMap = new HashMap<>();

        // Method to handle setting the temperature for a work area
        @Override
        public void setTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
            String workArea = request.getWorkArea();
            float temperature = request.getTemperature();

            // Stores the temperature in the map
            temperatureMap.put(workArea, temperature);

            // Creates a response message
            TemperatureResponse response = TemperatureResponse.newBuilder()
                    .setMessage("Temperature of " + workArea + " set to " + temperature + " °C")
                    .setCurrentTemperature(temperature)
                    .build();

            // Sends the response to the client
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }


        // Method to handle getting the temperature of a work area
        @Override
        public void getTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
            String workArea = request.getWorkArea();
            Float temperature = temperatureMap.get(workArea);
            if (temperature != null) {

                // If temperature is available, creates a response message with the current temperature
                TemperatureResponse response = TemperatureResponse.newBuilder()
                        .setMessage("Current temperature of " + workArea + ": " + temperature + " °C")
                        .setCurrentTemperature(temperature)
                        .build();

                // Sends the response to the client
                responseObserver.onNext(response);

            } else {
                // If temperature is not available, then creates a response message indicating it
                TemperatureResponse response = TemperatureResponse.newBuilder()
                        .setMessage("Temperature of " + workArea + " not available")
                        .build();

                // Sends the response to the client
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
