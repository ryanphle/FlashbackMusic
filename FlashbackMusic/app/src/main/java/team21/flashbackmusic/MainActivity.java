package team21.flashbackmusic;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private static final int MEDIA_RES_ID = R.raw.xzq;
    private Map<String,Album> albums;
    private List<Song> songs;
    private List<Integer> res_ids;
    private static int index = 0;

    public void loadMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(res_ids.get(index));
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albums = new HashMap<>();
        songs = new ArrayList<>();
        res_ids = new ArrayList<>();

        setContentView(R.layout.activity_main);
        try {
            loadSongs();
        }catch(Exception e){
            e.printStackTrace();
        }

        Button prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == 0)
                    index = res_ids.size() - 1;
                else
                    index--;
                loadMedia();
                mediaPlayer.start();
            }
        });
        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                if (index == res_ids.size() - 1)
                    index = 0;
                else
                    index++;
                loadMedia();
                mediaPlayer.start();
            }
        });
        Button playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMedia();
                mediaPlayer.start();
            }
        });
        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

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
                albums.put(album, new Album(album, artist, img));
            }
            Song song = new Song(fields[count].getName(), artist, img);
            albums.get(album).addSong(song);
            songs.add(song);
            res_ids.add(resourceID);
        }
    }

}
