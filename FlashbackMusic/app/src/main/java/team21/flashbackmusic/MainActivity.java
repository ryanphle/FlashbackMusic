package team21.flashbackmusic;

//import android.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
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
    private Fragment fragmentFlashback;
    private FragmentManager fragmentManager;
    private transient FusedLocationProviderClient mFusedLocationClient;
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
    private ArrayList<Song> sorted_songs;
    private Fragment fragment;
    
    protected static final int SONG_FRAG = 0;
    protected static final int ALBUM_FRAG = 1;
    protected static final int FLASHBACK_FRAG = 2;
    private Location lastLocation;
    
    
    protected boolean songLoaded;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_uri = new ArrayList<>();
        flashback_song = new HashMap<>();
        sorted_songs = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        songLoaded = false;
        
        
        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //loadMedia(songs.get(index));
        albumList = new ArrayList<>(albums.values()); // Used to pass into Parceble ArrayList
        initialFragSetup();
        
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
                        break;
                    case FLASHBACK_FRAG:
                        if(item.getItemId() != R.id.navigation_flashback) {
                            flash_index = 0;
                            mediaPlayer.reset();
                            loadMedia(songs.get(index));
                            transaction.remove(fragment);
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
                        if (songLoaded) updateSongMetaData(currSongIdx, songPlayingFrag, false);
                        break;
                        
                    case R.id.navigation_flashback:
                        if(frag != FLASHBACK_FRAG) {
                            mediaPlayer.reset();
                            //Collections.shuffle(random_songs);
                            setFlashbackFragment();
                            loadMedia(sorted_songs.get(flash_index));
                            mediaPlayer.start();
                            
                            prevButton.setVisibility(View.INVISIBLE);
                            stopButton.setBackgroundResource(R.drawable.ic_playing);
                            transaction.add(R.id.main_container, fragment, "flash_songs");
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
                    newSong(index, songPlayingFrag);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else if (songPlayingFrag == ALBUM_FRAG) {
                    mediaPlayer.reset();
                    if (album_index == currAlbum.getSongs().size() - 1)
                        album_index = 0;
                    else
                        album_index++;
                    newSong(album_index, songPlayingFrag);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else {
                    mediaPlayer.reset();
                    if (flash_index == res_uri.size() - 1)
                        flash_index = 0;
                    else
                        flash_index++;
                    newSong(flash_index, frag);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });
        
        prevButton = (Button) findViewById(R.id.prev);
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
                    newSong(index, songPlayingFrag);
                    stopButton.setBackgroundResource(R.drawable.ic_playing);
                }
                else if (songPlayingFrag == ALBUM_FRAG) {
                    mediaPlayer.reset();
                    if (album_index == 0)
                        album_index = currAlbum.getSongs().size() - 1;
                    else
                        album_index--;
                    newSong(album_index, songPlayingFrag);
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
                    
                    int currIdx = 0;
                    
                    if (songPlayingFrag == SONG_FRAG) currIdx = index;
                    if (songPlayingFrag == ALBUM_FRAG) currIdx = album_index;
                    if (songPlayingFrag == FLASHBACK_FRAG) currIdx = flash_index;
                    
                    newSong(currIdx, songPlayingFrag);
                    //updateSongMetaData(currIdx, frag);
                    view.setBackgroundResource(R.drawable.ic_playing);
                }
            }
        });
        
        
        BroadcastReceiver locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getBundleExtra("Location");
                lastLocation = (Location) b.getParcelable("Location");
                Log.i("Raw MainActivity ", "  location in main : "+ lastLocation.toString());
                
                
            }
        };
        
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
        
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter("LastLocation"));
        
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
        currSongIdx = index;
        mediaPlayer.reset();
        loadMedia(s);
        mediaPlayer.start();
        storePlayInformation(s);
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
    
    
    private void storePlayInformation(Song song){
        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Play play = new Play(this, lastLocation);
        song.setTimeStamp(play.getTime());
        List<Address> myList = new ArrayList<>();

        try {
            Geocoder myLocation = new Geocoder(this, Locale.getDefault());
            myList = myLocation.getFromLocation(play.getLocation().getLatitude(), play.getLocation().getLongitude(), 1);
        }catch (IOException e) {

        }
        Address address = (Address) myList.get(0);
        song.setLocation(address);

        Gson gson = new Gson();
        String json = gson.toJson(play);
        editor.putString(song.getName(), json);
        editor.apply();
        
        String json2 = getSharedPreferences("plays", MODE_PRIVATE).getString(song.getName(),"");
        Play samePlay = gson.fromJson(json2, Play.class);
        System.out.print("time: "+samePlay.getTime().getTime()+" time of day: "+samePlay.getTimeOfDay());
        
    }
    
    public void newSong(int index, int mode) {
        ArrayList<Song> songList = songs;
        songPlayingFrag = mode;
        currSongIdx = index;
        
        if (mode == FLASHBACK_FRAG)
            songList = sorted_songs;
        if (mode == ALBUM_FRAG) {
            songList = (ArrayList) currAlbum.getSongs();
        }
        
        currSong = songList.get(index);
        
        loadMedia(songList.get(index));
        mediaPlayer.start();
        updateSongMetaData(index, mode, true);
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
            
            //random_songs.add(song);
        }
    }
    
    private void setSongFragment() {
        fragmentSong = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragmentSong.setArguments(bundle);
    }
    
    private void setFlashbackFragment() {
        
        fragment = new FlashbackFragment();
        Bundle bundle = new Bundle();
        
        sort_songs();
        //ArrayList<Song> sorted_songs = sort_songs(getSharedPreferences("play", 0));
        
        bundle.putParcelableArrayList("songs", sorted_songs);
        fragment.setArguments(bundle);
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
    
    
    public void sort_songs() {
        
        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();
        
        //ArrayList<Song> new_songs_list = new ArrayList<Song>();
        //Map<String, ?> allEntries = sharedPreferences.getAll();
        
        /*
         public ArrayList<Song> sort_songs(SharedPreferences sharedPreferences) {
         Gson gson = new Gson();
         ArrayList<Song> sorted_songs = new ArrayList<Song>();
         Map<String, ?> allEntries = sharedPreferences.getAll();
         for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
         String json = sharedPreferences.getString(entry.getKey(), "");
         flashback_song.put(entry.getKey(), gson.fromJson(json, Play.class));
         }*/
        sorted_songs = new ArrayList<Song>();
        
        
        for (int i = 0; i < songs.size(); i++) {
            if(songs.get(i).getFavorite() == -1)
                continue;

            sorted_songs.add(songs.get(i));
            String name = songs.get(i).getName();
            String json = sharedPreferences.getString(name,"");
            play = gson.fromJson(json,Play.class);
            
            int score = 0;
            
            Log.i("Raw Songs name: ",lastLocation.toString());

            //Log.i("Raw Songs name: ", play.getLocation().toString());
            // 304.8 m = 1000 foot
            if(play != null && play.getLocation().distanceTo(lastLocation)  < 304.8 ){
                score++;
            }
            
            //songs.get(i).setTimeStamp(play.getTime());
            //Timestamp tsTemp = new Timestamp(songs.get(i).getTimeStemp());
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
