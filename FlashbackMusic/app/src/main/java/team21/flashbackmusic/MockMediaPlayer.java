package team21.flashbackmusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by petternarvhus on 18/02/2018.
 */

public class MockMediaPlayer extends MediaPlayer {
        Uri dataSource;

        @Override
        public void setDataSource(Context path, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
        {
            // TODO Auto-generated method stub
            super.setDataSource(path,uri);
            this.dataSource = uri;
        }

        public Uri getDataSource()
        {
            return dataSource;
        }
}
