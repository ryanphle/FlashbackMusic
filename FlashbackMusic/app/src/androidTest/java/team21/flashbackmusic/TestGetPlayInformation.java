package team21.flashbackmusic;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jerryliu on 3/3/18.
 */

public class TestGetPlayInformation {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1() {
        String value = mainActivity.getActivity().getPlayInformation(new Song("baby", "justin", null, null, null));
        assertEquals("Jay", value);
    }
}
