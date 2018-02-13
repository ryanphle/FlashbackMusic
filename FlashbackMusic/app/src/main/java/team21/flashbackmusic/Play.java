package team21.flashbackmusic;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;


/**
 * Created by petternarvhus on 12/02/2018.
 */

public class Play {
    private static Clock clock = Clock.systemDefaultZone();
    private static ZoneId zoneId = ZoneId.systemDefault();
    Location location;
    String timeOfDay;
    LocalTime time;
    private final LocalTime morning = LocalTime.parse("08:00:00");
    private final LocalTime afternoon = LocalTime.parse("16:00:00");
    private final LocalTime night = LocalTime.parse("00:00:00");
    //private FusedLocationProviderClient fusedLocationProviderClient;


    public Play(){
      //  this.location = location;
        this.time = now().toLocalTime();
        //setLastLocation();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(getClock());
    }

    //private void setLastLocation(){
    //String locationProvider = LocationManager.GPS_PROVIDER;
    //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
   // Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
   // }


    public static void useSystemDefaultZoneClock(){
        clock = Clock.systemDefaultZone();
    }

    private static Clock getClock() {
        return clock;
    }
    private void setTimeOfDay(){
        LocalTime currentTime = now().toLocalTime();
        if (currentTime.isBefore(morning)){
            this.timeOfDay = "Night";
        } else if (currentTime.isBefore(afternoon)){
            this.timeOfDay = "Morning";
        } else {
            this.timeOfDay = "Afternoon";
        }

    }
}
