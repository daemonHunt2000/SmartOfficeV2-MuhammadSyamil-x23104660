package com.example.smartoffice;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.example.smartoffice.LightRequest;
import com.example.smartoffice.LightResponse;
import com.example.smartoffice.TurnOnWithUserInputRequest;
import com.example.smartoffice.TurnOffWithUserInputRequest;

public class SmartLightServiceImpl extends SmartLightGrpc.SmartLightImplBase {

    @Override
    public void turnOn(LightRequest request, StreamObserver<LightResponse> responseObserver) {
        // Dummy implementation
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned on for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void turnOff(LightRequest request, StreamObserver<LightResponse> responseObserver) {
        // Dummy implementation
        String workspaceId = request.getWorkspaceId();
        String status = "Lights turned off for workspace: " + workspaceId;
        LightResponse response = LightResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

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
        return workspaceId.equals("Conference Room") ||
                workspaceId.equals("Meeting Room") ||
                workspaceId.equals("Server Room") ||
                workspaceId.equals("Cafeteria");
    }
}
