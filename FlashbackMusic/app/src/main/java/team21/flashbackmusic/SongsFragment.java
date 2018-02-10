package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryanle on 2/8/18.
 */

public class SongsFragment extends Fragment {

    private SongAdapter adapter;

    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        ListView listView = rootView.findViewById(R.id.song_list);

        final ArrayList<Song> songs = getArguments().getParcelableArrayList("songs");
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getAdapter().getItem(position);
                ((MainActivity)getActivity()).playSelectedSong(s.getUri());
            }
        });

        return rootView;
    }

}
