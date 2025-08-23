package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(1080)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket socket = server.accept();
                System.out.println("New user connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}

