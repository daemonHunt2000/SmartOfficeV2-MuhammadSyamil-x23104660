/*
Smart Offices - CA Project of Distributed Systems
SmartLightServiceImpl.java
@author Muhammad Syamil (x23104660)
21/04/2024
*/


package com.example.smartoffice;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;



public class SmartLightServiceImpl extends SmartLightGrpc.SmartLightImplBase {


    // Override method for turning on the lights
    @Override
    public void turnOn(LightRequest request, StreamObserver<LightResponse> responseObserver) {

        // Dummy implementation for turn on function
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned on for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Override method for turning off the lights
    @Override
    public void turnOff(LightRequest request, StreamObserver<LightResponse> responseObserver) {

        // Dummy implementation for turn off function
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned off for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Override method for turning on lights with the input from user
    @Override
    public void turnOnWithUserInput(TurnOnWithUserInputRequest request, StreamObserver<LightResponse> responseObserver) {

        // Gets the user input for workspace
        String workspaceId = request.getWorkspaceId();

        // Validates workspace
        if (!isValidWorkspace(workspaceId)) {

            // Sends error response
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid workspace: " + workspaceId)
                    .asRuntimeException());
            return;
        }

        // Dummy implementation for turn on function
        String status = "Lights turned on for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Overrides the method for turning off lights with user input
    @Override
    public void turnOffWithUserInput(TurnOffWithUserInputRequest request, StreamObserver<LightResponse> responseObserver) {

        // Gets user input for workspace
        String workspaceId = request.getWorkspaceId();

        // Validates workspace
        if (!isValidWorkspace(workspaceId)) {

            // Sends error response
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid workspace: " + workspaceId)
                    .asRuntimeException());
            return;
        }

        // Dummy implementation for turn off function
        String status = "Lights turned off for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Validates workspace
    private boolean isValidWorkspace(String workspaceId) {

        // Lists of valid workspace IDs
        return workspaceId.equals("Conference Room Dublin") ||
                workspaceId.equals("Conference Room Cork") ||
                workspaceId.equals("Conference Room Galway") ||
                workspaceId.equals("Meeting Room Limerick") ||
                workspaceId.equals("Meeting Room Waterford") ||
                workspaceId.equals("Server Room") ||
                workspaceId.equals("Cafeteria");
    }
}
