package team21.flashbackmusic;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ryanle on 2/8/18.
 */

public class AlbumsFragment extends Fragment {
    public AlbumsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }
}
