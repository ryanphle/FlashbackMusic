package team21.flashbackmusic;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jerryliu on 3/4/18.
 */

public class TestGetUserName {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1() {
        String username = mainActivity.getActivity().getMyUserName();
        assertEquals("Chenglin", username);
    }
}
