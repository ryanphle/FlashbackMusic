package team21.flashbackmusic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ryanle on 2/7/18.
 */

public class SongAdapter extends ArrayAdapter<Song>{
    private Context context;
    public SongAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public SongAdapter(Context context, int resource, List<Song> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v==null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_listview, null);
        }

        final Song s = getItem(position);

        if (s != null) {
            final TextView songName = (TextView) v.findViewById(R.id.song_text);
            TextView artistName = (TextView) v.findViewById(R.id.artist_text);
            TextView albumName = (TextView) v.findViewById(R.id.album_text);
            final Button likeDislikeButton = (Button) v.findViewById(R.id.likeDislikeButton);

            if (s.getFavorite() == 0) {
                likeDislikeButton.setBackgroundResource(R.drawable.ic_neutral);

            }
            else if (s.getFavorite() == 1) {
                likeDislikeButton.setBackgroundResource(R.drawable.ic_like);
            }
            else {
                likeDislikeButton.setBackgroundResource(R.drawable.ic_dislike);
            }

            likeDislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (s.getFavorite() == 0) {
                        s.setFavorite(1);
                        ((MainActivity)context).like_editor.putInt(s.getName(),s.getFavorite());
                        ((MainActivity)context).like_editor.apply();
                        Log.d("currentSong", Integer.toString(((MainActivity)context).like_setting.getInt(s.getName(),-1)));


                        likeDislikeButton.setBackgroundResource(R.drawable.ic_like);
                    }
                    else if (s.getFavorite() == 1) {
                        Song currentSong = ((MainActivity)context).mediaPlayerWrapper.getSong();
                        s.setFavorite(-1);

                        ((MainActivity)context).like_editor.putInt(s.getName(),s.getFavorite());
                        ((MainActivity)context).like_editor.apply();

                        Log.d("currentSong", Integer.toString(((MainActivity)context).like_setting.getInt(s.getName(),-1)));

                        //Log.d("currentSong", s.getName());

                        likeDislikeButton.setBackgroundResource(R.drawable.ic_dislike);
                        if (((MainActivity)context).mediaPlayerWrapper.isPlaying() && s.equals(currentSong)) {
                            ((MainActivity) context).nextSong(true);
                        }
                    }
                    else {
                        s.setFavorite(0);
                        ((MainActivity)context).like_editor.putInt(s.getName(),s.getFavorite());
                        ((MainActivity)context).like_editor.apply();

                        likeDislikeButton.setBackgroundResource(R.drawable.ic_neutral);
                    }
                }
            });

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
