package team21.flashbackmusic;

import android.app.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by brian on 2/18/2018.
 */

public class SongsFragmentTest {


    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {

    }

    @Test
    public void updateSongUITest() {

        /*Song testSong1 = new Song("song1", "artist", Uri.parse("android.resource://team21.flashbackmusic/2131558400"), "byte".getBytes(), "album");

        List<Fragment> fragments = mainActivity.getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragments) {
            if(fragment.getTag().compareTo("songs") ==1) {
                SongsFragment sfragment = (SongsFragment)fragment;

                Calendar calendar = Calendar.getInstance();
                Timestamp time = new Timestamp(100000000);
                testSong1.setTimeStamp(time);
                calendar.setTimeInMillis(time.getTime());
                calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                String readableTime = calendar.get(Calendar.MONTH) + 1 + "/" +  calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                TextView songName = (TextView) sfragment.getActivity().findViewById(R.id.big_song_name);
                TextView artistAlbumInfo = (TextView) sfragment.getActivity().findViewById(R.id.big_song_artist);
                TextView songTime = (TextView) sfragment.getActivity().findViewById(R.id.big_song_time);

                assertEquals(testSong1.getName(), songName.getText());
                assertEquals(readableTime, songTime.getText());
                assertEquals(testSong1.getArtist() + " - " + testSong1.getAlbum(), artistAlbumInfo.getText());
            }
        }*/
    }
}
