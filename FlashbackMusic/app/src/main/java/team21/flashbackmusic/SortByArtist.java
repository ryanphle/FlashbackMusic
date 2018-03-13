package team21.flashbackmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jerryliu on 3/11/18.
 */

public class SortByArtist implements Sorter{

    public void sort(ArrayList<Song> s) {
        Collections.sort(s, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                if (song.getArtist().equalsIgnoreCase(t1.getArtist()))
                    return song.getName().compareTo(t1.getName());
                return song.getArtist().compareTo(t1.getArtist());
            }
        });
    }
}
