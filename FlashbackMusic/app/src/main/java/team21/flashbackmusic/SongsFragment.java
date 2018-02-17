package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
    public TextView artistAlbumInfo;

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
                if(s.favorite != -1) {
                    ((MainActivity) getActivity()).playSelectedSong(s);
                    updateSongUI(s);
                    ((MainActivity) getActivity()).songLoaded = true;
                    ((MainActivity) getActivity()).songPlayingFrag = ((MainActivity) getActivity()).SONG_FRAG;
                    ((MainActivity) getActivity()).currSong = s;
                }

            }
        });

        return rootView;
    }

   public void updateSongUI(Song s) {
        Log.i("Song update: ", s.getName());

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.small_album_art);
        TextView songName = (TextView) rootView.findViewById(R.id.small_song_name);
        artistAlbumInfo = (TextView) rootView.findViewById(R.id.small_artist_album_name);
        artistAlbumInfo.setSelected(false);

        artistAlbumInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(v);
            }
        });

        Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
        albumImage.setImageBitmap(bmp);

        songName.setText(s.getName());
        String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
        artistAlbumInfo.setText(artistAndAlbumStr);
    }

    public void scroll(View view) {
        TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.small_artist_album_name);
        artistAlbumInfo.setSelected(true);
    }

}
