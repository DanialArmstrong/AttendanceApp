package com.example.attendanceapp;

public class Student {
    //fields
    private int studentID;
    private String studentName;

    public Student(){}

    public Student(int id, String studentName){
        this.studentID = id;
        this.studentName = studentName;
    }

    public void setStudentID(int id){
        this.studentID = id;
    }

    public int getStudentID(){
        return this.studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
