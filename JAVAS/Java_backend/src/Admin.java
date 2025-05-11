import java.util.ArrayList;

public class Admin {


    //TODO: add a DB in which whenever someone makes a new Account, it adds that to the DB
    //TODO: same thing applies to what is above for playlists and tracks
    //TODO: add a method where we cycle through every song and pick the top 10 or top 5 most likedTracks and return that


    ///WIP
    static ArrayList<Account> AccountList = new ArrayList();
    static ArrayList<PlayList> PlaylistList = new ArrayList();
    static ArrayList<Track> TrackList = new ArrayList();
    static void addAccountToList(Account account) {
        AccountList.add(account);
    }
    static void addPlaylistToList(PlayList playlist) {
        PlaylistList.add(playlist);
    }
    static void addTrackToList(Track track) {
        TrackList.add(track);
    }

    /// END OF WIP

    public String findTheMostLikedTracks(){
        Track[] mostLiked = new Track[10];
        String mostLikedTracks = "";
        int maxLikes = 0;
        int i = 0;
        for(Track track : TrackList){
            if(track.getLikes() > maxLikes){
                maxLikes = track.getLikes();
                mostLiked[i++] = track;
                if(i == 9){
                    break;
                }
            }
        }
        for(Track track : mostLiked){
            mostLikedTracks += track.toString() + "\n";
        }
        return mostLikedTracks;
    }

    public void getAccountInfo(String accountId) throws IdNotFoundException {
        for(Account account : AccountList){
            if(account.getUserId().equals(accountId)){
                System.out.println(account.toString());
                return;
            }
        }
        throw new IdNotFoundException("There is no account with this id.");
    }
    public void getPlayListInfo(String playlistId) throws IdNotFoundException {
        for(PlayList playlist : PlaylistList){
            if(playlist.getPlayListID().equals(playlistId)){
                System.out.println(playlist.toString());
                return;
            }
        }
        throw new IdNotFoundException("There is no playlist with this id.");
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
