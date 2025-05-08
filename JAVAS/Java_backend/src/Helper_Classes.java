import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Helper_Classes {
}

interface CanShare{
    void shareTrack(Track track, Account... accounts) throws CanNotShareWithException;
    void sharePlayList(PlayList playList, Account... accounts) throws CanNotShareWithException;
}

interface TrackManager{
    void addTrack(Track track);
    void removeTrack(Track track);
}

class AudioSorter{
    public ArrayList<Track> sortTracksAlphabetically(ArrayList<Track> allTracks) {
        Collections.sort(allTracks);
        return allTracks;
    }

    public ArrayList<Track> sortTracksByDate(ArrayList<Track> allTracks){
        Comparator<Track> trackComparator = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getTrackDate().compareTo(o2.getTrackDate());
            }
        };
        Collections.sort(allTracks, trackComparator);
        return allTracks;
    }

    public ArrayList<Track> sortTracksByLikes(ArrayList<Track> allTracks){
        Comparator<Track> likesComp = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getLikes().compareTo(o2.getLikes());
            }
        };
        Collections.sort(allTracks, likesComp);
        return allTracks;
    }
}