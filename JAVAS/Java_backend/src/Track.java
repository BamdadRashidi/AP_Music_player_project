import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.*;

public class Track implements Serializable , Comparable<Track> {
    private String trackName;
    private String trackId;
    private String artistName;
    private LocalDate trackDate;
    private String trackDateStringed;

    //TODO: using the variables below
    private Duration trackLength;
    private String trackLengthString;
    private boolean isLiked;
    private Integer likes = 0;

    private int numberOfListens;

    //TODO: IMPLEMENT THE SYSTEM THAT TAKES THE LENGTH OF A TRACK
    public Track(String trackName, String artistName) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        trackDateStringed = trackDate.format(formatter);
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
    public void PlayTrack(){
        numberOfListens++;
    }
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public LocalDate  getTrackDate() {
        return trackDate;
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
        if(likes <= 0){
            this.likes = 0;
        }
        this.likes = likes;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Duration getTrackLength() {
        return trackLength;
    }

    public String getTrackDateStringed() {
        return trackDateStringed;
    }

    public void setTrackDateStringed(String trackDateStringed) {
        this.trackDateStringed = trackDateStringed;
    }

    public void setTrackLength(Duration trackLength) {
        this.trackLength = trackLength;
        trackDateStringed = String.format("%02d:%02d",
                trackLength.toMinutes(), trackLength);
    }

    public int getNumberOfListens() {
        return numberOfListens;
    }

    @Override
    public String toString() {
        return "[trackName: " + trackName + "]" +", [trackId: " + trackId + ", Plays: " + getNumberOfListens() +", [Artist Name: " + artistName + ", [trackDate: " + trackDateStringed + "]" + ", [Likes: " +  + likes + "]";
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
