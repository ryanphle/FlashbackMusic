package team21.flashbackmusic;

//import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
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

        ArrayList<Album> albums = getArguments().getParcelableArrayList("albums");
        adapter = new AlbumAdapter(getActivity(), R.layout.album_gridview, albums);
        gridView = rootView.findViewById(R.id.album_grid);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                a = (Album) parent.getAdapter().getItem(position);
                Song s = a.getSongs().get(0);
                /*((MainActivity)getActivity()).playSelectedSong(s);
                updateSongUI(s);
                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).songPlayingFrag = ((MainActivity)getActivity()).ALBUM_FRAG;
                ((MainActivity)getActivity()).album_index = 0;
                ((MainActivity)getActivity()).currSongIdx = 0;
                ((MainActivity)getActivity()).songLoaded = true;
                ((MainActivity)getActivity()).currSong = s;*/

                ((MainActivity)getActivity()).songLoaded = true;
                ((MainActivity)getActivity()).currAlbum = a;
                ((MainActivity)getActivity()).stopButton.setBackgroundResource(R.drawable.ic_playing);
                ((MainActivity)getActivity()).album_index = 0;
                ((MainActivity)getActivity()).currSongIdx = 0;
                ((MainActivity)getActivity()).currSong = s;
                ((MainActivity)getActivity()).album_dislike = 0;

                ((MainActivity)getActivity()).newSong(0,((MainActivity)getActivity()).ALBUM_FRAG,true,true);



            }
        });

        return rootView;
    }

    public void updateSongUI(Song s) {
        Log.i("Song update: ", s.getName());

        /*if (s==null) return;

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.small_album_art);
        TextView songName = (TextView) rootView.findViewById(R.id.small_song_name);
        TextView artistAlbumInfo = (TextView) rootView.findViewById(R.id.small_artist_album_name);

        Bitmap bmp = BitmapFactory.decodeByteArray(s.getImg(), 0, s.getImg().length);
        albumImage.setImageBitmap(bmp);

        songName.setText(s.getName());
        String artistAndAlbumStr = s.getArtist() + " - " + s.getAlbum();
        artistAlbumInfo.setText(artistAndAlbumStr);*/
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String sName = s.getName();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Songs").exists() && dataSnapshot.child("Songs").child(sName).exists()) {
                    songLocation.setText(dataSnapshot.child("Songs").child(sName).child("last_play_location").getValue(String.class));
                    songTime.setText( dataSnapshot.child("Songs").child(sName).child("last_play_time").getValue(String.class));
                    lastPlayedBy.setText("Last played by: " + dataSnapshot.child("Songs").child(sName).child("last_play_proxy").getValue(String.class));
                }
                else {
                    songLocation.setText("N/A");
                    songTime.setText("N/A");
                    lastPlayedBy.setText("N/A");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG1", "Failed to read value.", databaseError.toException());
            }
        });

    }
}
