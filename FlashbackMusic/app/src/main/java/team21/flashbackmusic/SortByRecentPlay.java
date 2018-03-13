package team21.flashbackmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jerryliu on 3/11/18.
 */

public class SortByRecentPlay implements Sorter {
    public void sort(ArrayList<Song> s) {
        Collections.sort(s, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return -1;
            }
        });
    }
}