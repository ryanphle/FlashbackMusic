package team21.flashbackmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ryanle on 2/7/18.
 */

public class SongAdapter extends ArrayAdapter<Song> {
    public SongAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SongAdapter(Context context, int resource, List<Song> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v==null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_listview, null);
        }

        Song s = getItem(position);

        if (s != null) {
            TextView songName = (TextView) v.findViewById(R.id.song_text);
            TextView artistName = (TextView) v.findViewById(R.id.artist_text);
            TextView albumName = (TextView) v.findViewById(R.id.album_text);

            if (songName != null) {
                songName.setText(s.getName().trim());
            }

            if (artistName != null) {
                artistName.setText(s.getArtist().trim());
            }

            if (albumName != null) {
                albumName.setText(s.getAlbum().trim());
            }
        }

        return v;
    }
}
