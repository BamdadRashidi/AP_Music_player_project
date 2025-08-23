
package models.utility;
import models.*;

public interface CanShare{
    void shareTrack(Track track, Account... accounts);
    void sharePlayList(PlayList playList, Account... accounts);
}