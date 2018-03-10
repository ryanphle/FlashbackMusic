package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ryanle on 2/8/18.
 */

public class SongsFragment extends Fragment {

    private SongAdapter adapter;
    public View rootView;
    public ListView listView;
    public TextView artistAlbumInfo;
    private ArrayList<Song> songs;

    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        listView = rootView.findViewById(R.id.song_list);

        songs = getArguments().getParcelableArrayList("songs");
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getAdapter().getItem(position);
                if(s.getFavorite() != -1) {

                    ((MainActivity) getActivity()).songLoaded = true;
                    ((MainActivity) getActivity()).songPlayingFrag = ((MainActivity) getActivity()).SONG_FRAG;
                    ((MainActivity) getActivity()).stopButton.setBackgroundResource(R.drawable.ic_playing);
                    ((MainActivity) getActivity()).currSong = s;

                    ((MainActivity)getActivity()).mediaPlayerWrapper.setSongs(songs);
                    ((MainActivity)getActivity()).mediaPlayerWrapper.newSong(position);

                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    ((MainActivity) getActivity()).storePlayInformation(
                            ((MainActivity) getActivity()).mediaPlayerWrapper.getSong(),
                            ((MainActivity)getActivity()).lastLocation,
                            "plays",
                            time
                    );
                    updateSongUI(s);
                }
            }
        });

        return rootView;
    }

   public void updateSongUI(Song s) {

       adapter.notifyDataSetChanged();
       ImageView albumImage = (ImageView) rootView.findViewById(R.id.large_album_art);
       TextView songName = (TextView) rootView.findViewById(R.id.big_song_name);
       TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.big_song_artist);
       TextView songLocation = (TextView) rootView.findViewById(R.id.big_song_location);
       TextView songTime = (TextView) rootView.findViewById(R.id.big_song_time);
       Calendar calendar;

       Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
       albumImage.setImageBitmap(bmp);

       songName.setText(s.getName());
       String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
       artistAlbumInfo.setText(artistAndAlbumStr);

       calendar = Calendar.getInstance();
       calendar.setTimeInMillis(s.getTimeStamp().getTime());
       calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

       Address address = s.getLocation();
       String addressStr = "";
       addressStr += address.getAddressLine(0) + ", ";
       addressStr += address.getAddressLine(1) + ", ";
       addressStr += address.getAddressLine(2);

       songLocation.setText(addressStr);
       songTime.setText(calendar.get(Calendar.MONTH) + 1 + "/" +  calendar.get(Calendar.DATE) + " "
               + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

   }


}
