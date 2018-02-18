package team21.flashbackmusic;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends IntentService {
    private FusedLocationProviderClient mFusedLocationClient;
    private Location currLocation;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "team21.flashbackmusic.action.FOO";
    private static final String ACTION_BAZ = "team21.flashbackmusic.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "team21.flashbackmusic.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "team21.flashbackmusic.extra.PARAM2";

    public LocationService() {
        super("LocationService");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        //Toast.makeText(getBaseContext(), " Service start ", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags, startId);
    }

    @Override
    public void onDestroy(){
        //Toast.makeText(getApplicationContext(),"service stopped",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private static void sendMessageBack(Context context,Location L){
        Intent intent = new Intent("LastLocation");
        Bundle b = new Bundle();
        b.putParcelable("Location", L);
        intent.putExtra("Location",b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "Permission Denied for location", Toast.LENGTH_SHORT).show();

            }


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currLocation = location;
                                Toast.makeText(getApplicationContext(), "location" + location.toString(), Toast.LENGTH_SHORT).show();
                                Log.i("Raw Songs name: ", "  raw location: "+ location.toString());
                                Log.i("Raw Songs name: ", "  curr location: "+ currLocation.toString());
                                sendMessageBack(getApplicationContext(),currLocation);
                            }
                        }
                    });
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
