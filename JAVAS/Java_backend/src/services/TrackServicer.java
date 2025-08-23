package services;

import API_messages.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.*;
import server.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.File;

import java.util.*;

public class TrackServicer {

    private static DataBase dataBase = DataBase.getInstance();

    public static Response uploadTrack(JsonObject payload) {
        try {

            if (!payload.has("userId") || !payload.has("trackName") ||
                    !payload.has("artistName") || !payload.has("fileData")) {
                return new Response("failed", "missing required fields", null);
            }

            String userId = payload.get("userId").getAsString();
            String trackName = payload.get("trackName").getAsString().trim();
            String artistName = payload.get("artistName").getAsString().trim();
            String genreStr = payload.has("genre") ? payload.get("genre").getAsString() : "Default";
            boolean explicitness = payload.has("isExplicit") &&
                    payload.get("isExplicit").getAsString().equalsIgnoreCase("true");

            String base64Data = payload.get("fileData").getAsString();

            // تنظیم ژانر
            Genres genreForReal = settingGenre(genreStr);
            if (genreForReal == null) genreForReal = Genres.Default;


            Track track = new Track(trackName, artistName, genreForReal, explicitness);
            track.setTrackId(UUID.randomUUID().toString());


            try {
                byte[] fileBytes = Base64.getDecoder().decode(base64Data);
                Files.createDirectories(Paths.get("tracks"));


                String relativePath = "tracks/" + track.getTrackId() + ".mp3";
                Path target = Paths.get(relativePath);

                Files.write(target, fileBytes);
                track.setSongUrl(relativePath);
            } catch (IOException e) {
                e.printStackTrace();
                return new Response("failed", "cannot save track file: " + e.getMessage(), null);
            }


            DataBase.getInstance().addTrackAndAssignToUser(track, userId);


            JsonObject dateObj = new JsonObject();
            LocalDate date = track.getFullTrackDate();
            dateObj.addProperty("year", date.getYear());
            dateObj.addProperty("month", date.getMonthValue());
            dateObj.addProperty("day", date.getDayOfMonth());

            JsonObject responsePl = new JsonObject();
            responsePl.addProperty("trackId", track.getTrackId());
            responsePl.addProperty("trackName", track.getTrackName());
            responsePl.addProperty("artistName", track.getArtistName());
            responsePl.addProperty("genre", track.getGenre().name());
            responsePl.addProperty("isExplicit", track.isExplicit());
            responsePl.add("trackDate", dateObj);
            responsePl.addProperty("songUrl", track.getSongUrl()); // مسیر نسبی

            System.out.println("Track uploaded successfully: " + track.getTrackId() + " for user " + userId);
            return new Response("success", "track uploaded successfully", responsePl);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response("failed", "upload failed: " + e.getMessage(), null);
        }
    }






