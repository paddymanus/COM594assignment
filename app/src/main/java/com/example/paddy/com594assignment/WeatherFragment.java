package com.example.paddy.com594assignment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        String latitude = getArguments().getString("latitude");
        String longitude = getArguments().getString("longitude");
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        getWeatherWithCoord(latLng);
        return view;
    }


 /**   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    } */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
                temperature = String.valueOf(main.getDouble("temp"));
                weatherDetails.put("temperature", temperature);


                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return weatherDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> weatherDetails) {
            temperature.setText("Current temp - " + weatherDetails.get("temperature"));


        }
    }
}
