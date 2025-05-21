import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client extends Thread {
    private Socket socket;
    public Client(Socket s){
        socket=s;
    }
public void run(){
        try(
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer =new PrintWriter(socket.getOutputStream());


                ){
            String received;
            while ((received=reader.readLine())!=null){
                System.out.println("got"+received);
                writer.println("server got"+received);

            }
socket.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
}
}
