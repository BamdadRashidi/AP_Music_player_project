package models;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class PlayList implements infoShower, TrackManager {

    private Set<Track> tracksList = new HashSet<>();
    private String playlistName;
    private final String playListID;
    private int playListDate;

    // TODO: use these later
    // private Duration playlistTime;
    // private String playListTimeStringed;

    public PlayList(String playlistName) {
        this.playlistName = playlistName;
        this.playListID = Id_generator.generateId();
        this.playListDate = LocalDate.now().getYear();
        // this.playlistTime = Duration.ZERO;
    }

    public void addTrack(Track t) {
        if (t != null) {
            tracksList.add(t);
            // TODO: update playlistTime here if you implement it
        }
    }

    public void removeTrack(Track t) {
        tracksList.remove(t);
        // TODO: update playlistTime here
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
        return tracksList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayList)) return false;
        PlayList playList = (PlayList) o;
        return Objects.equals(tracksList, playList.tracksList) &&
                Objects.equals(playlistName, playList.playlistName) &&
                Objects.equals(playListID, playList.playListID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tracksList, playlistName, playListID);
    }

    @Override
    public String toString() {
        return "PlayList Name: " + playlistName +
                ", PlayList ID: " + playListID +
                ", Tracks: " + tracksList.size() + '\n';
    }

    @Override
    public String showInfo() {
        StringBuilder songsNames = new StringBuilder();
        for (Track track : tracksList) {
            songsNames.append(track.toString()).append('\n');
        }
        return songsNames.toString();
    }
}
