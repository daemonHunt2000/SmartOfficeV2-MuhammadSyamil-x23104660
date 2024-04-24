package com.example.smartoffice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class SmartLightClient {
    public static void main(String[] args) {
        // Create a channel to the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // Create a stub for the SmartLight service
        SmartLightGrpc.SmartLightBlockingStub stub = SmartLightGrpc.newBlockingStub(channel);

        // Create a scanner for user input
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;

        while (!exit) {
            int actionChoice = 0;

            // Prompt the user to choose action
            while (actionChoice != 1 && actionChoice != 2) {
                System.out.println("Choose an action:");
                System.out.println("1. Turn On Lights");
                System.out.println("2. Turn Off Lights");
                System.out.print("Enter the number corresponding to the action: ");

                // Read the user's action choice
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }
                actionChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                // Validate action choice
                if (actionChoice != 1 && actionChoice != 2) {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            }

            String workspace = null;
            while (workspace == null) {
                System.out.println("Select a workspace:");
                System.out.println("1. Conference Room");
                System.out.println("2. Meeting Room");
                System.out.println("3. Server Room");
                System.out.println("4. Cafeteria");
                System.out.print("Enter the number corresponding to the workspace: ");

                // Read the user's workspace choice
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }
                int workspaceChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                // Map the user's choice to the workspace name
                switch (workspaceChoice) {
                    case 1:
                        workspace = "Conference Room";
                        break;
                    case 2:
                        workspace = "Meeting Room";
                        break;
                    case 3:
                        workspace = "Server Room";
                        break;
                    case 4:
                        workspace = "Cafeteria";
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            }

            // Perform the selected action
            if (actionChoice == 1) {
                performTurnOn(stub, workspace);
            } else {
                performTurnOff(stub, workspace);
            }

            // Ask if the user wants to continue
            String continueChoice = "";
            while (!continueChoice.equalsIgnoreCase("yes") && !continueChoice.equalsIgnoreCase("no")) {
                System.out.print("Do you want to perform another action? (yes/no): ");
                continueChoice = scanner.nextLine().trim();
                if (!continueChoice.equalsIgnoreCase("yes") && !continueChoice.equalsIgnoreCase("no")) {
                    System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
                }
            }

            exit = continueChoice.equalsIgnoreCase("no");
        }

        // Shutdown the channel
        channel.shutdown();
    }

    private static void performTurnOn(SmartLightGrpc.SmartLightBlockingStub stub, String workspace) {
        // Send a request to turn on the lights for the selected workspace
        LightRequest request = LightRequest.newBuilder()
                .setWorkspaceId(workspace)
                .build();
        LightResponse response = stub.turnOn(request);
        System.out.println("Response: " + response.getStatus());
    }

    private static void performTurnOff(SmartLightGrpc.SmartLightBlockingStub stub, String workspace) {
        // Send a request to turn off the lights for the selected workspace
        LightRequest request = LightRequest.newBuilder()
                .setWorkspaceId(workspace)
                .build();
        LightResponse response = stub.turnOff(request);
        System.out.println("Response: " + response.getStatus());
    }
}
