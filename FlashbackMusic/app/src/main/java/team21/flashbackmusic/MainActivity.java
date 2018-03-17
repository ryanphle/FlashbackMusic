package team21.flashbackmusic;

//import android.app.Fragment;
import android.Manifest;
import android.app.DownloadManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.DialogFragment;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import static java.util.Locale.CHINA;
import static java.util.Locale.JAPAN;
import static java.nio.file.StandardCopyOption.*;
import static java.util.Locale.US;


import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "TAG";

    private Map<String, Album> albums;
    private ArrayList<Album> albumList;
    protected ArrayList<Song> songs;
    private Iterable<DataSnapshot> databaseSongs;
    public DataSnapshot allPlays;

    protected Fragment fragmentSong;
    protected Fragment fragmentAlbums;
    protected FragmentManager fragmentManager;
    private FusedLocationProviderClient mFusedLocationClient;
    protected BottomNavigationView bottomNavigationView;
    private List<Uri> res_uri;

    private Map<String, Play> flashback_song;

    protected static int index = 0;
    protected MediaPlayer mediaPlayer;
    protected Button stopButton;
    protected Button prevButton;
    protected Button nextButton;
    protected static int frag = 0;
    protected static int songPlayingFrag = 0;
    protected static int currSongIdx = 0;
    protected static Album currAlbum;
    protected static Song currSong;

    protected static int flash_index = 0;
    protected static int album_index = 0;
    private ArrayList<Song> random_songs;
    private Fragment random_fragmentFlashback;
    protected ArrayList<Song> sorted_songs;
    protected Fragment fragmentFlashback;

    protected static final int SONG_FRAG = 0;
    public static final int PERMISSION_CONSTANT = 1;
    protected static final int ALBUM_FRAG = 1;
    protected static final int FLASHBACK_FRAG = 2;
    protected Location lastLocation;
    private int currentDay;
    private int currentHour;
    private Calendar calendar;
    private boolean firstFlash = true;

    protected boolean songLoaded;
    protected boolean songListEmpty;


    //default img
    byte[] default_album = new byte[100];

    //default setting when enter
    protected SharedPreferences pre_setting;
    protected SharedPreferences.Editor pre_editor;

    //like dislike status
    protected SharedPreferences like_setting;
    protected SharedPreferences.Editor like_editor;

    private int null_title_offset = 0;
    protected int album_dislike = 0;

    private BroadcastReceiver locationReceiver;

    private GetLocationService getLocationService;
    private boolean isBound;
    private boolean enterFlash = false;

    protected MediaPlayerWrapper mediaPlayerWrapper;

    private File Music;
    private File Download;

    //private DownloadManager downloadManager;
    private long downloadRef;
    private String download_uri;

    private Path source;
    private Path target;

    private ContentDownload contentDownloadManager;
    ProgressDialog mProgressDialog;

    private String myUserName;
    private String myUserID;
    private String myUserEmail;
    private String myProxyName;
    private List<Person> connections;
    private LocationManager locationManager;
    private String locationProvider;
    private LocationListener locationListener;

    private GoogleSignInAccount account;
    private SignInButton signIn;
    private GoogleSignInClient mGoogleSignInClient;
    private String authCode = "";

    private Location lastPlayLocation;
    private String lastPlayUser;
    private long lastPlayTime;
    private boolean isCustomTime;
    private Timestamp time;


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
        calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        like_setting = getSharedPreferences("like_setting", MODE_PRIVATE);
        like_editor = like_setting.edit();

        pre_setting = getSharedPreferences("pre_setting",MODE_PRIVATE);
        pre_editor = pre_setting.edit();

        try{
            frag = pre_setting.getInt("frag_mode",0);
        } catch (NullPointerException e){

        }

        albumList = new ArrayList<>(albums.values()); // Used to pass into Parceble ArrayList

        /* Setting up all Listeners */

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String tag = "";
                final android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                Log.i("CURR FRAG: ", Integer.toString(frag));
                switch(frag) {
                    case SONG_FRAG:
                        transaction.hide(fragmentSong);
                        Log.i("CURR FRAG: ", Integer.toString(frag));
                        break;
                    case ALBUM_FRAG:

                        Fragment songList;

                        songList = getSupportFragmentManager().findFragmentByTag("albumsongs");


                        if(songList != null){

                            Log.i("fragment",""+songList.toString());

                            transaction.remove(songList);
                            getSupportFragmentManager().popBackStackImmediate();

                            Log.i("fragment",""+"songlist removed");


                        }
                        else{

                            Log.i("fragment","songlist is null");

                        }

                        if(item.getItemId() != R.id.navigation_albums) {
                            transaction.hide(fragmentAlbums);
                        }
                        album_dislike = 0;
                        break;
                    case FLASHBACK_FRAG:
                        if(item.getItemId() != R.id.navigation_flashback) {
                            flash_index = 0;
                            mediaPlayerWrapper.setSongs(sorted_songs);
                            mediaPlayerWrapper.setIndex(-1);
                            mediaPlayerWrapper.next();
                            mediaPlayerWrapper.forcePause();
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
                        ((SongsFragment)fragmentSong).updateListView();

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
                        ((AlbumsFragment)fragmentAlbums).updateListView();

                        break;
                    case R.id.navigation_flashback:
                        if(frag != FLASHBACK_FRAG) {
                            frag = FLASHBACK_FRAG;

                            setFlashbackFragment();
                            //TODO: Uncomment if broken
                            //mediaPlayerWrapper.setSongs(sorted_songs);
                            //mediaPlayerWrapper.setIndex(0);
                            //mediaPlayerWrapper.newSong(mediaPlayerWrapper.getIndex());

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
                return true;
            }
        });

        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.prev);
        stopButton = (Button) findViewById(R.id.play);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.setBackgroundResource(R.drawable.ic_playing);
                mediaPlayerWrapper.next();

                currSong = mediaPlayerWrapper.getSong();

                Timestamp time = getTime();
                updateSongMetaData(mediaPlayerWrapper.getIndex(), songPlayingFrag, true);
                storePlayInformation(mediaPlayerWrapper.getSong(), lastLocation,
                        time);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.setBackgroundResource(R.drawable.ic_playing);
                mediaPlayerWrapper.prev();
                currSong = mediaPlayerWrapper.getSong();

                Timestamp time = getTime();

                updateSongMetaData(mediaPlayerWrapper.getIndex(), songPlayingFrag, true);
                storePlayInformation(mediaPlayerWrapper.getSong(), lastLocation,
                        time);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                songLoaded = true;
                if (mediaPlayerWrapper.isPlaying()) {
                    view.setBackgroundResource(R.drawable.ic_stopping);
                }
                else {
                    view.setBackgroundResource(R.drawable.ic_playing);
                }
                Timestamp time = getTime();
                updateSongMetaData(mediaPlayerWrapper.getIndex(), songPlayingFrag, false);
                storePlayInformation(mediaPlayerWrapper.getSong(), lastLocation,
                        time);
                mediaPlayerWrapper.stopAndStart();
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        final Context mainContext = this.getApplicationContext();

        final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                contentDownloadManager.checkStatus();

                if(songListEmpty){
                    contentDownloadManager.updateList();
                    songListEmpty = false;
                    mediaPlayerWrapper = new MediaPlayerWrapper(songs, mainContext,MainActivity.this);
                }
                else {
                    contentDownloadManager.updateList();
                }


                Toast toast = Toast.makeText(MainActivity.this, contentDownloadManager.checkType()+" Download Complete", Toast.LENGTH_LONG);
                toast.show();


                if (songPlayingFrag==FLASHBACK_FRAG && firstFlash) {
                    firstFlash = false;
                    mediaPlayerWrapper.setSongs(sorted_songs);
                    mediaPlayerWrapper.setIndex(0);
                    mediaPlayerWrapper.newSong(mediaPlayerWrapper.getIndex());
                    if (sorted_songs.size()>1 && !sorted_songs.get(1).isDownloaded()){
                        String type = fileExtension(sorted_songs.get(1).getUrl());
                        startDownload(sorted_songs.get(1).getUrl(),type);
                        sorted_songs.get(1).setIsDownloaded(true);
                    }
                }



                SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().findFragmentByTag("songs");
                AlbumsFragment albumFragment = (AlbumsFragment) getSupportFragmentManager().findFragmentByTag("albums");

                fragmentSong.updateListView();
                albumFragment.updateListView();
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = LocationManager.GPS_PROVIDER;

        List<String> permissions = new ArrayList<String>();

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        final Activity activity = this;
        signIn = findViewById(R.id.sign_in_button);



        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            // signed in. Show the "sign out" button and explanation.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_id))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/contacts"))
                    .requestServerAuthCode(getString(R.string.client_id), false)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
            signIn();
            signIn.setVisibility(View.GONE);
        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK");
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestScopes(new Scope("https://www.googleapis.com/auth/contacts"))
                        .requestServerAuthCode(getString(R.string.client_id), false)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                signIn();
            }
        });

       final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final GetDataListener dataListener = new GetDataListener() {
            ProgressDialog mProgressDialog;
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure() {
                Log.d("Failing: ", "FAILED");
            }
        };

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readData(ref,dataListener);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataListener.onFailure();
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                /*|| ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[permissions.size()]),
                    PERMISSION_CONSTANT);
            Log.d("permission","requested");
            return;
        }
        else {
            lastLocation = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(locationProvider,0,200,locationListener);
            //setUpFragAndMedia();
            readData(true);
        }
    }


    public void readData(final boolean beforeSetUp) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        readData(ref, new GetDataListener() {
            boolean firstSetup = beforeSetUp;
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                allPlays = dataSnapshot;
                Log.i("All plays: ", allPlays.toString());
                if(firstSetup ){
                    Log.i("setup","setup");
                    setUpFragAndMedia();
                    firstSetup = false;
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onStart() {
                Log.d("Starting: ", "STARTED");
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog((Activity)MainActivity.this);
                    mProgressDialog.setMessage("loading");
                    mProgressDialog.setIndeterminate(true);
                }

                mProgressDialog.show();
            }

            @Override
            public void onFailure() {
                Log.d("Failing: ", "FAILED");
            }
        });
    }



    public void readData(DatabaseReference ref, final GetDataListener listener) {
        listener.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("test", "test");
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }

                    lastLocation = locationManager.getLastKnownLocation(locationProvider);
                    locationManager.requestLocationUpdates(locationProvider,0,200,locationListener);
                    readData(false);
                    setUpFragAndMedia();


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setUpFragAndMedia() {
        albumList = new ArrayList<Album>();

        try {
            if (isExternalStorageReadable()) {
                Log.i("Enviroment", Environment.DIRECTORY_MUSIC);
                File rootpath = new File("storage/emulated/0/Music");
                loadSongs(rootpath);
            }

            Log.i("Oncreate", "Songs loaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        like_editor.apply();
        initialFragSetup(frag);

        if(songs.size() == 0)
            songListEmpty = true;
        else
            songListEmpty = false;

        if(!songListEmpty){
            if (frag == FLASHBACK_FRAG)
                mediaPlayerWrapper = new MediaPlayerWrapper(sorted_songs, this.getApplicationContext(), this);
            else
                mediaPlayerWrapper = new MediaPlayerWrapper(songs, this.getApplicationContext(), this);

            mediaPlayerWrapper.forcePause();
        }

        proxyGenerator();

        myUserName = getMyUserName();
        myUserID = getMyID();
        myUserEmail = getMyEmail();
    }

    public void startDownload(String url, String download_type){

        download_uri = url;

        Log.i("downloading url", url);

        /*
        new GetFileName(new GetFileName.GetFileNameListener() {
            @Override
            public void onTaskCompleted(String fileName) {
                Log.i("FileName", "real filename is " + fileName);
            }
        }).execute(url);
        */


        if(download_type.equals("Song") ) {

            contentDownloadManager = new SongDownloadManager(this);
            Log.i("downloading type", "Song");
        }
        else if(download_type.equals("Album") ) {

            contentDownloadManager = new AlbumDownloadManager(this);
            //contentDownloadManager = new UnknownContentDownload(this);
            Log.i("downloading type", "Album");
        }
        else{

            String fileextension = url.substring( url.lastIndexOf('.')+1,
                                                  url.lastIndexOf('?'));

            Log.i("downloading type extension", fileextension);


            if(fileextension.length() >= 5) {
                contentDownloadManager = new UnknownContentDownload(this);
                Log.i("downloading type", "Unknown");

            }
            else if(fileextension.equals("zip")){
                contentDownloadManager = new AlbumDownloadManager(this);
                Log.i("downloading type", "Album");

            }
            else if(fileextension.equals("mp3")||fileextension.equals("m4a")||fileextension.equals("flac")||fileextension.equals("ape")
                    ||fileextension.equals("aac")||fileextension.equals("m4p")||fileextension.equals("wav")||fileextension.equals("wma"))  {
                contentDownloadManager = new SongDownloadManager(this);
                Log.i("downloading type", "Song");
            }
            else{

                Log.i("downloading type check fail", fileextension);

                return;
            }

            //contentDownloadManager = new SongDownloadManager(this);


        }

        List<String> permissions = new ArrayList<String>();
        contentDownloadManager.download(url);
    }

    public void storeSong(String url, String name, String artist,Uri uri, byte[] img, String album){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        String ID = hashFunction(name + artist);

        myRef.child("Songs").child(ID).child("Title").setValue(name);
        myRef.child("Songs").child(ID).child("Url").setValue(url);
        myRef.child("Songs").child(ID).child("Artist").setValue(artist);
        myRef.child("Songs").child(ID).child("Uri").setValue(uri.toString());
        myRef.child("Songs").child(ID).child("Img").setValue(img.toString());
        myRef.child("Songs").child(ID).child("Album").setValue(album);
    }

    public void initialFragSetup(int frag) {
        setSongFragment();
        setAlbumFragment();

        FragmentTransaction initTransaction = getSupportFragmentManager().beginTransaction();
        initTransaction.add(R.id.main_container, fragmentSong, "songs");
        initTransaction.addToBackStack("songs");
        initTransaction.add(R.id.main_container, fragmentAlbums, "albums");
        initTransaction.addToBackStack("albums");

        if (frag == SONG_FRAG) {
            initTransaction.hide(fragmentAlbums);
            initTransaction.addToBackStack("songs");
            bottomNavigationView.getMenu().getItem(SONG_FRAG).setChecked(true);
        }
        else if (frag == ALBUM_FRAG){
            initTransaction.hide(fragmentSong);
            Log.i("InitFrag", "" + fragmentSong.isVisible());
            bottomNavigationView.getMenu().getItem(ALBUM_FRAG).setChecked(true);
            initTransaction.addToBackStack("albums");
        }
        else {
            initTransaction.hide(fragmentSong);
            initTransaction.hide(fragmentAlbums);
            setFlashbackFragment();

            prevButton.setVisibility(View.INVISIBLE);
            stopButton.setBackgroundResource(R.drawable.ic_playing);
            initTransaction.add(R.id.main_container, fragmentFlashback, "flash_songs");
            initTransaction.addToBackStack("albums");

            initTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            bottomNavigationView.getMenu().getItem(FLASHBACK_FRAG).setChecked(true);
        }
        initTransaction.commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public String getMyUserName() {
        String user = "";
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            user = account.getDisplayName();
        return user;
    }

    public String getMyEmail() {
        String user = "";
        int hash;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            user = account.getEmail();
        return user;
    }

    public String getMyID(){
        String user = "";
        int hash;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            user = account.getEmail();
            user = hashFunction(user);
        return user;
    }

    public String hashFunction(String email){
        String res;
        int hash;
        res = email.split("@")[0];
        hash = res.hashCode();
        res = Integer.toString(hash);
        return res;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            System.out.println("Checking account: " + account.toString());
            try {
                if (account == null) System.out.println("ACCOUNT NULL");
                authCode = account.getServerAuthCode();

                // TODO(developer): send code to server and exchange for access/refresh/ID tokens
                System.out.println("AUTHCODE " + authCode);
                new LoginRunner().execute();
                signIn.setVisibility(View.GONE);
            }
            catch (Exception e) {
                Log.w(TAG, "Sign-in failed", e);
            }

        }
        catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private class LoginRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                setUp();
            }
            catch (IOException e) {

            }
            return null;
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void setUp() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // Go to the Google API Console, open your application's
        // credentials page, and copy the client ID and client secret.
        // Then paste them into the following code.
        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);

        String serverClientId = clientId;

        String redirectUrl = getString(R.string.redirect_url);

        // Step 2: Exchange -->
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        httpTransport, jsonFactory, clientId, clientSecret, authCode, redirectUrl)
                        .execute();
        // End of Step 2 <--

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setFromTokenResponse(tokenResponse);

        PeopleService peopleService =
                new PeopleService.Builder(httpTransport, jsonFactory, credential).build();

        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();
        connections = response.getConnections();
        System.out.println("Size of connections: " + connections.size());
        System.out.println("Name of first person: " + connections.get(0).getNames().get(0).getDisplayName());
    }

    protected void showDownloadDialog() {
        DialogFragment downloadFragment = new DownloadFragment();
        downloadFragment.show(fragmentManager, "download");
    }

    protected void showTimeDialog() {
        DialogFragment timeFragment = new TimeSetterFragment();
        timeFragment.show(fragmentManager, "time_setter");
    }

    public void storePlayInformation(Song song, Location location, Timestamp time) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        if (location != null) {
            Play play = new Play(this, location.getLatitude(), location.getLongitude(), time);
            song.setTimeStamp(play.getTime());
        }
        FBSongInfo thisSong = new FBSongInfo(time.getTime(),location,myUserID);
        Address address = null;
        String addressStr = "";
        List<Address> myList = new ArrayList<>();

        try {
            Geocoder myLocation = new Geocoder(this, Locale.getDefault());
            myList = myLocation.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException e) {

        }

        address = myList.get(0);
        song.setLocation(address);

        addressStr += address.getAddressLine(0) + ", ";
        addressStr += address.getAddressLine(1) + ", ";
        addressStr += address.getAddressLine(2);
        myRef.child("Plays").child(song.getID()).setValue(thisSong);
        myRef.child("Plays").child(song.getID()).child("last_play_location_string").setValue(addressStr);
    }


    public void getPlayInfomation(final Song s) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Plays").exists() && dataSnapshot.child("Plays").child(s.getID()).exists()) {
                    lastPlayLocation = dataSnapshot.child("Plays").child(s.getID()).child("last_play_location").getValue(Location.class);
                    lastPlayTime = dataSnapshot.child("Plays").child(s.getID()).child("last_play_time").getValue(long.class);
                    lastPlayUser = dataSnapshot.child("Plays").child(s.getID()).child("last_play_user").getValue(String.class);
                    //FBSongInfo lastPlay = dataSnapshot.child("Songs").child(s.getName()).getValue(FBSongInfo.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG1", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void updateSongMetaData(int index, int mode, boolean songChange) {
        ArrayList<Song> songList = songs;

        if (mode == FLASHBACK_FRAG)
            songList = sorted_songs;
        if (mode == ALBUM_FRAG)
            songList = (ArrayList<Song>) currAlbum.getSongs();

        Log.i("fragment:","current Fragment"+mode);

        Song song = songList.get(index);

        if (!songChange) {
            song = mediaPlayerWrapper.getSong();
        }

        Log.i("UPdate Song Meta:",""+frag);
        Log.i("UPdate Song Meta:",""+song.getName());


        if(song != null) {
            switch (frag) {
                case SONG_FRAG:
                    SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().findFragmentByTag("songs");
                    fragmentSong.updateSongUI(song);
                    break;
                case ALBUM_FRAG:
                    SongsFragment fragmentAlbumSongs = (SongsFragment) getSupportFragmentManager().findFragmentByTag("albumsongs");
                    AlbumsFragment fragmentAlbum = (AlbumsFragment) getSupportFragmentManager().findFragmentByTag("albums");


                    if(fragmentAlbumSongs == null) {
                        fragmentAlbum.updateSongUI(song);
                        Log.i("Curr Song",song.getName());

                    }
                    else{

                        fragmentAlbum.updateSongUI(song);

                        Log.i("UPdate song frag",fragmentAlbumSongs.toString());

                        fragmentAlbumSongs.updateSongUI(song);

                    }
                    break;
                case FLASHBACK_FRAG:
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

        if (isChangingConfigurations() && mediaPlayerWrapper.isPlaying()) {
            ; // do nothing
        }
    }

    @Override
    public void onDestroy() {
        pre_editor.putInt("frag_mode",frag);
        pre_editor.apply();
        super.onDestroy();
        mediaPlayerWrapper.release();
    }

    protected void loadSongs(File path) throws IllegalArgumentException, IllegalAccessException {

        if(isExternalStorageReadable()){

            Music = path;

        }

        Log.i("File Path", Music.getAbsolutePath());

        File[] songFiles = Music.listFiles();

        SharedPreferences sharedPreferences = getSharedPreferences("plays", MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();
        loadListofFiles(songFiles, sharedPreferences, gson);
    }

    protected void loadListofFiles(File[] songFiles, SharedPreferences sharedPreferences, Gson gson) throws IllegalAccessException {
        for(int count=0; count < songFiles.length; count++){


            if(!songFiles[count].isDirectory()) {
                Uri uri = Uri.fromFile(songFiles[count]);

                addSong(sharedPreferences, gson, uri);
            }
            else{

                loadSongs(songFiles[count]);

            }
        }
    }

    protected void addSong(SharedPreferences sharedPreferences, Gson gson, Uri uri) {
        Play play;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);
        }catch(RuntimeException e){

           // Log.i("LoadSongs", "setDataSourcefail"+ uri.toString());
            return;

        }

        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] img;
        int liked = 0;
        if(retriever.getEmbeddedPicture() != null) {
            img = retriever.getEmbeddedPicture();
            Log.i("LoadSongs", "Album picture loaded");
        }
        else{
            img = default_album;
            Log.i("LoadSongs", "No album picture for this song");
        }

        if (title == null) { title = this.getFileName(uri); }
        if (album == null) { album = "Unknown Album";}
        if (artist == null) {
            artist = "Unknown Artist";
        }



        Log.i("Raw Songs name: ", title+ "  album:"+ album+ "   artist  "+artist+" uri "+uri);

        if (albums.get(album)==null) {
            albums.put(album, new Album(album, artist,img));
            albumList.add(albums.get(album));

        }

        Album a = albums.get(album);
        Song song = new Song(hashFunction(title + artist),title, artist, uri, img, a.getName(), true, null);


        String json = sharedPreferences.getString(title,"");
        play = gson.fromJson(json,Play.class);
        if(play != null){
            song.setTimeStamp(play.getTime());
            List<Address> mylist = new ArrayList<>();
            try{
                Geocoder mylocation = new Geocoder(this, Locale.getDefault());
                mylist = mylocation.getFromLocation(play.getLatitude(),play.getLongitude(),1);

            }catch (IOException e){}
        }


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
        if (!sorted_songs.isEmpty() && download_uri!=null){
            for (Song sorted_song : sorted_songs){
                if (download_uri.equals(sorted_song.getUrl())){
                    sorted_song.setUri(uri);

                }
            }
        }

        random_songs.add(song);
        if (download_uri!=null){
            storeSong(download_uri, title, artist, uri, img, a.getName());
            download_uri = null;
        }
    }


    protected SongsFragment setAlbumSongFragment(ArrayList<Song> songList,Album album) {
        SongsFragment songListFragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songList);
        bundle.putBoolean("isAlbum",true);
        bundle.putString("title",album.getName());
        songListFragment.setArguments(bundle);

        return songListFragment;

    }




    private void setSongFragment() {
        fragmentSong = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        bundle.putBoolean("isAlbum",false);
        fragmentSong.setArguments(bundle);
    }

    protected void setFlashbackFragment() {

        fragmentFlashback = new FlashbackFragment();
        final Context mainContext = this.getApplicationContext();

        updateTime();
        sort_songs(songs, lastLocation);

        while (!sorted_songs.get(0).isDownloaded()){
            String type = fileExtension(sorted_songs.get(0).getUrl());
            startDownload(sorted_songs.get(0).getUrl(),type);
            sorted_songs.get(0).setIsDownloaded(true);
        }

        new Download_Async(this,sorted_songs.get(0).getUrl(), fragmentFlashback).execute();


        if (songListEmpty){
            songListEmpty = false;
            mediaPlayerWrapper = new MediaPlayerWrapper(sorted_songs, mainContext,MainActivity.this);
        }


        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList("songs", sorted_songs);
        fragmentFlashback.setArguments(bundle);
    }

    private void updateTime(){
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
    }

    private void setAlbumFragment() {
        fragmentAlbums = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("albums", albumList);
        fragmentAlbums.setArguments(bundle);
    }

    public void nextSong(boolean next) {
        if (next)
            nextButton.performClick();
        else
            prevButton.performClick();
    }

    public List<Song> getSongs(){return songs;}

    public void sort_songs(final List<Song> songs, final Location location) {
        Log.i("check","check");
        //readData(false);


        sorted_songs = new ArrayList<>();
        Location location_song = new Location(lastLocation);
        for (DataSnapshot song : allPlays.child("Songs").getChildren()){
            boolean added = false;
            String ID = song.getKey();
            for (Song existingSong : songs){
                if (existingSong.getID().equals(ID)){
                    sorted_songs.add(existingSong);
                    added = true;
                    break;
                }
            }
            if (!added){
                String title = song.child("Title").getValue(String.class);
                String artist = song.child("Artist").getValue(String.class);
                String album = song.child("Album").getValue(String.class);
                String url = song.child("Url").getValue(String.class);
                byte[] img = song.child("Img").getValue(String.class).getBytes();
                sorted_songs.add(new Song(ID,title,artist,null,img,album,false,url));
            }

        }
        Log.i("Sorted songs", Integer.toString(sorted_songs.size()));
        for (int i = 0; i < sorted_songs.size(); i++) {

            String ID = sorted_songs.get(i).getID();


            int score = 0;


            if (location != null) {
                Log.i("Raw Songs name: ", location.toString());
            }

            if (allPlays.child(ID).exists()) {

                location_song.setLatitude(allPlays.child("Plays").child(ID).child("last_play_location").child("latitude").getValue(double.class));
                location_song.setLongitude(allPlays.child("Plays").child(ID).child("last_play_location").child("longitude").getValue(double.class));
            }

            if(allPlays.child("Plays").child(ID).exists() && location != null && location_song.distanceTo(location)  < 304.8 ){
                score+=12;
            }

            if (allPlays.child("Plays").child(ID).exists()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(allPlays.child("Plays").child(ID).child("last_play_time").getValue(long.class));
                c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                Calendar cal = Calendar.getInstance();
                if (cal.getTimeInMillis() <= c.getTimeInMillis() + 604800000) {
                    score += 11;
                }
            }
            String user = allPlays.child("Plays").child(ID).child("last_play_user").getValue(String.class);
            if (allPlays.child("Plays").child(ID).exists()) {
                if (isFriend(user,connections)){score+=10;}
            }





            sorted_songs.get(i).setScore(score);

            Log.i("Raw Songs name: ", sorted_songs.get(i).getName() + " score " + sorted_songs.get(i).getScore());
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

    }



    public List<Song> getSortedSongs(){
        return sorted_songs;
    }
    public void proxyGenerator() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        //userID = UUID.randomUUID().toString();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(myUserID).exists()){
                    myProxyName = dataSnapshot.child("Users").child(myUserID).child("Proxy").getValue(String.class);
                } else {
                    Iterable<DataSnapshot> proxies = dataSnapshot.child("Proxy").getChildren();
                    for (DataSnapshot proxy : proxies) {
                        //FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Proxy").setValue(proxy.getValue(String.class));
                        myProxyName = proxy.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(myUserID).child("Proxy").setValue(proxy.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference().child("Users").child(myUserID).child("Username").setValue(myUserName);
                        ref.child("Proxy").child(proxy.getValue(String.class)).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });
    }
    public String getProxy(){
        return myProxyName;
    }
    public void setProxy(String proxy){
        myProxyName = proxy;
    }


    public void setData(final TextView songLocation, final TextView songTime, final TextView lastPlayedBy, final String sName){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Plays").exists() && dataSnapshot.child("Plays").child(sName).child("last_play_time").exists()) {
                    songLocation.setText(dataSnapshot.child("Plays").child(sName).child("last_play_location_string").getValue(String.class));
                    songTime.setText(getCurrentTime((new Timestamp(dataSnapshot.child("Plays").child(sName).child("last_play_time").getValue(long.class)))));
                    String user = dataSnapshot.child("Plays").child(sName).child("last_play_user").getValue(String.class);
                    Log.i("connection_user",user);
                    boolean isFriend = isFriend(user, connections);

                    if (isFriend){
                        lastPlayedBy.setText("Last played by: " + dataSnapshot.child("Users").child(user).child("Username").getValue(String.class));
                    }
                    else if (myUserID.equals(user) && myUserID!=null) {
                        lastPlayedBy.setText("Last played by: you");
                    }
                    else {
                        lastPlayedBy.setText("Last played by: " + dataSnapshot.child("Users").child(user).child("Proxy").getValue(String.class));
                    }
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

    protected boolean isFriend(String user, List<Person> connections) {
        boolean isFriend = false;
        if (connections!=null){
            for (Person connection : connections) {
                for (EmailAddress address : connection.getEmailAddresses()){
                    if (hashFunction(address.getValue()).equals(user)){
                        isFriend = true;
                    }
                }

            }
        }

        return isFriend;
    }

    public String getCurrentTime(Timestamp time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.getTime());
        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        String currentTime = calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        return currentTime;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public List<Person> getTestConnections() {
        return connections;
    }

    protected Timestamp getTime() {
        if (!isCustomTime)
            return new Timestamp(System.currentTimeMillis());
        else
            return this.time;
    }

    protected boolean isCustomTime() {
        return isCustomTime;
    }

    protected void setCustomTime(Timestamp time) {
        isCustomTime = true;
        this.time = time;
    }

    protected void resetTime() {
        isCustomTime = false;
        time = new Timestamp(System.currentTimeMillis());
    }

    protected String fileExtension(String url){

        String fileextension = url.substring( url.lastIndexOf('.')+1,
                url.length() );
        String type;

        Log.i("Song extension",fileextension);


        if(fileextension.equals("zip")){
            type = "Album";

        }
        else if(fileextension.equals("mp3")||fileextension.equals("m4a")||fileextension.equals("flac")||fileextension.equals("ape")
                ||fileextension.equals("aac")||fileextension.equals("m4p")||fileextension.equals("wav")||fileextension.equals("wma"))  {

            type = "Song";
        }
        else{

            type = "Unknown";
        }



        return type;
    }


}

