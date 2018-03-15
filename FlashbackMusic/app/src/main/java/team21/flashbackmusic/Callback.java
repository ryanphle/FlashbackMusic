package team21.flashbackmusic;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by petternarvhus on 11/03/2018.
 */

public interface Callback {
    void callback(DataSnapshot dataSnapshot);
}
