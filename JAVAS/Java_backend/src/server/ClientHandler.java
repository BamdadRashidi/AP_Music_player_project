package server;

import API_messages.Request;
import API_messages.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final Gson gson = new Gson();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                System.out.println("📥 Raw request received: " + line);
                System.out.println("Received JSON length: " + line.length());

                Request request = gson.fromJson(line, Request.class);
                JsonObject payload = request.getPayload();

                if (payload != null && payload.has("fileData")) {
                    String base64Data = payload.get("fileData").getAsString();
                    System.out.println("Base64 data length: " + base64Data.length());
                }

                Response response = RequestHandler.handle(request);
                String responseString = gson.toJson(response);

                writer.write(responseString);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("ClientHandler error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Socket close error: " + e.getMessage());
            }
        }
    }

}











