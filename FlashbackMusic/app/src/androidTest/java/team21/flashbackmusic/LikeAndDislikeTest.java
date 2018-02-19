package team21.flashbackmusic;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by ryanle on 2/18/18.
 */

public class LikeAndDislikeTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    private ListView songList;
    private SharedPreferences sp;
    private ArrayList<Song> songs;
    private View song1View;
    private Button like;
    private Song currSong;
    private Button playButton, nextButton, prevButton;

    @Before
    public void before() {
        songList = mainActivity.getActivity().findViewById(R.id.song_list);

        playButton = mainActivity.getActivity().stopButton;
        nextButton = mainActivity.getActivity().nextButton;
        prevButton = mainActivity.getActivity().prevButton;

        songs = mainActivity.getActivity().songs;
        currSong = songs.get(0);

        song1View = songList.getChildAt(0);
        like = song1View.findViewById(R.id.likeDislikeButton);

        for (Song s : songs) {
            s.setFavorite(0);
        }
    }

    @UiThreadTest
    @Test
    public void testNeutralToLike() {
        like.performClick();
        assertEquals(1, currSong.getFavorite());
    }

    @UiThreadTest
    @Test
    public void testLikeToDislike() {
        currSong.setFavorite(1);
        like.performClick();
        assertEquals(-1, currSong.getFavorite());
    }

    @UiThreadTest
    @Test
    public void testDislikeToNeutral() {
        currSong.setFavorite(-1);
        like.performClick();
        assertEquals(0, currSong.getFavorite());
    }

    @UiThreadTest
    @Test
    public void testSelectingDislikedSong() {
        currSong.setFavorite(-1);
        playButton.performClick();
        song1View.performClick();
        assertTrue(!mainActivity.getActivity().currSong.getName().equals(currSong.getName()));
        playButton.performClick();
    }

    @UiThreadTest
    @Test
    public void testMovingToNextSongIfDisliked() {
        currSong.setFavorite(1);
        playButton.performClick();
        song1View.performClick();
        like.performClick();
        assertTrue(!mainActivity.getActivity().currSong.getName().equals(currSong.getName()));
        playButton.performClick();
    }

    @UiThreadTest
    @Test
    public void testSkippingDislikedSongOnNext() {
        currSong.setFavorite(0);
        Song song2 = songs.get(1);
        Song song3 = songs.get(2);

        song2.setFavorite(-1);
        song3.setFavorite(0);

        song1View.performClick();
        nextButton.performClick();
        assertTrue(!mainActivity.getActivity().currSong.getName().equals(song2.getName()));
    }

    @UiThreadTest
    @Test
    public void testSkippingDislikedSongOnPrev() {
        currSong.setFavorite(0);
        Song song2 = songs.get(1);
        Song song3 = songs.get(2);

        song2.setFavorite(-1);
        song3.setFavorite(0);

        View song3View = songList.getChildAt(2);
        song3View.performClick();
        prevButton.performClick();
        assertTrue(!mainActivity.getActivity().currSong.getName().equals(song2.getName()));
    }


}
