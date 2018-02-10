package team21.flashbackmusic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petternarvhus on 07/02/2018.
 */

public class Album implements Parcelable {

    String name;
    String artist;
    List<Song> songs;
    byte[] img;

    public Album(String name, String artist, byte[] img){
        this.name = name;
        this.artist = artist;
        this.img = img;
        songs = new ArrayList<>();
    }

    public void addSong(Song song){
        songs.add(song);
    }
    public String getName(){
        return this.name;
    }

    public int getNumberOfSongs(){
        return songs.size();
    }
    public String getArtist(){
        return this.artist;
    }
    public byte[] getImg(){
        return this.img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public List<Song> getSongs(){return songs;}

}
