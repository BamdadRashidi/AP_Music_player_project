package models;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import com.google.gson.*;

public class PlayList implements infoShower, TrackManager {
    private Set<Track> tracksList = new HashSet<>();
    private List<String> songUrls = new ArrayList<>();
    private String playlistName;
    private String playListID;
    private int playListDate;

    public PlayList(String playlistName) {
        this.playlistName = playlistName;
        this.playListID = Id_generator.generateId();
        this.playListDate = LocalDate.now().getYear();
    }

    public void addSongUrl(String songUrl) {
        if (songUrl != null && !songUrls.contains(songUrl)) {
            songUrls.add(songUrl);
            Track track = new Track();
            track.setSongUrl(songUrl);
            tracksList.add(track);
        }
    }

    public void removeSongUrl(String songUrl) {
        songUrls.remove(songUrl);
        tracksList.removeIf(track -> songUrl.equals(track.getSongUrl()));
    }


    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("playlistId", this.playListID);
        obj.addProperty("name", this.getPlaylistName());

        JsonArray songsArray = new JsonArray();
        for (Track t : tracksList) {
            JsonObject trackObj = new JsonObject();
            trackObj.addProperty("id", t.getTrackId());
            trackObj.addProperty("title", t.getTrackName());
            trackObj.addProperty("artist", t.getArtistName());
            trackObj.addProperty("songUrl", t.getSongUrl());
            trackObj.addProperty("genre", t.getGenre().toString());
            trackObj.addProperty("isExplicit", t.isExplicit());
            songsArray.add(trackObj);
        }

        obj.add("songs", songsArray);
        return obj;
    }


    public List<String> getSongUrls() {
        return Collections.unmodifiableList(songUrls);
    }

    public void addTrack(Track t) {
        if (t != null) {
            tracksList.add(t);
            String songUrl = t.getSongUrl();
            if (songUrl != null && !songUrls.contains(songUrl)) {
                songUrls.add(songUrl);
            }
        }
    }

    public void removeTrack(Track t) {
        if (t != null) {
            tracksList.remove(t);
            String songUrl = t.getSongUrl();
            songUrls.remove(songUrl);
        }
    }

    public Set<Track> getTracksList() {
        return Collections.unmodifiableSet(tracksList);
    }

    public String getPlayListID() {
        return playListID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public int getPlayListDate() {
        return playListDate;
    }

    public int getSongCount() {
        return songUrls.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayList)) return false;
        PlayList playList = (PlayList) o;
        return Objects.equals(songUrls, playList.songUrls) &&
                Objects.equals(playlistName, playList.playlistName) &&
                Objects.equals(playListID, playList.playListID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songUrls, playlistName, playListID);
    }

    @Override
    public String toString() {
        return "PlayList Name: " + playlistName +
                ", PlayList ID: " + playListID +
                ", Tracks: " + songUrls.size() + '\n';
    }

    public void setName(String name){
        playlistName = name;
    }
    public String getName(){
        return playlistName;
    }

    public void setPlayListID(){
        playListID = Id_generator.generateId();
    }
    @Override
    public String showInfo() {
        StringBuilder songsNames = new StringBuilder();
        for (String songUrl : songUrls) {
            songsNames.append(songUrl).append('\n');
        }
        return songsNames.toString();
    }
}
