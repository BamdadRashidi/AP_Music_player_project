package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {


    public static  void main(String []args) {

        try (ServerSocket server = new ServerSocket(1080))
        {
            System.out.println("Conecting...");
           while (true){
               Socket socket= server.accept();
               System.out.println("New user connected");
               new Client(socket).start();
           }
        }catch (IOException w){
            System.out.println(w.getMessage());
        }


    }
}
