import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Admin extends AudioSorter{

    //TODO: add a DB in which whenever someone makes a new Account, it adds that to the DB
    //TODO: same thing applies to what is above for playlists and tracks


    ///WIP
    static Map<Account,String> AccountList = new HashMap<Account,String>();
    static Map<PlayList,String> PlaylistList = new HashMap<PlayList,String>();
    static ArrayList<Track> TrackList = new ArrayList();
    static void addAccountToList(Account account) {
        AccountList.put(account,account.getUserId());
    }
    static void addPlaylistToList(PlayList playlist) {
        PlaylistList.put(playlist, playlist.getPlayListID());
    }
    static void addTrackToList(Track track) {
        TrackList.add(track);
    }

    /// END OF WIP

    public String findTheMostLikedTracks(){
        ArrayList<Track> sorted = sortTracksByLikes(TrackList);
        StringBuilder mostLikedTracks = new StringBuilder();

        int count = Math.min(10, sorted.size());
        for (int i = 0; i < count; i++) {
            mostLikedTracks.append(sorted.get(i).toString()).append("\n");
        }

        return mostLikedTracks.toString();
    }
    public String findTheMostListenedTracks(){
        ArrayList<Track> sorted = sortTracksByListens(TrackList);
        StringBuilder mostListened = new StringBuilder();

        int count = Math.min(10, sorted.size());
        for (int i = 0; i < count; i++) {
            mostListened.append(sorted.get(i).toString()).append("\n");
        }

        return mostListened.toString();
    }

    public void getAccountInfo(String accountId, boolean shouldExpand) throws IdNotFoundException {
        for (Account account : AccountList.keySet()) {
            if (account.getUserId().equals(accountId)) {
                System.out.println(account.toString());
                if (shouldExpand) {
                    System.out.println(account.showInfo());
                }
                return;
            }
        }
        throw new IdNotFoundException("There is no account with this id.");
    }
    public void getPlayListInfo(String playlistId, boolean shouldExpand) throws IdNotFoundException {
        for (PlayList playlist : PlaylistList.keySet()){
            if (playlist.getPlaylistName().equals(playlistId)) {
                System.out.println(playlist.toString());
                if (shouldExpand) {
                    System.out.println(playlist.showInfo());
                }
                return;
            }
        }
        throw new IdNotFoundException("There is no playlist with this id."); //TODO: make it so that if it didn't find an ID in the ID DB, it throws this same for others
    }


    public void getTrackInfo(String Trackname) throws IdNotFoundException {
        for(Track track : TrackList){
            if(track.getTrackName().equals(Trackname)){
                System.out.println(track.toString());
                return;
            }
        }
        throw new IdNotFoundException("There is no track with this id.");
    }


    public static void main(String[] args) {

    }
}
