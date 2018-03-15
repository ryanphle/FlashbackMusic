package team21.flashbackmusic;

//import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Telephony;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        final ArrayList<Song> songs = getArguments().getParcelableArrayList("songs");
        if( songs == null) {
            Log.i("Song size", "songs is null");
        }
        else {
            Log.i("song size", "" + songs.size());
        }


        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

            updateSongUI(songs.get(0));


        ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity)getActivity()).FLASHBACK_FRAG;

        return rootView;
    }

    public void updateSongUI(Song s) {

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
}
