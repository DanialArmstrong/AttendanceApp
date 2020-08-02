package com.example.attendanceapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


public class Attendance extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        final DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
        Intent intent = getIntent();
        String id = intent.getStringExtra("StudentID");
        String name = intent.getStringExtra("StudentName");
        final Student student = new Student(Integer.parseInt(id), name);
        final Button attendButton = findViewById(R.id.attendSessionBtn);
        final TextView nextSessionTextView = findViewById(R.id.nextSession);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        attendButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view){
                signAttendance(view);
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void signAttendance(View view){
                Date currentTime = Calendar.getInstance().getTime();
                String nextSession = dbHandler.getTimeOfNextSession(student);
                //String newFormat = "hh:mm dd-MM-yyyy";
                String oldFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(oldFormat);
                try{
                    Date session = formatter.parse(nextSession);
                    formatter.format(session);
                    formatter.format(currentTime);
                    long difference = currentTime.getTime() - session.getTime();
                    int minutesDifference = (int) ((Math.abs(difference))/(1000 * 60));
                    if (minutesDifference <= 60 && minutesDifference >= 0){
                        Location sessionLocation = dbHandler.getLocationData(student);
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            GPS();
                        }
                        else
                            getLocation();
                        Location currentLocation = getLocation();
                        float[] distance = new float[2];
                        Location.distanceBetween(sessionLocation.getLatitude(), sessionLocation.getLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude(), distance);
                        int radius = dbHandler.getRadius(student);
                        if(distance[0] < radius){
                            //Attend session
                            attendSession(student, sessionLocation, nextSession);


                        }
                        else{
                            Toast.makeText(getApplicationContext(),"You are too far from your session",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Your session isn't at this time",Toast.LENGTH_SHORT).show();
                    }

                   //Duration differencetime = Duration.between(session.getTime(), currentTime.getTime());
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        nextSessionTextView.setText("Your next session is at " + getNextSession(student));


    }
    private void GPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public Location getLocation(){
        if (ActivityCompat.checkSelfPermission(
                Attendance.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                Attendance.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        final Location currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location){
                location.getLongitude();
                location.getLatitude();
                currentLocation.setLongitude(location.getLongitude());
                currentLocation.setLatitude(location.getLatitude());
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,10.0f, locationListener);
        return currentLocation;
    }

    public boolean attendSession(Student student, Location location, String session){
        final DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
        boolean attended = dbHandler.attendSession(student, location, session);
        return attended;
    }

    public String getNextSession(Student student){
        final DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
        String nextSession = dbHandler.getTimeOfNextSession(student);
        String newFormat = "HH:mm dd-MM-yyyy";
        String oldFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(oldFormat);
        try{
            Date date = formatter.parse(nextSession);
            formatter.applyPattern(newFormat);
            String newDate = formatter.format(date);
            Calendar c = Calendar.getInstance();
            c.setTime(c.getTime());
            c.add(Calendar.HOUR_OF_DAY, -1);
            Date time = c.getTime();
            if(date.before(time)){
                //delete this date
                dbHandler.nonAttendanceSession(student);
                return getNextSession(student);
            }
            else
                return newDate;
        }catch (ParseException e){
            e.printStackTrace();
            return "";
        }
    }
}