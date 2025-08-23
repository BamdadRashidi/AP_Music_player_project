package services;
import com.google.gson.JsonObject;
import models.*;
import models.utility.AudioSorter;
import server.*;
import API_messages.*;
import models.utility.*;

import java.util.ArrayList;

public class PlayListServicer extends AudioSorter {
    private PlayList playlist;
    private static DataBase dataBase = DataBase.getInstance();
    public PlayListServicer(PlayList playlist) {
        this.playlist = playlist;
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
                try{
                    acc.addPlaylist(playlist);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                dataBase.addPlaylist(playlist);
                JsonObject responsePl = new JsonObject();
                responsePl.addProperty("playlistId", playlist.getPlayListID());
                responsePl.addProperty("playlistDate", playlist.getPlayListDate());
                return new Response("success", "new Playlist Created!", responsePl);
            }
        }

        return new Response("failed", "no playlist added to account", null);
    }


    public static Response removePlayList(JsonObject payload){
        String userId = payload.get("userId").getAsString();
        String playlistId = payload.get("playlistId").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if(acc.getUserId().equals(userId)){
                for(PlayList p : acc.getPlayLists()){
                    if(p.getPlayListID().equals(playlistId)){
                        acc.removePlaylist(p);
                        dataBase.removePlaylist(p);
                        dataBase.saveDbFile();
                        return new Response("success","playlist deleted",null);
                    }
                }
            }
        }
        return new Response("failed","playlist not deleted",null);
    }


    public static Response getPlaylistName(JsonObject payload){
        String userId = payload.get("userId").getAsString();
        String playlistName = payload.get("playlistName").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if(acc.getUserId().equals(userId)){
                for(PlayList p : acc.getPlayLists()){
                    if(p.getPlaylistName().equals(playlistName)){
                        JsonObject responsePl = new JsonObject();
                        responsePl.addProperty("playlistId", p.getPlayListID());
                        responsePl.addProperty("playlistName", p.getPlayListDate());
                        return new Response("success","playlist found",responsePl);
                    }
                }
            }
        }
        return new Response("failed","couldn't fetch playlist name and ID found",null);
    }

    public static Response sharePlayListWith(JsonObject payload) {
        String fromUserId = payload.get("fromUserId").getAsString();
        String toUserId = payload.get("toUserId").getAsString();
        String playListId = payload.get("playlistId").getAsString();
        Account fromAccount = null;
        Account toAccount = null;
        PlayList deliveryPlaylist = null;
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(fromUserId)) {
                fromAccount = acc;
            }
            if (acc.getUserId().equals(toUserId)) {
                toAccount = acc;
            }
        }
        if (fromAccount == null || toAccount == null) {
            return new Response("failed", "either sender or receiver account not found", null);
        }
        for (PlayList p : fromAccount.getPlayLists()) {
            if (p.getPlayListID().equals(playListId)) {
                deliveryPlaylist = p;
                break;
            }
        }
        if (deliveryPlaylist == null) {
            return new Response("failed", "playlist not found in sender's account", null);
        }
        if (!toAccount.canShareWith()) {
            return new Response("failed", "playlist cannot be shared with " + toAccount.getAccountName(), null);
        }
        try {
            toAccount.addPlaylist(deliveryPlaylist);
            dataBase.saveDbFile();
            return new Response("success", "playlist shared with " + toAccount.getAccountName(), null);
        } catch (Exception e) {
            return new Response("failed", "couldn't share playlist", null);
        }
    }

    public static Response changePlName(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String playlistId = payload.get("playlistId").getAsString();
        String newPlaylistName = payload.get("newPlName").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if(acc.getUserId().equals(userId)){
                for(PlayList p : acc.getPlayLists()){
                    if(p.getPlaylistName().equals(newPlaylistName)){
                        return new Response("fail", "playlist already exists", null);
                    }
                    if(p.getPlayListID().equals(playlistId)){
                        p.setPlaylistName(newPlaylistName);
                        dataBase.saveDbFile();
                        return new Response("success", "playlist name updated", null);
                    }
                }
            }
        }
        return new Response("failed", "playlist didn't update found", null);
    }


    public ArrayList<Track> alphabeticalSort(){
        return sortTracksAlphabetically(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByYear(){
        return sortTracksByDate(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByLikes(){
        return sortTracksByLikes(playlist.getTracksList());
    }
}
