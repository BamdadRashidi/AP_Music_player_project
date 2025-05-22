package models;

import java.io.Serializable;
import java.util.Objects;
import java.time.*;

public class Track implements Serializable , Comparable<Track> {
    private String trackName;
    private String trackId;
    private String artistName;
    private String genre;
    private LocalDate trackDate;
    private boolean isLiked;
    private Integer likes = 0;
    private int plays = 0;

    private boolean isExplicit = false;
    public Track(String trackName, String artistName, String genre, boolean explicit) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackDate = LocalDate.now();
        this.isExplicit = explicit;
        this.genre = genre;
        this.trackId = Id_generator.generateId();
        Admin.addTrackToList(this);
    }

    @Override
    public int compareTo(Track other) {
        return this.trackName.compareToIgnoreCase(other.trackName);
    }

    public void likeTrack(){
        isLiked = true;
        likes++;
    }

    public void unlikeTrack(){
        isLiked = false;
        likes--;
    }


    public void PlayTrack(){
        plays++;
    }
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public int getTrackDate() {
        return trackDate.getYear();
    }

    public void setTrackDate(LocalDate trackDate) {
        this.trackDate = trackDate;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getArtistName() {
        return artistName;
    }


    @Override
    public String toString() {
        return "[trackName: " + trackName + "]"+ ", [Plays:" + getPlays()+ "]" +", [trackId: " + trackId + "]" + ", [Artist Name: " + artistName + "]"+ ", [Genre: "+ genre + "]" + ", [trackDate: " + trackDate.getYear() + "]" + ", [Likes: " +  + likes + "]" + ", [Explicit: " + isExplicit + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return isLiked == track.isLiked && Objects.equals(trackName, track.trackName) && Objects.equals(trackId, track.trackId) && Objects.equals(trackDate, track.trackDate) && Objects.equals(likes, track.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackName, trackId, trackDate, isLiked, likes);
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public int getPlays() {
        return plays;
    }


    public void setExplicit(boolean explicit) {
        isExplicit = explicit;
    }

    public boolean isExplicit() {
        return isExplicit;
    }
}