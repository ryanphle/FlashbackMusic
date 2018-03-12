package team21.flashbackmusic;

import android.location.Address;
import android.location.Location;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;

import java.sql.Timestamp;
import java.util.Locale;

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
    private Timestamp timeStamp;
    private Address location;
    private String ID;
    private boolean downloaded;

    public Song(String ID, String name, String artist,Uri uri, byte[] img, String album, boolean downloaded){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.uri = uri;
        this.album = album;
        this.score = 0;
        this.favorite = 0;
        this.timeStamp = new Timestamp(0);
        this.location = new Address(Locale.getDefault());
        this.ID = ID;
        this.downloaded = downloaded;
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
    public Timestamp getTimeStamp(){
        return this.timeStamp;
    }
    public Address getLocation() {
        return this.location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }
    public String getAlbum() { return this.album; }
    public int getScore() { return this.score; }
    public void setScore(int score) {this.score = score; }
    public  void setFavorite(int favorite) {
        this.favorite = favorite;
    }
    public void setTimeStamp(Timestamp timeStemp){
        this.timeStamp = timeStemp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public Uri getUri() {return this.uri;}

    public String getID() {return this.ID;}

    public boolean isDownloaded(){return this.downloaded;}



    public boolean equals(Song s) {
        return s.getName().equals(name);
    }

}
