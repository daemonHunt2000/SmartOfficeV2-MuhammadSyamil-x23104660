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

    public SmartTemperatureControlServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new TemperatureControlImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Smart Temperature Control Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down Smart Temperature Control server since JVM is shutting down");
            SmartTemperatureControlServer.this.stop();
            System.err.println("*** Smart Temperature Control server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        SmartTemperatureControlServer server = new SmartTemperatureControlServer(9092);
        server.start();
        server.blockUntilShutdown();
    }

    static class TemperatureControlImpl extends SmartTemperatureControlGrpc.SmartTemperatureControlImplBase {
        private final Map<String, Float> temperatureMap = new HashMap<>();

        @Override
        public void setTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
            String workArea = request.getWorkArea();
            float temperature = request.getTemperature();
            temperatureMap.put(workArea, temperature);
            TemperatureResponse response = TemperatureResponse.newBuilder()
                    .setMessage("Temperature of " + workArea + " set to " + temperature + " °C")
                    .setCurrentTemperature(temperature)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
            String workArea = request.getWorkArea();
            Float temperature = temperatureMap.get(workArea);
            if (temperature != null) {
                TemperatureResponse response = TemperatureResponse.newBuilder()
                        .setMessage("Current temperature of " + workArea + ": " + temperature + " °C")
                        .setCurrentTemperature(temperature)
                        .build();
                responseObserver.onNext(response);
            } else {
                TemperatureResponse response = TemperatureResponse.newBuilder()
                        .setMessage("Temperature of " + workArea + " not available")
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
