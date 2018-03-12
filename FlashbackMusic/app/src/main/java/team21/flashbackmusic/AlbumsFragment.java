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
                /*a = (Album) parent.getAdapter().getItem(position);
                Song s = a.getSongs().get(0);

                ((MainActivity)getActivity()).songLoaded = true;
                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).stopButton.setBackgroundResource(R.drawable.ic_playing);
                ((MainActivity)getActivity()).album_index = 0;
                ((MainActivity)getActivity()).currSongIdx = 0;
                ((MainActivity)getActivity()).currSong = s;
                ((MainActivity)getActivity()).album_dislike = 0;
                ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity) getActivity()).ALBUM_FRAG;
                ((MainActivity) getActivity()).stopButton.setBackgroundResource(R.drawable.ic_playing);

                ((MainActivity)getActivity()).mediaPlayerWrapper.setSongs(a.getSongs());
                ((MainActivity)getActivity()).mediaPlayerWrapper.newSong(0);

                Timestamp time = new Timestamp(System.currentTimeMillis());
                updateSongUI(s);
                ((MainActivity) getActivity()).storePlayInformation(
                        ((MainActivity) getActivity()).mediaPlayerWrapper.getSong(),
                        ((MainActivity) getActivity()).lastLocation,
                        time
                );*/

                a = (Album) parent.getAdapter().getItem(position);
                ArrayList<Song> s = new ArrayList<Song>(a.getSongs());

                ((MainActivity)getActivity()).songLoaded = true;
                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).album_index = 0;
                ((MainActivity)getActivity()).currSongIdx = 0;
                ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity) getActivity()).ALBUM_FRAG;


                SongsFragment songList = ((MainActivity)getActivity()).setAlbumSongFragment(s,a);



                FragmentTransaction songsTransaction = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();

                //((MainActivity)getActivity()).getSupportFragmentManager().popBackStack();

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

        /*Address address = s.getLocation();
        String addressStr = "";
        addressStr += address.getAddressLine(0) + ", ";
        addressStr += address.getAddressLine(1) + ", ";
        addressStr += address.getAddressLine(2);

        songLocation.setText(addressStr);
        songTime.setText(calendar.get(Calendar.MONTH) + 1 + "/" +  calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
*/
        ((MainActivity) getActivity()).setData(songLocation,songTime,lastPlayedBy,s.getName());

    }


    public void updateListView() {
        adapter.notifyDataSetChanged();
    }

}
