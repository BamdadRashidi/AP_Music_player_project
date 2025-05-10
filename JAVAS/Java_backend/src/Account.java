import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Account extends AudioSorter implements CanShare,TrackManager{
    private String Username;
    private String AccountName; // people see the account with this name
    private String password;
    private boolean doesExist = true;

    private boolean canShareWith = true;
    ArrayList<PlayList> PlayListList = new ArrayList<>();
    ArrayList<Track> allTracks = new ArrayList<>();

    public Account(String accName,String name,String pass){
        AccountName = accName;
        Username = name;
        password = pass;
    }

    public void shareTrack(Track track, Account... accounts) throws CanNotShareWithException {
        for(Account acc :accounts){
            if(!acc.canShareWith){
                throw new CanNotShareWithException("Can not share Track with this user");
            }
            if(acc != null){
                acc.addTrack(track);
            }
        }
    }
    public void sharePlayList(PlayList playList, Account... accounts) throws CanNotShareWithException,
                                                                            RedundantPlayListNameException{
        for(Account acc :accounts){
            if(!acc.canShareWith){
                throw new CanNotShareWithException("Can not share Playlist with this user");
            }
            if(acc != null){
                acc.Addplaylist(playList);
            }
        }
    }

    public void Addplaylist(PlayList p) throws RedundantPlayListNameException{
        for(PlayList playList : PlayListList){
            if(p.getPlaylistName().equals(playList.getPlaylistName())){
                throw new RedundantPlayListNameException("There is a playlist with the same name!");
            }
        }
        PlayListList.add(p);
    }
    public void Removeplaylist(PlayList p){
        PlayListList.remove(p);
    }
    public void addTrack(Track t){
        allTracks.add(t);
    }
    public void removeTrack(Track t){
        allTracks.remove(t);
    }
    public void Exist(String s){
        if(s.equals("delete")){
            doesExist = false;
        }
    }


    //TODO: add downloading and uploading feature
    public void downloadTrack(){}

    public void UploadTrack(){}

    //------------------------------------------


    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return password;
    }

    public boolean doesExist() {
        return doesExist;
    }

    public ArrayList<PlayList> getPlayLists() {
        return PlayListList;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDoesExist(boolean doesExist) {
        this.doesExist = doesExist;
    }



    public boolean CanShareWith() {
        return canShareWith;
    }

    public void setCanShareWith(boolean canShareWith) {
        this.canShareWith = canShareWith;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    /// EVERYTHING RELATED TO ALL TRACKS MANAGEMENT

    public ArrayList<Track> alphabeticalSort(){
        return sortTracksAlphabetically(allTracks);
    }

    public ArrayList<Track> sortTracksByYear(){
        return sortTracksByDate(allTracks);
    }

    public ArrayList<Track> sortTracksByLikes(){
        return sortTracksByLikes(allTracks);
    }

    @Override
    public String toString(){
        String toStringedAccount = "[Account Name: " + AccountName +"]" + " ,[Username: " + Username + "]" + " ,[Password: " + password + "]" + " ,[CanShareWith: " + canShareWith + "]" + '\n';
        for(PlayList playList : PlayListList){
            toStringedAccount += playList.toString() + '\n';
        }
        return toStringedAccount;
    }

}


