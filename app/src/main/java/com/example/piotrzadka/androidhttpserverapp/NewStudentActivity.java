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

public class NewStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        //get the spinner from the xml.
        final Spinner dropdown = findViewById(R.id.spinnerGender);
        //create a list of items for the spinner.
        String[] items = new String[]{"M", "F"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);


        Button submit = findViewById(R.id.submitButton);
        final HashMap<String, String> params = new HashMap<>();
        final EditText name = findViewById(R.id.stuNameTextView);
        final EditText dob = findViewById(R.id.dobTextView);
        final EditText address = findViewById(R.id.addressTextView);
        final EditText postcode = findViewById(R.id.postcodeTextView);
        final EditText id = findViewById(R.id.idTextView);
        final EditText courseTitle = findViewById(R.id.courseTitleTextView);
        final EditText startDate = findViewById(R.id.startDateTextView);
        final EditText bursary = findViewById(R.id.bursaryTextView);
        final EditText email = findViewById(R.id.emailTextView);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String nameS = name.getText().toString();
                String dobS = dob.getText().toString();
                String addressS = address.getText().toString();
                String postcodeS = postcode.getText().toString();
                int idS = Integer.parseInt(id.getText().toString());
                String courseTitleS = courseTitle.getText().toString();
                String startDateS = startDate.getText().toString();
                String emailS = email.getText().toString();
                float bursaryS = Float.parseFloat(bursary.getText().toString());
                String genderS = dropdown.getSelectedItem().toString();

                Student student = new Student(nameS, genderS, dobS, addressS, postcodeS, idS, courseTitleS, startDateS, bursaryS, emailS);
                String studentJson = gson.toJson(student);
                System.out.println(studentJson);
                params.put("json", studentJson);
                params.put("apikey", "13d0d2fcfa");
                String url = "http://radikaldesign.co.uk/sandbox/studentapi/add.php";
                postNewStudent(url, params);

                // Clear Activity Stack and return to edit activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

        public String postNewStudent(String requestURL, HashMap<String, String> postDataParams) {
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
                    Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }
                else {
                    Toast.makeText(this, "Error adding Student", Toast.LENGTH_SHORT).show();
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
