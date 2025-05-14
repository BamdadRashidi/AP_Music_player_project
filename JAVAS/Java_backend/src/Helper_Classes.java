import java.util.*;

public class Helper_Classes {
}

interface CanShare{
    void shareTrack(Track track, Account... accounts) throws CanNotShareWithException;
    void sharePlayList(PlayList playList, Account... accounts) throws CanNotShareWithException,RedundantPlayListNameException;
}

interface TrackManager{
    void addTrack(Track track);
    void removeTrack(Track track);
}

interface infoShower{
    public String showInfo() throws IdNotFoundException;
}

class AudioSorter{
    public Set<Track> sortTracksAlphabetically(Collection<Track> allTracks) {
        Set<Track> sortedSet = new TreeSet<>(Comparator.comparing(Track::getTrackName));
        sortedSet.addAll(allTracks);
        return sortedSet;
    }

    public Set<Track> sortTracksByDate(Collection<Track> allTracks) {
        Set<Track> sortedSet = new TreeSet<>(Comparator.comparing(Track::getTrackDate));
        sortedSet.addAll(allTracks);
        return sortedSet;
    }

    public Set<Track> sortTracksByLikes(Collection<Track> allTracks) {
        Set<Track> sortedSet = new TreeSet<>(Comparator.comparing(Track::getLikes));
        sortedSet.addAll(allTracks);
        return sortedSet;
    }

    public Set<Track> sortTracksByListens(Collection<Track> allTracks) {
        Set<Track> sortedSet = new TreeSet<>(Comparator.comparing(Track::getPlays));
        sortedSet.addAll(allTracks);
        return sortedSet;
    }
}



class Id_generator{
    static String charactersToSelect = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    static Random random = new Random();

    static String generateId(){
        String id = "";
        int i = 0;
        while(i < 10){
            int index = random.nextInt(charactersToSelect.length());
            id += charactersToSelect.charAt(index);
            i++;
        }
        return id;

    }
}