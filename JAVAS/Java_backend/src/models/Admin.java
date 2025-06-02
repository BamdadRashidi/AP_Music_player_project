//package models;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//public class Admin{
//
//    //TODO: FIX THE ADMIN CLASS I REPEAT FIX THE ADMIN CLASS
//    //TODO: add a DB in which whenever someone makes a new Account, it adds that to the DB
//    //TODO: same thing applies to what is above for playlists and tracks
//
//
//    ///WIP
//    static Map<Account,String> AccountList = new HashMap<Account,String>();
//    static Map<PlayList,String> PlaylistList = new HashMap<PlayList,String>();
//    static ArrayList<Track> TrackList = new ArrayList();
//    static void addAccountToList(Account account) {
//        AccountList.put(account,account.getUserId());
//    }
//    static void addPlaylistToList(PlayList playlist) {
//        PlaylistList.put(playlist, playlist.getPlayListID());
//    }
//    static void addTrackToList(Track track) {
//        TrackList.add(track);
//    }
//
//    /// END OF WIP
//
//    public static String findTheMostLikedTracks(){
//        ArrayList<Track> sorted = sortTracksByLikes(TrackList);
//        StringBuilder mostLikedTracks = new StringBuilder();
//        int count = Math.min(10, sorted.size());
//        for (int i = 0; i < count; i++) {
//            mostLikedTracks.append(sorted.get(i).toString()).append("\n");
//        }
//
//        return mostLikedTracks.toString();
//    }
//    public static String findTheMostListenedTracks(){
////        ArrayList<Track> sorted = sortTracksByListens(TrackList);
//        StringBuilder mostListened = new StringBuilder();
//
//        int count = Math.min(10, sorted.size());
//        for (int i = 0; i < count; i++) {
//            mostListened.append(sorted.get(i).toString()).append("\n");
//        }
//
//        return mostListened.toString();
//    }
//
//    public static void getAccountInfo(String accountId, boolean shouldExpand) throws IdNotFoundException {
//        for (Account account : AccountList.keySet()) {
//            if (account.getUserId().equals(accountId)) {
//                System.out.println(account.toString());
//                if (shouldExpand) {
//                    System.out.println(account.showInfo());
//                }
//                return;
//            }
//        }
//        throw new IdNotFoundException("There is no account with this id.");
//    }
//    public static void getPlayListInfo(String playlistId, boolean shouldExpand) throws IdNotFoundException {
//        for (PlayList playlist : PlaylistList.keySet()){
//            if (playlist.getPlayListID().equals(playlistId)) {
//                System.out.println(playlist.toString());
//                if (shouldExpand) {
//                    System.out.println(playlist.showInfo());
//                }
//                return;
//            }
//        }
//        throw new IdNotFoundException("There is no playlist with this id."); //TODO: make it so that if it didn't find an ID in the ID DB, it throws this same for others
//    }
//
//
//    public void getTrackInfo(String Trackname) throws IdNotFoundException {
//        for(Track track : TrackList){
//            if(track.getTrackName().equals(Trackname)){
//                System.out.println(track.toString());
//                return;
//            }
//        }
//        throw new IdNotFoundException("There is no track with this id.");
//    }
//
//
//    public static void main(String[] args) {
//        while(true) {
//            int choice = 0;
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Welcome to the Admin Menu. please choose a number");
//            System.out.println("1: checking an account (account id , more info or not (true/false))");
//            System.out.println("2: checking a playlist (playlist id , more info or not (true/false))");
//            System.out.println("3: find the top 10 most liked tracks");
//            System.out.println("4: find the top 10 most played tracks");
//            System.out.println("5: exit");
//            choice = scanner.nextInt();
//            try {
//                switch (choice) {
//                    case 1:
//                        String accountId = scanner.next();
//                        boolean shouldExpand = scanner.nextBoolean();
//                        getAccountInfo(accountId, shouldExpand);
//                        break;
//                    case 2:
//                        String playlistID = scanner.next();
//                        boolean expandornot = scanner.nextBoolean();
//                        getPlayListInfo(playlistID, expandornot);
//                        break;
//                    case 3:
//                        System.out.println(findTheMostLikedTracks());
//                        break;
//                    case 4:
//                        System.out.println(findTheMostListenedTracks());
//                        break;
//                    case 5:
//                        System.exit(0);
//                        break;
//                    default:
//                        System.out.println("Invalid choice.");
//                }
//
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                scanner.nextLine();
//            }
//        }
//    }
//}
