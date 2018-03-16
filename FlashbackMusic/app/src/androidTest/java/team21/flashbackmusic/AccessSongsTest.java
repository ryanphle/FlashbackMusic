package team21.flashbackmusic;

import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by petternarvhus on 09/02/2018.
 */

public class AccessSongsTest {

    private List<Song> songs;

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        this.songs = mainActivity.getActivity().getSongs();
    }

    @Test
    public void numberOfSongsTest(){
        Field[] fields = R.raw.class.getFields();
        assertEquals(songs.size(),fields.length);
    }

    public void songTest(){
        /*Song song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        assertEquals(song.getName(),"hey");
        assertEquals(song.getArtist(),"hello");
        assertEquals(song.getUri(),Uri.parse("android.resource://team21.flashbackmusic/2131558400"));
        assertEquals(song.getImg(),"byte".getBytes());*/
    }

    public void albumTest(){
        /*Song song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(),"album");
        List<Song> albumSongs= new ArrayList<Song>();
        albumSongs.add(song);
        Album album = new Album("album","hello","byte".getBytes());
        album.addSong(song);
        assertEquals(album.getNumberOfSongs(),1);
        assertEquals(album.getName(),"album");
        assertEquals(album.getArtist(),"hello");
        assertEquals(album.getImg(),"byte".getBytes());
        assertEquals(album.getSongs(),albumSongs);*/

    }


}
