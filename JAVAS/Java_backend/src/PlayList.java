import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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


}
