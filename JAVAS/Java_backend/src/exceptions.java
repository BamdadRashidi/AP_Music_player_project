import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.*;
import org.junit.jupiter.api.Test;

public class exceptions {

    public static void main(String[] args) {




        //TODO: SHIT TO FIX
        //TODO: 1 make the IDS to stay the same regardless and not be randomized each time we start the program
        //TODO: 2 fix the null methods in admin for most likes and plays

        try{
            Admin admin = new Admin();
            Account acc1 = new Account("Hamed Pahlan","hamham@gmail.com","1234");
            Account acc2 = new Account("Mamad gholi", "mamad@gmail.com","abc1487");
            Track t1 = new Track("Needles","System of a Down",true);
            Track t2 = new Track("Sweden","C418",false);
            Track t3 = new Track("Sonne", "Rammstein", false);
            Track t4 = new Track("Moai", "Exyl", false);
            PlayList pl1 = new PlayList("thefirst");
            PlayList pl2 = new PlayList("thesecond");
            PlayList pl3 = new PlayList("thethird");
            PlayList pl4 = new PlayList("thefourth");

            acc2.setCanShareWith(false);

            acc1.Addplaylist(pl1);
            acc1.Addplaylist(pl2);
            acc1.addTrackToPlayList(t1,pl1);
            acc1.addTrackToPlayList(t2,pl1);
            acc1.addTrackToPlayList(t2,pl2);


            acc2.Addplaylist(pl3);
            acc2.Addplaylist(pl4);
            acc2.addTrackToPlayList(t3,pl3);
            acc2.addTrackToPlayList(t4,pl3);
            acc2.addTrackToPlayList(t1,pl3);
            for(int i = 0; i < 100; i++){
                t1.likeTrack();
                t1.likeTrack();
            }
            for(int i = 0; i < 50; i++){
                t2.likeTrack();
                t2.PlayTrack();
            }

            System.out.println(admin.findTheMostLikedTracks());
            System.out.println("----------------------------");
            System.out.println(admin.findTheMostListenedTracks());


        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.getStackTrace();
        }






        // test 4 admin tests

    }

}

class CanNotShareWithException extends Exception{
    String message;
    public CanNotShareWithException() {
        super();
    }
    public CanNotShareWithException(String message) {
        super(message);
    }
}

class AccountNotFoundException extends Exception{
    String message;
    public AccountNotFoundException() {
        super();
    }
    public AccountNotFoundException(String message) {
        super(message);
    }
}

class RedundantPlayListNameException extends Exception{
    String message;
    public RedundantPlayListNameException() {
        super();
    }
    public RedundantPlayListNameException(String message) {
        super(message);
    }
}


class WrongPasswordException extends Exception{
    String message;
    public WrongPasswordException() {
        super();
    }
    public WrongPasswordException(String message) {
        super(message);
    }
}
class WrongUserNameException extends Exception{
    String message;
    public WrongUserNameException() {
        super();
    }
    public WrongUserNameException(String message) {
        super(message);
    }
}

class RedundantUsernameException extends Exception{
    // email/phone
    String message;
    public RedundantUsernameException() {
        super();
    }
    public RedundantUsernameException(String message) {
        super(message);
    }
}

class RedundantAccountNameException extends Exception{
    // account name duuuuh!
    String message;
    public RedundantAccountNameException() {
        super();
    }
    public RedundantAccountNameException(String message) {
        super(message);
    }
}


class IdNotFoundException extends Exception{
    String message;
    public IdNotFoundException() {
        super();
    }
    public IdNotFoundException(String message) {
        super(message);
    }
}