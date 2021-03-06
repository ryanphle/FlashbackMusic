package team21.flashbackmusic;

//import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private boolean isAlbum;
    Sorter sorter;

    public SongsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        listView = rootView.findViewById(R.id.song_list);

        songs = getArguments().getParcelableArrayList("songs");
        isAlbum = getArguments().getBoolean("isAlbum");
        if(isAlbum){

            String title = getArguments().getString("title");

            TextView songName = (TextView) rootView.findViewById(R.id.Frag_title);

            songName.setText(title);

        }
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getAdapter().getItem(position);
                if(s.getFavorite() != -1) {
                    updateSongUI(s);
                    ((MainActivity) getActivity()).songLoaded = true;
                    if(!isAlbum) {
                        ((MainActivity) getActivity()).songPlayingFrag = ((MainActivity) getActivity()).SONG_FRAG;
                    }
                    else{
                        ((MainActivity) getActivity()).songPlayingFrag = ((MainActivity) getActivity()).ALBUM_FRAG;
                    }
                    ((MainActivity) getActivity()).stopButton.setBackgroundResource(R.drawable.ic_playing);
                    ((MainActivity) getActivity()).currSong = s;

                    ((MainActivity)getActivity()).mediaPlayerWrapper.setSongs(songs);
                    ((MainActivity)getActivity()).mediaPlayerWrapper.newSong(position);

                    Timestamp time = ((MainActivity) getActivity()).getTime();
                    updateSongUI(s);
                    ((MainActivity) getActivity()).storePlayInformation(
                            ((MainActivity) getActivity()).mediaPlayerWrapper.getSong(),
                            ((MainActivity)getActivity()).lastLocation,
                            time
                    );
                }
            }
        });

        Button downloadButton = rootView.findViewById(R.id.download_btn);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showDownloadDialog();
            }
        });

        if (isAlbum && ((MainActivity)getActivity()).currSong != null) {
            updateSongUI(((MainActivity)getActivity()).currSong);
        }

        final ImageButton mybutton = rootView.findViewById(R.id.sorting);
        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), mybutton);
                popupMenu.getMenuInflater().inflate(R.menu.sort_songs, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(
                                getContext(),
                                "You Clicked : " + menuItem.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();

                        switch (menuItem.getItemId()) {
                            case R.id.sort_artist:
                                sorter = new SortByArtist();
                                break;
                            case R.id.sort_album:
                                sorter = new SortByAlbum();
                                break;
                            case R.id.sort_title:
                                sorter = new SortByTitle();
                                break;
                            case R.id.sort_favorite:
                                sorter = new SortByFavorite();
                                break;
                        }

                        Song currSong = ((MainActivity) getActivity()).mediaPlayerWrapper.getSong();

                        sorter.sort(songs);
                        updateListView();
                        listView.invalidate();

                        // Reset mediaplayer to reflect new order
                        ((MainActivity) getActivity()).mediaPlayerWrapper.setSongs(songs);
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i) == currSong)
                                ((MainActivity) getActivity()).mediaPlayerWrapper.setIndex(i);
                        }

                        return true;
                    }
                });
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

    public void updateListView(){

        adapter.notifyDataSetChanged();

    }


}
