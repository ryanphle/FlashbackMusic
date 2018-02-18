package team21.flashbackmusic;

//import android.app.Fragment;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;

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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.LocalBroadcastManager;

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


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Map<String,Album> albums;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songs;
    private Fragment fragmentSong;
    private Fragment fragmentAlbums;
    private FragmentManager fragmentManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private BottomNavigationView bottomNavigationView;
    private List<Uri> res_uri;


    private Map<String, Play> flashback_song;
    private Location currentLocation;
    private FusedLocationProviderClient myFusedLocationClient;

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
    private ArrayList<Song> sorted_songs;
    private Fragment fragmentFlashback;

    protected static final int SONG_FRAG = 0;
    protected static final int ALBUM_FRAG = 1;
    protected static final int FLASHBACK_FRAG = 2;
    private Location lastLocation;


    //public Location lastLocation;
    //BroadcastReceiver locationReceiver;
    Intent locationIntent;


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

    //public Location lastLocation;

    private BroadcastReceiver locationReceiver;

    private GetLocationService getLocationService;
    private boolean isBound;
    private boolean enterFlash = false;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_uri = new ArrayList<>();
        random_songs = new ArrayList<>();

        flashback_song = new HashMap<>();
        sorted_songs = new ArrayList<>();
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

        Intent intent = new Intent(this,GetLocationService.class);
        bindService(intent, serviceConnection,Context.BIND_AUTO_CREATE);



        albumList = new ArrayList<>(albums.values()); // Used to pass into Parceble ArrayList
        //initialFragSetup(frag);

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
                            transaction.remove(fragmentFlashback);
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
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        frag = SONG_FRAG;
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

                            //newSong(flash_index, FLASHBACK_FRAG,true,false);

                            //transaction.remove(random_fragmentFlashback);

                            //Collections.shuffle(random_songs);

                            setFlashbackFragment();

                            newSong(flash_index, FLASHBACK_FRAG,true,false);


                            prevButton.setVisibility(View.INVISIBLE);
                            stopButton.setBackgroundResource(R.drawable.ic_playing);
                            transaction.add(R.id.main_container, fragmentFlashback, "flash_songs");
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                        }
                        frag = FLASHBACK_FRAG;
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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            //return;
        }


        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getBundleExtra("Location");
                lastLocation = (Location) b.getParcelable("Location");
                Song song = (Song)b.getParcelable("Song");
                if(!enterFlash) {
                    storePlayInformation(song);
                    //Log.i("RawMainActivity ", "  location in main : " + lastLocation.toString());
                }
                else{
                    enterFlash = false;
                    //Log.i("Sortgetlocation ", "  location : " + lastLocation.toString());
                    initialFragSetup(frag);

                    //sort_songs();
                }
            }
        };

        //while(getLocationService == null){}








        /*
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationReceiver, new IntentFilter("LastLocation")
        );
        //updateSongMetaData(currSongIdx,SONG_FRAG,false);
         */

    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GetLocationService.locationService locationservice = (GetLocationService.locationService) iBinder;
            getLocationService = locationservice.getService();
            isBound = true;

            if(frag == FLASHBACK_FRAG) {
                enterFlash = true;

                getLocationService.getLocation(songs.get(0));

                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                        locationReceiver, new IntentFilter("LastLocation")
                );
            }
            else{
                initialFragSetup(frag);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            isBound = false;

        }
    };


    public void initialFragSetup(int frag) {
        setSongFragment();
        setAlbumFragment();

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

            //Collections.shuffle(random_songs);


            setFlashbackFragment();
            //loadMedia(random_songs.get(flash_index));
            //mediaPlayer.start();
            newSong(0,FLASHBACK_FRAG,true,false);

            prevButton.setVisibility(View.INVISIBLE);
            stopButton.setBackgroundResource(R.drawable.ic_playing);
            initTransaction.add(R.id.main_container, fragmentFlashback, "flash_songs");
            initTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            bottomNavigationView.getMenu().getItem(FLASHBACK_FRAG).setChecked(true);

        }
        initTransaction.commit();


    }

    public void playSelectedSong(Song s) {
        Uri uri = s.getUri();
        index = songs.indexOf(s);
        stopButton.setBackgroundResource(R.drawable.ic_playing);
        currSongIdx = index;
        mediaPlayer.reset();
        loadMedia(s);

        /*
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationReceiver, new IntentFilter("LastLocation")
        );
        */

        startLocationService(s);

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

    private void startLocationService(Song song) {

        /*Intent intent = new Intent(MainActivity.this, LocationService.class);
        Bundle b = new Bundle();
        b.putParcelable("Song", song);
        intent.putExtra("Song",b);
        startService(intent);*/

        getLocationService.getLocation(song);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationReceiver, new IntentFilter("LastLocation")
        );


    }


    private void storePlayInformation(Song song){


        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(lastLocation != null) {
            Play play = new Play(this, lastLocation);
            song.setTimeStamp(play.getTime());

            List<Address> myList = new ArrayList<>();

            try{

                Geocoder myLocation = new Geocoder(this, Locale.getDefault());
                myList = myLocation.getFromLocation(play.getLocation().getLatitude(), play.getLocation().getLongitude(),1);

            }
            catch( IOException e) {

            }

            Address address = (Address) myList.get(0);
            song.setLocation(address);

            Gson gson = new Gson();
            String json = gson.toJson(play);
            editor.putString(song.getName(), json);
            editor.apply();

            String json2 = getSharedPreferences("plays", MODE_PRIVATE).getString(song.getName(), "");
            Play samePlay = gson.fromJson(json2, Play.class);
            //System.out.print("time: " + samePlay.getTime().getTime() + " time of day: " + samePlay.getTimeOfDay());
        }

    }




    //public void newSong(int index, int mode) {}
    public void newSong(int index, int mode, boolean next, boolean update) {
        ArrayList<Song> songList = songs;
        songPlayingFrag = mode;
        currSongIdx = index;

        mediaPlayer.reset();

        if (mode == FLASHBACK_FRAG){
            songList = sorted_songs;
        }
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
        startLocationService(songList.get(index));
        mediaPlayer.start();
        if(update) {
            updateSongMetaData(index, mode, true);
        }
    }

    public void updateSongMetaData(int index, int mode, boolean songChange) {
        ArrayList<Song> songList = songs;

        if (mode == FLASHBACK_FRAG)
            songList = sorted_songs;
        if (mode == ALBUM_FRAG)
            songList = (ArrayList<Song>) currAlbum.getSongs();

        Song song  = songList.get(index);

        if (!songChange) {
            song = currSong;
        }

        //Log.i("currSong", currSong.getName());



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
        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();

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


            String json = sharedPreferences.getString(title,"");
            play = gson.fromJson(json,Play.class);
            song.setTimeStamp(play.getTime());


            liked = like_setting.getInt(song.getName(),-2);
            if(liked == -2){

                like_editor.putInt(song.getName(),0);
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



        //sort_getLocation();
        sort_songs();
        //ArrayList<Song> sorted_songs = sort_songs(getSharedPreferences("play", 0));

        bundle.putParcelableArrayList("songs", sorted_songs);
        fragmentFlashback.setArguments(bundle);
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


    public void sort_getLocation(){

        enterFlash = true;

        getLocationService.getLocation(songs.get(0));

    }


    public void sort_songs() {

        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();
        boolean AnySongsPlayed = false;

        sorted_songs = new ArrayList<Song>();

        for(int i =0; i< songs.size();i++){
            if(songs.get(i).getFavorite() == -1)
                continue;

            if(!(songs.get(i).getTimeStamp().equals(new Timestamp(0))))
                AnySongsPlayed = true;

            sorted_songs.add(songs.get(i));
        }

        if(AnySongsPlayed == false){
            sorted_songs = new ArrayList<Song>();
            //sorted_songs.add(new Song("No songs played ever before"," Please play some songs", null, default_album,"See you later"));
            return;
        }

        for (int i = 0; i < sorted_songs.size(); i++) {

            String name = sorted_songs.get(i).getName();
            String json = sharedPreferences.getString(name,"");
            play = gson.fromJson(json,Play.class);

            int score = 0;

            //Log.i("Raw Songs name: ",lastLocation.toString());

            //Log.i("Raw Songs name: ", play.getLocation().toString());
            // 304.8 m = 1000 foot
            if(play != null && lastLocation != null && play.getLocation().distanceTo(lastLocation)  < 304.8 ){
                score++;
            }

            //sorted_songs.get(i).setTimeStamp(play.getTime());
            //Timestamp tsTemp = songs.get(i).getTimeStamp();
            if (play != null) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(play.getTime().getTime());
                c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                int dayofWeek = c.get(Calendar.DAY_OF_WEEK);
                int hour = c.get(Calendar.HOUR_OF_DAY);

                c.setTimeInMillis(System.currentTimeMillis());
                int currentDay = c.get(Calendar.DAY_OF_WEEK);
                int currentHour = c.get(Calendar.HOUR_OF_DAY);
                if (hour <= 10 && hour > 2 && currentHour <= 10 && currentHour > 2) {
                    score++;
                } else if (hour <= 18 && hour > 10 && currentHour <= 18 && currentHour > 10) {
                    score++;
                } else if ((hour <= 2 && hour > 18) && (currentHour <= 2 && currentHour > 18)) {
                    score++;
                }
                if (dayofWeek == currentDay) {
                    score++;
                }

            }
            sorted_songs.get(i).setScore(score);

            Log.i("Raw Songs name: ", sorted_songs.get(i).getName()+ " score "+ sorted_songs.get(i).getScore());

        }

        Collections.sort(sorted_songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                if (lhs.getScore() > rhs.getScore())
                    return -1;
                if (lhs.getScore() < rhs.getScore())
                    return 1;
                if (lhs.getFavorite() > rhs.getFavorite())
                    return  -1;
                if (lhs.getFavorite() < rhs.getFavorite())
                    return 1;
                if (lhs.getTimeStamp().after( rhs.getTimeStamp())){
                    return -1;
                }
                if (lhs.getTimeStamp().before( rhs.getTimeStamp())){
                    return 1;
                }
                return 0;
            }
        });
        //return sorted_songs;
    }

}
