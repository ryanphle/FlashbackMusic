package team21.flashbackmusic;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;

/**
 * Created by petternarvhus on 07/02/2018.
 */

public class Song implements Parcelable {
    String name;
    String artist;
    String album;
    byte[] img;
    Uri uri;
    int favorite;


    public Song(String name, String artist,Uri uri, byte[] img, String album){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.uri = uri;
        this.album = album;
        this.favorite = 0;
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

    public String getAlbum() { return this.album; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public Uri getUri(){return this.uri;}

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int state) {
        favorite = state;
    }

    public boolean equals(Song s) {
        return s.getName().equals(name);
    }

}
