package com.emmanuelphilip.dadtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service{

    private static final String TAG = LocationService.class.getName();
    private static Context context = null;
    private LocationManager locationManager = null;
    private static final int LOCATION_INTERVAL = 1000; //5 mins
    private static final float LOCATION_DISTANCE = 10; //500 mtrs
    private static String smsRecipient = null;
    LocationListener[] locationListeners;

    private class LocationListener implements android.location.LocationListener{

        private final String TAG = LocationService.class.getName() + "." + LocationListener.class.getName();
        Location location;
        Location pointLocation;
        double range;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);

            SharedPreferences pref = context.getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
            float pointLat = pref.getFloat("Lat", 0);
            float pointLong = pref.getFloat("Long", 0);
            range = pref.getFloat("Range", 500);
            pointLocation = new Location("Point Location");
            pointLocation.setLatitude(pointLat);
            pointLocation.setLongitude(pointLong);
            Intent intent = new Intent(getBaseContext(), SmsService.class);
            if(smsRecipient == null || smsRecipient.isEmpty()){
                smsRecipient = "phoneNumber";
            }
            intent.putExtra("smsRecipient", smsRecipient);
            intent.putExtra("smsData",  1 + ", " + 2);
            startService(intent);

            location = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            checkLocation(location);

        }

        private void checkLocation(Location location){
            Log.e(TAG, "checkLocation: ");
            SharedPreferences pref = context.getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            double distance  = pointLocation.distanceTo(location);
            Log.e(TAG, "Distance from marked point: " + distance);
            //Toast.makeText(context,"distance: " + distance, Toast.LENGTH_LONG).show();
            if(distance>range && pref.getBoolean("inside", false)){
                Toast.makeText(context,"Exiting Circle. distance: " + distance, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Exited circle");
                Log.e(TAG, "Sending SMS");
                Intent intent = new Intent(getBaseContext(), SmsService.class);
                if(smsRecipient == null || smsRecipient.isEmpty()){
                    smsRecipient = "phoneNumber";
                }
                intent.putExtra("smsRecipient", smsRecipient);
                intent.putExtra("smsData",  lat + ", " + lng);
                startService(intent);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("inside", false);
                editor.putBoolean("preventRestart", true);
                editor.commit(); // commit changes
                Log.e(TAG, "Stopping LocationService");
                stopSelf();

            }
            else if(distance<=range && !pref.getBoolean("inside", false)){
                Log.e(TAG, "Entered circle");
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("inside", true);
                editor.commit(); // commit changes
                stopSelf();

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        String number = intent.getStringExtra("PhoneNumber");
        if(number!=null){

        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        context = getApplicationContext();
        locationListeners = new LocationListener[] {
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        initializeLocationManager();
        SharedPreferences pref = context.getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        float distance = pref.getFloat("Range", 500);
        if(LOCATION_DISTANCE*2<distance){
            distance = LOCATION_DISTANCE;
        }
        else {
            distance /= 2;
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, distance,
                    locationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, distance,
                        locationListeners[0]);
            }
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        locationManager.removeUpdates(locationListeners[0]);
        locationManager.removeUpdates(locationListeners[1]);
        Intent restartService = new Intent(getBaseContext(), RestartService.class);
        sendBroadcast(restartService);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
