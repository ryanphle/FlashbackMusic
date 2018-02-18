package team21.flashbackmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GetLocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private Location currLocation;
    private final IBinder iBinder = new locationService();

    class locationService extends Binder{

        public GetLocationService getService(){
            return GetLocationService.this;
        }


    }

    public GetLocationService() {
    }


    public void getLocation(Song s){
        final Song song = s;

        Log.i("RawService: ", "  on handle intent ");


        if (song != null) {

            Log.i("RawService: ", "  on handle intent ");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(getApplicationContext(), "Permission Denied for location", Toast.LENGTH_SHORT).show();

            }


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currLocation = location;
                                //Toast.makeText(getApplicationContext(), "location" + location.toString(), Toast.LENGTH_SHORT).show();
                                Log.i("RawService: ", "  raw location: "+ location.toString());
                                Log.i("RawService: ", "  curr location: "+ currLocation.toString());
                                sendMessageBack(getApplicationContext(),currLocation, song);


                            }
                        }
                    });

        }

    }


    private static void sendMessageBack(Context context, Location L, Song song){
        Intent i = new Intent("LastLocation");
        //Bundle bSong = intent.getBundleExtra("Song");
        Bundle b = new Bundle();
        b.putParcelable("Location", L);
        b.putParcelable("Song",song);
        i.putExtra("Location",b);
        Log.i("RawService: ", "  sending message: ");
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        //Toast.makeText(getBaseContext(), " Service start ", Toast.LENGTH_SHORT).show();
        //Log.i("RawService: ", "  service start");
        return super.onStartCommand(intent,flags, startId);
    }



    @Override
    public void onDestroy(){
        //Toast.makeText(getApplicationContext(),"service stopped",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return iBinder;

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
