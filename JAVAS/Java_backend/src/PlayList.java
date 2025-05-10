import java.util.*;

public class PlayList extends AudioSorter implements TrackManager{

    ArrayList<Track> TracksList = new ArrayList<>();
    private String playlistName;

    public PlayList(String playlistName) {
        this.playlistName = playlistName;
    }

    public void addTrack(Track track) {
        if(track != null) {
            TracksList.add(track);
        }
    }
    public void addTrackToAnotherPlaylist(Track track,PlayList... playLists) {
        for (PlayList playList : playLists) {
            if(track != null && playList != null) {
                playList.addTrack(track);
            }
        }
    }

    public void removeTrack(Track track) {
        if(track != null && TracksList.contains(track)) {
            TracksList.remove(track);
        }
    }

    public ArrayList<Track> alphabeticalSort(){
        return sortTracksAlphabetically(TracksList);
    }

    public ArrayList<Track> sortTracksByYear(){
        return sortTracksByDate(TracksList);
    }

    public ArrayList<Track> sortTracksByLikes(){
        return sortTracksByLikes(TracksList);
    }


    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

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
        return "PlayList Name: " + playlistName;
        
    }
}
