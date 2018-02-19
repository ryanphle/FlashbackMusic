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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class GetLocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private Location currLocation;
    private final IBinder iBinder = new locationService();
    private LocationRequest mLocationRequest;
    private LocationCallback myLocationCallback;

    class locationService extends Binder{

        public GetLocationService getService(){
            return GetLocationService.this;
        }


    }

    public GetLocationService() {
    }


    public void getLocation(Song s){
        final Song song = s;
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
                                sendMessageBack(getApplicationContext(),currLocation, song);

                            }
                        }
                    });

       // }

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private static void sendMessageBack(Context context, Location L, Song song){
        Intent i = new Intent("LastLocation");
        Bundle b = new Bundle();
        b.putParcelable("Location", L);
        b.putParcelable("Song",song);
        i.putExtra("Location",b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        return super.onStartCommand(intent,flags, startId);
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return iBinder;

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
