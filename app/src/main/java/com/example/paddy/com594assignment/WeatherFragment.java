package com.example.paddy.com594assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;



public class WeatherFragment extends Fragment {

    final private String OPENWEATHERMAP_API_KEY = "735e36d0ae1710ad63029200d0988b13";
    private OnFragmentInteractionListener mListener;
    TextView temperature;
    TextView summary;
    TextView windSpeed;
    TextView pressure;
    TextView humidity;
    ImageView weatherIcon;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        temperature = view.findViewById(R.id.textView_temp);
        windSpeed = view.findViewById(R.id.textView_windSpeed);
        summary = view.findViewById(R.id.textView_summary);
        pressure = view.findViewById(R.id.textView_pressure);
        humidity = view.findViewById(R.id.textView_humidity);
        weatherIcon = view.findViewById(R.id.imageView_weatherPicture);
        summary = view.findViewById(R.id.textView_summary);
        String latitude = getArguments().getString("latitude");
        String longitude = getArguments().getString("longitude");
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        getWeatherWithCoord(latLng);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getWeatherWithCoord(LatLng location) {

        String urlCoord = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.latitude +
                "&lon=" + location.longitude +
                "&units=" + "metric" +
                "&appid=" + OPENWEATHERMAP_API_KEY;

        new GetWeatherTask().execute(urlCoord);
    }



    private class GetWeatherTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            HashMap<String, String> weatherDetails = new HashMap<String, String>();
            String temperature = "UNDEFINED";
            String windSpeed = "UNDEFINED";
            String humidity = "UNDEFINED";
            String summary = "UNDEFINED";


            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONObject main = topLevel.getJSONObject("main");
                JSONObject wind = topLevel.getJSONObject("wind");
                JSONArray weatherArray = topLevel.getJSONArray("weather");
                temperature = String.valueOf(main.getDouble("temp"));
                windSpeed = String.valueOf(wind.getDouble("speed"));
                humidity = String.valueOf(main.getDouble("humidity"));
                weatherDetails.put("temperature", temperature);
                weatherDetails.put("windSpeed", windSpeed);
                weatherDetails.put("humidity", humidity);
                weatherDetails.put("pressure", String.valueOf(main.getDouble("pressure")));
                JSONObject weather = new JSONObject();
                for (int i = 0; i < weatherArray.length(); i++) {
                    weather = weatherArray.getJSONObject(i);
                }
                summary = String.valueOf(weather.getString("main"));
                weatherDetails.put("summary", summary);
                weatherDetails.put("description", String.valueOf(weather.getString("description")));
                weatherDetails.put("icon", String.valueOf(weather.getString("icon")));





                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return weatherDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> weatherDetails) {
            JSONObject json = new JSONObject();
            temperature.setText(weatherDetails.get("temperature") + " °C");
            windSpeed.setText("Wind speed: " + weatherDetails.get("windSpeed") + " m/s");
            pressure.setText("Pressure: " + weatherDetails.get("pressure") + " hPa");
            humidity.setText("Humidity: " + weatherDetails.get("humidity") + "%");
            summary.setText(weatherDetails.get("summary"));
            String iconExt = weatherDetails.get("icon");
            String url = "http://openweathermap.org/img/w/" + iconExt + ".png";
            new DownloadImageTask((ImageView) getView().findViewById(R.id.imageView_weatherPicture))
                    .execute(url);

        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}




