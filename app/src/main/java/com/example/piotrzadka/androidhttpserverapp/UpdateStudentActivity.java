package com.example.piotrzadka.androidhttpserverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UpdateStudentActivity extends AppCompatActivity {
    Student student;
    EditText studentName,studentDOB,studentAddress,studentPostcode,studentNumber,studentCourse,studentStart,studentBursary,studentEmail;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);
    }

    // When we get back to the Activity
    protected void onResume(){
        super.onResume();
        Bundle extras = getIntent().getExtras();

        // dropdown M/F
        final Spinner studentGender = findViewById(R.id.spinnerGenderUpdate);
        //create a list of items for the spinner.
        String[] genders = new String[]{"M", "F"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        //set the spinners adapter to the previously created one.
        studentGender.setAdapter(adapter);

        update = findViewById(R.id.buttonUpdate);
        final HashMap<String, String> params = new HashMap<>();

        studentName = findViewById(R.id.stuNameUpdateTextView);
        studentDOB = findViewById(R.id.dobUpdateTextView);
        studentAddress = findViewById(R.id.addressUpdateTextView);
        studentPostcode = findViewById(R.id.postcodeUpdateTextView);
        studentNumber = findViewById(R.id.idUpdateTextView);
        studentCourse = findViewById(R.id.courseTitleUpdateTextView);
        studentStart = findViewById(R.id.startDateUpdateTextView);
        studentBursary = findViewById(R.id.bursaryUpdateTextView);
        studentEmail = findViewById(R.id.emailUpdateTextView);

        //Set EditTest fields with existing values from DetailsActivity
        student = (Student) extras.get("student");

        studentName.setText(student.getName());
        studentDOB.setText(student.getDob());
        studentAddress.setText(student.getAddress());
        studentPostcode.setText(student.getPostcode());
        studentNumber.setText(String.valueOf(student.getStudentNumber()));
        studentCourse.setText(student.getCourseTitle());
        studentStart.setText(student.getStartDate());
        studentBursary.setText(String.valueOf(student.getBursary()));
        studentEmail.setText(student.getEmail());

        // Check what gender was previously selected and set in edit the same value
        if(student.getGender().equals("M")){
            studentGender.setSelection(0);
        }
        else{
            studentGender.setSelection(1);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set variables for new student Object that we going to pass to update the API.
                Gson gson = new Gson();
                String updateName = studentName.getText().toString();
                String updateDOB = studentDOB.getText().toString();
                String updateAddress = studentAddress.getText().toString();
                String updatePostcode = studentPostcode.getText().toString();
                int updateNumber = Integer.parseInt(studentNumber.getText().toString());
                String updateCourse = studentCourse.getText().toString();
                String updateStart = studentStart.getText().toString();
                float updateBursary = Float.parseFloat(studentBursary.getText().toString());
                String updateEmail = studentEmail.getText().toString();
                String updateGender = studentGender.getSelectedItem().toString();

                Student updateStudent = new Student(updateName, updateGender, updateDOB, updateAddress, updatePostcode, updateNumber, updateCourse, updateStart, updateBursary, updateEmail);
                String studentJson = gson.toJson(updateStudent);
                System.out.println(studentJson);
                params.put("json", studentJson);
                params.put("apikey", "13d0d2fcfa");
                String url = "http://radikaldesign.co.uk/sandbox/studentapi/update.php";
                postUpdateStudent(url, params);

                // Clear Activity Stack and return to edit activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public String postUpdateStudent(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            // create the connection object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //write/send/POST data to the connection using output stream and buffered writer
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            //clear writer
            writer.flush();
            writer.close();

            //get the server response code to determine what to do next (errors/success)
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, "Student Updated", Toast.LENGTH_SHORT).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            else {
                Toast.makeText(this, "Error updating Student", Toast.LENGTH_SHORT).show();
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("response = " + response);
        return response;
    }

    // this method converts a hashmap to a URL query string of key/value pairs (eg. name=piotr&age=15&...)
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
