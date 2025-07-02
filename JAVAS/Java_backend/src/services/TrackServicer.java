package services;
import API_messages.Response;
import com.google.gson.JsonObject;
import models.*;
import server.*;
import java.io.*;
import java.util.Base64;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

public class TrackServicer {

    private static final String TRACKS_DIR = "server_tracks";
    private Account account;

    private static DataBase dataBase = DataBase.getInstance();
    public static Response uploadTrack(JsonObject payload) {


        return new Response("fail","couldn't upload track",null);
    }

    public static Response likeTrack(JsonObject payload) {
        String trackId = payload.get("trackId").getAsString();
        String userId = payload.get("userId").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if(acc.getUserId().equals(userId)){
                for(Track t: acc.getAllTracks()){
                    if(t.getTrackId().equals(trackId)){
                        t.likeTrack();
                        JsonObject trackResponse = new JsonObject();
                        trackResponse.addProperty("likes",t.getLikes());
                        dataBase.saveDbFile();
                        return new Response("success", "Track like successfully", trackResponse);
                    }
                }
            }
        }
        return new Response("fail", "Track did not get liked!", null);
    }


//    public Map<String, String> downloadTrack(String trackName) {
//        try {
//            Path path = Paths.get(TRACKS_DIR, trackName);
//            if (!Files.exists(path)) return null;
//
//            byte[] fileBytes = Files.readAllBytes(path);
//            String audioB64data = Base64.getEncoder().encodeToString(fileBytes);
//            return Map.of("base64Data", audioB64data);
//        }
//        catch (IOException e) {
//            return null;
//        }

}
