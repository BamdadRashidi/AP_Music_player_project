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
    public ArrayList<Track> sortTracksAlphabetically(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparing(Track::getTrackName));
        return sortedList;
    }

    public ArrayList<Track> sortTracksByDate(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparing(Track::getTrackDate));
        return sortedList;
    }

    public ArrayList<Track> sortTracksByLikes(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparingInt(Track::getLikes).reversed());
        return sortedList;
    }

    public ArrayList<Track> sortTracksByListens(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        Collections.sort(sortedList, Comparator.comparing(Track::getPlays).reversed());
        return sortedList;
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