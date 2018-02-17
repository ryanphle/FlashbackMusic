package team21.flashbackmusic;

//import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.support.v4.content.ContextCompat;
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
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Map<String,Album> albums;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songs;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private transient FusedLocationProviderClient mFusedLocationClient;
    private BottomNavigationView bottomNavigationView;
    private List<Uri> res_uri;
    private static int index = 0;
    private MediaPlayer mediaPlayer;
    private Button stopButton;
    private Map<String, Play> flashback_song;
    private Location currentLocation;
    private FusedLocationProviderClient myFusedLocationClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_uri = new ArrayList<>();
        flashback_song = new HashMap<>();


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
                        setFlashbackFragment();
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
        storePlayInformation(s);
    }

    public void loadMedia(Uri uri) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();

        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }


    private void storePlayInformation(Song song){
        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Play play = new Play(this);
        Gson gson = new Gson();
        String json = gson.toJson(play);
        editor.putString(song.getName(), json);
        editor.apply();

        String json2 = getSharedPreferences("plays", MODE_PRIVATE).getString(song.getName(),"");
        Play samePlay = gson.fromJson(json2, Play.class);
        System.out.print("time: "+samePlay.getTime().getTime()+" time of day: "+samePlay.getTimeOfDay());

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
        fragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragment.setArguments(bundle);
    }

    private void setFlashbackFragment() {

        fragment = new FlashbackFragment();
        Bundle bundle = new Bundle();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "Permission Denied for location", Toast.LENGTH_SHORT).show();
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation = location;
                        }
                    }
                });

        ArrayList<Song> sorted_songs = sort_songs();

        bundle.putParcelableArrayList("songs", sorted_songs);
        fragment.setArguments(bundle);
    }

    private void setAlbumFragment() {
        fragment = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("albums", albumList);
        fragment.setArguments(bundle);
    }

    public List<Song> getSongs(){return songs;}


    public ArrayList<Song> sort_songs() {

        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();

        ArrayList<Song> sorted_songs = new ArrayList<Song>();
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



        for (int i = 0; i < songs.size(); i++) {
            if(songs.get(i).getFavorite() == -1)
                continue;
            sorted_songs.add(songs.get(i));
            String name = sorted_songs.get(i).getName();
            String json = sharedPreferences.getString(name,"");
            play = gson.fromJson(json,Play.class);

            int score = 0;

            Log.i("Raw Songs name: ",currentLocation.toString());
            Log.i("Raw Songs name: ", play.getLocation().toString());
            // 304.8 m = 1000 foot
            if(play.getLocation().distanceTo(currentLocation)  < 304.8 ){

                score++;
            }

            songs.get(i).setTimeStamp(play.getTime());
            //Timestamp tsTemp = new Timestamp(songs.get(i).getTimeStemp());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(play.getTime().getTime());
            c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            int dayofWeek = c.get(Calendar.DAY_OF_WEEK);
            int hour = c.get(Calendar.HOUR_OF_DAY);

            c.setTimeInMillis(System.currentTimeMillis());
            int currentDay = c.get(Calendar.DAY_OF_WEEK);
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            if( hour <= 10 && hour > 2 && currentHour <= 10 && currentHour > 2){
                score++;
            }
            else if( hour <= 18 && hour > 10 && currentHour <= 18 && currentHour > 10){
                score++;
            }
            else if((hour <= 2 && hour > 18) && (currentHour <= 2 && currentHour > 18)){
                score++;
            }
            if( dayofWeek == currentDay){
                score++;
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
        return sorted_songs;
    }

}

