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
    private double longit;
    private double lat;
    private String timeOfDay;
    private Timestamp time;
    private transient Context activity;
    private final LocalTime morning = LocalTime.parse("08:00:00");
    private final LocalTime afternoon = LocalTime.parse("16:00:00");

    public Play(){};

    public Play( Context activity, Location location ) {

        this.activity = activity;
        setTimeOfDay();
        setTime();
        this.lat = location.getLatitude();
        this.longit = location.getLongitude();
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

    private static Clock getClock() {
        return clock;
    }

    //public Location getLocation() { return location; }
    public double getLongitude() { return longit; }
    public double getLatitude() { return lat; }
    public String getTimeOfDay() { return timeOfDay; }

    public Timestamp getTime() { return time; }

    private void setTimeOfDay() {
        LocalTime currentTime = now().toLocalTime();
        if (currentTime.isBefore(morning)) {
            this.timeOfDay = "Night";
        } else if (currentTime.isBefore(afternoon)) {
            this.timeOfDay = "Morning";
        } else {
            this.timeOfDay = "Afternoon";
        }
    }
}