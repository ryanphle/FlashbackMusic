package team21.flashbackmusic;

import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.SUNDAY;
import static org.junit.Assert.assertEquals;

/**
 * Created by petternarvhus on 18/02/2018.
 */

public class MainActivityTest {

    private List<Song> songs;
    private MockMediaPlayer mediaPlayer;

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        this.songs = mainActivity.getActivity().getSongs();
        this.mediaPlayer = new MockMediaPlayer();
    }

    @Test
    public void playSongTest(){

    }

    /*@Test
    public void storePlayInfoTest(){

        Song song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        final Location location = new Location("mockLocation");
        location.setLatitude(50);
        location.setLongitude(20);
        // 9 hours after the 1970 start
        Timestamp time = new Timestamp(100000);
        mainActivity.getActivity().storePlayInformation(song,location,time);
        Play play;
        Gson gson = new Gson();
        String json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLongitude(), 0);

        assertEquals(time.getTime() ,play.getTime().getTime());
        assertEquals("Afternoon", play.getTimeOfDay());


        song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        location.setLatitude(32.880250);
        location.setLongitude(-117.233971);
        // 9 hours after the 1970 start
        time = new Timestamp(59235000);
        mainActivity.getActivity().storePlayInformation(song,location,time);
        gson = new Gson();
        json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLongitude(), 0);

        assertEquals(time.getTime(),play.getTime().getTime());
        assertEquals("Night", play.getTimeOfDay());


        song = new Song("hello", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        location.setLatitude(38.538291);
        location.setLongitude(-121.761562);
        // 9 hours after the 1970 start
        time = new Timestamp(49235000);
        mainActivity.getActivity().storePlayInformation(song,location,time);
        gson = new Gson();
        json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLongitude(), 0);

        assertEquals(time.getTime(),play.getTime().getTime());
        assertEquals("Morning", play.getTimeOfDay());
    }

    @UiThreadTest
    @Test
    public void initialSongsFragSetupTest(){

        mainActivity.getActivity().initialFragSetup(0);
        List<Fragment> fragments = mainActivity.getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments){
            if(fragment.isVisible()){
                assertEquals("songs",fragment.getTag());
            }
        }
    }

    @Test
    public void numberOfSongsTest(){
        Field[] fields = R.raw.class.getFields();
        assertEquals(songs.size(),fields.length);
    }

    @Test
    public void sortSongsTest(){
        List<Song> songs = new ArrayList<>();

        Song song1 = new Song("song1", "artist1", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album1");
        song1.setFavorite(1);
        songs.add(song1);

        final Location location = new Location("mockLocation");
        location.setLatitude(50);
        location.setLongitude(20);

        Timestamp time = new Timestamp(100000);
        mainActivity.getActivity().storePlayInformation(song1,location,time);


        Song song2 = new Song("song2", "artist2", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album2");
        song2.setFavorite(1);
        songs.add(song2);
        location.setLatitude(30);
        location.setLongitude(20);

        time = new Timestamp(100000);
        mainActivity.getActivity().storePlayInformation(song2,location,time);


        Song song3 = new Song("song3", "artist3", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album3");
        song1.setFavorite(1);
        songs.add(song3);
        location.setLatitude(30);
        location.setLongitude(20);

        time = new Timestamp(59235000);
        mainActivity.getActivity().storePlayInformation(song3,location,time);


        Song song4 = new Song("song4", "artist4", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album4");
        song1.setFavorite(0);
        songs.add(song4);
        location.setLatitude(30);
        location.setLongitude(20);

        time = new Timestamp(59235000);
        mainActivity.getActivity().storePlayInformation(song3,location,time);


        location.setLatitude(50);
        location.setLongitude(20);
        int currentDay = SUNDAY;
        int currentHour = 22;
        mainActivity.getActivity().sort_songs(songs,"test",currentDay,currentHour,location);

        assertEquals(song1.getName(),mainActivity.getActivity().getSortedSongs().get(0).getName());
        assertEquals(song2.getName(),mainActivity.getActivity().getSortedSongs().get(1).getName());
        assertEquals(song3.getName(),mainActivity.getActivity().getSortedSongs().get(2).getName());
        assertEquals(song4.getName(),mainActivity.getActivity().getSortedSongs().get(3).getName());
    }*/
}
