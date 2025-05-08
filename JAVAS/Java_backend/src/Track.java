public class Track {
    private String trackName;
    private String trackDate;
    private boolean isLiked;
    private int likes = 0;

    public Track(String trackName, String trackDate) {
        this.trackName = trackName;
        this.trackDate = trackDate;
    }
    public void likeTrack(){
        isLiked = true;
        likes++;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(String trackDate) {
        this.trackDate = trackDate;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
