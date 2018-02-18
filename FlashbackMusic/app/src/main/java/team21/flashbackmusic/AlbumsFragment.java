package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ryanle on 2/8/18.
 */

public class AlbumsFragment extends Fragment {

    private AlbumAdapter adapter;
    private View rootView;
    private GridView gridView;
    protected Album a;

    public AlbumsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        rootView = inflater.inflate(R.layout.fragment_albums, container, false);

        ArrayList<Album> albums = getArguments().getParcelableArrayList("albums");
        adapter = new AlbumAdapter(getActivity(), R.layout.album_gridview, albums);
        gridView = rootView.findViewById(R.id.album_grid);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                a = (Album) parent.getAdapter().getItem(position);
                Song s = a.getSongs().get(0);
                ((MainActivity)getActivity()).playSelectedSong(s);
                updateSongUI(s);
                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity)getActivity()).ALBUM_FRAG;
                ((MainActivity)getActivity()).album_index = 0;
                ((MainActivity)getActivity()).currSongIdx = 0;
                ((MainActivity)getActivity()).songLoaded = true;
                ((MainActivity)getActivity()).currSong = s;

            }
        });

        return rootView;
    }

    public void updateSongUI(Song s) {
        Log.i("Song update: ", s.getName());

        if (s==null) return;

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.small_album_art);
        TextView songName = (TextView) rootView.findViewById(R.id.small_song_name);
        TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.small_artist_album_name);

        Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
        albumImage.setImageBitmap(bmp);

        songName.setText(s.getName());
        String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
        artistAlbumInfo.setText(artistAndAlbumStr);
    }
}
