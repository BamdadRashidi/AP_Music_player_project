package server;
import API_messages.Request;
import API_messages.Response;
import models.*;
import com.google.gson.*;
import services.*;

public class RequestHandler {
    private static final Gson gson = new Gson();

    public static Response handle(Request req) {
        String action = req.getAction();
        JsonObject payload = req.getPayload();
        Response response = null;

        switch (action) {
            case "signIn": response = AccountServicer.signIn(payload); break;
            case "logIn": response = AccountServicer.logIn(payload); break;
            case "logOut": response = AccountServicer.logOut(); break;
            case "changeAccountName": response = AccountServicer.changeAccountName(payload); break;
            case "changePassword": response = AccountServicer.changePassword(payload); break;
            case "changeUsername": response = AccountServicer.changeUsername(payload); break;
            case "DeleteAccount": response = AccountServicer.DeleteAccount(payload); break;
            //TODO: more actions coming soon!
            default:
                return new Response("Failed","incorrect action",null);
        }
        return response;
    }
}
