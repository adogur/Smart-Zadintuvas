package com.example.smart_zadintuvas;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private LocationListener listener;
    private LocationManager locationManager;


    private final String url2 = "https://api.openweathermap.org/data/2.5/weather";
    private final String appID = "250fa86bc13f410ed32092cfcdcc92cb";

    public String orai = "Failed to get weather update, click to retry.";
    public int weatherID = 0;
    public final DecimalFormat df = new DecimalFormat("#.#");
    public int icon = 0;
    public String channelID = "AlarmNotificationChannel0";

    @Override
    public void onReceive(Context context, Intent intent) {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat  = round(location.getLatitude(), 2);
                double longt = round(location.getLongitude(), 2);
                gaunaOrus(lat, longt, context);
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,5,listener);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "SmartAlarm:notificationLock");

        Intent notificationIntent = new Intent(context, WeatherActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPending = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch(weatherID) {
                    case 0:
                        icon = R.drawable.ic_baseline_alarm_24;
                        break;
                    case 1:
                        icon = R.drawable.notification_clear;
                        channelID = "AlarmNotificationChannel1";
                        break;
                    case 2:
                        icon = R.drawable.notification_rain;
                        channelID = "AlarmNotificationChannel2";
                        break;
                    case 3:
                        icon = R.drawable.notification_clouds;
                        channelID = "AlarmNotificationChannel0";
                        break;
                    case 4:
                        icon = R.drawable.notification_thunder;
                        channelID = "AlarmNotificationChannel3";
                        break;
                    case 5:
                        icon = R.drawable.notification_snow;
                        channelID = "AlarmNotificationChannel4";
                        break;
                    case 6:
                        icon = R.drawable.notification_mist;
                        channelID = "AlarmNotificationChannel5";
                        break;
                    default:
                        icon =R.drawable.ic_baseline_alarm_24;
                        channelID = "AlarmNotificationChannel0";
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                        .setSmallIcon(icon)
                        .setContentTitle("ALARM")
                        .setContentText(orai)
                        .setContentIntent(notificationPending)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_INSISTENT;
                notificationManager.notify(1, notification);
                if (!pm.isInteractive()) {
                    wl.acquire(10000);
                }
                locationManager.removeUpdates(listener);
            }
        }, 30000);
    }

    private void gaunaOrus(double lat, double longt, Context context){
        Log.d("gaunaOrus: ", "pradeda");
        String tempUrl = "";
        String latitude = "" + lat;
        String longtitude = "" + longt;
        tempUrl = url2 + "?lat=" + latitude + "&lon=" + longtitude + "&appid=" + appID;
        Log.d("url2: ", tempUrl);
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, tempUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            String orai = "";
                            String output = "";
                            Log.d("response2", response);
                            apdorotiOrus(response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         orai = "Failed to get weather update, click to retry.";
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(context);
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
        orai = "";
        String[] output;
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonArraySys = jsonResponse.getJSONObject("sys");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            String miestas = jsonResponse.getString("name");
            String main = jsonObjectWeather.getString("main");
            String description = jsonObjectWeather.getString("description");
            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            double temp = jsonObjectMain.getDouble("temp") - 273.1;
            switch(main) {
                case "Clear":
                    weatherID = 1;
                    break;
                case "Rain":
                    weatherID = 2;
                    break;
                case "Clouds":
                    weatherID = 3;
                    break;
                case "Thunderstorm":
                    weatherID = 4;
                    break;
                case "Snow":
                    weatherID = 5;
                    break;
                case "Mist":
                    weatherID = 6;
                    break;
                default:
                    weatherID = 0;
            }
            orai = "Current weather in " + miestas + ": " + df.format(temp) + " °C, " + description + ".";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


