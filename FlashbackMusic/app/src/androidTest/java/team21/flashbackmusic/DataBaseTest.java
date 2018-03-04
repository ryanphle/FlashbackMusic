package team21.flashbackmusic;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by petternarvhus on 03/03/2018.
 */

public class DataBaseTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void proxyGeneratorTest(){
        mainActivity.getActivity().proxyGenerator();

        assertEquals("Mickey Mouse",mainActivity.getActivity().proxy);
    }
}
