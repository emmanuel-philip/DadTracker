package com.emmanuelphilip.dadtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = this.getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        float lat = pref.getFloat("Lat", 0);
        float lng = pref.getFloat("Long", 0);
        float range = pref.getFloat("Range", 500);

        updatePreferences(lat, lng, range);
    }



    public void start(View view){
        Log.e(TAG, "Start");
        Toast.makeText(this, "started", Toast.LENGTH_LONG).show();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("checkLocation", true); // Storing boolean - true/false
        editor.putBoolean("inside", false);
        editor.commit(); // commit changes
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    public void stop(View view){
        Log.e(TAG, "Stop");
        Log.e(TAG, "Setting preventRestart to True");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("preventRestart", true); // Storing boolean - true/false
        editor.commit(); // commit changes
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    public  void setLocation(View view){
        Log.e(TAG, "setLocation");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        float lat = data.getFloatExtra("Lat", (float) 18.558359);
        float lng = data.getFloatExtra("Long", (float) 73.805328);
        float range = data.getFloatExtra("Range", 500);

        updatePreferences(lat, lng, range);
    }

    private void updatePreferences(float lat, float lng, float range){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("Lat",lat );
        editor.putFloat("Long", lng);
        editor.putFloat("Range", range);
        editor.commit();
        updateViewValues(lat, lng, range);
    }

    private void updateViewValues(float lat, float lng, float range){
        TextView latText = findViewById(R.id.lat_value);
        latText.setText(String.valueOf(lat));
        TextView longText = findViewById(R.id.long_value);
        longText.setText(String.valueOf(lng));
        TextView rangeText = findViewById(R.id.range_value);
        rangeText.setText(String.valueOf(range));
    }
}
