package team21.flashbackmusic;

/**
 * Created by samliu on 2/7/18.
 */

public class Song {
    String name;
    String artist;
    byte[] img;

    public Song(String name, String artist, byte[] img){
        this.name = name;
        this.artist = artist;
        this.img = img;
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
}
