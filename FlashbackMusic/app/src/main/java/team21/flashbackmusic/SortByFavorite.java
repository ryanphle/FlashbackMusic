package team21.flashbackmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jerryliu on 3/11/18.
 */

public class SortByFavorite implements Sorter {
    public void sort(ArrayList<Song> s) {
        Collections.sort(s, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                if (song.getFavorite() == t1.getFavorite())
                    return song.getName().compareTo(t1.getName());
                if (song.getFavorite() > t1.getFavorite())
                    return -1;
                return 1;
            }
        });
    }
}