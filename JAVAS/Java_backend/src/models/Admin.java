
package models;
import models.utility.AudioSorter;
import server.DataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Admin extends AudioSorter{
    private static void getTrackinfo(String trackId) throws IdNotFoundException {
        for(Track track : DataBase.getInstance().getTracks().values()){
            if(track.getTrackId().equals(trackId)){
                System.out.println(track.toString());
                return;
            }
        }
        throw new IdNotFoundException("There is no track with this id.");
    }

    public static void getAccountInfo(String accountId, boolean shouldExpand) throws IdNotFoundException {
        for (Account account : DataBase.getInstance().getAccounts().values()) {
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
    public static void getPlayListInfo(String playlistId, boolean shouldExpand) throws IdNotFoundException {
        for (PlayList playlist : DataBase.getInstance().getPlaylists().values()){
            if (playlist.getPlayListID().equals(playlistId)) {
                System.out.println(playlist.toString());
                if (shouldExpand) {
                    System.out.println(playlist.showInfo());
                }
                return;
            }
        }
        throw new IdNotFoundException("There is no playlist with this id.");
    }


    public static void getAllAccountInfo(){
        for (Account account : DataBase.getInstance().getAccounts().values()) {
            System.out.println(account.toString());
            System.out.println(account.showInfo());
        }
    }
    public static void getAllPlaylistInfo(){
        for(PlayList playlist : DataBase.getInstance().getPlaylists().values()){
            System.out.println(playlist.toString());
            System.out.println(playlist.showInfo());
        }
    }
    public static void getAllTrackInfo(){
        for(Track track : DataBase.getInstance().getTracks().values()){
            System.out.println(track.toString());
        }
    }



    public static void main(String[] args) {
        while(true) {
            int choice = 0;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Admin Menu. please choose a number");
            System.out.println("1: checking an account (account id , more info or not (true/false))");
            System.out.println("2: checking a playlist (playlist id , more info or not (true/false))");
            System.out.println("3: checking a song");
            System.out.println("4: seeing the info of all accounts in detail");
            System.out.println("5: seeing the info of all playlists in detail");
            System.out.println("6: seeing the info of all tracks in detail");
            System.out.println("7: exit");
            choice = scanner.nextInt();
            try {
                switch (choice) {
                    case 1:
                        System.out.println("Enter account id: ");
                        String accountId = scanner.next();
                        System.out.println("expand the information: True/False write it exactly this way");
                        boolean shouldExpand = scanner.nextBoolean();
                        getAccountInfo(accountId, shouldExpand);
                        break;
                    case 2:
                        System.out.println("Enter playlist id: ");
                        String playlistID = scanner.next();
                        System.out.println("expand the information: True/False write it exactly this way");
                        boolean expandornot = scanner.nextBoolean();
                        getPlayListInfo(playlistID, expandornot);
                        break;
                    case 3:
                        System.out.println("Enter track id: ");
                        String trackID = scanner.next();
                        getTrackinfo(trackID);
                        break;
                    case 4:
                        getAllAccountInfo();
                        break;
                    case 5:
                        getAllPlaylistInfo();
                        break;
                    case 6:
                        getAllTrackInfo();
                        break;
                    case 7:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
    }


}