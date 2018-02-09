package team21.flashbackmusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ryanle on 2/9/18.
 */

public class AlbumAdapter extends ArrayAdapter<Album> {
    public AlbumAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public AlbumAdapter(Context context, int resource, List<Album> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v==null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.album_gridview, null);
        }

        Album a = getItem(position);

        if (a != null) {
            ImageView albumArt = v.findViewById(R.id.album_art);
            TextView albumTitle = v.findViewById(R.id.album_title);

            if (albumArt != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(a.getImg(), 0, a.getImg().length);
                albumArt.setImageBitmap(bmp);
            }
            else {
                albumArt.setBackgroundColor(Color.rgb(0,0,0));
            }

            if (albumTitle != null) {
                albumTitle.setText(a.getName());
            }
        }

        return v;
    }

}
