package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ryanle on 2/8/18.
 */

public class FlashbackFragment extends Fragment {

    private SongAdapter adapter;

    public FlashbackFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        View rootView = inflater.inflate(R.layout.fragment_flashback, container, false);
        ListView listView = rootView.findViewById(R.id.flashback_list);

        final ArrayList<Song> songs = getArguments().getParcelableArrayList("songs");
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);


        Button next = rootView.findViewById(R.id.flashback_hidden_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = ((MainActivity)getActivity()).index;
                Song s = songs.get(index);
                updateSongUI(s);
            }
        });

        Button prev = rootView.findViewById(R.id.flashback_hidden_prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = ((MainActivity)getActivity()).index;
                Song s = songs.get(index);
                updateSongUI(s);
            }
        });


        return rootView;
    }

    public void updateSongUI(Song s) {



    }
}
