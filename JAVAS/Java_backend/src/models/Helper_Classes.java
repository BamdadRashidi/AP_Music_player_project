package models;

import java.util.*;

public class Helper_Classes {
}



interface TrackManager{
    void addTrack(Track track);
    void removeTrack(Track track);
}

interface infoShower{
    public String showInfo() throws IdNotFoundException;
}


class Id_generator{
    static String TokenForUser;

    static String userId;
    static Random random = new Random();

    static String generateId(){
        userId = UUID.randomUUID().toString();
        return userId;

    }

    static String generateToken(){
        TokenForUser = UUID.randomUUID().toString();
        return TokenForUser;
    }
}