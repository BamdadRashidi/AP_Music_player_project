package models;

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
    public static ArrayList<Track> sortTracksAlphabetically(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparing(Track::getTrackName));
        return sortedList;
    }

    public static ArrayList<Track> sortTracksByDate(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparing(Track::getTrackDate));
        return sortedList;
    }

    public static ArrayList<Track> sortTracksByLikes(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparingInt(Track::getLikes).reversed());
        return sortedList;
    }

    public static ArrayList<Track> sortTracksByListens(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        Collections.sort(sortedList, Comparator.comparing(Track::getPlays).reversed());
        return sortedList;
    }

}



class Id_generator{
    static String TokenForUser;

    static String userId;
    static Random random = new Random();

    static String generateId(){
        userId = UUID.randomUUID().toString();
        return userId;

    }

    static String generateToken(){
        TokenForUser = UUID.randomUUID().toString();
        return TokenForUser;
    }
}