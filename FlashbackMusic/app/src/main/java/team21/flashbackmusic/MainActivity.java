package team21.flashbackmusic;

//import android.app.Fragment;
import android.Manifest;
import android.app.DownloadManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
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
    private ArrayList<Song> sorted_songs;
    protected Fragment fragmentFlashback;

    protected static final int SONG_FRAG = 0;
    protected static final int ALBUM_FRAG = 1;
    protected static final int FLASHBACK_FRAG = 2;
    protected Location lastLocation;
    private int currentDay;
    private int currentHour;
    private Calendar calendar;

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
                        transaction.hide(fragmentAlbums);
                        album_dislike = 0;
                        break;
                    case FLASHBACK_FRAG:
                        if(item.getItemId() != R.id.navigation_flashback) {
                            flash_index = 0;
                            mediaPlayerWrapper.setSongs(songs);
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
                            mediaPlayerWrapper.setSongs(sorted_songs);
                            mediaPlayerWrapper.setIndex(0);
                            mediaPlayerWrapper.newSong(mediaPlayerWrapper.getIndex());

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
                stopButton.setBackgroundResource(R.drawable.ic_playing);
                mediaPlayerWrapper.next();
                Timestamp time = new Timestamp(System.currentTimeMillis());
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
                Timestamp time = new Timestamp(System.currentTimeMillis());
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
                Timestamp time = new Timestamp(System.currentTimeMillis());
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
                else
                    contentDownloadManager.updateList();


                Toast toast = Toast.makeText(MainActivity.this, contentDownloadManager.checkType()+" Download Complete", Toast.LENGTH_LONG);
                toast.show();


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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                /*|| ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[permissions.size()]),
                    1);
            Log.d("permission","requested");
            return;
        }
        else {
            lastLocation = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(locationProvider,0,200,locationListener);

            //Log.d("LastLocation",lastLocation.toString());

            String netLocation = LocationManager.NETWORK_PROVIDER;


            albumList = new ArrayList<Album>();

            try {
                if(isExternalStorageReadable()) {
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
        }

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
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }

                    lastLocation = locationManager.getLastKnownLocation(locationProvider);
                    locationManager.requestLocationUpdates(locationProvider,0,200,locationListener);

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

    public void startDownload(String url, String download_type){
        download_uri = url;
        if(download_type.equals("Song") ) {

            contentDownloadManager = new SongDownloadManager(this);
            Log.i("downloading type", "Song");
        }
        else{

            contentDownloadManager = new AlbumDownloadManager(this);
            Log.i("downloading type", "Album");

        }

        List<String> permissions = new ArrayList<String>();
        contentDownloadManager.download(url);
    }

    public void storeSong(String url, String name, String artist,Uri uri, byte[] img, String album){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        String ID = hashFunction(name + artist);

        myRef.child("TestSongs").child(ID).child("Title").setValue(name);
        myRef.child("TestSongs").child(ID).child("Url").setValue(url);
        myRef.child("TestSongs").child(ID).child("Artist").setValue(artist);
        myRef.child("TestSongs").child(ID).child("Uri").setValue(uri.toString());
        myRef.child("TestSongs").child(ID).child("Img").setValue(img.toString());
        myRef.child("TestSongs").child(ID).child("Album").setValue(album);
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

        Song song = songList.get(index);

        if (!songChange) {
            song = mediaPlayerWrapper.getSong();
        }

        if(song != null) {
            switch (frag) {
                case SONG_FRAG:
                    SongsFragment fragmentSong = (SongsFragment) getSupportFragmentManager().findFragmentByTag("songs");
                    fragmentSong.updateSongUI(song);
                    break;
                case ALBUM_FRAG:
                    AlbumsFragment fragmentAlbum = (AlbumsFragment) getSupportFragmentManager().findFragmentByTag("albums");
                    fragmentAlbum.updateSongUI(song);
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
        Play play;MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);
        }catch(RuntimeException e){

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
        Song song = new Song(hashFunction(title + artist),title, artist, uri, img, a.getName(), true);


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

        random_songs.add(song);
        if (download_uri!=null){
            storeSong(download_uri, title, artist, uri, img, a.getName());
            download_uri = null;
        }
    }




    private void setSongFragment() {
        fragmentSong = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        fragmentSong.setArguments(bundle);
    }

    protected void setFlashbackFragment() {

        fragmentFlashback = new FlashbackFragment();
        Bundle bundle = new Bundle();

        updateTime();
        sort_songs(songs, "plays",currentDay,currentHour, lastLocation);

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

   /* public void sort_songs(List<Song> songs,String prefName, int currentDay, int currentHour, Location location ) {

        SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        Play play;
        Gson gson = new Gson();
        boolean AnySongsPlayed = false;
        Location location_song = new Location(lastLocation);

        sorted_songs = new ArrayList<Song>();

        int sorted_song_index = 0;
        for(int i =0; i< songs.size();i++){
            if(songs.get(i).getFavorite() == -1)
                continue;

            String json = sharedPreferences.getString(songs.get(i).getName(), "");
            play = gson.fromJson(json, Play.class);
            if(!(songs.get(i).getTimeStamp().equals(new Timestamp(0))))
                AnySongsPlayed = true;


            sorted_songs.add(songs.get(i));
            if (play != null) {
                sorted_songs.get(sorted_song_index).setTimeStamp(play.getTime());
                List<Address> myList = new ArrayList<>();

                try {
                    Geocoder myLocation = new Geocoder(this, Locale.getDefault());
                    myList = myLocation.getFromLocation(play.getLatitude(), play.getLongitude(), 1);
                }catch (IOException e) {

                }

                Address address;

                try {
                    address = (Address) myList.get(0);
                }catch(IndexOutOfBoundsException e){

                    address = new Address(US);
                    address.setAddressLine(0,"Unknown");
                    address.setAddressLine(1,"Unknown");
                    address.setAddressLine(2,"Unknown");
                    address.setCountryName("Unknown");

                }



                sorted_songs.get(sorted_song_index).setLocation(address);
            }
            sorted_song_index++;
        }

        if(AnySongsPlayed == false){
            sorted_songs = new ArrayList<Song>();
            sorted_songs.add(new Song("","No songs played ever before"," Please play some songs", null, default_album,"and come back later"));
            return;
        }

        for (int i = 0; i < sorted_songs.size(); i++) {

            String name = sorted_songs.get(i).getName();
            String json = sharedPreferences.getString(name,"");
            play = gson.fromJson(json,Play.class);

            int score = 0;


            if (location != null) {
                Log.i("Raw Songs name: ",location.toString());
            }

            if(play != null) {
                location_song.setLatitude(play.getLatitude());
                location_song.setLongitude(play.getLongitude());
            }

            if(play != null && location != null && location_song.distanceTo(location)  < 304.8 ){
                score++;
            }

            if (play != null) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(play.getTime().getTime());
                c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                int dayofWeek = c.get(Calendar.DAY_OF_WEEK);
                int hour = c.get(Calendar.HOUR_OF_DAY);

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

    }*/

    public void sort_songs(final List<Song> songs, String prefName, final int currentDay, final int currentHour, final Location location) {
        Log.i("check","check");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Activity activity = this;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allPlays = dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<Song> sorted_songs = new ArrayList<>();
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("DataSnapshot", "");
        Log.i("datasnap2", json);
         allPlays = gson.fromJson(json, DataSnapshot.class);


        Location location_song = new Location(lastLocation);
        for (DataSnapshot song : allPlays.child("TestSongs").getChildren()){
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
                Uri uri = Uri.parse(song.child("Uri").getValue(String.class));
                byte[] img = song.child("Img").getValue(String.class).getBytes();
                sorted_songs.add(new Song(ID,title,artist,uri,img,album,false));
            }

        }
        for (int i = 0; i < sorted_songs.size(); i++) {

            String ID = sorted_songs.get(i).getID();


            int score = 0;


            if (location != null) {
                Log.i("Raw Songs name: ",location.toString());
            }

            if(allPlays.child(ID).exists()) {

                location_song.setLatitude(allPlays.child(ID).child("last_play_location").child("latitude").getValue(double.class));
                location_song.setLongitude(allPlays.child(ID).child("last_play_location").child("longitude").getValue(double.class));
            }

            if(allPlays.child(ID).exists() && location != null && location_song.distanceTo(location)  < 304.8 ){
                score++;
            }

            if (allPlays.child(ID).exists()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(allPlays.child(ID).child("last_play_time").getValue(long.class));
                c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                int dayofWeek = c.get(Calendar.DAY_OF_WEEK);
                int hour = c.get(Calendar.HOUR_OF_DAY);

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
                    Iterable<DataSnapshot> proxies = dataSnapshot.child("Proxy Test").getChildren();
                    for (DataSnapshot proxy : proxies) {
                        //FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Proxy").setValue(proxy.getValue(String.class));
                        myProxyName = proxy.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(myUserID).child("Proxy").setValue(proxy.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference().child("Users").child(myUserID).child("Username").setValue(myUserName);
                        ref.child("Proxy Test").child(proxy.getValue(String.class)).removeValue();
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
                    songTime.setText(getCurrentTime(new Timestamp(dataSnapshot.child("Plays").child(sName).child("last_play_time").getValue(long.class))));
                    //if (dataSnapshot.child("Songs").child(sName).child("last_play_user").getValue(String.class)
                    String user = dataSnapshot.child("Plays").child(sName).child("last_play_user").getValue(String.class);
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


}
