import java.util.ArrayList;

public class Account {
    private String Username;
    private String password;
    private boolean Isexist=true;
    ArrayList<PlayList>list=new ArrayList<>();

    public Account(String name,String pass){
        Username=name;
        password=pass;
    }

    public void Addplaylist(PlayList p){
        
        list.add(play);
    }
    public void Removeplaylist(PlayList p){
        list.remove(p);
    }

    public void Exist(String s){
        if(s.equals("delete")){
            Isexist=false;
        }
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isIsexist() {
        return Isexist;
    }

    public ArrayList<PlayList> getList() {
        return list;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsexist(boolean isexist) {
        Isexist = isexist;
    }

    public void setList(ArrayList<PlayList> list) {
        this.list = list;
    }
}
