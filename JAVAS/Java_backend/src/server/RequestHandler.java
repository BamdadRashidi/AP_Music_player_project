package server;
import API_messages.Request;
import API_messages.Response;
import models.*;
import com.google.gson.*;
import services.*;
import server.DataBase;

public class RequestHandler {
    private static final Gson gson = new Gson();
    private static DataBase dataBase = DataBase.getInstance();

    public static Response handle(Request req) {
        String action = req.getAction();
        JsonObject payload = req.getPayload();
        Response response = null;
        System.out.println("Action received: '" + req.getAction() + "'");

        switch (action) {
            case "signIn": response = AccountServicer.signIn(payload); break;
            case "logIn": response = AccountServicer.logIn(payload); break;
            case "logOut": response = AccountServicer.logOut(); break;
            case "updateAccount": response = AccountServicer.updateAccount(payload); break;
            case "deleteAccount": response = AccountServicer.DeleteAccount(payload); break;
            case "addPlaylist" : response = PlayListServicer.addPlayList(payload); break;
            case "removePlaylist" : response = PlayListServicer.removePlayList(payload); break;
            case "sharePlaylist" : response = PlayListServicer.sharePlayListWith(payload); break;
            case "changePlaylistName": response = PlayListServicer.changePlName(payload); break;
            case "likeTrack" : response = TrackServicer.likeTrack(payload); break;
            case "addTrack": response = TrackServicer.addTrack(payload); break;
            case "removeTrack" : response = TrackServicer.removeTrack(payload); break;
            case "addTrackToPlayList": response = TrackServicer.addSongToPlayList(payload); break;
            case "removeTrackFromPlayList": response = TrackServicer.removeSongFromPlayList(payload); break;
            case "moveTrackToOtherPl": response = TrackServicer.moveTrackToOtherPl(payload); break;
            case "shareTrack": response = TrackServicer.shareTrack(payload); break;
            case "updateTrack": response = TrackServicer.updateTrack(payload); break;
            case "downloadTrack": response = TrackServicer.downloadTrack(payload); break;
            case "getPlaylistName": response = PlayListServicer.getPlaylistName(payload); break;
            case "getLibrary":
                String userId = null;
                if (payload.has("userId")) {
                    userId = payload.get("userId").getAsString();
                } else if (payload.has("identifier")) {
                    userId = payload.get("identifier").getAsString();
                } else {
                    return new Response("fail", "User ID missing in payload", null);
                }
                JsonObject data = new JsonObject();

                data.add("songs", gson.toJsonTree(dataBase.getUserLibrary(userId)));
                response = new Response("success", "Library fetched", data);
                break;

            case "uploadTrack": response = TrackServicer.uploadTrack(payload);
                System.out.println("fuck");break;


            default:
                return new Response("Failed","incorrect action",null);
        }
        return response;
    }
}
