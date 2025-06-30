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
        try {
            File dir = new File(TRACKS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String userId = payload.get("userId").getAsString();
            String trackName = payload.get("trackName").getAsString();
            String artistName = payload.get("artistName").getAsString();
            String genreStr = payload.get("genre").getAsString();
            boolean explicit = payload.get("explicit").getAsBoolean();

            byte[] fileBytes = Base64.getDecoder().decode(payload.get("audioBase64").getAsString());
            String trackId = UUID.randomUUID().toString();
            Path path = Paths.get(TRACKS_DIR, trackId + ".mp3");
            Files.write(path, fileBytes);

            Genres genre = Genres.valueOf(genreStr);
            Track newTrack = new Track(trackName, artistName, genre, explicit);


            DataBase.getInstance().addTrackAndAssignToUser(newTrack, userId);

            return new Response("success", "Track uploaded successfully", null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Response("fail", "Track did not upload successfully!", null);
        }
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
