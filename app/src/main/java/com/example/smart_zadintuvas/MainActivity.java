package com.example.smart_zadintuvas;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        Button setAlarmButton = (Button) findViewById(R.id.button2);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatDialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button cancelAlarmButton = (Button) findViewById(R.id.button3);
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

        Button weatherButton = (Button) findViewById(R.id.button4);
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

        String valandos;
        String minutes;
        if (hour < 10)
            valandos = "0" + hour;
        else valandos = "" + hour;

        if (minute < 10)
            minutes = "0" + minute;
        else minutes = "" + minute;

        TextView textView = findViewById(R.id.textView2);
        //textView.setText("Hour: " + valandos + " Minute: " + minutes);
        Toast toast = Toast.makeText(getApplicationContext(), "Alarm created for " + valandos + ":" + minutes, Toast.LENGTH_SHORT);
        toast.show();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        myCountDownTimer = skaiciuojaLaika(c, textView);
        myCountDownTimer.start();
        startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

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


    }

    public CountDownTimer skaiciuojaLaika(Calendar c, TextView textView) {
        CountDownTimer counter = new CountDownTimer(c.getTimeInMillis() - System.currentTimeMillis(), 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                textView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }

            public void onFinish() {
                textView.setText("No Alarm Set");
            }

        };
        return counter;
    }
    
}


