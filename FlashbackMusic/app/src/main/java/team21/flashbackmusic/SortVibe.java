package team21.flashbackmusic;

import android.app.Activity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by jerryliu on 3/13/18.
 */

public class SortVibe implements SimpleCallback {
    private SongAdapter adapter;

    @Override
    public void callback(DataSnapshot dataSnapshot, ListView listView, Activity activity) {
        ArrayList<Song> songs = new ArrayList<>();

        //Todo get song from datasnapshot and sort

        adapter = new SongAdapter(activity, R.layout.activity_listview, songs);
        listView.setAdapter(adapter);
    }

}
