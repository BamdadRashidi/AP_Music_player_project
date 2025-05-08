import java.util.ArrayList;

public class User {
    ArrayList<Account> accounts= new ArrayList<>();

    public void addAccounts(Account acc){
        accounts.add(acc);
    }
    public void removeAccount(Account acc){
        accounts.remove(acc);
    }
    public ArrayList<Account> ShowAccounts(){
        return accounts;
    }
    public Account Search(Account acc) throws AccountNotFoundException{
        if(accounts.contains(acc)){
            return acc;
        }
        else {
            throw new AccountNotFoundException("Account not found!");
        }
    }


    //TODO: make the option possible to choose an account (change accounts)
}
