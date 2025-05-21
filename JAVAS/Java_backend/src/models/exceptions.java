package models;



public class exceptions {

    public static void main(String[] args) {

        try{
            Admin admin = new Admin();
            Account acc1 = new Account("Hamed Pahlan","hamham@gmail.com","1234");
            Account acc2 = new Account("Mamad gholi", "mamad@gmail.com","abc1487");
            Track t1 = new Track("Needles","System of a Down",true);
            Track t5 = new Track("Toxicity","System of a Down",true);
            Track t2 = new Track("Sweden","C418",false);
            Track t3 = new Track("Sonne", "Rammstein", false);
            Track t4 = new Track("Moai", "Exyl", false);
            PlayList pl1 = new PlayList("thefirst");
            PlayList pl2 = new PlayList("thesecond");
            PlayList pl3 = new PlayList("thethird");
            PlayList pl4 = new PlayList("thefourth");


            acc1.Addplaylist(pl1);
            acc1.Addplaylist(pl2);
            t1.PlayTrack();
            t2.PlayTrack();
            t2.PlayTrack();
            t2.PlayTrack();
            t3.PlayTrack();
            t3.PlayTrack();
            t4.PlayTrack();
            t3.PlayTrack();

            t1.likeTrack();
            t2.likeTrack();
            t2.likeTrack();
            t2.likeTrack();
            t3.likeTrack();
            t3.likeTrack();
            t4.likeTrack();
            t3.likeTrack();
            pl1.addTrack(t1);
            pl1.addTrack(t2);
            acc1.addTrack(t1);
            acc1.addTrack(t2);
            acc1.addTrack(t3);
            acc1.addTrack(t4);
            acc1.addTrack(t5);
            System.out.println("------------");
            admin.getTrackInfo("Needles");


        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.getStackTrace();
        }


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

class NoneExistentAlbumException extends Exception{
    String message;
    public NoneExistentAlbumException() {
        super();
    }
    public NoneExistentAlbumException(String message) {
        super(message);
    }
}