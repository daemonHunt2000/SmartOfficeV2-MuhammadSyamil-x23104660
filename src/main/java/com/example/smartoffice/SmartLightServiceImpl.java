package com.example.smartoffice;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class SmartLightServiceImpl extends SmartLightGrpc.SmartLightImplBase {

    // Override method for turning on lights
    @Override
    public void turnOn(LightRequest request, StreamObserver<LightResponse> responseObserver) {
        // Dummy implementation
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned on for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Override method for turning off lights
    @Override
    public void turnOff(LightRequest request, StreamObserver<LightResponse> responseObserver) {
        // Dummy implementation
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned off for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Override method for turning on lights with user input
    @Override
    public void turnOnWithUserInput(TurnOnWithUserInputRequest request, StreamObserver<LightResponse> responseObserver) {
        // Get user input for workspace
        String workspaceId = request.getWorkspaceId();

        // Validate workspace
        if (!isValidWorkspace(workspaceId)) {
            // Send error response
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid workspace: " + workspaceId)
                    .asRuntimeException());
            return;
        }

        // Dummy implementation
        String status = "Lights turned on for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Override method for turning off lights with user input
    @Override
    public void turnOffWithUserInput(TurnOffWithUserInputRequest request, StreamObserver<LightResponse> responseObserver) {
        // Get user input for workspace
        String workspaceId = request.getWorkspaceId();

        // Validate workspace
        if (!isValidWorkspace(workspaceId)) {
            // Send error response
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid workspace: " + workspaceId)
                    .asRuntimeException());
            return;
        }

        // Dummy implementation
        String status = "Lights turned off for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Validate workspace
    private boolean isValidWorkspace(String workspaceId) {
        // List of valid workspace IDs
        return workspaceId.equals("Conference Room Dublin") ||
                workspaceId.equals("Conference Room Cork") ||
                workspaceId.equals("Conference Room Galway") ||
                workspaceId.equals("Meeting Room Limerick") ||
                workspaceId.equals("Meeting Room Waterford") ||
                workspaceId.equals("Server Room") ||
                workspaceId.equals("Cafeteria");
    }
}
