public class exceptions {
    /// USE THIS TO TEST THE PROGRAM AS A PROTOTYPE
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
