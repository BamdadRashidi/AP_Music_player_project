import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class PlayList {

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
        if(track != null) {
            TracksList.remove(track);
        }
    }

    public ArrayList<Track> sortTracksAlphabetically() {
        Collections.sort(TracksList);
        return TracksList;
    }

    public ArrayList<Track> sortTracksByDate(){
        Comparator<Track> trackComparator = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getTrackDate().compareTo(o2.getTrackDate());
            }
        };
            Collections.sort(TracksList, trackComparator);
        return TracksList;
    }

    public ArrayList<Track> sortTracksByLikes(){
        Comparator<Track> likesComp = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getLikes().compareTo(o2.getLikes());
            }
        };
        Collections.sort(TracksList, likesComp);
        return TracksList;
    }


}
