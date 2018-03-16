package team21.flashbackmusic;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by ryanle on 3/15/18.
 */

public interface GetDataListener {

    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
