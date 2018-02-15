package team21.flashbackmusic;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;

import java.sql.Timestamp;

/**
 * Created by petternarvhus on 07/02/2018.
 */

public class Song implements Parcelable {
    private String name;
    private String artist;
    private String album;
    private byte[] img;
    private Uri uri;
    private int score;
    private int favorite;
    private Timestamp timeStemp;

    public Song(String name, String artist,Uri uri, byte[] img, String album){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.uri = uri;
        this.album = album;
        this.score = 0;
        this.favorite = 0;
        this.timeStemp.setTime(0);
    }

    public String getName(){
        return this.name;
    }
    public byte[] getImg(){
        return this.img;
    }
    public String getArtist(){
        return this.artist;
    }
    public int getFavorite() {
        return this.favorite;
    }
    public Timestamp getTimeStemp(){
        return this.timeStemp;
    }

    public String getAlbum() { return this.album; }
    public int getScore() { return this.score; }
    public void setScore(int score) {this.score = score; }
    public  void setFavorite(int favorite) {
        this.favorite = favorite;
    }
    public void setTimeStemp(Timestamp timeStemp){
        this.timeStemp = timeStemp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public Uri getUri(){return this.uri;}

}
