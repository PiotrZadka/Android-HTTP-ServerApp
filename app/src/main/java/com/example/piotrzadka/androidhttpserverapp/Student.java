package com.example.piotrzadka.androidhttpserverapp;

import java.io.Serializable;

/**
 * Created by Piotr Zadka on 21.03.2018.
 */

public class Student implements Serializable {
    private String name,gender,address,postcode,courseTitle,email,dob,startDate;
    private float bursary;
    private int studentNumber;

    Student(String name, String gender, String dob, String address, String postcode,
            int studentNumber, String courseTitle, String startDate, float bursary, String email){

        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.postcode = postcode;
        this.studentNumber = studentNumber;
        this.courseTitle = courseTitle;
        this.startDate = startDate;
        this.bursary = bursary;
        this.email = email;
    }

    // Getters
    public String getName(){
        return name;
    }
    public String getGender(){
        return gender;
    }
    public String getDob() {
        return dob;
    }
    public String getAddress() {
        return address;
    }
    public String getPostcode() {
        return postcode;
    }
    public int getStudentNumber() {
        return studentNumber;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public String getStartDate() {
        return startDate;
    }
    public float getBursary() {
        return bursary;
    }
    public String getEmail() {
        return email;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setBursary(float bursary) {
        this.bursary = bursary;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
