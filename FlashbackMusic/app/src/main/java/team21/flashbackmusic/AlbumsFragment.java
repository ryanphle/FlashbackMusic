package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by ryanle on 2/8/18.
 */

public class AlbumsFragment extends Fragment {

    private AlbumAdapter adapter;

    public AlbumsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        ArrayList<Album> albums = getArguments().getParcelableArrayList("albums");
        adapter = new AlbumAdapter(getActivity(), R.layout.album_gridview, albums);
        GridView gridView = view.findViewById(R.id.album_grid);
        gridView.setAdapter(adapter);

        return view;
    }
}
