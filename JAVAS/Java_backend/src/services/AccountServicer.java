package services;

import API_messages.Response;
import com.google.gson.*;
import models.*;
import server.DataBase;
import models.utility.AudioSorter;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountServicer extends AudioSorter {
    private Account account;
    private static DataBase dataBase = DataBase.getInstance();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalAdaptor())
            .setPrettyPrinting()
            .create();

    private static Account activeAccount;

    public AccountServicer(Account account) {
        this.account = account;
    }

    public static Response signIn(JsonObject payload) {
        String accountName = payload.get("accountName").getAsString();
        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUsername().equals(username)) {
                return new Response("fail", "This username Already Exists.", null);
            }
        }
        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getAccountName().equals(accountName)) {
                return new Response("fail", "A username with this AccountName exists.", null);
            }
        }

        Account account = new Account(username, password, accountName);
        dataBase.addAccount(account);

        JsonObject accountPayload = new JsonObject();
        accountPayload.addProperty("userId", account.getUserId());
        accountPayload.addProperty("Token", account.getUserToken());

        return new Response("Success", "Account created", accountPayload);
    }

    public static Response logIn(JsonObject payload) {
        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();

        DataBase db = DataBase.getInstance();
        db.reloadDataFromFile();

        for (Account acc : db.getAccounts().values()) {
            if (acc.getUsername().equals(username) && acc.getPassword().equals(password)) {

                JsonObject accountPayload = new JsonObject();
                accountPayload.addProperty("userId", acc.getUserId());
                accountPayload.addProperty("token", acc.getUserToken());
                accountPayload.addProperty("accountName", acc.getAccountName());

                List<Track> library = db.getUserLibrary(acc.getUserId());

                Gson customGson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                            if (json == null || json.isJsonNull()) return null;
                            if (json.isJsonPrimitive()) {
                                return LocalDate.parse(json.getAsString());
                            } else if (json.isJsonObject()) {
                                JsonObject jo = json.getAsJsonObject();
                                int year = jo.has("year") ? jo.get("year").getAsInt() : 1970;
                                int month = jo.has("month") ? jo.get("month").getAsInt() : 1;
                                int day = jo.has("day") ? jo.get("day").getAsInt() : 1;
                                return LocalDate.of(year, month, day);
                            }
                            return null;
                        }).create();

                accountPayload.add("library", customGson.toJsonTree(library));

                return new Response("success", "Login successful", accountPayload);
            }
        }

        return new Response("failed", "Couldn't log in :(", null);
    }

    public static Response logOut() {
        if (activeAccount != null) {
            activeAccount = null;
            return new Response("success", "Logged out.", null);
        } else {
            return new Response("fail", "No user is logged in.", null);
        }
    }

    public static Response DeleteAccount(JsonObject payload) {
        String userId = payload.get("userId").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                dataBase.removeAccount(acc);
                return new Response("success", "Account successfully deleted.", null);
            }
        }
        return new Response("fail", "Couldn't delete your account :(", null);
    }

    public static Response updateAccount(JsonObject payload) {
        String userId = payload.get("userId").getAsString();
        String newUsername = payload.get("newUsername").getAsString();
        String newPassword = payload.get("newPassword").getAsString();
        String newAccountName = payload.get("newAccountName").getAsString();
        String canShare = payload.get("canShare").getAsString();

        for (Account acc : dataBase.getAccounts().values()) {
            if (acc.getUserId().equals(userId)) {
                if (!newAccountName.isEmpty()) {
                    acc.setAccountName(newAccountName);
                }
                if (!newPassword.isEmpty()) {
                    acc.setPassword(newPassword);
                }
                if (!newUsername.isEmpty()) {
                    acc.setUsername(newUsername);
                }
                acc.setCanShareWith(canShare.equalsIgnoreCase("true"));

                dataBase.addAccount(acc);
                return new Response("success", "Account successfully updated.", null);
            }
        }
        return new Response("fail", "Couldn't update your account :(", null);
    }

    public static Response downloadTrack(JsonObject payload) {
        try {
            String fileUrl = payload.get("fileUrl").getAsString();
            String savePath = "downloads/" + UUID.randomUUID() + ".mp3";

            Files.createDirectories(Paths.get("downloads"));
            try (InputStream in = new URL(fileUrl).openStream()) {
                Files.copy(in, Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
            }

            JsonObject res = new JsonObject();
            res.addProperty("localPath", savePath);

            return new Response("success", "Track downloaded", res);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("fail", "Download failed", null);
        }
    }

    public static Response UploadTrack() {
        try {
            String uploadDir = "uploaded_tracks";
            Files.createDirectories(Paths.get(uploadDir));

            // در عمل، باید از استریم ورودی بگیریم
            // اینجا به صورت نمایشی یک فایل تست ذخیره می‌کنیم
            String fileName = UUID.randomUUID() + ".mp3";
            Path filePath = Paths.get(uploadDir, fileName);

            // فایل تست
            Files.write(filePath, "dummy mp3 data".getBytes());

            String fileUrl = "http://your-server-address/" + uploadDir + "/" + fileName;

            JsonObject res = new JsonObject();
            res.addProperty("fileUrl", fileUrl);

            return new Response("success", "Track uploaded", res);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("fail", "Upload failed", null);
        }
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

    public void shareTrack(Track track, Account... accounts) {
        for (Account acc : accounts) {
            if (acc != null && acc.canShareWith()) {
                acc.addTrack(track);
            }
        }
    }

    public void sharePlayList(PlayList playList, Account... accounts) {
        for (Account acc : accounts) {
            if (acc != null && acc.canShareWith()) {
                try {
                    acc.addPlaylist(playList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


