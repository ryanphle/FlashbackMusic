package team21.flashbackmusic;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import static team21.flashbackmusic.MainActivity.SONG_FRAG;
import static team21.flashbackmusic.MainActivity.currSongIdx;
import static team21.flashbackmusic.MainActivity.songPlayingFrag;


/**
 * Created by ryanle on 2/18/18.
 */

public class MediaPlayerTest {

    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button prevButton;
    private Button nextButton;
    private BottomNavigationView bottomNavigationView;
    private Song song;


    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        this.mediaPlayer = mainActivity.getActivity().mediaPlayer;
        playButton = mainActivity.getActivity().stopButton;
        prevButton = mainActivity.getActivity().prevButton;
        nextButton = mainActivity.getActivity().nextButton;

        for (Song s : mainActivity.getActivity().songs) {
            s.setFavorite(0);
        }

        bottomNavigationView = mainActivity.getActivity().findViewById(R.id.navigation);
    }

    @UiThreadTest
    @Test
    public void testPlayAndPause() {
        if (mediaPlayer.isPlaying()) {
            playButton.performClick();
            assertTrue(!mediaPlayer.isPlaying());
            playButton.performClick();
            assertTrue(mediaPlayer.isPlaying());
        }
        else {
            playButton.performClick();
            assertTrue(mediaPlayer.isPlaying());
            playButton.performClick();
            assertTrue(!mediaPlayer.isPlaying());
        }
    }

    @UiThreadTest
    @Test
    public void testNextButton() {
        playButton.performClick();
        Song currSong = mainActivity.getActivity().currSong;
        nextButton.performClick();
        assertTrue(!currSong.getName().equals(mainActivity.getActivity().currSong.getName()));
    }

    @UiThreadTest
    @Test
    public void testPrevButton() {
        if (mainActivity.getActivity().frag != 2) {
            playButton.performClick();
            Song currSong = mainActivity.getActivity().currSong;
            prevButton.performClick();
            assertTrue(!currSong.getName().equals(mainActivity.getActivity().currSong.getName()));
        }
        else {

        }
    }
}
