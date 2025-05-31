package models.utility;
import models.Track;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class AudioSorter{
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