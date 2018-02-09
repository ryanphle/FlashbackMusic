package team21.flashbackmusic;

import android.net.Uri;

/**
 * Created by petternarvhus on 07/02/2018.
 */

public class Song {
    String name;
    String artist;
    Uri uri;
    byte[] img;

    public Song(String name, String artist,Uri uri, byte[] img){
        this.name = name;
        this.artist = artist;
        this.img = img;
        this.uri = uri;
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
    public Uri getUri(){return this.uri;}
}
