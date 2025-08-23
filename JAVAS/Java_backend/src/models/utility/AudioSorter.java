package models.utility;

import models.Track;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class AudioSorter {

    public static ArrayList<Track> sortTracksAlphabetically(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparing(Track::getTrackName, String.CASE_INSENSITIVE_ORDER));
        return sortedList;
    }

    public static ArrayList<Track> sortTracksByDate(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        // مرتب سازی بر اساس تاریخ کامل (سال، ماه، روز)
        sortedList.sort(Comparator.comparing(Track::getFullTrackDate));
        return sortedList;
    }

    public static ArrayList<Track> sortTracksByLikes(Collection<Track> allTracks) {
        ArrayList<Track> sortedList = new ArrayList<>(allTracks);
        sortedList.sort(Comparator.comparingInt(Track::getLikes).reversed());
        return sortedList;
    }

}
