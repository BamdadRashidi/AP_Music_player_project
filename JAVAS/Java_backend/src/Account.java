import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Account {
    private String Username;
    private String password;
    private boolean doesExist = true;

    private boolean canShareWith = true;
    ArrayList<PlayList> PlayListList = new ArrayList<>();
    ArrayList<Track> allTracks = new ArrayList<>();

    public Account(String name,String pass){
        Username=name;
        password=pass;
    }

    public void shareTrack(Track track, Account... accounts) throws CanNotShareWithException {
        for(Account acc :accounts){
            if(acc != null && acc.canShareWith){
                acc.addTrack(track);
            }
        }
    }
    public void sharePlayList(PlayList playList, Account... accounts) throws CanNotShareWithException {
        for(Account acc :accounts){
            if(acc != null && acc.canShareWith){
                acc.Addplaylist(playList);
            }
        }
    }

    public void Addplaylist(PlayList p){
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

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return password;
    }

    public boolean doesExist() {
        return doesExist;
    }

    public ArrayList<PlayList> getPlayListList() {
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

    public void setPlayListList(ArrayList<PlayList> playListList) {
        this.PlayListList = playListList;
    }

    public boolean isCanShareWith() {
        return canShareWith;
    }

    public void setCanShareWith(boolean canShareWith) {
        this.canShareWith = canShareWith;
    }

    /// EVERYTHING RELATED TO ALL TRACKS MANAGEMENT
    //TODO: need to eventually remove these and use polymorphism for playlist and track it's more readable and gives more points
    public ArrayList<Track> sortTracksAlphabetically() {
        Collections.sort(allTracks);
        return allTracks;
    }

    public ArrayList<Track> sortTracksByDate(){
        Comparator<Track> trackComparator = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getTrackDate().compareTo(o2.getTrackDate());
            }
        };
        Collections.sort(allTracks, trackComparator);
        return allTracks;
    }

    public ArrayList<Track> sortTracksByLikes(){
        Comparator<Track> likesComp = new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getLikes().compareTo(o2.getLikes());
            }
        };
        Collections.sort(allTracks, likesComp);
        return allTracks;
    }
}


