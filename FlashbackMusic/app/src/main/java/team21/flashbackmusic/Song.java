package team21.flashbackmusic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by petternarvhus on 07/02/2018.
 */

public class Song implements Parcelable {
    String name;
    String artist;
    String album;
    byte[] img;

    public Song(String name, String artist, byte[] img, String album){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.album = album;
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
}
