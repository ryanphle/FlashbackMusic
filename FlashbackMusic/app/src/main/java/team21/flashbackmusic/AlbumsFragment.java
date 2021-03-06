package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

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
        gridView = rootView.findViewById(R.id.album_grid);


        ArrayList<Album> albums = getArguments().getParcelableArrayList("albums");
        adapter = new AlbumAdapter(getActivity(), R.layout.album_gridview, albums);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                a = (Album) parent.getAdapter().getItem(position);
                ArrayList<Song> s = new ArrayList<Song>(a.getSongs());

                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).album_index = 0;

                SongsFragment songList = ((MainActivity)getActivity()).setAlbumSongFragment(s,a);

                FragmentTransaction songsTransaction = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                songsTransaction.add(R.id.main_container,songList,"albumsongs");
                songsTransaction.addToBackStack("albumsongs");
                songsTransaction.commit();
            }
        });

        Button downloadButton = rootView.findViewById(R.id.download_btn);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showDownloadDialog();
            }
        });

        Button timeButton = rootView.findViewById(R.id.time_btn);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showTimeDialog();
            }
        });

        return rootView;
    }

    public void updateSongUI(Song s) {
        adapter.notifyDataSetChanged();
        Log.i("Song update: ", s.getName());

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.large_album_art);
        TextView songName = (TextView) rootView.findViewById(R.id.big_song_name);
        TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.big_song_artist);
        final TextView songLocation = (TextView) rootView.findViewById(R.id.big_song_location);
        final TextView songTime = (TextView) rootView.findViewById(R.id.big_song_time);
        final TextView lastPlayedBy = (TextView) rootView.findViewById(R.id.last_played_by);
        Calendar calendar;

        Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
        albumImage.setImageBitmap(bmp);

        songName.setText(s.getName());
        String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
        artistAlbumInfo.setText(artistAndAlbumStr);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(s.getTimeStamp().getTime());
        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        ((MainActivity) getActivity()).setData(songLocation,songTime,lastPlayedBy,s.getID());
    }


    public void updateListView() {
        adapter.notifyDataSetChanged();
    }

}
