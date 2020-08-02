package com.example.attendanceapp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {
    //Database setup
    private static final int DATABASE_VERSION               = 1;
    private static final String DATABASE_NAME               = "AttendanceDB";
    public static final String USER_TABLE_NAME              = "Users";
    public static final String COLUMN_USER_ID               = "UserID";
    public static final String COLUMN_USER_NAME             = "UserName";

    public static final String SESSION_TABLE_NAME           = "Sessions";
    public static final String COLUMN_SESSION_ID            = "SessionID";
    public static final String COLUMN_SESSION_TIME          = "SessionTime";
    public static final String COLUMN_SESSION_TUTOR         = "Tutor";

    public static final String LOCATION_TABLE_NAME          = "Locations";
    public static final String COLUMN_LOCATION_ID           = "LocationID";
    public static final String COLUMN_LATITUDE              = "Latitude";
    public static final String COLUMN_LONGITUDE             = "Longitude";

    public static final String COLUMN_RADIUS                = "Radius";

    public static final String STUDENT_SESSIONS_TABLE_NAME              = "Student_Sessions";
    public static final String COLUMN_STUDENT_SESSIONS_ID               = "UID";
    public static final String COLUMN_STUDENT_SESSIONS_SESSION_ID       = "SessionID";
    public static final String COLUMN_STUDENT_SESSIONS_USER_ID          = "UserID";
    public static final String COLUMN_STUDENT_SESSIONS_LOCATION_ID      = "LocationID";
    public static final String COLUMN_STUDENT_SESSIONS_ATTEND           = "Attendance"; //0 Not yet attended, 1 attended, 2 didn't attend.



    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        //Creation of the database
        String CREATE_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS " + SESSION_TABLE_NAME + " (" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY, " + COLUMN_SESSION_TIME + " TEXT, " + COLUMN_SESSION_TUTOR + " TEXT );";
        String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + LOCATION_TABLE_NAME + " (" + COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY, " + COLUMN_LATITUDE + " TEXT, " + COLUMN_LONGITUDE + " TEXT, " + COLUMN_RADIUS + " INTEGER );";
        String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY, " + COLUMN_USER_NAME + " TEXT );";
        String CREATE_STUDENT_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS " + STUDENT_SESSIONS_TABLE_NAME + " (" + COLUMN_STUDENT_SESSIONS_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_STUDENT_SESSIONS_SESSION_ID + " INTEGER, "
                + COLUMN_STUDENT_SESSIONS_USER_ID + " INTEGER, "
                + COLUMN_STUDENT_SESSIONS_LOCATION_ID + " INTEGER, "
                + COLUMN_STUDENT_SESSIONS_ATTEND + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_STUDENT_SESSIONS_SESSION_ID + ") REFERENCES " +SESSION_TABLE_NAME + "(" + COLUMN_SESSION_ID+ "), " +
                "FOREIGN KEY (" + COLUMN_STUDENT_SESSIONS_USER_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + COLUMN_USER_ID+ "), " +
                "FOREIGN KEY (" + COLUMN_STUDENT_SESSIONS_LOCATION_ID + ") REFERENCES " +LOCATION_TABLE_NAME + "(" + COLUMN_LOCATION_ID+ "));";
        //Simulating data
        String POPULATE_TABLE = "INSERT INTO " + USER_TABLE_NAME + " (" + COLUMN_USER_ID + ", " + COLUMN_USER_NAME+ ")" +
                "VALUES ('21', 'Dan'), ('43', 'Pete'),( '1', 'Dave');";
        String POPULATE_LOCATION = "INSERT INTO " + LOCATION_TABLE_NAME + " (" + COLUMN_LOCATION_ID + ", " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE + ", " + COLUMN_RADIUS + ") " +
                "VALUES ('1', '53.3734903', '-1.4925763', '50'), ('2', '53.379189320070346', '-1.465623654452366', '50'), ('3', '53.37686715384463', '-1.4677884038548594', '50');";
        //Collegiate, Owen, Cantor
        String POPULATE_SESSION = "INSERT INTO "+ SESSION_TABLE_NAME + " (" + COLUMN_SESSION_ID + ", " + COLUMN_SESSION_TIME + ", " + COLUMN_SESSION_TUTOR + ") " +
                "VALUES ('1', '2020-08-20 10:00:00', 'Mark'),('2', '2020-08-20 12:00:00', 'Steve'),('3', '2020-08-20 15:00:00', 'Trisha'), ('4', '2020-07-23 16:20:00', 'Frances');";
        String POPULATE_STUDENT_SESSION = "INSERT INTO "+ STUDENT_SESSIONS_TABLE_NAME + " (" + COLUMN_STUDENT_SESSIONS_ID + ", " + COLUMN_STUDENT_SESSIONS_SESSION_ID + ", " + COLUMN_STUDENT_SESSIONS_USER_ID + ", " + COLUMN_STUDENT_SESSIONS_LOCATION_ID +", " +COLUMN_STUDENT_SESSIONS_ATTEND + ") " +
                "VALUES ('2', '1', '43', '2', '0'), ('3', '2', '21', '3', '0'), ('4', '3', '1', '1', '0'), ('5', '2', '43', '3', '0'), ('6', '3', '21', '1', '0'), ('7','4','21', '2', '0');";
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_STUDENT_SESSION_TABLE);
        db.execSQL(POPULATE_TABLE);
        db.execSQL(POPULATE_LOCATION);
        db.execSQL(POPULATE_SESSION);
        db.execSQL(POPULATE_STUDENT_SESSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version, int newVersion){}
    public String loadHandler() {
        String result = "";
        String q = "Select * FROM " + USER_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        while (cursor.moveToNext()){
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 + System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
    public void addHandler (Student student){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, student.getStudentID());
        values.put(COLUMN_USER_NAME, student.getStudentName());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }
    public Student findHandler (String studentID){

        String q = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = " + "'" + studentID + "';";

        SQLiteDatabase db = this.getWritableDatabase();

        Student student = new Student();
        Cursor cursor = db.rawQuery(q, null );
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            student.setStudentID(Integer.parseInt(cursor.getString(0)));
            student.setStudentName((cursor.getString(1)));
            cursor.close();
        }
        else
            student = null;
        db.close();
        return student;
    }
    public boolean deleteHandler(int ID){
        boolean result = false;
        String q = "Select * FROM " + USER_TABLE_NAME + " WHERE " + COLUMN_USER_ID + "= '" + String.valueOf(ID) + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        Student student = new Student();
        if (cursor.moveToFirst()){
            student.setStudentID(Integer.parseInt(cursor.getString(0)));
            db.delete(USER_TABLE_NAME, COLUMN_USER_ID + "=?", new String[] {
                    String.valueOf(student.getStudentID())
            });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
    public boolean updateHandler(int ID, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(COLUMN_USER_ID, ID);
        vals.put(COLUMN_USER_NAME, name);
        return db.update(USER_TABLE_NAME, vals, COLUMN_USER_ID + "=" + ID, null) > 0;
    }

    public String[] getAllUsers(){
        String q = "Select " + COLUMN_USER_ID + " FROM " + USER_TABLE_NAME + ";";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(q, null);
        int counter = 0;
        String[] users = new String[cursor.getCount()];
        while (cursor.moveToNext()){
            users[counter] = Integer.toString(cursor.getInt(0));
            counter = counter + 1;
        }
        cursor.close();
        db.close();
        return users;
    }

    public String getTimeOfNextSession (Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + ", " +
                COLUMN_SESSION_TIME + ", " +
                USER_TABLE_NAME + "." + COLUMN_USER_ID + ", " +
                COLUMN_STUDENT_SESSIONS_ATTEND + " " +
                "FROM " + STUDENT_SESSIONS_TABLE_NAME + " " +
                "INNER JOIN " + USER_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_USER_ID + " = " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "INNER JOIN " + SESSION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + " " +
                "WHERE " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " IS " + student.getStudentID() + " " +
                "AND " + COLUMN_STUDENT_SESSIONS_ATTEND + " IS 0 " +
                "ORDER BY " + COLUMN_SESSION_TIME + " ASC;";
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(1);
            cursor.close();
            db.close();
            return s;
        }
        return "No next session";
    }

    public boolean nonAttendanceSession (Student student){
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + ", " +
                COLUMN_SESSION_TIME + ", " +
                USER_TABLE_NAME + "." + COLUMN_USER_ID + ", " +
                COLUMN_STUDENT_SESSIONS_ATTEND + " " +
                "FROM " + STUDENT_SESSIONS_TABLE_NAME + " " +
                "INNER JOIN " + USER_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_USER_ID + " = " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "INNER JOIN " + SESSION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + " " +
                "WHERE " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " IS " + student.getStudentID() + " " +
                "AND " + COLUMN_STUDENT_SESSIONS_ATTEND + " IS 0 " +
                "ORDER BY " + COLUMN_SESSION_TIME + " ASC;";
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            ContentValues vals = new ContentValues();
            vals.put(COLUMN_STUDENT_SESSIONS_ATTEND, 2);
            result = true;
            db.update(STUDENT_SESSIONS_TABLE_NAME, vals, COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + cursor.getString(0), null);
        }
        cursor.close();
        db.close();
        return result;
    }




    public Location getLocationData(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + ", " +
                COLUMN_SESSION_TIME + ", " +
                LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID + ", " +
                COLUMN_LONGITUDE + ", " +
                COLUMN_LATITUDE + ", " +
                USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "FROM " + STUDENT_SESSIONS_TABLE_NAME + " " +
                "INNER JOIN " + LOCATION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_LOCATION_ID + " = " + LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID +  " " +
                "INNER JOIN " + USER_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_USER_ID + " = " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "INNER JOIN " + SESSION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + "; " +
                "WHERE " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " IS " + student.getStudentID() + " " +
                "AND " + COLUMN_STUDENT_SESSIONS_ATTEND + " IS NOT 2 " +
                "ORDER BY DATETIME(" + COLUMN_SESSION_TIME + ") DESC";

        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            String longitude = cursor.getString(3);
            String latitude = cursor.getString(4);
            Location location = new Location("");
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));

            return location;
        }
        return null;

    }

    public boolean attendSession(Student student, Location location, String session){
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String q = "SELECT " + COLUMN_STUDENT_SESSIONS_ID + ", " +
                SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + ", " +
                COLUMN_SESSION_TIME + ", " +
                LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID + ", " +
                COLUMN_LONGITUDE + ", " +
                COLUMN_LATITUDE + ", " +
                USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "FROM " + STUDENT_SESSIONS_TABLE_NAME + " " +
                "INNER JOIN " + LOCATION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_LOCATION_ID + " = " + LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID +  " " +
                "INNER JOIN " + USER_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_USER_ID + " = " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "INNER JOIN " + SESSION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + " " +
                "WHERE " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " IS " + Integer.toString(student.getStudentID())+ " " +
                "AND " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_TIME + " IS '" + session + "' " +
                "AND " + LOCATION_TABLE_NAME + "." + COLUMN_LATITUDE + " IS '" + location.getLatitude() + "' " +
                "AND " + LOCATION_TABLE_NAME + "." + COLUMN_LONGITUDE + " IS '" + location.getLongitude() + "' " +
                "ORDER BY DATETIME(" + COLUMN_SESSION_TIME + ") ASC;";

        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            ContentValues vals = new ContentValues();
            vals.put(COLUMN_STUDENT_SESSIONS_ATTEND, 1);
            result = true;
            db.update(STUDENT_SESSIONS_TABLE_NAME, vals, COLUMN_STUDENT_SESSIONS_ID + " = " + cursor.getString(0), null);
        }
        db.close();
        cursor.close();
        return result;

    }

    public int getRadius(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + ", " +
                COLUMN_SESSION_TIME + ", " +
                LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID + ", " +
                COLUMN_RADIUS + ", " +
                USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "FROM " + STUDENT_SESSIONS_TABLE_NAME + " " +
                "INNER JOIN " + LOCATION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_LOCATION_ID + " = " + LOCATION_TABLE_NAME + "." + COLUMN_LOCATION_ID +  " " +
                "INNER JOIN " + USER_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_USER_ID + " = " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " " +
                "INNER JOIN " + SESSION_TABLE_NAME + " ON " + STUDENT_SESSIONS_TABLE_NAME +"." + COLUMN_STUDENT_SESSIONS_SESSION_ID + " = " + SESSION_TABLE_NAME + "." + COLUMN_SESSION_ID + "; " +
                "WHERE " + USER_TABLE_NAME + "." + COLUMN_USER_ID + " IS " + student.getStudentID() +
                "ORDER BY DATETIME(" + COLUMN_SESSION_TIME + ") DESC";

        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            return Integer.parseInt(cursor.getString(3));
        }
        return 0;

    }

    public void addLocationHandler (AttendanceLocation location){
        ContentValues values = new ContentValues();
        if(location.getLocationID() != 0) {
            values.put(COLUMN_LOCATION_ID, location.getLocationID());
        }
        values.put(COLUMN_LONGITUDE, location.getLongitude());
        values.put(COLUMN_LATITUDE, location.getLatitude());
        values.put(COLUMN_RADIUS, location.getRadius());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LOCATION_TABLE_NAME, null, values);
        db.close();
    }
    public boolean addSessionHandler (Session session){
        ContentValues values = new ContentValues();
        if(session.getSessionID() != 0){
            values.put(COLUMN_SESSION_ID, session.getSessionID());
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        f.setLenient(false);
        Date d;
        try {
            d = f.parse(session.getSessionTime());
        }
        catch(ParseException e){
            e.printStackTrace();
            return false;
        }

        values.put(COLUMN_SESSION_TIME, session.getSessionTime());
        values.put(COLUMN_SESSION_TUTOR, session.getTutor());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(SESSION_TABLE_NAME, null, values);
        db.close();
        return true;
    }
    public boolean addClassHandler (int sessionID, int locationID, int userID){

        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_SESSIONS_SESSION_ID, sessionID);
        values.put(COLUMN_STUDENT_SESSIONS_LOCATION_ID, locationID);
        values.put(COLUMN_STUDENT_SESSIONS_USER_ID, userID);
        values.put(COLUMN_STUDENT_SESSIONS_ATTEND, 0);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(STUDENT_SESSIONS_TABLE_NAME, null, values);
        db.close();
        return true;
    }
}
