package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import API_messages.*;
import com.google.gson.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private final Gson gson = new Gson();

    private Request request;
    private Response response;


    public ClientHandler(Socket s){
        socket=s;
    }
    public void run(){
        try(
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer =new PrintWriter(socket.getOutputStream());
        ){
            String jsonString;
            String responseString;

            while ((jsonString=reader.readLine())!=null){
                System.out.println("Raw input: " + request);
                request = gson.fromJson(jsonString, Request.class);
                response = RequestHandler.handle(request);
                responseString = gson.toJson(response);
                writer.println(responseString);
                System.out.println("Received payload: " + response);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        finally{
            try{
                if(socket!=null && !socket.isClosed()){
                    socket.close();
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

        }
    }
}
