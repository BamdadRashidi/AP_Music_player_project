package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.*;
import models.utility.AudioSorter;
import server.*;
import API_messages.*;
import models.utility.*;

import java.util.ArrayList;
import java.util.List;

public class PlayListServicer extends AudioSorter {
    private PlayList playlist;
    private static DataBase dataBase = DataBase.getInstance();

    public PlayListServicer(PlayList playlist) {
        this.playlist = playlist;
    }

    public static Response savePlaylist(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        JsonArray playlists = payload.getAsJsonArray("playlists");

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                try {
                    List<PlayList> newPlayLists = new ArrayList<>();
                    for (int i = 0; i < playlists.size(); i++) {
                        JsonObject playlistJson = playlists.get(i).getAsJsonObject();
                        String playlistName = playlistJson.get("name").getAsString();
                        JsonArray songUrls = playlistJson.getAsJsonArray("songUrls");

                        PlayList playlist = new PlayList(playlistName);
                        for (int j = 0; j < songUrls.size(); j++) {
                            String songUrl = songUrls.get(j).getAsString();
                            playlist.addSongUrl(songUrl);
                        }
                        newPlayLists.add(playlist);
                        dataBase.addPlaylist(playlist);
                    }
                    acc.setPlayLists(newPlayLists);
                    dataBase.saveDbFile();
                    return new Response("success", "Playlists saved successfully.", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response("failed", "Failed to save playlists: " + e.getMessage(), null);
                }
            }
        }
        return new Response("failed", "User not found", null);
    }

    public static Response getPlaylists(JsonObject payload) {
        String userId = payload.get("userId").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                try {
                    JsonArray playlists = new JsonArray();
                    for (PlayList p : acc.getPlayLists()) {
                        JsonObject playlistJson = new JsonObject();
                        playlistJson.addProperty("name", p.getPlaylistName());
                        JsonArray songUrls = new JsonArray();
                        for (String songUrl : p.getSongUrls()) {
                            songUrls.add(songUrl);
                        }
                        playlistJson.add("songUrls", songUrls);
                        playlists.add(playlistJson);
                    }
                    JsonObject responsePayload = new JsonObject();
                    responsePayload.add("playlists", playlists);
                    return new Response("success", "Playlists retrieved successfully.", responsePayload);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response("failed", "Failed to retrieve playlists: " + e.getMessage(), null);
                }
            }
        }
        return new Response("failed", "User not found", null);
    }

    public static Response addPlayList(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playListName = payload.get("playlistName").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList p : acc.getPlayLists()) {
                    if (p.getPlaylistName().equals(playListName)) {
                        return new Response("failed", "redundant playlist name", null);
                    }
                }
                PlayList playlist = new PlayList(playListName);
                try {
                    acc.addPlaylist(playlist);
                    dataBase.addPlaylist(playlist);
                    dataBase.saveDbFile();
                    JsonObject responsePl = new JsonObject();
                    responsePl.addProperty("playlistId", playlist.getPlayListID());
                    responsePl.addProperty("playlistDate", playlist.getPlayListDate());
                    return new Response("success", "new Playlist Created!", responsePl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response("failed", "Failed to add playlist: " + e.getMessage(), null);
                }
            }
        }
        return new Response("failed", "User not found", null);
    }

    public static Response removePlayList(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playlistId = payload.get("playlistId").getAsString();
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList p : acc.getPlayLists()) {
                    if (p.getPlayListID().equals(playlistId)) {
                        acc.removePlaylist(p);
                        dataBase.removePlaylist(p);
                        dataBase.saveDbFile();
                        return new Response("success", "Playlist deleted", null);
                    }
                }
                return new Response("failed", "Playlist not found", null);
            }
        }
        return new Response("failed", "User not found", null);
    }
    public static Response getPlaylistName(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playlistName = payload.get("playlistName").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList p : acc.getPlayLists()) {
                    if (p.getPlaylistName().equals(playlistName)) {
                        JsonObject responsePl = new JsonObject();
                        responsePl.addProperty("playlistId", p.getPlayListID());
                        responsePl.addProperty("playlistName", p.getPlaylistName());
                        responsePl.addProperty("playlistDate", p.getPlayListDate());

                        return new Response("success", "playlist found", responsePl);
                    }
                }
            }
        }
        return new Response("failed", "couldn't fetch playlist name and ID", null);
    }

    public static Response sharePlayListWith(JsonObject payload) {
        System.out.println("🔹 sharePlayListWith called with payload: " + payload);

        String fromUserId = payload.get("fromUserId").getAsString();
        String toAccountName = payload.get("toAccountName").getAsString();
        String playListId = payload.get("playlistId").getAsString();

        System.out.println("🔹 fromUserId: " + fromUserId);
        System.out.println("🔹 toAccountName: " + toAccountName);
        System.out.println("🔹 playListId: " + playListId);

        Account fromAccount = null;
        Account toAccount = null;
        PlayList deliveryPlaylist = null;

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(fromUserId)) {
                fromAccount = acc;
                System.out.println("✅ Found sender account: " + acc.getAccountName());
            }
            if (acc.getAccountName().equalsIgnoreCase(toAccountName)) {
                toAccount = acc;
                System.out.println("✅ Found receiver account: " + acc.getAccountName());
            }
        }

        if (fromAccount == null || toAccount == null) {
            System.out.println("❌ Sender or receiver not found");
            return new Response("failed", "Either sender or receiver account not found", null);
        }

        for (PlayList p : fromAccount.getPlayLists()) {
            if (p.getPlayListID().equals(playListId)) {
                deliveryPlaylist = p;
                System.out.println("✅ Found playlist in sender account: " + p.getPlaylistName());
                break;
            }
        }

        if (deliveryPlaylist == null) {
            System.out.println("❌ Playlist not found in sender account");
            return new Response("failed", "Playlist not found in sender's account", null);
        }

        if (!toAccount.canShareWith()) {
            System.out.println("❌ Receiver cannot share with this account: " + toAccount.getAccountName());
            return new Response("failed", "Playlist cannot be shared with " + toAccount.getAccountName(), null);
        }

        try {
            System.out.println("🔹 Creating copy of playlist for sharing...");
            PlayList deliveryCopy = new PlayList(deliveryPlaylist.getName());
            deliveryCopy.setPlayListID();
            System.out.println("🔹 New playlist ID: " + deliveryCopy.getPlayListID());

            System.out.println("🔹 Copying tracks...");
            int trackCount = 0;
            for (Track t : deliveryPlaylist.getTracksList()) {
                Track copiedTrack = new Track(t);
                deliveryCopy.addTrack(copiedTrack);
                System.out.println("   ➕ Copied track: " + copiedTrack.getTrackName() + " | URL: " + copiedTrack.getSongUrl());
                trackCount++;
            }
            System.out.println("🔹 Total tracks copied: " + trackCount);

            for (Track t : deliveryCopy.getTracksList()) {
                deliveryCopy.addSongUrl(t.getSongUrl());
            }

            System.out.println("🔹 Adding playlist copy to receiver account: " + toAccount.getAccountName());
            toAccount.addPlaylist(deliveryCopy);
            dataBase.addPlaylist(deliveryCopy);

            System.out.println("🔹 Saving database...");
            dataBase.saveDbFile();
            dataBase.addPlaylist(deliveryCopy);
            JsonObject payloadRes = new JsonObject();
            payloadRes.addProperty("toUserId", toAccount.getUserId());
            payloadRes.addProperty("playlistId", deliveryCopy.getPlayListID());

            System.out.println("✅ Playlist shared successfully!");
            return new Response("success", "Playlist shared with " + toAccount.getAccountName(), payloadRes);
        } catch (Exception e) {
            System.out.println("❌ Exception while sharing playlist: " + e.getMessage());
            e.printStackTrace();
            return new Response("failed", "Couldn't share playlist: " + e.getMessage(), null);
        }
    }


    public static Response changePlName(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playlistId = payload.get("playlistId").getAsString();
        String newPlaylistName = payload.get("newPlName").getAsString();
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                for (PlayList p : acc.getPlayLists()) {
                    if (p.getPlaylistName().equals(newPlaylistName)) {
                        return new Response("failed", "Playlist already exists", null);
                    }
                    if (p.getPlayListID().equals(playlistId)) {
                        p.setPlaylistName(newPlaylistName);
                        dataBase.saveDbFile();
                        return new Response("success", "Playlist name updated", null);
                    }
                }
                return new Response("failed", "Playlist not found", null);
            }
        }
        return new Response("failed", "User not found", null);
    }

    public ArrayList<Track> alphabeticalSort() {
        return sortTracksAlphabetically(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByYear() {
        return sortTracksByDate(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByLikes() {
        return sortTracksByLikes(playlist.getTracksList());
    }
}
