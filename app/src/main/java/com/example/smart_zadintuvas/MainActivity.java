package com.example.smart_zadintuvas;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    CountDownTimer myCountDownTimer;
    Boolean alarmSet = false;
    Button editAlarmButton;
    Button setAlarmButton;
    Button weatherButton;
    Button cancelAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Button editAlarmButton = (Button) findViewById(R.id.button6);
        Button setAlarmButton = (Button) findViewById(R.id.button2);
        Button weatherButton = (Button) findViewById(R.id.button4);
        Button cancelAlarmButton = (Button) findViewById(R.id.button3);
        TextView textView = findViewById(R.id.textView2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    0);
            return;
        }

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatDialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                setAlarmButton.setEnabled(false);
                cancelAlarmButton.setEnabled(true);
                editAlarmButton.setEnabled(true);
            }
        });

        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                setAlarmButton.setEnabled(true);
                editAlarmButton.setEnabled(false);
            }
        });

        editAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatDialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker edit");

            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        TextView textView = findViewById(R.id.textView2);
        if(alarmSet == false) {
            String valandos;
            String minutes;
            if (hour < 10)
                valandos = "0" + hour;
            else valandos = "" + hour;

            if (minute < 10)
                minutes = "0" + minute;
            else minutes = "" + minute;


            Toast toast = Toast.makeText(getApplicationContext(), "Alarm created for " + valandos + ":" + minutes, Toast.LENGTH_SHORT);
            toast.show();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            myCountDownTimer = skaiciuojaLaika(c, textView);
            myCountDownTimer.start();
            startAlarm1(c);
        }
        else
        {
            String valandos;
            String minutes;
            if (hour < 10)
                valandos = "0" + hour;
            else valandos = "" + hour;

            if (minute < 10)
                minutes = "0" + minute;
            else minutes = "" + minute;

            Toast toast = Toast.makeText(getApplicationContext(), "Alarm edited for " + valandos + ":" + minutes, Toast.LENGTH_SHORT);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 30000, pendingIntent);
            if (myCountDownTimer != null)
                myCountDownTimer.cancel();
            myCountDownTimer = skaiciuojaLaika(c, textView);
            myCountDownTimer.start();

        }
    }

    private void startAlarm1(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 30000, pendingIntent);
        alarmSet = true;
    }


    private void cancelAlarm() {
        TextView textView = findViewById(R.id.textView2);
        textView.setText("No alarm set");
        Toast toast = Toast.makeText(getApplicationContext(), "Alarm cancelled", Toast.LENGTH_SHORT);
        toast.show();

        if (myCountDownTimer != null)
            myCountDownTimer.cancel();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        alarmSet = false;
    }


    public CountDownTimer skaiciuojaLaika(@NonNull Calendar c, TextView textView) {
        CountDownTimer counter = new CountDownTimer(c.getTimeInMillis() - System.currentTimeMillis(), 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                textView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
                textView.setText("No alarm set");
            }
        };
        return counter;
    }
}


