package services;
import API_messages.Response;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import models.*;
import server.*;
import models.utility.*;
import java.util.ArrayList;

public class AccountServicer extends AudioSorter implements CanShare{
    private Account account;
    private static DataBase dataBase = DataBase.getInstance();
    private static final Gson gson = new Gson();

    private static Account activeAccount;

    public AccountServicer(Account account) {
        this.account = account;
    }
    public static Response signIn(JsonObject payload){
        String accountName = payload.get("accountName").getAsString();
        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUsername().equals(username)) {
                return new Response("fail", "This username Already Exists.", null);
            }
        }
        Account account = new Account(username, password, accountName);
        dataBase.addAccount(account);
        JsonObject accountPayload = new JsonObject();
        accountPayload.addProperty("userId", account.getUserId());
        accountPayload.addProperty("Token", account.getUserToken());
        return new Response("Success","Account created",accountPayload);
    }
    public static Response logIn(JsonObject payload){
        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();
        JsonObject accountPayload = new JsonObject();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUsername().equals(username) && acc.getPassword().equals(password)) {
                activeAccount = acc;
                accountPayload.addProperty("userId", acc.getUserId());
                accountPayload.addProperty("Token", acc.getUserToken());
                accountPayload.addProperty("accountName", acc.getAccountName());
                return new Response("success", "Login successful.", accountPayload);
            }
        }
        return new Response("fail", "Couldn't log in :(", null);
    }

    public static Response logOut(){
        if (activeAccount != null) {
            activeAccount = null;
            return new Response("success", "Logged out.", null);
        } else {
            return new Response("fail", "No user is logged in.", null);
        }
    }

    public static Response changePassword(JsonObject payload){
        String userId = payload.get("userId").getAsString();
        String oldPassword = payload.get("oldPassword").getAsString();
        String newPassword = payload.get("newPassword").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUserId().equals(userId)) {
                acc.setPassword(newPassword);
                dataBase.addAccount(acc);
                return new Response("success", "Password changed.", null);
            }
        }
        return new Response("fail", "Couldn't change password :(", null);
    }

    public static Response changeUsername(JsonObject payload){
        //TODO: maybe remove the tracks and playlists because they are changing the password after all idk lololololol
        String userId = payload.get("userId").getAsString();
        String oldUsername = payload.get("oldUsername").getAsString();
        String newUsername = payload.get("newUsername").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUserId().equals(userId)) {
                acc.setUsername(newUsername);
                dataBase.addAccount(acc);
                return new Response("success", "Username changed.", null);
            }
        }
        return new Response("fail", "Couldn't change Username :(", null);
    }

    public static Response changeAccountName(JsonObject payload){
        String userId = payload.get("userId").getAsString();
        String oldAccountName = payload.get("oldAccountName").getAsString();
        String newAccountName = payload.get("newAccountName").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUserId().equals(userId)) {
                acc.setAccountName(newAccountName);
                dataBase.addAccount(acc);
                return new Response("success", "Account name changed.", null);
            }
        }
        return new Response("fail", "Couldn't change your Account name :(", null);
    }
    public static Response DeleteAccount(JsonObject payload){
        String userId = payload.get("userId").getAsString();
        for(Account acc : dataBase.getAccounts().values()){
            if (acc.getUserId().equals(userId)) {
                dataBase.removeAccount(acc);
                return new Response("success", "Account successfully deleted.", null);
            }
        }
        return new Response("fail", "Couldn't delete your account :(", null);
    }
    public static Response downloadTrack(JsonObject payload){
        return null;
    }

    public static Response UploadTrack(){
        return null;
    }

    public ArrayList<Track> alphabeticalSort() {
        return sortTracksAlphabetically(account.getAllTracks());
    }

    public ArrayList<Track> sortTracksByYear() {
        return AudioSorter.sortTracksByDate(account.getAllTracks());
    }

    public ArrayList<Track> sortTracksByLikes() {
        return sortTracksByLikes(account.getAllTracks());
    }

    public void shareTrack(Track track, Account... accounts){
        for(Account acc :accounts){
            if(!acc.CanShareWith()){
                return;
            }
            if(acc != null){
                acc.addTrack(track);
            }
        }
    }
    public void sharePlayList(PlayList playList, Account... accounts){
        for(Account acc :accounts){
            if(!acc.CanShareWith()){
               return;
            }
            if(acc != null){
                try {
                acc.Addplaylist(playList);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }




}
