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


    public Song(String name, String artist,Uri uri, byte[] img, String album){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.uri = uri;
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

    public Uri getUri(){return this.uri;}

}
