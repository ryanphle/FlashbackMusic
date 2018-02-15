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

import java.util.Collection;
import java.util.Collections;
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

    protected static int flash_index = 0;
    private ArrayList<Song> random_songs;
    private Fragment random_fragmentFlashback;

    private static final int SONG_FRAG = 0;
    private static final int ALBUM_FRAG = 1;
    private static final int FLASHBACK_FRAG = 2;

    protected boolean songLoaded;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_uri = new ArrayList<>();
        random_songs = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        songLoaded = false;

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        albumList = new ArrayList<>(albums.values()); // Used to pass into Parceble ArrayList
        initialFragSetup();

        /* Setting up all Listeners */

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if(frag != FLASHBACK_FRAG) {
                    mediaPlayer.reset();
                    if (index == res_uri.size() - 1)
                        index = 0;
                    else
                        index++;
                    loadMedia(songs.get(index));
                    mediaPlayer.start();
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else {
                    mediaPlayer.reset();
                    if (flash_index == res_uri.size() - 1)
                        flash_index = 0;
                    else
                        flash_index++;
                    loadMedia(songs.get(flash_index));
                    mediaPlayer.start();
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String tag = "";
                final android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch(frag) {
                    case SONG_FRAG:
                        transaction.hide(fragmentSong);
                        break;
                    case ALBUM_FRAG:
                        transaction.hide(fragmentAlbums);
                        break;
                    case FLASHBACK_FRAG:
                        if(item.getItemId() != R.id.navigation_flashback) {
                            flash_index = 0;
                            mediaPlayer.reset();
                            loadMedia(songs.get(index));
                            transaction.remove(random_fragmentFlashback);
                        }
                        break;
                }

                switch (item.getItemId()) {
                    case R.id.navigation_songs:
                        if (frag == FLASHBACK_FRAG) {
                            stopButton.setBackgroundResource(R.drawable.ic_stopping);
                        }
                        prevButton.setVisibility(View.VISIBLE);
                        transaction.show(fragmentSong);
                        frag = SONG_FRAG;
                        break;

                    case R.id.navigation_albums:
                        if (frag == FLASHBACK_FRAG) {
                            stopButton.setBackgroundResource(R.drawable.ic_stopping);
                        }
                        frag = ALBUM_FRAG;
                        prevButton.setVisibility(View.VISIBLE);
                        transaction.show(fragmentAlbums);
                        break;

                    case R.id.navigation_flashback:
                        if(frag != FLASHBACK_FRAG) {
                            mediaPlayer.reset();
                            Collections.shuffle(random_songs);
                            random_setFlashbackFragment();
                            loadMedia(random_songs.get(flash_index));
                            mediaPlayer.start();

                            prevButton.setVisibility(View.INVISIBLE);
                            stopButton.setBackgroundResource(R.drawable.ic_playing);
                            transaction.add(R.id.main_container, random_fragmentFlashback, "flash_songs");
                        }
                        frag = FLASHBACK_FRAG;
                        break;
                }

                transaction.commit();
                transaction.addToBackStack(tag);
                return true;
            }
        });

        nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if(frag != FLASHBACK_FRAG) {
                    mediaPlayer.reset();
                    if (index == res_uri.size() - 1)
                        index = 0;
                    else
                        index++;
                    newSong(index, false);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else {
                    mediaPlayer.reset();
                    if (flash_index == res_uri.size() - 1)
                        flash_index = 0;
                    else
                        flash_index++;
                    newSong(flash_index, true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if(frag != FLASHBACK_FRAG) {
                    mediaPlayer.reset();
                    if (index == 0)
                        index = res_uri.size() - 1;
                    else
                        index--;
                    newSong(index, false);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        stopButton = (Button) findViewById(R.id.play);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    view.setBackgroundResource(R.drawable.ic_stopping);
                }
                else {
                    int currIdx = frag == FLASHBACK_FRAG ? flash_index : index;
                    newSong(currIdx, frag == FLASHBACK_FRAG);
                    updateSongMetaData(currIdx, frag == FLASHBACK_FRAG);
                    view.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });
    }

    public void initialFragSetup() {
        setSongFragment();
        setAlbumFragment();

        FragmentTransaction initTransaction = getSupportFragmentManager().beginTransaction();
        initTransaction.add(R.id.main_container, fragmentSong, "songs");
        initTransaction.addToBackStack("songs");
        initTransaction.add(R.id.main_container, fragmentAlbums, "albums");
        initTransaction.addToBackStack("albums");
        initTransaction.hide(fragmentAlbums);
        initTransaction.commit();

        loadMedia(songs.get(0));

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

        try {
            mediaPlayer.setDataSource(this, song.getUri());
            mediaPlayer.prepare();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void newSong(int index, boolean flashback) {
        ArrayList<Song> songList = songs;

        if (flashback)
            songList = random_songs;

        loadMedia(songList.get(index));
        mediaPlayer.start();
        updateSongMetaData(index, flashback);
    }

    public void updateSongMetaData(int index, boolean flashback) {
        ArrayList<Song> songList = songs;

        if (flashback)
            songList = random_songs;

        Song song = songList.get(index);
        switch (frag) {
            case SONG_FRAG:
                SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().getFragments().get(0);
                fragmentSong.updateSongUI(song);
                break;
            case ALBUM_FRAG:
                AlbumsFragment fragmentAlbum = (AlbumsFragment)
                        getSupportFragmentManager().getFragments().get(1);
                fragmentAlbum.updateSongUI(song);
                break;
            case FLASHBACK_FRAG:
                FlashbackFragment fragmentFlash = (FlashbackFragment)
                        getSupportFragmentManager().getFragments().get(2);
                fragmentFlash.updateSongUI(song);
                break;
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

            random_songs.add(song);
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

    private void random_setFlashbackFragment() {
        random_fragmentFlashback = new FlashbackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("random_songs", random_songs);
        random_fragmentFlashback.setArguments(bundle);
    }

    private void setAlbumFragment() {
        fragmentAlbums = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("albums", albumList);
        fragmentAlbums.setArguments(bundle);
    }

    public void scroll(View view) {
        if (songLoaded) {
            TextView artistAlbumInfo = (TextView) view.findViewById(R.id.small_artist_album_name);
            artistAlbumInfo.setSelected(true);
        }
    }

    public List<Song> getSongs(){return songs;}

}

