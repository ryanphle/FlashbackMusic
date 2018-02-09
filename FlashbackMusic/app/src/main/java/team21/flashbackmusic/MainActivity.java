package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
//import android.app.FragmentManager;
import android.support.v4.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    private Map<String,Album> albums;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songs;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_songs:
                        fragment = new SongsFragment();
                        break;
                    case R.id.navigation_albums:
                        fragment = new AlbumsFragment();
                        break;
                    case R.id.navigation_flashback:
                        fragment = new FlashbackFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        };
    }*/

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albums = new HashMap<>();
        songs = new ArrayList<>();

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        albumList = new ArrayList<Album>(albums.values());
        SongAdapter adapter = new SongAdapter(this, R.layout.activity_listview, songs);

        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_songs:
                        setSongFragment();
                        break;
                    case R.id.navigation_albums:
                        setAlbumFragment();
                        break;
                    case R.id.navigation_flashback:
                        fragment = new FlashbackFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });

        setSongFragment();
        FragmentTransaction transaction1 = fragmentManager.beginTransaction();
        transaction1.replace(R.id.main_container, fragment).commit();

        /*fragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("adapter", adapter);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();*/

        //((SongsFragment)fragment).setListView(adapter);

    }

    private void loadSongs() throws IllegalArgumentException, IllegalAccessException {

        Field[] fields=R.raw.class.getFields();

        for(int count=0; count < fields.length; count++){

            int resourceID = fields[count].getInt(fields[count]);
            Uri myUri = Uri.parse("android.resource://team21.flashbackmusic/" + resourceID);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, myUri);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] img = retriever.getEmbeddedPicture();
            //Log.i("Raw Songs name: ", fields[count].getName()+ "  album:"+ album+ "   artist  "+artist);

            if (albums.get(album)==null) {
                albums.put(album, new Album(album, artist,img));
            }

            Album a = albums.get(album);
            Song song = new Song(fields[count].getName(), artist,img, a.getName());
            albums.get(album).addSong(song);
            songs.add(song);
        }
    }

    private void setSongFragment() {
        fragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragment.setArguments(bundle);
    }

    private void setAlbumFragment() {
        fragment = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("albums", albumList);
        fragment.setArguments(bundle);
    }
}

