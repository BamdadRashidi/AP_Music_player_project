import java.time.LocalTime;
import java.util.*;

public class PlayList extends AudioSorter implements TrackManager,infoShower{

    Set<Track> TracksList = new HashSet<>();
    private String playlistName;
    private String playListID;

    private String playListDate;

    private int songCount = 0;

    //TODO: use the two variables below
    private LocalTime playlistTime;
    private String playListTimeStringed;

    //TODO: add a system which sums the length of the tracks and adds them to the playListTime;
    public PlayList(String playlistName) {
        this.playlistName = playlistName;
        playListID = Id_generator.generateId();
        Admin.addPlaylistToList(this);
    }

    public void addTrack(Track track) {
        if(track != null) {
            TracksList.add(track);
            songCount++;
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
            songCount--;
        }
    }

    public Set<Track> alphabeticalSort(){
        return sortTracksAlphabetically(TracksList);
    }

    public Set<Track> sortTracksByYear(){
        return sortTracksByDate(TracksList);
    }

    public Set<Track> sortTracksByLikes(){
        return sortTracksByLikes(TracksList);
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

    public String getPlayListTimeStringed() {
        return playListTimeStringed;
    }

    public int getSongCount() {
        return songCount;
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
        String toStringedPlaylist = "PlayList Name: " + playlistName + " PlayList ID: "+ getPlayListID() + " Tracks: "+ getSongCount() + '\n';
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
