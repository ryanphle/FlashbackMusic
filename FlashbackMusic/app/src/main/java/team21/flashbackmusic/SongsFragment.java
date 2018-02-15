package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    public View rootView;
    public ListView listView;

    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        listView = rootView.findViewById(R.id.song_list);

        final ArrayList<Song> songs = getArguments().getParcelableArrayList("songs");
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getAdapter().getItem(position);
                ((MainActivity)getActivity()).playSelectedSong(s);
                updateSongUI(s);
            }
        });

        return rootView;
    }

   public void updateSongUI(Song s) {

        Log.i("Song update: ", s.getName());
        /*ImageView albumImage = (ImageView) getActivity().findViewById(R.id.small_album_art);
        TextView songName = (TextView) getActivity().findViewById(R.id.small_song_name);
        TextView artistAlbumInfo = (TextView) getActivity().findViewById(R.id.small_artist_album_name);*/

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
