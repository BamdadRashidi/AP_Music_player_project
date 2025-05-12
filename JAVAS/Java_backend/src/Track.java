import java.io.Serializable;
import java.util.Objects;
import java.time.*;

public class Track implements Serializable , Comparable<Track> {
    private String trackName;
    private String trackId;
    private String artistName;
    private LocalTime trackDate;
    private boolean isLiked;
    private Integer likes = 0;

    public Track(String trackName, String artistName) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackDate = LocalTime.now();
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

    //TODO: add feature to play a track
    public void PlayTrack(){}
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public LocalTime getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(LocalTime trackDate) {
        this.trackDate = trackDate;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
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
        return "[trackName: " + trackName + "]" +"[trackId: " + trackId +", [Artist Name: " + artistName + ", [trackDate: " + trackDate + "]" + ", [Likes: " +  + likes + "]";
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
}
