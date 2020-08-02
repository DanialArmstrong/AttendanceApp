package com.example.attendanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SimulateDataActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulate_data);
        //Create session
        final EditText sessionID = findViewById(R.id.sessionIdEditText);
        final EditText date = findViewById(R.id.dateEditText);
        final EditText tutorName = findViewById(R.id.tutorNameEditText);
        final Button createSession = findViewById(R.id.sessionCreateBtn);
        //Create a location
        final EditText locationID = findViewById(R.id.locationIDEditText);
        final EditText longitude = findViewById(R.id.longitudeEditText);
        final EditText latitude = findViewById(R.id.latitudeEditText);
        final EditText radius = findViewById(R.id.radiusEditText);
        final Button createLocation = findViewById(R.id.createLocationBtn);
        //Create a class from session, location, and users
        final EditText locationIDForClass = findViewById(R.id.locationIDClassEditText);
        final EditText sessionIDForClass = findViewById(R.id.sessionIDClassEditText);
        final ListView usersListView = findViewById(R.id.usersListView);
        final Button createClass = findViewById(R.id.createClassBtn);

        final DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
        String[] USERS = dbHandler.getAllUsers();

        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersListView.setItemsCanFocus(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, USERS);
        final ArrayList<String> selectedList = new ArrayList();
        usersListView.setAdapter(adapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedList.add(usersListView.getItemAtPosition(position).toString());
            }
        });

        createLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int id;
                try{
                    id = Integer.parseInt(locationID.getText().toString());
                }catch(NumberFormatException e)
                {
                    id = 0;
                }
                AttendanceLocation loc = new AttendanceLocation(
                        id,
                        longitude.getText().toString(),
                        latitude.getText().toString(),
                        Integer.parseInt(radius.getText().toString()));

                dbHandler.addLocationHandler(loc);
                Toast.makeText(getApplicationContext(), "Added location successfully" ,Toast.LENGTH_SHORT).show();
            }
        });

        createSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int id;
                try {
                    id = Integer.parseInt(sessionID.getText().toString());
                }catch(NumberFormatException e)
                {
                    id = 0;
                }
                Session session = new Session(
                        id,
                        date.getText().toString(),
                        tutorName.getText().toString()
                );
                 String message = dbHandler.addSessionHandler(session) ? "Successfully added a session" : "Couldn't add the session";
                Toast.makeText(getApplicationContext(), message ,Toast.LENGTH_SHORT).show();

            }
        });

        createClass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int sessionId = Integer.parseInt(sessionIDForClass.getText().toString());
                int locationId = Integer.parseInt(locationIDForClass.getText().toString());
                for (String user : selectedList) {
                    dbHandler.addClassHandler(sessionId, locationId, Integer.parseInt(user));
                }

                Toast.makeText(getApplicationContext(), "Added classes" ,Toast.LENGTH_SHORT).show();

            }
        });


    }
}