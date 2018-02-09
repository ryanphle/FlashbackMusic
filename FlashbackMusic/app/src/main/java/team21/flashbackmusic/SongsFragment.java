package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryanle on 2/8/18.
 */

public class SongsFragment extends Fragment {
    //ListView listView;
    private Map<String,Album> albums;
    private List<Song> songs;
    private SongAdapter adapter;
    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        albums = new HashMap<>();
        songs = new ArrayList<>();

        try {
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.song_list);
        adapter = new SongAdapter(getActivity(), R.layout.activity_listview, songs);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void loadSongs() throws IllegalArgumentException, IllegalAccessException {

        Field[] fields=R.raw.class.getFields();

        for(int count=0; count < fields.length; count++){

            int resourceID = fields[count].getInt(fields[count]);
            Uri myUri = Uri.parse("android.resource://team21.flashbackmusic/" + resourceID);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity(), myUri);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] img = retriever.getEmbeddedPicture();

            if (albums.get(album)==null) {
                albums.put(album, new Album(album, artist,img));
            }

            Album a = albums.get(album);
            Song song = new Song(fields[count].getName(), artist,img, a.getName());
            albums.get(album).addSong(song);
            songs.add(song);
        }
    }

    /*public void setListView(SongAdapter adapter) {
        //ListView listView = (ListView) getActivity().findViewById(R.id.song_list);
        listView.setAdapter(adapter);
    }*/
}
