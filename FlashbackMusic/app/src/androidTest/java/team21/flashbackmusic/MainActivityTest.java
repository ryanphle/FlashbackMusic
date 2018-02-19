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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
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

    @Test
    public void storePlayInfo(){

        Song song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        final Location location = new Location("mockLocation");
        location.setLatitude(50);
        location.setLongitude(20);
        // 9 hours after the 1970 start
        Timestamp time = new Timestamp(100000);
        mainActivity.getActivity().storePlayInformation(song,location,"test",time);
        Play play;
        Gson gson = new Gson();
        String json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLocation().getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLocation().getLongitude(), 0);

        assertEquals(time.getTime() ,play.getTime().getTime());
        assertEquals("Night", play.getTimeOfDay());


        song = new Song("hey", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        location.setLatitude(32.880250);
        location.setLongitude(-117.233971);
        // 9 hours after the 1970 start
        time = new Timestamp(59235000);
        mainActivity.getActivity().storePlayInformation(song,location,"test",time);
        gson = new Gson();
        json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLocation().getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLocation().getLongitude(), 0);

        assertEquals(time.getTime(),play.getTime().getTime());
        assertEquals("Morning", play.getTimeOfDay());


        song = new Song("hello", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        location.setLatitude(38.538291);
        location.setLongitude(-121.761562);
        // 9 hours after the 1970 start
        time = new Timestamp(49235000);
        mainActivity.getActivity().storePlayInformation(song,location,"test",time);
        gson = new Gson();
        json = mainActivity.getActivity().getSharedPreferences("test", MODE_PRIVATE).getString(song.getName(), "");
        play = gson.fromJson(json,Play.class);

        assertEquals(location.getLatitude(),play.getLocation().getLatitude(), 0);
        assertEquals(location.getLongitude(),play.getLocation().getLongitude(), 0);

        assertEquals(time.getTime(),play.getTime().getTime());
        assertEquals("Afternoon", play.getTimeOfDay());
    }

    @Test
    public void loadMediaTest(){
         Song song = new Song("hello", "hello", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
         mainActivity.getActivity().loadMedia(song,mediaPlayer);
         assertEquals(song.getUri(),mediaPlayer.getDataSource());
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

    @UiThreadTest
    @Test
    public void initialAlbumsFragSetupTest(){

        mainActivity.getActivity().initialFragSetup(1);
        List<Fragment> fragments = mainActivity.getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments){
            if(fragment.isVisible()){
                assertEquals("songs",fragment.getTag());
            }
        }
    }

    @UiThreadTest
    @Test
    public void initialFlashFragSetupTest(){

        mainActivity.getActivity().initialFragSetup(2);
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



}
