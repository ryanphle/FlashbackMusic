package team21.flashbackmusic;

import android.location.Location;

/**
 * Created by jerryliu on 3/9/18.
 */

public class FBSongInfo {
    public String last_play_user;
    public long last_play_time;
    public Location last_play_location;
    public  String last_play_proxy;

    FBSongInfo() {}

    FBSongInfo(String user, long time, Location location, String proxy) {
        this.last_play_user = user;
        this.last_play_time = time;
        this.last_play_location = location;
        this.last_play_proxy = proxy;
    }

}
