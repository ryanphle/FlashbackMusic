package team21.flashbackmusic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by petternarvhus on 12/02/2018.
 */

public class Play {
    private static Clock clock = Clock.systemDefaultZone();
    private Location location;
    private String timeOfDay;
    private Timestamp time;
    private transient Context activity;
    private LocalDateTime morning;
    private LocalDateTime afternoon;

    public Play(){};


    public Play( Context activity, Location location, Timestamp time ) {

        this.activity = activity;
        this.time = time;
        this.location = location;
        setTimeOfDay(time);
        Log.i("Play_location", location.toString());
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(getClock());
    }

    public void setFusedLocationClient(MainActivity mainActivity) {
        this.activity = activity;
    }

    private void setTime() {
        long miliTime = System.currentTimeMillis();
        time = new Timestamp(miliTime);
    }

   /* private void setLastLocation() {
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = new Location("dummy");//mFusedLocationClient.getLastLocation().getResult();
    }*/

    private static Clock getClock() {
        return clock;
    }

    public Location getLocation() { return location; }

    public String getTimeOfDay() { return timeOfDay; }

    public Timestamp getTime() { return time; }

    private void setTimeOfDay(Timestamp time) {
       long miliTime = time.getTime();
       Calendar calender = Calendar.getInstance();
       TimeZone tz = calender.getTimeZone();
       LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time.getTime()), tz.toZoneId());
       Log.i("hour", Integer.toString(dateTime.getHour()));
       morning = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 8, 0, 0);
       afternoon = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 16, 0, 0);

        if (dateTime.isBefore(morning)) {
            this.timeOfDay = "Night";
        } else if (dateTime.isBefore(afternoon)) {
            this.timeOfDay = "Morning";
        } else {
            this.timeOfDay = "Afternoon";
        }
    }
}
