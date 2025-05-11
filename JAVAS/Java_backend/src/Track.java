import java.io.Serializable;

public class Track implements Serializable , Comparable<Track> {
    private String trackName;
    private String trackId;
    private Integer trackDate;
    private boolean isLiked;
    private Integer likes = 0;

    public Track(String trackName, Integer trackDate) {
        this.trackName = trackName;
        this.trackDate = trackDate;
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

    public Integer getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(Integer trackDate) {
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

    @Override
    public String toString() {
        return "[trackName: " + trackName + "]" +"[trackId: " + trackId +", [trackDate: " + trackDate + "]" + ", [Likes: " +  + likes + "]";
    }
}
