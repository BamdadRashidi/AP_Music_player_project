package server;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;

public class FileServer {
    public static void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tracks", exchange -> {
            String path = exchange.getRequestURI().getPath();
            File file = new File(DataBase.TRACKS_FOLDER, path.substring("/tracks/".length()));
            if (file.exists()) {
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });

        server.start();
        System.out.println("File server started on port " + port);
    }
}

