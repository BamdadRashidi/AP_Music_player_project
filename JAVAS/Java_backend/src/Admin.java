import java.util.ArrayList;

public class Admin {


    //TODO: add a DB in which whenever someone makes a new Account, it adds that to the DB
    //TODO: same thing applies to what is above for playlists and tracks
    //TODO: add a method where we cycle through every song and pick the top 10 or top 5 most likedTracks and return that


    ///WIP
    static ArrayList<Account> AccountList = new ArrayList();
    static void addAccountToList(Account account) {
        AccountList.add(account);
    }
    /// END OF WIP

    public Track[] findTheMostLikedTracks(){
        Track[] mostLiked = new Track[10];
        int maxLikes = 0;

        //TODO: during cycling we compare the likes with maxlikes and find the most liked tracks and add them to the array;

        return mostLiked;
    }



    public static void main(String[] args) {

    }
}
