package com.example.smart_zadintuvas;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){

        Uri[] uris = new Uri[6];
        uris[0] = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        uris[1] = Uri.parse("android.resource://com.example.smart_zadintuvas/" +  R.raw.birds);
        uris[2] = Uri.parse("android.resource://com.example.smart_zadintuvas/" +  R.raw.raining);
        uris[3] = Uri.parse("android.resource://com.example.smart_zadintuvas/" +  R.raw.rain);
        uris[4] = Uri.parse("android.resource://com.example.smart_zadintuvas/" +  R.raw.let_it_snow);
        uris[5] = Uri.parse("android.resource://com.example.smart_zadintuvas/" +  R.raw.fog_horn);
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        for(int i = 0; i < 6; i++) {
                NotificationChannel channel = new NotificationChannel("AlarmNotificationChannel" + i, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                channel.setSound(uris[i], audioAttributes);
                notificationManager.createNotificationChannel(channel);
        }
    }
}
