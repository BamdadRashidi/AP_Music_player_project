import java.util.*;


public class Account extends AudioSorter implements CanShare,TrackManager,infoShower{
    private String Username;
    private String AccountName; // people see the account with this name
    private String password;
    private boolean doesExist = true;
    private Set<PlayList> playists = new HashSet<>();

    private String userId;

    private boolean canShareWith = true;

    Set<Track> allTracks = new HashSet<>();



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
        for(PlayList playList : playists){
            if(p.getPlaylistName().equals(playList.getPlaylistName())){
                throw new RedundantPlayListNameException("There is a playlist with the same name!");
            }
        }
        playists.add(p);
    }
    public void Removeplaylist(PlayList p){
        playists.remove(p);
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

    public Set<Track> getSongs(){
        return allTracks;
    }
    public Set<Track> getTop10Songs() {
        Set<Track> allSongs = getSongs();
        List<Track> sortedSongs = new ArrayList<>(allSongs);
        sortedSongs.sort((track1, track2) -> Integer.compare(track2.getPlays(), track1.getPlays()));
        Set<Track> top10Songs = new LinkedHashSet<>();
        int count = 0;
        for (Track track : sortedSongs) {
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

    public Set<Track> FilterByBeingLiked(){
        Set<Track>FilteredByLikes=new HashSet<>();
        Set<Track>allsongs=new HashSet<>(getSongs());
        for (Track track:allsongs){
            if(track.isLiked()){
                FilteredByLikes.add(track);
            }
        }
        return FilteredByLikes;
    }


    public void addTrackToPlayList(Track track, PlayList playList) throws NoneExistentAlbumException{
        if(playList != null){
            playList.addTrack(track);
        }
        throw new NoneExistentAlbumException("No album found");
    }
    public void removeTrackFromPlayList(Track track, PlayList playList) throws NoneExistentAlbumException{
        if(playList != null){
            playList.removeTrack(track);
        }
        throw new NoneExistentAlbumException("No album found");
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
        return playists;
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
        String toStringedAccount = "[Account Name: " + AccountName +"]" + " ,[Id: " + getUserId() + "]"+ " ,[Username: " + Username + "]" + " ,[Password: " + password + "]" + " ,[CanShareWith: " + canShareWith + "]" + '\n';
        return toStringedAccount;
    }
    public String showInfo(){
        String PlaylistNames = "";
        for(PlayList playList : playists){
            PlaylistNames += playList.toString() + '\n';
        }
        return PlaylistNames;
    }

}