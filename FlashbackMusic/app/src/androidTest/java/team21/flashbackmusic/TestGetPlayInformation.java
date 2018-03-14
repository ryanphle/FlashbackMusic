package team21.flashbackmusic;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by jerryliu on 3/11/18.
 */

public class TestGetPlayInformation {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1() {
        Song s = new Song ("Beware", null, null, null, null);


        SimpleCallback sc = new SimpleCallback() {
            @Override
            public void callback(String data) {
                //assertEquals("1471418147", data);
                ((MainActivity) mainActivity.getActivity()).setUser(data);
            }
        };
        mainActivity.getActivity().getPlayInfomation(s, sc);
        assertEquals("1471418147", mainActivity.getActivity().getUser());

    }
}
