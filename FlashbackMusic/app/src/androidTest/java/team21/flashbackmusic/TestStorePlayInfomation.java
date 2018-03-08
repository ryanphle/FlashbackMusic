package team21.flashbackmusic;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jerryliu on 3/4/18.
 */

public class TestStorePlayInfomation {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1() {
        Song s = new Song ("baby", null, null, null, null);
        mainActivity.getActivity().storePlayInformation(s, null, null, "Jay", "proxy");
    }
}
