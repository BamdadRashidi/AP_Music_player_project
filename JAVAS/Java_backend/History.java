import java.util.LinkedList;
import java.util.List;

public class History {
    private static final int Max = 10;
    private List<Track> history;

    public History() {
        history = new LinkedList<>();
    }


    public void addToHistory(Track song) {
        if (!history.contains(song)) {
            if (history.size() >= Max) {
                history.remove(0);
            }
            history.add(song);
        }
    }

    public List<Track> getLastPlayedSongs() {
        return history;
    }


    public void displayHistory() {
        if (history.isEmpty()) {
            System.out.println("There is no song");
        } else {
            System.out.println("Last Tracks");
            for (Track song :history) {
                System.out.println( song.getArtistName());
            }
        }
    }
}

