package com.example.smart_zadintuvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

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
                TextView textView = findViewById(R.id.textView2);
                textView.setText("No alarm set");
                Toast toast = Toast.makeText(getApplicationContext(), "Alarm cancelled", Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        ;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        //test
        String valandos;
        String minutes;
        if (hour < 10)
            valandos = "0" + hour;
        else valandos = "" + hour;

        if (minute < 10)
            minutes = "0" + minute;
        else minutes = "" + minute;

        TextView textView = findViewById(R.id.textView2);
        textView.setText("Hour: " + valandos + " Minute: " + minutes);
        Toast toast = Toast.makeText(getApplicationContext(), "Alarm created for " + valandos + ":" + minutes, Toast.LENGTH_SHORT);
        toast.show();
        //TODO
        //Kreiptis i AlarmManager
    }
}