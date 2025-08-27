package models;

import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDate;

public class Track implements Serializable, Comparable<Track> {
    private String trackName;
    private String trackId;
    private String artistName;
    private Genres genre;
    private LocalDate trackDate;
    private boolean isLiked;
    private Integer likes = 0;
    private boolean isExplicit = false;


    private String songUrl;
    public Track() {
        this.trackDate = LocalDate.now();
        this.trackId = Id_generator.generateId();
    }

    public Track(String trackName, String artistName, Genres genre, boolean explicit) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.genre = genre;
        this.isExplicit = explicit;
        this.trackDate = LocalDate.now();
        this.trackId = Id_generator.generateId();
    }

    public Track(Track other){
        this.trackName = other.trackName;
        this.artistName = other.artistName;
        this.genre = other.genre;
        this.isExplicit = other.isExplicit;
        this.trackDate = other.trackDate;
        this.trackId = Id_generator.generateId();
        this.songUrl = other.songUrl;
    }

    public String getTrackName() { return trackName; }
    public void setTrackName(String trackName) { this.trackName = trackName; }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public Genres getGenre() { return genre; }
    public void setGenre(Genres genre) { this.genre = genre; }

    public LocalDate getFullTrackDate() { return trackDate; }
    public void setTrackDate(LocalDate trackDate) { this.trackDate = trackDate; }
    public String getTrackDate() { return trackDate.toString(); }

    public boolean isLiked() { return isLiked; }
    public void likeTrack() {
        isLiked = !isLiked;
        if (isLiked) likes++; else likes--;
    }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public boolean isExplicit() { return isExplicit; }
    public void setExplicit(boolean explicit) { isExplicit = explicit; }

    public String getSongUrl() { return songUrl; }
    public void setSongUrl(String songUrl) { this.songUrl = songUrl; }

    @Override
    public int compareTo(Track other) {
        return this.trackName.compareToIgnoreCase(other.trackName);
    }

    @Override
    public String toString() {
        return "[trackName: " + trackName + "]" +
                ", [trackId: " + trackId + "]" +
                ", [Artist Name: " + artistName + "]" +
                ", [Genre: " + genre + "]" +
                ", [trackDate: " + trackDate + "]" +
                ", [Likes: " + likes + "]" +
                ", [Explicit: " + isExplicit + "]" +
                ", [songUrl: " + songUrl + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return Objects.equals(trackId, track.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId);
    }
}




