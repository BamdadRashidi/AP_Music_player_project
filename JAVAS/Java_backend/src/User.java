import java.util.ArrayList;

public class User {
    ArrayList<Account> accounts= new ArrayList<>();

    public void addAccounts(Account a){
        accounts.add(a);
    }
    public void removeAccount(Account a){
        accounts.remove(a);
    }
    public ArrayList<Account> ShowAccounts(){
        return accounts;
    }
    public Account Search(Account a) throws
            Exception{
        if(accounts.contains(a)){
            return a;
        }
        else {
            throw new Exception("Nothing");
        }
    }

}
