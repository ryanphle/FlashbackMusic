package team21.flashbackmusic;

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

    private TextView mTextMessage;
    private Map<String,Album> albums;
    private List<Song> songs;
    private TextView text;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_songs);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_albums);
                    return true;
            }
            return false;
        }
    };

    String[] dummySongs = {"song1", "song2", "song3", "song4", "song5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        albums = new HashMap<>();
        songs = new ArrayList<>();

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        //mTextMessage.setText(songs.get(0).getName());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, dummySongs);

        ListView listView = (ListView) findViewById(R.id.song_list);
        listView.setAdapter(adapter);
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
            Song song = new Song(fields[count].getName(), artist,img);
            albums.get(album).addSong(song);
            songs.add(song);
        }
    }
}
