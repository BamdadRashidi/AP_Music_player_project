import java.util.*;


public class Account extends AudioSorter implements CanShare,TrackManager,infoShower{
    private String Username;
    private String AccountName; // people see the account with this name
    private String password;
    private boolean doesExist = true;
    private List<PlayList>playists=new ArrayList<>();

    private String userId;

    private boolean canShareWith = true;
    ArrayList<PlayList> PlayListList = new ArrayList<>();
    ArrayList<Track> allTracks = new ArrayList<>();



    public Account(String accName,String name,String pass){
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

    public  Set<Track> getSongs(){
        Set<Track> allSongs = new HashSet<>();
        for (PlayList playlist : playists) {
            allSongs.addAll(playlist.getTracksList());
        }
return allSongs;
    }
    public List<Track> getTop10Songs() {
      List<Track>allSongs=new ArrayList<>(getSongs());

        allSongs.sort((track1, track2) -> Integer.compare(track2.getCounter(), track1.getCounter()));


        List<Track> top10Songs = new ArrayList<>();
        int count = 0;
        for (Track track : allTracks) {
            if (count == 10) break;
            top10Songs.add(track);
            count++;
        }

        return top10Songs;
    }

    public Set<Track> FiltredArtistName(String name){
        Set<Track> filterByName=new HashSet<>();
        Set<Track>allsongs=new HashSet<>(getSongs());
        for(Track track:allsongs){
            if(track.getArtistName().equals(name)){
                filterByName.add(track);
            }
        }

return filterByName;

    }
    public Set<Track> FiltredByDate(int year){
        Set<Track>FiltredByYear=new HashSet<>();
        Set<Track>allsongs=new HashSet<>(getSongs());
        for (Track track:allsongs){
            if(year==track.getTrackDate().getYear()){
                FiltredByYear.add(track);
            }
        }
        return FiltredByYear;
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


