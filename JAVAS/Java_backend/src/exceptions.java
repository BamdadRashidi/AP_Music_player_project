import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.*;
import org.junit.jupiter.api.Test;

public class exceptions {

    public static void main(String[] args) {

            Account acc1 = new Account("MamadGholi","099059099083","mamad83983");
            Track t1 = new Track("Sonne","Rammstein");
            Track t2 = new Track("Toxicity","System of a Down");
            Track t3 = new Track("Sweden","C418");
            PlayList pl1 = new PlayList("German");
            PlayList pl2 = new PlayList("Armenia");

            try
            {
                acc1.Addplaylist(pl1);
                acc1.Addplaylist(pl2);
                acc1.addTrackToPlayList(t1,pl1);
                acc1.addTrackToPlayList(t2,pl2);
                acc1.addTrackToAnotherPlaylist(t1,pl2);

            }
            catch(Exception e){
                System.out.println(e.getMessage());
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