package com.example.attendanceapp;

public class AttendanceLocation {
    //fields
    private int locationID;
    private String longitude;
    private String latitude;
    private int radius;

    public AttendanceLocation(){}

    public AttendanceLocation(int id, String longitude, String latitude, int radius){
        this.locationID = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


}
