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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanle on 2/8/18.
 */

public class FlashbackFragment extends Fragment {

    private SongAdapter adapter;
    private View rootView;
    private ListView listView;

    public FlashbackFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {

        rootView = inflater.inflate(R.layout.fragment_flashback, container, false);
        listView = rootView.findViewById(R.id.flashback_list);

        final ArrayList<Song> songs = getArguments().getParcelableArrayList("random_songs");
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        updateSongUI(((MainActivity)getActivity()).currSong);

        ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity)getActivity()).FLASHBACK_FRAG;

        return rootView;
    }

    public void updateSongUI(Song s) {

        Log.i("Song update: ", s.getName());
        ImageView albumImage = (ImageView) rootView.findViewById(R.id.large_album_art);
        TextView songName = (TextView) rootView.findViewById(R.id.big_song_name);
        TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.big_song_artist);
        TextView songLocation = (TextView) rootView.findViewById(R.id.big_song_location);
        TextView songTime = (TextView) rootView.findViewById(R.id.big_song_time);

        Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
        albumImage.setImageBitmap(bmp);

        songName.setText(s.getName());
        String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
        artistAlbumInfo.setText(artistAndAlbumStr);

    }
}
