package team21.flashbackmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by brian on 3/12/2018.
 */

public class SortByAlbum implements Sorter {
    public void sort(ArrayList<Song> s) {
        Collections.sort(s, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                if (song.getAlbum().equalsIgnoreCase(t1.getAlbum()))
                    return song.getName().compareTo(t1.getName());
                return song.getAlbum().compareTo(t1.getAlbum());
            }
        });
    }
}
