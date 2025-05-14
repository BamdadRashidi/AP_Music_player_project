import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.*;
import org.junit.jupiter.api.Test;

public class exceptions {

    public static void main(String[] args) {

        //test 1 making accounts, tracks, playlists etc...

        //test 2 sharing playlists

        // test 3 adding playlists, tracks and to each other etc...

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