package com.example.attendanceapp;

import java.util.Date;

public class Session {
    //fields
    private int sessionID;
    private String sessionTime;
    private String tutor;

    public Session(){}

    public Session(int id, String time, String tutor){
        this.sessionID = id;
        this.sessionTime = time;
        this.tutor = tutor;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }
}
