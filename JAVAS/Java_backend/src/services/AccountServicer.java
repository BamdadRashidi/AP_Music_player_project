package services;
import models.*;
import server.*;
import models.utility.*;
import java.util.ArrayList;

public class AccountServicer extends AudioSorter implements CanShare{
    private Account account;

    public AccountServicer(Account account) {
        this.account = account;
    }
    public void signIn(){

    }
    public void logIn(){

    }

    public void logOut(){

    }

    public void changeEmail(){

    }
    public void changePassword(){

    }

    public void changeUsername(){

    }

    public void downloadTrack(){

    }

    public void UploadTrack(){


    }

    public ArrayList<Track> alphabeticalSort() {
        return sortTracksAlphabetically(account.getAllTracks());
    }

    public ArrayList<Track> sortTracksByYear() {
        return AudioSorter.sortTracksByDate(account.getAllTracks());
    }

    public ArrayList<Track> sortTracksByLikes() {
        return sortTracksByLikes(account.getAllTracks());
    }

    public void shareTrack(Track track, Account... accounts){
        for(Account acc :accounts){
            if(!acc.CanShareWith()){
                return;
            }
            if(acc != null){
                acc.addTrack(track);
            }
        }
    }
    public void sharePlayList(PlayList playList, Account... accounts){
        for(Account acc :accounts){
            if(!acc.CanShareWith()){
               return;
            }
            if(acc != null){
                try {
                acc.Addplaylist(playList);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }




}
