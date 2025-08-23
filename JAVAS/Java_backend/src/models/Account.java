package models;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Account implements Serializable {
    private String userId;
    private String userToken;
    private String username;
    private String password; // هش ذخیره می‌شود
    private String accountName;
    private String email;
    private boolean canShareWith;

    private Map<String, Track> tracks = new HashMap<>();
    private List<PlayList> playLists = new ArrayList<>();
    private Set<String> ownedTrackIds = new HashSet<>();

    public Account(String username, String password, String accountName) {
        this.userId = Id_generator.generateId();
        this.username = username;
        this.password = password;
        this.accountName = accountName;
        this.canShareWith = true;
        this.userToken = generateUserToken();
    }

    private String generateUserToken() {
        return UUID.randomUUID().toString();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(hashPassword(inputPassword));
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean canShareWith() {
        return canShareWith;
    }
    public void setCanShareWith(boolean canShareWith) {
        this.canShareWith = canShareWith;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Track> getTracks() {
        return Collections.unmodifiableMap(tracks);
    }
    public void setTracks(Map<String, Track> tracks) {
        this.tracks = tracks != null ? new HashMap<>(tracks) : new HashMap<>();
    }

    public List<PlayList> getPlayLists() {
        return Collections.unmodifiableList(playLists);
    }
    public void setPlayLists(List<PlayList> playLists) {
        this.playLists = playLists != null ? new ArrayList<>(playLists) : new ArrayList<>();
    }

    public void addOwnedTrack(String trackId) {
        ownedTrackIds.add(trackId);
    }

    public void removeOwnedTrack(String trackId) {
        ownedTrackIds.remove(trackId);
    }

    public Set<String> getOwnedTrackIds() {
        return Collections.unmodifiableSet(ownedTrackIds);
    }
    public void setOwnedTrackIds(Set<String> ownedTrackIds) {
        this.ownedTrackIds = ownedTrackIds != null ? new HashSet<>(ownedTrackIds) : new HashSet<>();
    }

    public List<Track> getAllTracks() {
        return new ArrayList<>(tracks.values());
    }

    public void addTrack(Track track) {
        if (track != null) {
            tracks.put(track.getTrackId(), track);
        }
    }

    public void removeTrack(Track track) {
        if (track != null) {
            tracks.remove(track.getTrackId());
        }
    }

    public void addPlaylist(PlayList playlist) throws Exception {
        if (playlist == null) return;
        for (PlayList pl : playLists) {
            if (pl.getPlayListID().equals(playlist.getPlayListID())) {
                throw new Exception("Playlist already exists in account");
            }
        }
        playLists.add(playlist);
    }

    public void removePlaylist(PlayList playlist) {
        playLists.remove(playlist);
    }

    public String showInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Account Details:\n");
        sb.append("User ID: ").append(userId).append("\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("Account Name: ").append(accountName).append("\n");
        sb.append("Can Share With Others: ").append(canShareWith).append("\n");
        sb.append("Number of Playlists: ").append(playLists.size()).append("\n");
        sb.append("Number of Tracks: ").append(tracks.size()).append("\n");
        sb.append("Tracks:\n");
        for (Track t : tracks.values()) {
            sb.append("  - ").append(t.getTrackName()).append("\n");
        }
        sb.append("Playlists:\n");
        for (PlayList pl : playLists) {
            sb.append("  - ").append(pl.getPlaylistName()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Account{" +
                "userId='" + userId + '\'' +
                ", userToken='" + userToken + '\'' +
                ", username='" + username + '\'' +
                ", accountName='" + accountName + '\'' +
                ", canShareWith=" + canShareWith +
                ", tracks=" + tracks.size() +
                ", playLists=" + playLists.size() +
                '}';
    }
}













