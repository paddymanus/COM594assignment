package com.example.paddy.com594assignment;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class mapWeather extends AppCompatActivity
        implements SensorEventListener {

    //    private Button btn1, btn2;
//    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = mapWeather.class.getSimpleName();
    private SensorManager sensorManager;
    private long lastUpdateTime;
    private static float SHAKE_THRESHOLD_GRAVITY = 2;
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_weather);

        Bundle bundlePre = new Bundle();
        bundlePre.putString("latitude", "54.9966");
        bundlePre.putString("longitude", "-7.3086");
        MapFragment mapPre = new MapFragment();
        mapPre.setArguments(bundlePre);
        WeatherFragment weatherPre = new WeatherFragment();
        weatherPre.setArguments(bundlePre);

        addFragment1(mapPre, false, "one");
        addFragment2(weatherPre, false, "one");


        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPlaceSearchIntent();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdateTime = System.currentTimeMillis();
    }

    private void callPlaceSearchIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        if(!isNetworkAvailable()){
            Toast toast = Toast.makeText(getApplicationContext(), "NO NETWORK CONNECTION", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            place = PlaceAutocomplete.getPlace(this, data);
            if (place != null) {
                MapFragment mapF = new MapFragment();
                WeatherFragment weatherF = new WeatherFragment();
                Bundle bundle = new Bundle();
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place:" + place.toString());
                LatLng location = place.getLatLng();
                String Latitude = Double.toString(location.latitude);
                String Longitude = Double.toString(location.longitude);
                bundle.putString("latitude", Latitude);
                bundle.putString("longitude", Longitude);
                mapF.setArguments(bundle);
                weatherF.setArguments(bundle);
                addFragment1(mapF, false, "MAP");
                addFragment2(weatherF, false, "WEATHER");
            }
        }
    }


    public void addFragment1(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.container_frame_map, fragment, tag);
        ft.commitAllowingStateLoss();
        // change
    }
    public void addFragment2(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.container_frame_weather, fragment, tag);
        ft.commitAllowingStateLoss();
        // change
    }

    @Override
    public void onResume(){
        super.onResume();
        // register this class as a listener for the orientation and accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        long currentTime = System.currentTimeMillis();
        if (gForce >= SHAKE_THRESHOLD_GRAVITY)
        {
            if (currentTime - lastUpdateTime < 200) {
                return;
            }
            lastUpdateTime = currentTime;
            Toast toast = Toast.makeText(this, "Device was shaken - you have now logged out", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent intent = new Intent(mapWeather.this, MainActivity.class);
            startActivity(intent);
//            Fragment fragment = new SOFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.container_frame_SO, fragment);
//            fragmentTransaction.commit();
        }
    }
}

