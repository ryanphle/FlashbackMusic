package team21.flashbackmusic;

import android.app.Activity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by jerryliu on 3/11/18.
 */

public interface SimpleCallback {
    void callback(DataSnapshot dataSnapshot, ListView listView, Activity activity);
}
