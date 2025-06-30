package models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class PlayList implements infoShower,TrackManager{

    Set<Track> TracksList = new HashSet<>();
    private String playlistName;
    private final String playListID;

    private int playListDate;


    //TODO: use the two variables below
    //private Duration playlistTime;
    //private String playListTimeStringed;

    //TODO: add a system which sums the length of the tracks and adds them to the playListTime;
    public PlayList(String playlistName) {
        this.playlistName = playlistName;
        playListID = Id_generator.generateId();
        playListDate = LocalDate.now().getYear();
        //this.playlistTime = Duration.ZERO;
    }

    public void addTrack(Track t){
        TracksList.add(t);
    }
    public void removeTrack(Track t){
        TracksList.remove(t);
    }


    public Set<Track> getTracksList() {
        return TracksList;
    }



    public String getPlayListID() {
        return playListID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public int getPlayListDate() {
        return playListDate;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

//    public String getPlayListTimeStringed() {
//        return playListTimeStringed;
//    }

    public int getSongCount() {
        return TracksList.size();
    }


//    public Duration getPlaylistTime() {
//        return playlistTime;
//    }
//
//    public void setPlaylistTime(Duration playlistTime) {
//        this.playlistTime = playlistTime;
//    }
//
//
//    public void setPlayListTimeStringed(String playListTimeStringed) {
//        this.playListTimeStringed = playListTimeStringed;
//    }
//
//    public LocalDate getPlayListDate() {
//        return playListDate;
//    }



    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayList)) return false;
        PlayList playList = (PlayList) o;
        return Objects.equals(TracksList, playList.TracksList) && Objects.equals(playlistName, playList.playlistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TracksList, playlistName);
    }

    @Override
    public String toString() {
        String toStringedPlaylist = "PlayList Name: " + playlistName + ", PlayList ID: "+ getPlayListID() + ", Tracks: "+ TracksList.size() + '\n';
        return toStringedPlaylist;
    }

    public String showInfo() {
        String songsnames = "";
        for(Track track : TracksList) {
            songsnames += track.toString() + '\n';
        }
        return songsnames;
    }
}
