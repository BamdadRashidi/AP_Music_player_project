package server;
import models.Account;
import models.PlayList;
import models.Track;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;
public class DataBase {
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Track> tracks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayList> playlists = new ConcurrentHashMap<>();
    private final String dataBaseFile = "db.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ReentrantReadWriteLock ReadAndWriteLock = new ReentrantReadWriteLock();

    private static volatile DataBase db = new DataBase();

    public DataBase() {
        loadDbFile();
    }

    public static DataBase getInstance() {
        if (db == null) {
            synchronized (DataBase.class) {
                if (db == null) {
                    db = new DataBase();
                }
            }
        }
        return db;
    }

    private void loadDbFile(){
        File file = new File(dataBaseFile);
        if (!file.exists()){
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<Map<String, JsonElement>>() {}.getType();
            Map<String, JsonElement> jsonData = gson.fromJson(reader, listType);

            if (jsonData != null) {
                Type accountMapType = new TypeToken<ConcurrentHashMap<String, Account>>() {}.getType();
                Type trackMapType = new TypeToken<ConcurrentHashMap<String, Track>>() {}.getType();
                Type playlistMapType = new TypeToken<ConcurrentHashMap<String, PlayList>>() {}.getType();

                //TODO: just wrote this in case shit goes wrong due to the strings in jsonData.get("insert your String here"); to make debugging easier
                ConcurrentHashMap<String, Account> loadedAccounts = gson.fromJson(jsonData.get("accounts"), accountMapType);
                ConcurrentHashMap<String, Track> loadedTracks = gson.fromJson(jsonData.get("tracks"), trackMapType);
                ConcurrentHashMap<String, PlayList> loadedPlaylists = gson.fromJson(jsonData.get("playlists"), playlistMapType);

                if (loadedAccounts != null){
                    accounts.putAll(loadedAccounts);
                }
                if (loadedTracks != null){
                    tracks.putAll(loadedTracks);
                }
                if (loadedPlaylists != null){
                    playlists.putAll(loadedPlaylists);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDbFile(){
        try (Writer writer = new FileWriter(dataBaseFile)) {
            Map<String, Object> data = new HashMap<>();
            data.put("accounts", accounts);
            data.put("tracks", tracks);
            data.put("playlists", playlists);
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // adding, removing and all that fancy CRUD stuff


    public void addAccount(Account acc) {
        ReadAndWriteLock.writeLock().lock();
        try {
            accounts.put(acc.getUserId(), acc);
            saveDbFile();
        } finally {
            ReadAndWriteLock.writeLock().unlock();
        }
    }

    public Account getAccount(String userId) {
        ReadAndWriteLock.readLock().lock();
        try {
            return accounts.get(userId);
        } finally {
            ReadAndWriteLock.readLock().unlock();
        }
    }

    public void addTrack(Track t) {
        ReadAndWriteLock.writeLock().lock();
        try {
            tracks.put(t.getTrackId(), t);
            saveDbFile();
        }
        finally {
            ReadAndWriteLock.writeLock().unlock();
        }
    }

    public Track getTrack(String id) {
        ReadAndWriteLock.readLock().lock();
        try {
            return tracks.get(id);
        }
        finally {
            ReadAndWriteLock.readLock().unlock();
        }
    }

    public void addPlaylist(PlayList p) {
        ReadAndWriteLock.writeLock().lock();
        try {
            playlists.put(p.getPlayListID(), p);
            saveDbFile();
        }
        finally {
            ReadAndWriteLock.writeLock().unlock();
        }
    }

    public PlayList getPlaylist(String id) {
        ReadAndWriteLock.readLock().lock();
        try {
            return playlists.get(id);
        }
        finally {
            ReadAndWriteLock.readLock().unlock();
        }
    }



    //WARNING: this is for test use only unless we want to shut the app down lol :P
    private void clear(){
        for(Map.Entry<String, Account> entry : accounts.entrySet()){
            entry = null;
        }
        for(Map.Entry<String, Track> entry : tracks.entrySet()){
            entry = null;
        }
        for(Map.Entry<String, PlayList> entry : playlists.entrySet()){
            entry = null;
        }
        db = null;
    }
}
