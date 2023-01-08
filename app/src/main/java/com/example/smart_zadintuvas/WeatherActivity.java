package com.example.smart_zadintuvas;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WeatherActivity extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    TextView txtLat;
    TextView txtJSON;
    private final String url = "http://api.openweathermap.org/geo/1.0/reverse";
    private final String url2 = "https://api.openweathermap.org/data/2.5/weather";
    private final String appID = "250fa86bc13f410ed32092cfcdcc92cb";
    public final DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                5, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        txtLat = (TextView) findViewById(R.id.locationtextview);
        double lat  = round(location.getLatitude(), 2);
        double longt = round(location.getLongitude(), 2);
        txtLat.setText("Latitude: " + lat + ", Longitude: " + longt);
        gaunaOrus(lat, longt);
    }

    /*public void gaunaMiesta(double lat, double longt)
    {
        String tempUrl = "";
        String latitude = "" + lat;
        String longtitude = "" + longt;

        tempUrl = url + "?lat=" + latitude + "&lon=" + longtitude + "&appid=" + appID;
        Log.d("url: ", tempUrl);
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, tempUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.d("response", response);
                            try {
                                JSONArray jsonResponse = new JSONArray(response);
                                JSONObject json = jsonResponse.getJSONObject(0);
                                String miestas = json.getString("name");
                                Log.d("miestas: ", miestas);
                                String salis = json.getString("country");
                                Log.d("salis: ", salis);
                                gaunaOrus(miestas, salis);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }*/

    private void gaunaOrus(double lat, double longt){
        String tempUrl = "";
        //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
        String latitude = "" + lat;
        String longtitude = "" + longt;
        tempUrl = url2 + "?lat=" + latitude + "&lon=" + longtitude + "&appid=" + appID;
        Log.d("url2: ", tempUrl);
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, tempUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            String output = "";
                            Log.d("response2", response);
                            apdorotiOrus(response);
                            }
                        }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtJSON.setText("Failed to get weather information.");
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }

    public static double round(double value, int places) {
        if (places > 0) {
            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        }
        else return 0;
    }

    public void apdorotiOrus(String response)
    {
        String output = "";
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonArraySys = jsonResponse.getJSONObject("sys");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            String salis = jsonArraySys.getString("country");
            String miestas = jsonResponse.getString("name");
            String main = jsonObjectWeather.getString("main");
            String description = jsonObjectWeather.getString("description");
            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            double temp = jsonObjectMain.getDouble("temp") - 273.1;
            double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.1;
            output += " Current weather of " + miestas + " (" + salis + ")"
                    + "\n Temperature: " + df.format(temp) + " °C"
                    + "\n Feels Like: " + df.format(feelsLike) + " °C"
                    + "\n Weather: " + main
                    + "\n Description: " + description;
            txtJSON = findViewById(R.id.jsontextView);
            txtJSON.setText(output);
            txtJSON.setVisibility(View.VISIBLE);
            txtLat.setVisibility(View.VISIBLE);

            switch(main) {
                case "Clear":
                    findViewById(R.id.sunView).setVisibility(View.VISIBLE);
                    findViewById(R.id.cloudView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.rainView).setVisibility(View.INVISIBLE);
                    break;
                case "Rain":
                    findViewById(R.id.sunView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.cloudView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.rainView).setVisibility(View.VISIBLE);
                    break;
                case "Clouds":
                    findViewById(R.id.sunView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.cloudView).setVisibility(View.VISIBLE);
                    findViewById(R.id.rainView).setVisibility(View.INVISIBLE);
                    break;
                case "Snow":
                    findViewById(R.id.sunView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.cloudView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.rainView).setVisibility(View.INVISIBLE);
                    break;
                default:
            }
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

