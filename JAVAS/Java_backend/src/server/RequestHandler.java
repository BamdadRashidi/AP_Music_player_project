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
            case "SetCanShare" : response = AccountServicer.setCanShareWith(payload); break;
            case "upload_track" : response = TrackServicer.uploadTrack(payload); break;
            //TODO: add one for removing track and another for liking the track
            case "addPlaylist" : response = PlayListServicer.addPlayList(payload); break;
            case "removePlaylist" : response = PlayListServicer.removePlayList(payload); break;
            case "sharePlaylist" : response = PlayListServicer.sharePlayListWith(payload); break;
            /*TODO: the following
              1. adding a song to a playlist
              2. removing one from a playlist
              3. transfering one to another playlist
            */

            default:
                return new Response("Failed","incorrect action",null);
        }
        return response;
    }
}
