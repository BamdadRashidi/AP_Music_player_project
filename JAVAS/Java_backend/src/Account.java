import java.util.*;


public class Account extends AudioSorter implements CanShare,TrackManager,infoShower{
    private String Username;
    private String AccountName; // people see the account with this name
    private String password;
    private boolean doesExist = true;

    private String userId;

    private History trackHistory;

    private boolean canShareWith = true;
    Set<PlayList> PlayListList = new HashSet<PlayList>();
    Set<Track> allTracks = new HashSet<>();



    public Account(String accName,String name,String pass){
        trackHistory = new History();
        AccountName = accName;
        Username = name;
        password = pass;
        userId = Id_generator.generateId();
        /// WIP THING TO TEST ADMIN
        Admin.addAccountToList(this);
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

    // temp upload
    public void addTrack(Track t){
        trackHistory.addToHistory(t);
        allTracks.add(t);

    }
    public void removeTrack(Track t){
        allTracks.remove(t);
    }
    public void addTrackToPlayList(Track track,PlayList playList) {
        if(track != null) {
            playList.getTracksList().add(track);
            playList.setPlaylistTime(playList.getPlaylistTime().plus(track.getTrackLength()));
            playList.setPlayListTimeStringed(String.format("%02d:%02d",
                    playList.getPlaylistTime().toMinutes(), playList.getPlaylistTime()));

            playList.setSongCount(playList.getSongCount() + 1);
        }
    }
    public void addTrackToAnotherPlaylist(Track track,PlayList... playLists) {
        for (PlayList playList : playLists) {
            if(track != null && playList != null) {
                playList.addTrack(track);
            }
        }
    }

    public void removeTrackFromPlayList(Track track,PlayList playList) {
        if(track != null && playList.getTracksList().contains(track)) {
            playList.getTracksList().remove(track);
            playList.setSongCount(playList.getSongCount() - 1);
        }
    }
    public void Exist(String s){
        if(s.equals("delete")){
            doesExist = false;
        }
    }

    //TODO: signing 'in' and logging 'in' and their 'out' counterparts

    public void signIn() throws WrongPasswordException,WrongUserNameException
                                ,RedundantUsernameException,RedundantAccountNameException{

    }
    public void logIn() throws WrongPasswordException,WrongUserNameException
                                ,RedundantUsernameException,RedundantAccountNameException{

    }
    public void logOut(){

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

    public String getUserId() {
        return userId;
    }

    public boolean doesExist() {
        return doesExist;
    }

    public Set<PlayList> getPlayLists() {
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

    public Set<Track> alphabeticalSort() {
        return sortTracksAlphabetically(allTracks);
    }

    public Set<Track> sortTracksByYear() {
        return sortTracksByDate(allTracks);
    }

    public Set<Track> sortTracksByLikes() {
        return sortTracksByLikes(allTracks);
    }


    @Override
    public String toString(){
        String toStringedAccount = "[Account Name: " + AccountName +"]" + " ,[Id: " + getUserId() + " ,[Username: " + Username + "]" + " ,[Password: " + password + "]" + " ,[CanShareWith: " + canShareWith + "]" + '\n';
        return toStringedAccount;
    }
    public String showInfo(){
        String PlaylistNames = "";
        for(PlayList playList : PlayListList){
            PlaylistNames += playList.toString() + '\n';
        }
        return PlaylistNames;
    }

}


