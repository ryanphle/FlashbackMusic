package team21.flashbackmusic;

import android.location.Location;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;


/**
 * Created by petternarvhus on 07/03/2018.
 */

public class DatabaseTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void proxyTest(){
        mainActivity.getActivity().setProxy(null);
        assertNull(mainActivity.getActivity().getProxy());
        mainActivity.getActivity().proxyGenerator();
        while (mainActivity.getActivity().getProxy()==null){}
        assertNotNull(mainActivity.getActivity().getProxy());
    }

    @Test
    public void multipleProxyTest(){
        String proxy = mainActivity.getActivity().getProxy();
        mainActivity.getActivity().setProxy(null);
        mainActivity.getActivity().proxyGenerator();
        while (mainActivity.getActivity().getProxy()==null){}
        assertNotEquals(proxy,mainActivity.getActivity().getProxy());
    }

    @Test
    public void updateUITest(){
        final TextView songLocation = (TextView) mainActivity.getActivity().findViewById(R.id.big_song_location);
        final TextView songTime = (TextView) mainActivity.getActivity().findViewById(R.id.big_song_time);
        final TextView lastPlayedBy = (TextView) mainActivity.getActivity().findViewById(R.id.last_played_by);
        mainActivity.getActivity().setData(songLocation,songTime,lastPlayedBy,"Hello");
        while (songLocation.getText().toString().equals("Location") || lastPlayedBy.getText().toString().equals("Last played by:") ){}
        assertEquals("CSE Basement", songLocation.getText());
        assertEquals("Last played by: Mickey Mouse", lastPlayedBy.getText().toString());
        assertEquals("3/7 21:22", songTime.getText().toString());
    }

    @Test
    public void storePlayDataTest(){
        Song song = new Song("title", "artist", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),  "byte".getBytes(), "album");
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Location location = new Location("");
        location.setLatitude(32.715736);
        location.setLongitude(-117.161087);
        mainActivity.getActivity().storePlayInformation(song,location,time,"user","proxy");

        final TextView songLocation = (TextView) mainActivity.getActivity().findViewById(R.id.big_song_location);
        final TextView songTime = (TextView) mainActivity.getActivity().findViewById(R.id.big_song_time);
        final TextView lastPlayedBy = (TextView) mainActivity.getActivity().findViewById(R.id.last_played_by);
        mainActivity.getActivity().setData(songLocation,songTime,lastPlayedBy,"title");
        while (songLocation.getText().toString().equals("Location") || lastPlayedBy.getText().toString().equals("Last played by:") ){}
        assertEquals("402w Broadway, San Diego, CA 92101, USA", songLocation.getText());
        assertEquals("Last played by: proxy", lastPlayedBy.getText().toString());
        assertEquals(mainActivity.getActivity().getCurrentTime(time), songTime.getText().toString());



    }


}
