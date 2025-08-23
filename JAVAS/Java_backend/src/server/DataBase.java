package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.*;
import java.net.URL;
import java.io.InputStream;


import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBase {
    private final Map<String, Account> accounts = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, Track> tracks = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, PlayList> playlists = Collections.synchronizedMap(new LinkedHashMap<>());

    private final String dataBaseFile = "db.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ReentrantReadWriteLock readAndWriteLock = new ReentrantReadWriteLock();

    private static volatile DataBase db;

    public static final String TRACKS_FOLDER = "tracks";
    static {
        File tracksDir = new File(TRACKS_FOLDER);
        if (!tracksDir.exists()) {
            boolean ok = tracksDir.mkdirs();
            if (!ok) System.out.println("Warning: could not create tracks folder");
        }
    }

    private DataBase() {
        loadDbFileAndSyncTracks();
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

    public List<Track> getUserLibrary(String userId) {
        readAndWriteLock.readLock().lock();
        try {
            Account acc = accounts.get(userId);
            if (acc == null) return Collections.emptyList();
            return new ArrayList<>(acc.getTracks().values());
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public void reloadDataFromFile() {
        readAndWriteLock.writeLock().lock();
        try {
            accounts.clear();
            tracks.clear();
            playlists.clear();
            loadDbFileAndSyncTracks();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    private void loadDbFileAndSyncTracks() {
        File file = new File(dataBaseFile);
        if (!file.exists()) return;

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                    try {
                        if (json == null || json.isJsonNull()) return null;
                        if (json.isJsonPrimitive()) return LocalDate.parse(json.getAsString());
                        if (json.isJsonObject()) {
                            JsonObject jo = json.getAsJsonObject();
                            int year = jo.has("year") ? jo.get("year").getAsInt() : 1970;
                            int month = jo.has("month") ? jo.get("month").getAsInt() : 1;
                            int day = jo.has("day") ? jo.get("day").getAsInt() : 1;
                            return LocalDate.of(year, month, day);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .create();

        readAndWriteLock.writeLock().lock();
        try (Reader reader = new FileReader(file)) {
            Map<String, JsonElement> jsonData = gson.fromJson(reader, new TypeToken<Map<String, JsonElement>>() {}.getType());
            if (jsonData == null) return;

            Map<String, Account> loadedAccounts = jsonData.get("accounts") != null
                    ? gson.fromJson(jsonData.get("accounts"), new TypeToken<Map<String, Account>>() {}.getType())
                    : new HashMap<>();

            Map<String, Track> loadedTracks = jsonData.get("tracks") != null
                    ? gson.fromJson(jsonData.get("tracks"), new TypeToken<Map<String, Track>>() {}.getType())
                    : new HashMap<>();

            Map<String, PlayList> loadedPlaylists = jsonData.get("playlists") != null
                    ? gson.fromJson(jsonData.get("playlists"), new TypeToken<Map<String, PlayList>>() {}.getType())
                    : new HashMap<>();

            accounts.putAll(loadedAccounts);
            tracks.putAll(loadedTracks);
            playlists.putAll(loadedPlaylists);

            for (Account acc : accounts.values()) {
                if (acc.getTracks() == null) acc.setTracks(new HashMap<>());
                if (acc.getPlayLists() == null) acc.setPlayLists(new ArrayList<>());
                if (acc.getOwnedTrackIds() == null) acc.setOwnedTrackIds(new HashSet<>());

                Set<String> owned = new HashSet<>(acc.getOwnedTrackIds());
                for (String trackId : owned) {
                    Track t = tracks.get(trackId);
                    if (t != null) {
                        acc.addTrack(t);


//                        if (!isTrackFilePresent(t)) {
//                            downloadTrack(t);
//                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }


    private boolean isTrackFilePresent(Track track) {
        File f = new File(getLocalTrackPath(track));
        return f.exists();
    }


    private String getLocalTrackPath(Track track) {
        return "tracks/" + track.getTrackId() + ".mp3";
    }


//    private void downloadTrack(Track track) {
//        if (track.getSongBase64() == null || track.getSongBase64().isEmpty()) return;
//
//        try {
//            byte[] fileBytes = Base64.getDecoder().decode(track.getSongBase64());
//            Path target = Paths.get(TRACKS_FOLDER, track.getTrackId() + ".mp3");
//
//            Files.write(target, fileBytes);
//
//            track.setSongUrl(target.toString()); // حالا مسیر لوکال رو می‌ذاری
//            System.out.println("Track saved locally: " + track.getTrackId());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }





    public void saveDbFile() {
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

    public void addAccount(Account acc) {
        readAndWriteLock.writeLock().lock();
        try {
            accounts.put(acc.getUserId(), acc);
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public void removeAccount(Account acc) {
        readAndWriteLock.writeLock().lock();
        try {
            accounts.remove(acc.getUserId());
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public void addTrackAndAssignToUser(Track track, String userId) {
        readAndWriteLock.writeLock().lock();
        try {

            if (track.getSongUrl() != null && !track.getSongUrl().isEmpty()) {
                Path path = Paths.get(track.getSongUrl());
                if (!Files.exists(path)) {
                    System.err.println("Warning: Track file does not exist: " + track.getSongUrl());
                    track.setSongUrl(null);
                } else {
                    System.out.println("Track file exists: " + track.getSongUrl());
                }
            }


            tracks.put(track.getTrackId(), track);


            Account acc = accounts.get(userId);
            if (acc != null) {
                acc.addOwnedTrack(track.getTrackId());
                acc.addTrack(track);
            } else {
                System.err.println("Warning: userId not found. Track added to general collection but not assigned to user.");
            }

            System.out.println("Track added: " + track.getTrackId() + " for user: " + userId);
            saveDbFile();

        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }











    public void removeTrack(String userId, String trackId) {
        readAndWriteLock.writeLock().lock();
        try {
            Account acc = accounts.get(userId);
            if (acc != null) acc.removeOwnedTrack(trackId);
            Track removed = tracks.remove(trackId);
            if (removed != null && removed.getSongUrl() != null) {
                try {
                    Files.deleteIfExists(Paths.get(removed.getSongUrl()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public Account fetchAccount(String userId) {
        readAndWriteLock.readLock().lock();
        try {
            return accounts.get(userId);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public void addTrack(Track t) {
        readAndWriteLock.writeLock().lock();
        try {
            tracks.put(t.getTrackId(), t);
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public Track fetchTrack(String id) {
        readAndWriteLock.readLock().lock();
        try {
            System.out.println("🔎 Fetching from tracks map: " + id);
            System.out.println("📦 All track keys in map: " + tracks.keySet());
            return tracks.get(id);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }


    public void addPlaylist(PlayList p) {
        readAndWriteLock.writeLock().lock();
        try {
            playlists.put(p.getPlayListID(), p);
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public void removePlaylist(PlayList p) {
        readAndWriteLock.writeLock().lock();
        try {
            if (p != null) {
                playlists.remove(p.getPlayListID());
                saveDbFile();
            }
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }

    public PlayList fetchPlaylist(String id) {
        readAndWriteLock.readLock().lock();
        try {
            return playlists.get(id);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public Map<String, Account> getAccounts() {
        readAndWriteLock.readLock().lock();
        try {
            return Collections.unmodifiableMap(accounts);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public Map<String, PlayList> getPlaylists() {
        readAndWriteLock.readLock().lock();
        try {
            return Collections.unmodifiableMap(playlists);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public Map<String, Track> getTracks() {
        readAndWriteLock.readLock().lock();
        try {
            return Collections.unmodifiableMap(tracks);
        } finally {
            readAndWriteLock.readLock().unlock();
        }
    }

    public void clear() {
        readAndWriteLock.writeLock().lock();
        try {
            accounts.clear();
            tracks.clear();
            playlists.clear();
            db = null;
            saveDbFile();
        } finally {
            readAndWriteLock.writeLock().unlock();
        }
    }
}



