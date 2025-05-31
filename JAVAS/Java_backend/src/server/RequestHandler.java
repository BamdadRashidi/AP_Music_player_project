package server;
import API_messages.Request;
import API_messages.Response;
import models.*;
import com.google.gson.*;

public class RequestHandler {

    public static Response handle(Request req) {
        String action = req.getAction();
        JsonObject payload = req.getPayload();


        switch (action) {

        }

        return null; //TODO: get this implemented right
    }
}
