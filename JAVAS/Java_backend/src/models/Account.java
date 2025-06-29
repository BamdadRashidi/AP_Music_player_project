package models;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class Account implements TrackManager,infoShower{
    @SerializedName("username")
    private String Username;
    @SerializedName("password")
    private String password;
    @SerializedName("accountName")
    private String AccountName; // people see the account with this name

    @SerializedName("userToken")
    private final String UserToken;
    @SerializedName("userId")
    private final String userId;

    private static History trackHistory;

    private boolean canShareWith = true;
    Set<PlayList> PlayListList = new HashSet<PlayList>();
    static Set<Track> allTracks = new HashSet<>();



    public Account(String name,String pass,String accName){
        Username = name;
        password = pass;
        AccountName = accName;
        userId = Id_generator.generateId();
        UserToken = Id_generator.generateToken();
        trackHistory = new History();
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
        trackHistory.addToHistory(t);
        allTracks.add(t);
    }
    public void removeTrack(Track t){
        allTracks.remove(t);
    }
    public void addTrackToPlayList(Track track,PlayList playList) {
        if(track != null) {
            playList.addTrack(track);
        }
    }

    public void removeTrackFromPlayList(Track track,PlayList playList) {
        if(track != null && playList.getTracksList().contains(track)) {
            playList.removeTrack(track);
        }
    }
    @SerializedName("ownedTracks")
    private final Set<String> ownedTrackIds = new HashSet<>();

    public void addOwnedTrack(String trackId) {
        ownedTrackIds.add(trackId);
    }

    public Set<String> getOwnedTrackIds() {
        return ownedTrackIds;
    }


    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
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


    public String getUserToken() {
        return UserToken;
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

    public static Set<Track> getAllTracks() {
        return allTracks;
    }

    /// EVERYTHING RELATED TO ALL TRACKS MANAGEMENT




    @Override
    public String toString(){
        return "[Account Name: " + AccountName +"]" + " ,[Id: " + getUserId() + " ,[Username: " + Username + "]" + " ,[Password: " + password + "]" + " ,[CanShareWith: " + canShareWith + "]" + '\n';
    }
    public String showInfo(){
        String PlaylistNames = "";
        for(PlayList playList : PlayListList){
            PlaylistNames += playList.toString() + '\n';
        }
        return PlaylistNames;
    }

}


