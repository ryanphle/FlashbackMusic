package team21.flashbackmusic;

import android.location.Location;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ryanle on 3/16/18.
 */

public class M2TestSortVibe {

    private List<Song> songs;
    private Location loc1, loc2, loc3;
    private Song song1, song2, song3;
    private Timestamp time1, time2, time3;
    final private double LONG1 = 40, LONG2 = 39.9, LONG3 = 40.1;
    final private double LAT = -103;
    final private long BASE_TIME1 = 1521176400000L, /*3/15/2018 22:00:00*/
                        BASE_TIME2 = 1521172800000L, /*3/15/2018 21:00:00*/
                        BASE_TIME3 = 1521180000000L; /*3/15/2018 23:00:00*/
    final private long TIME1 = 1521173100000L, /* 3/15 21:05:00 */
                        TIME2 = 1521176700000L, /* 3/15 22:05:00 */
                        TIME3 = 1521180300000L; /* 3/15 23:05:00 */

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        songs = mainActivity.getActivity().songs;
        loc1 = new Location("");
        loc2 = new Location("");
        loc3 = new Location("");

        loc1.setLatitude(LONG1);
        loc1.setLongitude(LAT);

        loc2.setLatitude(LONG2);
        loc2.setLongitude(LAT);

        loc3.setLatitude(LONG3);
        loc3.setLongitude(LAT);

        time1 = new Timestamp(TIME1);
        time2 = new Timestamp(TIME2);
        time3 = new Timestamp(TIME3);

        song1 = new Song("-1805827744", "title1", "artist", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),
                "byte".getBytes(), "album", true, "url");
        song2 = new Song("-918324063", "title2", "artist", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),
                "byte".getBytes(), "album", true, "url");
        song3 = new Song("-30820382", "title3", "artist", Uri.parse("android.resource://team21.flashbackmusic/2131558400"),
                "byte".getBytes(), "album", true, "url");

        mainActivity.getActivity().storeSong("", song1.getName(), "artist", song1.getUri(), song1.getImg(), song1.getAlbum());
        mainActivity.getActivity().storeSong("", song2.getName(), "artist", song2.getUri(), song2.getImg(), song1.getAlbum());
        mainActivity.getActivity().storeSong("", song3.getName(), "artist", song3.getUri(), song3.getImg(), song1.getAlbum());


        mainActivity.getActivity().storePlayInformation(song1, loc1, time1);
        mainActivity.getActivity().storePlayInformation(song2, loc2, time2);
        mainActivity.getActivity().storePlayInformation(song3, loc3, time3);
    }

    @Test
    public void testSong1First() {
        mainActivity.getActivity().lastLocation = loc1;
        mainActivity.getActivity().setCustomTime(time1);
        mainActivity.getActivity().sort_songs(songs, loc1);

        List<Song> sorted_songs = mainActivity.getActivity().getSortedSongs();
        assertEquals(song1.getName(), sorted_songs.get(0).getName());
    }

    @Test
    public void testSong2First() {
        mainActivity.getActivity().lastLocation = loc2;
        mainActivity.getActivity().setCustomTime(time2);
        mainActivity.getActivity().sort_songs(songs, loc2);

        List<Song> sorted_songs = mainActivity.getActivity().getSortedSongs();
        assertEquals(song2.getName(), sorted_songs.get(0).getName());
    }

    @Test
    public void testSong3First() {
        mainActivity.getActivity().lastLocation = loc3;
        mainActivity.getActivity().setCustomTime(time3);
        mainActivity.getActivity().sort_songs(songs, loc3);

        List<Song> sorted_songs = mainActivity.getActivity().getSortedSongs();
        assertEquals(song3.getName(), sorted_songs.get(0).getName());
    }
}
