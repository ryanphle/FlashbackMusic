package tests;

import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import team21.flashbackmusic.FlashbackFragment;
import team21.flashbackmusic.MainActivity;
import team21.flashbackmusic.R;
import team21.flashbackmusic.SongsFragment;

import static junit.framework.Assert.assertEquals;

/**
 * Created by James on 2/18/2018.
 */

public class TestSwitch {


    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @UiThreadTest
    @Test
    public void test_SongtoFlash(){

        String tag = "";

        List<Fragment> fragmentList;
        Fragment currFragment;

        SongsFragment fragmentSong = (SongsFragment) mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag("songs");

        BottomNavigationView navigationView = mainActivity.getActivity().findViewById(R.id.navigation);
        View songView = navigationView.findViewById(R.id.navigation_songs);
        songView.performClick();

        View flashView = navigationView.findViewById(R.id.navigation_flashback);
        flashView.performClick();


        while(mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag("flash_songs").equals(null)){}


        FlashbackFragment fragmentFlash = (FlashbackFragment) mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag("flash_songs");

        assertEquals(fragmentFlash,mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag(tag));

        songView.performClick();

        while(mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag("songs").equals(null)){}


        tag = mainActivity.getActivity().getSupportFragmentManager().getBackStackEntryAt
                (mainActivity.getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName();


        assertEquals(fragmentSong,mainActivity.getActivity().getSupportFragmentManager().findFragmentByTag(tag));



    }


}
