package com.example.smartoffice;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SmartLightServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(new SmartLightServiceImpl())
                .build();

        server.start();

        System.out.println("Smart Light Server started on port 9090");

        server.awaitTermination();
    }
}
