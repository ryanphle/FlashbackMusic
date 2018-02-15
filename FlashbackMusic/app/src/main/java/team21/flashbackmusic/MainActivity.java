package team21.flashbackmusic;

//import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
//import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

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
    private Fragment fragmentSong;
    private Fragment fragmentAlbums;
    private Fragment fragmentFlashback;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private List<Uri> res_uri;
    protected static int index = 0;
    protected MediaPlayer mediaPlayer;
    protected Button stopButton;
    protected Button prevButton;
    protected Button nextButton;
    private int frag = 0;

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

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                if (index == res_uri.size() - 1)
                    index = 0;
                else
                    index++;
                loadMedia(songs.get(index));
                mediaPlayer.start();
                stopButton.setBackgroundResource(R.drawable.ic_playing);

                Button updateUI = findViewById(R.id.songs_hidden_next);
                updateUI.performClick();
            }
        });

        albumList = new ArrayList<Album>(albums.values());
        SongAdapter adapter = new SongAdapter(this, R.layout.activity_listview, songs);

        bottomNavigationView = findViewById(R.id.navigation);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String tag = "";
                final android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch(frag) {
                    case 0:
                        transaction.hide(fragmentSong);
                        break;
                    case 1:
                        transaction.hide(fragmentAlbums);
                        break;
                    case 2:
                        transaction.hide(fragmentFlashback);
                        break;
                }

                switch (item.getItemId()) {
                    case R.id.navigation_songs:
                        frag = 0;
                        transaction.show(fragmentSong);
                        break;
                    case R.id.navigation_albums:
                        frag = 1;
                        transaction.show(fragmentAlbums);
                        break;
                    case R.id.navigation_flashback:
                        frag = 2;
                        transaction.show(fragmentFlashback);
                        break;
                }

                transaction.commit();
                //transaction.replace(R.id.main_container, fragment, tag).commit();
                transaction.addToBackStack(tag);
                return true;
            }
        });

        setSongFragment();
        setAlbumFragment();
        setFlashbackFragment();
        android.support.v4.app.FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.main_container, fragmentSong, "songs");
        transaction1.add(R.id.main_container, fragmentAlbums, "songs");
        transaction1.add(R.id.main_container, fragmentFlashback, "songs");
        transaction1.hide(fragmentAlbums);
        transaction1.hide(fragmentFlashback);
        transaction1.commit();
        //transaction1.replace(R.id.main_container, fragment, "songs").commit();
        //transaction1.addToBackStack("songs");


        /*if (fragmentManager.getFragments().size() == 0) {
            Toast.makeText(MainActivity.this,
                    "NULL",Toast.LENGTH_SHORT).show();
        }*/

        loadMedia(songs.get(0));

        //Toast.makeText(MainActivity.this,
          //      getSupportFragmentManager().getFragments().toString(),Toast.LENGTH_SHORT).show();

        nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == res_uri.size() - 1)
                    index = 0;
                else
                    index++;
                loadMedia(songs.get(index));
                mediaPlayer.start();
                stopButton.setBackgroundResource(R.drawable.ic_playing);

                Button updateUI = findViewById(R.id.songs_hidden_next);
                updateUI.performClick();
            }
        });

        prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == 0)
                    index = res_uri.size() - 1;
                else
                    index--;
                loadMedia(songs.get(index));
                mediaPlayer.start();
                stopButton.setBackgroundResource(R.drawable.ic_playing);

                Button updateUI = findViewById(R.id.songs_hidden_prev);
                updateUI.performClick();
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
                    loadMedia(songs.get(index));
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
        loadMedia(s);
        mediaPlayer.start();
    }

    public void loadMedia(Song song) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        /*switch (frag) {
             case 0:
                 //SongsFragment fragmentSong = (SongsFragment) fragmentManager.getFragments().get(0);
                 //SongsFragment fragmentSong = (SongsFragment) fragmentManager.findFragmentByTag("songs");
                 //fragmentSong.updateSongUI(song);
                 break;
             case 1:
                 AlbumsFragment fragmentAlbum = (AlbumsFragment)
                         getSupportFragmentManager().getFragments().get(0);
                 fragmentAlbum.updateSongUI(song);
                 break;
             case 2:
                 FlashbackFragment fragmentFlash = (FlashbackFragment)
                         getSupportFragmentManager().getFragments().get(0);
                 fragmentFlash.updateSongUI(song);
                 break;
        }*/

       //AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(res_ids.get(index));
        try {
            mediaPlayer.setDataSource(this, song.getUri());
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

            if (title == null) { title = "No title found"; }
            if (album == null) { album = "No album found"; }
            if (artist == null) { artist = "No artist found"; }

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
        fragmentSong = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragmentSong.setArguments(bundle);
    }

    private void setFlashbackFragment() {
        fragmentFlashback = new FlashbackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragmentFlashback.setArguments(bundle);
    }

    private void setAlbumFragment() {
        fragmentAlbums = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("albums", albumList);
        fragmentAlbums.setArguments(bundle);
    }

    public List<Song> getSongs(){return songs;}

}

