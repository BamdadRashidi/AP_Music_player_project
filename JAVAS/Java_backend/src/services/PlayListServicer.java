package services;
import models.*;
import models.utility.AudioSorter;
import server.*;

import java.util.ArrayList;

public class PlayListServicer extends AudioSorter {
    private PlayList playlist;
    public PlayListServicer(PlayList playlist) {
        this.playlist = playlist;
    }
    public ArrayList<Track> alphabeticalSort(){
        return sortTracksAlphabetically(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByYear(){
        return sortTracksByDate(playlist.getTracksList());
    }

    public ArrayList<Track> sortTracksByLikes(){
        return sortTracksByLikes(playlist.getTracksList());
    }
}
