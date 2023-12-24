package Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JavaClient {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader serverResponseReader;
    private Thread responseThread;
    private BlockingQueue<String> responseQueue;

    public JavaClient() {
        try {
            socket = new Socket("localhost", 12345);
            output = new PrintWriter(socket.getOutputStream(), true);
            serverResponseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseQueue = new LinkedBlockingQueue<>();

            // Create a thread to handle server responses
            responseThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = serverResponseReader.readLine()) != null) {
                        responseQueue.put(serverResponse);
                    }
                } catch (SocketException e) {
                    // Socket closed, break out of the loop
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            responseThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendAndReceive(String userInput) {
        try {
            output.println(userInput);
            output.flush();
            return responseQueue.take(); // Wait for the response from the queue
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            output.close();
            serverResponseReader.close();
            socket.close();

            // Wait for the response thread to finish
            responseThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
