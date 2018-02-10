package team21.flashbackmusic;

//import android.app.Fragment;
import android.media.MediaPlayer;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String,Album> albums;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songs;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private List<Uri> res_uri;
    private static int index = 0;
    private MediaPlayer mediaPlayer;
    private Button stopButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_uri = new ArrayList<>();

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadMedia(res_uri.get(index));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        albumList = new ArrayList<Album>(albums.values());
        SongAdapter adapter = new SongAdapter(this, R.layout.activity_listview, songs);

        bottomNavigationView = findViewById(R.id.navigation);

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

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == res_uri.size() - 1)
                    index = 0;
                else
                    index++;
                loadMedia(res_uri.get(index));
                mediaPlayer.start();
                stopButton.setBackgroundResource(R.drawable.ic_playing);
            }
        });

        Button prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == 0)
                    index = res_uri.size() - 1;
                else
                    index--;
                loadMedia(res_uri.get(index));
                mediaPlayer.start();
                stopButton.setBackgroundResource(R.drawable.ic_playing);
            }
        });

        stopButton = (Button) findViewById(R.id.play);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    view.setBackgroundResource(R.drawable.ic_stopping);
                }
                else {
                    loadMedia(res_uri.get(index));
                    mediaPlayer.start();
                    view.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

    }

    public void playSelectedSong(Song s) {
        Uri uri = s.getUri();
        index = res_uri.indexOf(uri);
        stopButton.setBackgroundResource(R.drawable.ic_playing);
        mediaPlayer.reset();
        loadMedia(uri);
        mediaPlayer.start();
    }

    public void loadMedia(Uri uri) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }


        //AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(res_ids.get(index));
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mediaPlayer.isPlaying()) {
            ; // do nothing
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void loadSongs() throws IllegalArgumentException, IllegalAccessException {

        Field[] fields=R.raw.class.getFields();
        Log.d("Size of fields", Integer.toString(fields.length));

        for(int count=0; count < fields.length; count++){

            int resourceID = fields[count].getInt(fields[count]);
            Uri uri = Uri.parse("android.resource://team21.flashbackmusic/" + resourceID);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] img = retriever.getEmbeddedPicture();

            Log.i("Raw Songs name: ", title+ "  album:"+ album+ "   artist  "+artist+" uri "+uri);

            if (albums.get(album)==null) {
                albums.put(album, new Album(album, artist,img));
            }

            Album a = albums.get(album);
            Song song = new Song(title, artist, uri, img, a.getName());

            albums.get(album).addSong(song);
            songs.add(song);
            res_uri.add(uri);
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

    public List<Song> getSongs(){return songs;}

}

