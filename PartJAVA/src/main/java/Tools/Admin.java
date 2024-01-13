package Tools;

import java.util.Scanner;

public class Admin {
    public static void main(String[] args) {
        JavaClient client = new JavaClient();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    System.out.print("Enter a command (or 'exit' to quit): ");
                    String userInput = scanner.nextLine();

                    if (userInput.equalsIgnoreCase("exit")) {
                        break;
                    }

                    if (userInput.equalsIgnoreCase("read") || userInput.equalsIgnoreCase("save")) {
                        // For read or write, don't use sendAndReceive
                        client.send(userInput);
                    } else {
                        // For other messages, use sendAndReceive
                        String response = client.sendAndReceive(userInput);
                        System.out.println("Server response: " + response);
                    }
                } catch (Exception e) {
                    System.err.println("Unable to connect: " + e.getMessage());
                    System.exit(1); // Exit with an error code
                }
            }
        } finally {
            client.close();
        }
    }
}
