package com.example.piotrzadka.androidhttpserverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {
    TextView details;
    String studentDesc;
    Student student;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the intent
        Bundle extras = getIntent().getExtras();
        // create a student object from student object class that was passed over from the MainActivity
        student = (Student) extras.get("student");

        studentDesc =   "Name: "+student.getName()+"\n"+
                      "Gender: "+student.getGender()+"\n"+
                         "DOB: "+student.getDob()+"\n"+
                     "Address: "+student.getAddress()+"\n"+
                    "Postcode: "+student.getPostcode()+ "\n"+
                          "ID: "+student.getStudentNumber()+"\n"+
                "Course Title: "+student.getCourseTitle()+"\n"+
                  "Start Date: "+student.getStartDate()+"\n"+
                     "Bursary: "+student.getBursary()+"\n"+
                       "Email: "+student.getEmail();


        // Get element from activity
        details = findViewById(R.id.studentDesc);

        //Set details
        details.setText(String.valueOf(studentDesc));

    }

    // Placing edit button in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Edit button on click behaviour
    @Override
    public  boolean onOptionsItemSelected(MenuItem menu){
        Intent intent = new Intent(getApplicationContext(), UpdateStudentActivity.class);
        intent.putExtra("student", student);
        startActivity(intent);
        return super.onOptionsItemSelected(menu);
    }
}
