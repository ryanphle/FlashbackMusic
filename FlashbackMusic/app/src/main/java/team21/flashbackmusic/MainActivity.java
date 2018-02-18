package team21.flashbackmusic;

//import android.app.Fragment;
import android.content.SharedPreferences;
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

import java.util.Arrays;
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
    private static int frag = 0;
    protected static int songPlayingFrag = 0;
    protected static int currSongIdx = 0;
    protected static Album currAlbum;
    protected static Song currSong;

    protected static int flash_index = 0;
    protected static int album_index = 0;
    private ArrayList<Song> random_songs;
    private Fragment random_fragmentFlashback;

    protected static final int SONG_FRAG = 0;
    protected static final int ALBUM_FRAG = 1;
    protected static final int FLASHBACK_FRAG = 2;

    protected boolean songLoaded;

    //default img
    byte[] default_album = new byte[100];

    //default setting when enter
    private SharedPreferences pre_setting;
    private SharedPreferences.Editor pre_editor;

    //like dislike status
    protected SharedPreferences like_setting;
    protected SharedPreferences.Editor like_editor;

    private int null_title_offset = 0;
    protected int album_dislike = 0;


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

        like_setting = getSharedPreferences("like_setting",MODE_PRIVATE);
        like_editor = like_setting.edit();

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        like_editor.apply();

        pre_setting = getSharedPreferences("pre_setting",MODE_PRIVATE);
        pre_editor = pre_setting.edit();

        try{
            frag = pre_setting.getInt("frag_mode",0);

        } catch (NullPointerException e){

        }

        //Log.d("Fragment mode", Integer.toString(frag));




        albumList = new ArrayList<>(albums.values()); // Used to pass into Parceble ArrayList

        /* Setting up all Listeners */

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if(songPlayingFrag == SONG_FRAG) {
                    mediaPlayer.reset();
                    if (index == res_uri.size() - 1)
                        index = 0;
                    else
                        index++;
                    loadMedia(songs.get(index));
                    mediaPlayer.start();
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else if (songPlayingFrag == ALBUM_FRAG) {
                    mediaPlayer.reset();
                    if (album_index == currAlbum.getSongs().size() - 1)
                        album_index = 0;
                    else
                        album_index++;
                    loadMedia(currAlbum.getSongs().get(index));
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
                        album_dislike = 0;
                        break;
                    case FLASHBACK_FRAG:
                        if(item.getItemId() != R.id.navigation_flashback) {
                            flash_index = 0;
                            mediaPlayer.reset();
                            loadMedia(songs.get(index));
                            transaction.remove(random_fragmentFlashback);
                            songPlayingFrag = SONG_FRAG;
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
                        //update UI
                        //transaction.remove(fragmentSong);
                        //setSongFragment();
                        //transaction.add(R.id.main_container,fragmentSong,"songs");

                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        frag = SONG_FRAG;


                        Log.i("currSong", Integer.toString(currSongIdx));
                        if (songLoaded) updateSongMetaData(currSongIdx, songPlayingFrag, false);
                        break;

                    case R.id.navigation_albums:
                        if (frag == FLASHBACK_FRAG) {
                            stopButton.setBackgroundResource(R.drawable.ic_stopping);
                        }
                        frag = ALBUM_FRAG;
                        prevButton.setVisibility(View.VISIBLE);
                        transaction.show(fragmentAlbums);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                        if (songLoaded) updateSongMetaData(currSongIdx, songPlayingFrag, false);
                        break;

                    case R.id.navigation_flashback:
                        if(frag != FLASHBACK_FRAG) {
                            mediaPlayer.reset();
                            frag = FLASHBACK_FRAG;

                            //loadMedia(random_songs.get(flash_index));
                            //mediaPlayer.start();


                            //transaction.remove(random_fragmentFlashback);

                            Collections.shuffle(random_songs);
                            random_setFlashbackFragment();

                            newSong(flash_index, FLASHBACK_FRAG,true,false);


                            prevButton.setVisibility(View.INVISIBLE);
                            stopButton.setBackgroundResource(R.drawable.ic_playing);

                            //transaction.remove(random_fragmentFlashback);

                            transaction.add(R.id.main_container, random_fragmentFlashback, "flash_songs");
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);


                        }
                        songPlayingFrag = FLASHBACK_FRAG;
                        break;
                }


                transaction.commit();

                transaction.addToBackStack(tag);

                return true;
            }
        });

        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.prev);
        stopButton = (Button) findViewById(R.id.play);



        //frag = 1;
        initialFragSetup(frag);






        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if(songPlayingFrag == SONG_FRAG) {
                    mediaPlayer.reset();
                    if (index == res_uri.size() - 1)
                        index = 0;
                    else
                        index++;
                    newSong(index, songPlayingFrag, true,true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else if (songPlayingFrag == ALBUM_FRAG) {
                    mediaPlayer.reset();
                    if (album_index == currAlbum.getSongs().size() - 1)
                        album_index = 0;
                    else
                        album_index++;
                    newSong(album_index, songPlayingFrag, true,true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else {
                    mediaPlayer.reset();
                    if (flash_index == res_uri.size() - 1)
                        flash_index = 0;
                    else
                        flash_index++;
                    newSong(flash_index, frag, true,true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if(songPlayingFrag == SONG_FRAG) {
                    mediaPlayer.reset();
                    if (index == 0)
                        index = res_uri.size() - 1;
                    else
                        index--;
                    newSong(index, songPlayingFrag, false,true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else if (songPlayingFrag == ALBUM_FRAG) {
                    mediaPlayer.reset();
                    if (album_index == 0)
                        album_index = currAlbum.getSongs().size() - 1;
                    else
                        album_index--;
                    newSong(album_index, songPlayingFrag, false,true);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLoaded = true;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    view.setBackgroundResource(R.drawable.ic_stopping);
                }
                else {
                    int currIdx = 0;

                    if (songPlayingFrag == SONG_FRAG) currIdx = index;
                    if (songPlayingFrag == ALBUM_FRAG) currIdx = album_index;
                    if (songPlayingFrag == FLASHBACK_FRAG) currIdx = flash_index;

                    newSong(currIdx, songPlayingFrag, false,true);
                    //updateSongMetaData(currIdx, frag);
                    view.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });

        //updateSongMetaData(currSongIdx,SONG_FRAG,false);



    }

    public void initialFragSetup(int frag) {
        setSongFragment();
        setAlbumFragment();
        //random_setFlashbackFragment();


        FragmentTransaction initTransaction = getSupportFragmentManager().beginTransaction();
        initTransaction.add(R.id.main_container, fragmentSong, "songs");
        initTransaction.addToBackStack("songs");
        initTransaction.add(R.id.main_container, fragmentAlbums, "albums");
        initTransaction.addToBackStack("albums");
        //initTransaction.add(R.id.main_container, random_fragmentFlashback, "flash_songs");
        //initTransaction.addToBackStack("flash_songs");
        if(frag == SONG_FRAG) {
            initTransaction.hide(fragmentAlbums);
            //initTransaction.hide(random_fragmentFlashback);

            bottomNavigationView.getMenu().getItem(SONG_FRAG).setChecked(true);

            loadMedia(songs.get(0));

        }
        else if (frag == ALBUM_FRAG){
            initTransaction.hide(fragmentSong);
            //initTransaction.hide(random_fragmentFlashback);
            bottomNavigationView.getMenu().getItem(ALBUM_FRAG).setChecked(true);

            loadMedia(songs.get(0));

        }
        else{
            //mediaPlayer.reset();
            initTransaction.hide(fragmentSong);
            initTransaction.hide(fragmentAlbums);


            //initTransaction.remove(random_fragmentFlashback);

            Collections.shuffle(random_songs);


            random_setFlashbackFragment();
            //loadMedia(random_songs.get(flash_index));
            //mediaPlayer.start();
            newSong(0,FLASHBACK_FRAG,true,false);

            prevButton.setVisibility(View.INVISIBLE);
            stopButton.setBackgroundResource(R.drawable.ic_playing);
            initTransaction.add(R.id.main_container, random_fragmentFlashback, "flash_songs");
            initTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            bottomNavigationView.getMenu().getItem(FLASHBACK_FRAG).setChecked(true);

        }
        initTransaction.commit();


    }

    public void playSelectedSong(Song s) {
        Uri uri = s.getUri();
        index = res_uri.indexOf(uri);
        stopButton.setBackgroundResource(R.drawable.ic_playing);
        currSongIdx = index;
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

    public void newSong(int index, int mode, boolean next, boolean update) {
        ArrayList<Song> songList = songs;
        songPlayingFrag = mode;
        currSongIdx = index;

        mediaPlayer.reset();

        if (mode == FLASHBACK_FRAG)
            songList = random_songs;
        if (mode == ALBUM_FRAG) {
            songList = (ArrayList) currAlbum.getSongs();
            if(album_dislike == songList.size()){
                currSong = null;
                //updateSongMetaData(index, mode, true);
                stopButton.setBackgroundResource(R.drawable.ic_stopping);
                return;
            }
        }

        currSong = songList.get(index);

        Log.d("like", currSong.getName() + " " +Integer.toString(currSong.getFavorite()));

        if (currSong.getFavorite() == -1) {
            //nextSong(next);
            if(next){
                switch (mode){
                    case SONG_FRAG:
                        if (this.index == res_uri.size() - 1)
                            this.index = 0;
                        else
                            this.index++;
                        newSong(this.index,mode,next,update);
                        break;
                    case ALBUM_FRAG:
                        if (album_index == currAlbum.getSongs().size() - 1)
                            album_index = 0;
                        else
                            album_index++;
                        album_dislike++;
                        newSong(this.album_index,mode,next,update);
                        break;
                    case FLASHBACK_FRAG:
                        if (this.flash_index == res_uri.size() - 1)
                            this.flash_index = 0;
                        else
                            this.flash_index++;
                        newSong(this.flash_index,mode,next,update);
                        break;


                }

            }
            else{
                switch (mode){
                case SONG_FRAG:
                    if (this.index == 0)
                        this.index = res_uri.size() - 1;
                    else
                        this.index--;

                    newSong(this.index,mode,next,update);
                    break;
                case ALBUM_FRAG:
                    if (this.album_index == 0)
                        this.album_index = currAlbum.getSongs().size() - 1;
                    else
                        this.album_index--;
                    album_dislike++;
                    newSong(this.album_index,mode,next,update);
                    break;
                case FLASHBACK_FRAG:
                    if (this.flash_index == 0)
                        this.flash_index = res_uri.size() - 1;
                    else
                        this.flash_index--;
                    newSong(this.flash_index,mode,next,update);
                    break;


            }
            }

            return;
        }

        album_dislike = 0;

        loadMedia(songList.get(index));
        mediaPlayer.start();
        if(update) {
            updateSongMetaData(index, mode, true);
        }
    }

    public void updateSongMetaData(int index, int mode, boolean songChange) {
        ArrayList<Song> songList = songs;

        if (mode == FLASHBACK_FRAG)
            songList = random_songs;
        if (mode == ALBUM_FRAG)
            songList = (ArrayList<Song>) currAlbum.getSongs();

        Song song  = songList.get(index);

        if (!songChange) {
            song = currSong;
        }

        //Log.i("currSong", song.getName());
        //Log.i("currSongfrag", Integer.toString(frag));



        if(song != null) {
            switch (frag) {
                case SONG_FRAG:
                    //SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().getFragments().get(0);
                    SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().findFragmentByTag("songs");

                    //Log.i("currSongfrag", fragmentSong.toString());


                    fragmentSong.updateSongUI(song);
                    break;
                case ALBUM_FRAG:
                    //AlbumsFragment fragmentAlbum = (AlbumsFragment)
                    //      getSupportFragmentManager().getFragments().get(1);
                    AlbumsFragment fragmentAlbum = (AlbumsFragment) getSupportFragmentManager().findFragmentByTag("albums");
                    fragmentAlbum.updateSongUI(song);
                    break;
                case FLASHBACK_FRAG:
                    //FlashbackFragment fragmentFlash = (FlashbackFragment)
                    //      getSupportFragmentManager().getFragments().get(2);
                    FlashbackFragment fragmentFlash = (FlashbackFragment) getSupportFragmentManager().findFragmentByTag("flash_songs");

                    fragmentFlash.updateSongUI(song);
                    break;
            }
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        pre_editor.putInt("frag_mode",frag);
        pre_editor.apply();
        like_editor.apply();
        //Log.d("Fragment mode", Integer.toString(frag));
        if (isChangingConfigurations() && mediaPlayer.isPlaying()) {
            ; // do nothing
        }
    }
    @Override
    public void onDestroy() {

        pre_editor.putInt("frag_mode",frag);
        pre_editor.apply();
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
            byte[] img;
            int liked = 0;
            if(retriever.getEmbeddedPicture() != null) {
                img = retriever.getEmbeddedPicture();
            }
            else{
                 img = default_album;
            }

            //edited
            if (title == null) { title = "No title found" + null_title_offset; }
            if (album == null) { album = "No album found"+ null_title_offset; }
            if (artist == null) {
                artist = "No artist found"+ null_title_offset;
                null_title_offset++;
            }

            Log.i("Raw Songs name: ", title+ "  album:"+ album+ "   artist  "+artist+" uri "+uri);

            if (albums.get(album)==null) {
                albums.put(album, new Album(album, artist,img));
            }

            Album a = albums.get(album);
            Song song = new Song(title, artist, uri, img, a.getName());

            liked = like_setting.getInt(song.name,-2);
            if(liked == -2){

                like_editor.putInt(song.name,0);
                like_editor.apply();
                Log.d("inital_like_set", Integer.toString(liked));

            }
            else{
                Log.d("load_like", Integer.toString(liked));
                song.setFavorite(liked);
            }


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

    public void nextSong(boolean next) {
        if (next)
            nextButton.performClick();
        else
            prevButton.performClick();
    }

    public List<Song> getSongs(){return songs;}

}