    public static Response downloadTrack(JsonObject payload) {

        try {
            System.out.println("📦 Raw payload: " + payload);

            if (!payload.has("trackId")) {
                return new Response("failed", "missing trackId", null);
            }}catch (Exception s){
            System.out.println("Noo");

        }
        try {
            if (!payload.has("trackId")) {
                return new Response("failed", "missing trackId", null);
            }

            String trackId = payload.get("trackId").getAsString();
            System.out.println("📥 Requested trackId: " + trackId);


            Track track = DataBase.getInstance().fetchTrack(trackId);

            if (track == null) {
                return new Response("failed", "track not found", null);
            }

            String localPath = track.getSongUrl();
            if (localPath == null || localPath.isEmpty() || !Files.exists(Paths.get(localPath))) {
                return new Response("failed", "track file not found on server", null);
            }

            byte[] fileBytes = Files.readAllBytes(Paths.get(localPath));
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);

            JsonObject responsePl = new JsonObject();
            responsePl.addProperty("trackId", track.getTrackId());
            responsePl.addProperty("trackName", track.getTrackName());
            responsePl.addProperty("artistName", track.getArtistName());
            responsePl.addProperty("fileData", base64Data);
            System.out.println("🎵 Sending track: " + track.getTrackName() + " | ID: " + track.getTrackId());
            System.out.println("📦 File size (bytes): " + fileBytes.length);

            return new Response("success", "track downloaded successfully", responsePl);

        } catch (IOException e) {
            e.printStackTrace();
            return new Response("failed", "error reading track file: " + e.getMessage(), null);
        }
    }


















    public static Response likeTrack(JsonObject payload) {
        String trackId = payload.get("trackId").getAsString();
        String userId = payload.get("userId").getAsString();
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (Track t : acc.getAllTracks()) {
                    if (t.getTrackId().equals(trackId)) {
                        t.likeTrack();
                        JsonObject trackResponse = new JsonObject();
                        trackResponse.addProperty("likes", t.getLikes());
                        dataBase.saveDbFile();
                        return new Response("success", "Track liked successfully", trackResponse);
                    }
                }
            }
        }
        return new Response("fail", "Track did not get liked!", null);
    }

    public static Response addTrack(JsonObject payload) {
        try {
            String userId = payload.get("userId").getAsString();

            Track track = null;

            if (payload.has("track") && payload.get("track").isJsonObject()) {
                JsonObject tObj = payload.getAsJsonObject("track");
                String clientId = tObj.has("id") ? tObj.get("id").getAsString() : null;
                String title = tObj.has("title") ? tObj.get("title").getAsString() : "unknown";
                String artist = tObj.has("artist") ? tObj.get("artist").getAsString() : "unknown";
                String genreStr = tObj.has("genre") ? tObj.get("genre").getAsString() : "default";
                boolean explicitness = tObj.has("isExplicit") && tObj.get("isExplicit").getAsBoolean();

                Genres genreForReal = settingGenre(genreStr);
                track = new Track(title, artist, genreForReal, explicitness);


                if (clientId != null && !clientId.isEmpty()) {
                    track.setTrackId(clientId);
                } else {
                    track.setTrackId(UUID.randomUUID().toString());
                }


                if (tObj.has("songUrl") && !tObj.get("songUrl").getAsString().isEmpty()) {
                    track.setSongUrl(tObj.get("songUrl").getAsString());
                }

            } else {

                boolean explicitness = payload.has("isExplicit") && payload.get("isExplicit").getAsString().equalsIgnoreCase("true");
                String genreStr = payload.has("genre") ? payload.get("genre").getAsString() : "Default";
                Genres genreForReal = settingGenre(genreStr);

                String trackName = payload.get("trackName").getAsString();
                String artistName = payload.get("artistName").getAsString();

                track = new Track(trackName, artistName, genreForReal, explicitness);
                // همیشه trackId بساز
                track.setTrackId(UUID.randomUUID().toString());
            }


            String host = "192.168.1.101";
            int httpPort = 8081;
            if (track.getSongUrl() == null || track.getSongUrl().isEmpty()) {
                track.setSongUrl("http://" + host + ":" + httpPort + "/tracks/" + track.getTrackId() + ".mp3");
            }


            DataBase.getInstance().addTrackAndAssignToUser(track, userId);

            JsonObject responsePl = new JsonObject();
            responsePl.addProperty("trackId", track.getTrackId());
            responsePl.addProperty("trackDate", track.getTrackDate());
            responsePl.addProperty("songUrl", track.getSongUrl());

            return new Response("success", "new track added!", responsePl);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response("failed", "no track added to account: " + e.getMessage(), null);
        }
    }







    public static Response removeTrack(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String trackId = payload.get("trackId").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {

                for (Track trc : acc.getAllTracks()) {
                    if (trc.getTrackId().equals(trackId)) {
                        acc.removeTrack(trc);
                        acc.removeOwnedTrack(trackId);
                        dataBase.saveDbFile();
                        return new Response("success", "Track deleted", null);
                    }
                }
            }
        }
        return new Response("failed", "Track not deleted", null);
    }


    public static Response updateTrack(JsonObject payload) {
        boolean isExplicitNew = payload.get("newExplicitness").getAsString().equalsIgnoreCase("true");
        String userId = payload.get("userId").getAsString();
        String trackId = payload.get("trackId").getAsString();
        String newTrackName = payload.get("newTrackName").getAsString();
        String newArtistName = payload.get("newArtistName").getAsString();
        String newGenre = payload.get("newGenre").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (Track trc : acc.getAllTracks()) {
                    if (trc.getTrackId().equals(trackId)) {
                        if (!newTrackName.isEmpty()) trc.setTrackName(newTrackName);
                        if (!newArtistName.isEmpty()) trc.setArtistName(newArtistName);
                        trc.setGenre(settingGenre(newGenre));
                        trc.setExplicit(isExplicitNew);
                        dataBase.addTrack(trc);
                        dataBase.saveDbFile();
                        return new Response("success", "Track updated", null);
                    }
                }
            }
        }
        return new Response("failed", "Track did not update", null);
    }

    public static Response addSongToPlayList(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playListId = payload.get("playListId").getAsString();
        String trackId = payload.get("trackId").getAsString();
        Track chosenTrack = null;

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (Track trc : acc.getAllTracks()) {
                    if (trc.getTrackId().equals(trackId)) {
                        chosenTrack = trc;
                        break;
                    }
                }
                for (PlayList pl : acc.getPlayLists()) {
                    if (pl.getPlayListID().equals(playListId) && chosenTrack != null) {
                        pl.addTrack(chosenTrack);
                        dataBase.saveDbFile();
                        return new Response("success", "Track added to playlist: " + pl.getPlaylistName(), null);
                    }
                }
            }
        }
        return new Response("failed", "Track not added to playlist", null);
    }

    public static Response removeSongFromPlayList(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playListId = payload.get("playListId").getAsString();
        String trackId = payload.get("trackId").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList pl : acc.getPlayLists()) {
                    if (pl.getPlayListID().equals(playListId)) {
                        Iterator<Track> it = pl.getTracksList().iterator();
                        while (it.hasNext()) {
                            Track trc = it.next();
                            if (trc.getTrackId().equals(trackId)) {
                                it.remove();
                                dataBase.saveDbFile();
                                return new Response("success", "Track deleted from playlist: " + pl.getPlaylistName(), null);
                            }
                        }
                    }
                }
            }
        }
        return new Response("failed", "Track not deleted from playlist", null);
    }

    public static Response moveTrackToOtherPl(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String currentPlayListId = payload.get("currentPlayListId").getAsString();
        String otherPlayListId = payload.get("otherPlayListId").getAsString();
        String trackId = payload.get("trackId").getAsString();

        PlayList chosenPlayList = null;

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList pl : acc.getPlayLists()) {
                    if (pl.getPlayListID().equals(otherPlayListId)) {
                        chosenPlayList = pl;
                    }
                }
                for (PlayList pl : acc.getPlayLists()) {
                    if (pl.getPlayListID().equals(currentPlayListId) && chosenPlayList != null) {
                        for (Track trc : pl.getTracksList()) {
                            if (trc.getTrackId().equals(trackId)) {
                                chosenPlayList.addTrack(trc);
                                pl.removeTrack(trc);
                                dataBase.saveDbFile();
                                return new Response("success", "Track moved from " + pl.getPlaylistName() + " to " + chosenPlayList.getPlaylistName(), null);
                            }
                        }
                    }
                }
            }
        }
        return new Response("failed", "Track not moved from playlist to the other", null);
    }

    public static Response shareTrack(JsonObject payload) {
        String fromUserId = payload.get("fromUserId").getAsString();
        String toUserId = payload.get("toUserId").getAsString();
        String trackId = payload.get("trackId").getAsString();

        Account fromAccount = null;
        Account toAccount = null;
        Track sharedTrack = null;

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(fromUserId)) fromAccount = acc;
            if (acc.getUserId().equals(toUserId)) toAccount = acc;
        }

        if (fromAccount == null || toAccount == null) {
            return new Response("failed", "either sender or receiver account not found", null);
        }

        for (Track trc : fromAccount.getAllTracks()) {
            if (trc.getTrackId().equals(trackId)) {
                sharedTrack = trc;
                break;
            }
        }

        if (sharedTrack == null) {
            return new Response("failed", "shared track not found in sender's account", null);
        }

        if (!toAccount.canShareWith()) {
            return new Response("failed", "track cannot be shared with " + toAccount.getAccountName(), null);
        }

        try {
            toAccount.addTrack(sharedTrack);
            dataBase.saveDbFile();
            return new Response("success", "track shared with " + toAccount.getAccountName(), null);
        } catch (Exception e) {
            return new Response("failed", "couldn't share track", null);
        }
    }

    // 🔹 متد جدید برای برگردوندن تمام آهنگ‌های یک اکانت
    public static Response getLibrary(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                JsonArray tracksArr = new JsonArray();
                for (Track trc : acc.getAllTracks()) {
                    JsonObject tObj = new JsonObject();
                    tObj.addProperty("trackId", trc.getTrackId());
                    tObj.addProperty("trackName", trc.getTrackName());
                    tObj.addProperty("artistName", trc.getArtistName());
                    tObj.addProperty("genre", trc.getGenre().toString());
                    tObj.addProperty("likes", trc.getLikes());
                    tObj.addProperty("isExplicit", trc.isExplicit());
                    tObj.addProperty("trackDate", trc.getTrackDate());
                    tracksArr.add(tObj);
                }
                JsonObject res = new JsonObject();
                res.add("tracks", tracksArr);
                return new Response("success", "library fetched", res);
            }
        }
        return new Response("failed", "user not found", null);
    }

    public static Genres settingGenre(String genreSt) {
        switch (genreSt.toLowerCase()) {
            case "rock":
                return Genres.Rock;
            case "metal":
                return Genres.Metal;
            case "classic":
                return Genres.Classic;
            case "pop":
                return Genres.Pop;
            case "jazz":
                return Genres.Jazz;
            case "hiphop":
                return Genres.HipHop;
            case "country":
                return Genres.country;
            case "edm":
                return Genres.EDM;
            case "lofi":
                return Genres.Lofi;
            case "ambient":
                return Genres.Ambient;
            case "vgm":
                return Genres.VGM;
            default:
                return Genres.Default;
        }
    }
}

