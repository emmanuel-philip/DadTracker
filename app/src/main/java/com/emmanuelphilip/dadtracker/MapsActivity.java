package com.emmanuelphilip.dadtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();
    private GoogleMap mMap;
    private LatLng location;
    private double range = 500;
    private TextView rangeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rangeText = (EditText) findViewById(R.id.range_value);

        rangeText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged");
                try {
                    range = Double.parseDouble(s.toString());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(location));
                    mMap.addCircle(new CircleOptions()
                            .center(location)
                            .radius(range)
                            .strokeWidth(0f)
                            .fillColor(0x550000FF));
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: invalid input");
                    range = 0;
                }

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            mMap.setMyLocationEnabled(true);
        }
        LatLng lastLocation = new LatLng(1, 1);
        Location lastKnownlocation = getLocation();
        if(lastKnownlocation!=null){
            lastLocation = new LatLng(lastKnownlocation.getLatitude(), lastKnownlocation.getLongitude());
        }
        float zoom = 15;
        Log.e(TAG, "Moving camera to last known location:" + lastLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, zoom));
//        mMap.addMarker(new MarkerOptions().position(lastLocation).title("Marker in Sydney"));
//        location = lastLocation;
//        mMap.addCircle(new CircleOptions()
//                .center(lastLocation)
//                .radius(distance)
//                .strokeWidth(0f)
//                .fillColor(0x550000FF));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Log.e(TAG, "onMapClick");
                Log.e(TAG, "Clicked point: " + point);
                //lstLatLngs.add(point);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                location = point;
                mMap.addCircle(new CircleOptions()
                        .center(point)
                        .radius(range)
                        .strokeWidth(0f)
                        .fillColor(0x550000FF));
            }
        });
    }

    public void confirmLocation(View view){
        Log.e(TAG, "confirmLocation");
        Log.e(TAG, "Lat: " + location.latitude);
        Log.e(TAG, "Long: " + location.longitude);
        Log.e(TAG, "Range: " + range);
        if(location==null){
            Toast.makeText(this, "Location needs to be selected", Toast.LENGTH_LONG).show();
            return;
        }
        if(range<=0){
            Toast.makeText(this, "Watch Distance should be greater than 0", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("Lat", (float) location.latitude);
        intent.putExtra("Long", (float)location.longitude);
        intent.putExtra("Range", (float)range);
        setResult(0, intent);
        Log.e(TAG, "return to MainActivity");
        this.finish();
    }

    public Location getLocation() {
        Log.e(TAG, "getLocation");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location lastKnownLocationGPS = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            else{
                lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
//                    return TODO;
                }
                else{
                    loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
                return loc;
            }
        } else {
            return null;
        }
    }
}
