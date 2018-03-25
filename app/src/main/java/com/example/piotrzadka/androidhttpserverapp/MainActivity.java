package com.example.piotrzadka.androidhttpserverapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String[] students;
    ArrayList<Student> allStudent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Network thread hack
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void onResume(){
        super.onResume();

        //Point to element on the activity tab
        final ListView studentList = findViewById(R.id.studentList);
        Button addStudent = findViewById(R.id.addButton);

        //Http connection
        HttpURLConnection urlConnection;
        InputStream in = null;

        try{
            // url we want to connect to
            URL url = new URL("http://radikaldesign.co.uk/sandbox/studentapi/getallstudents.php?apikey=13d0d2fcfa");

            // open the connection to the specified URL
            urlConnection = (HttpURLConnection) url.openConnection();

            // get the response from the server in an input stream
            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //convert the input stream to a string
        String response = convertStreamToString(in);

        //Debuging result from the server response in Logcat
        System.out.println("Server response = "+response);


        try {
            // declare a new json array and pass it the string response from the server
            // this will convert the string into a JSON array which we can then iterate
            // over using a loop
            JSONArray jsonArray = new JSONArray(response);

            // instantiate the students array and set the size
            // to the amount of cheese object returned by the server
            students = new String[jsonArray.length()];

            // use a for loop to iterate over the JSON array
            for (int i=0; i < jsonArray.length(); i++)
            {
                // the following line of code will get the details of the student from the
                // current JSON object and store it in a string variables
                String name = jsonArray.getJSONObject(i).get("name").toString();
                String gender = jsonArray.getJSONObject(i).get("gender").toString();
                String dob = jsonArray.getJSONObject(i).get("dob").toString();
                String address = jsonArray.getJSONObject(i).get("address").toString();
                String postcode = jsonArray.getJSONObject(i).get("postcode").toString();
                int stuId = Integer.parseInt(jsonArray.getJSONObject(i).get("studentNumber").toString());
                String courseTitle = jsonArray.getJSONObject(i).get("courseTitle").toString();
                String startDate = jsonArray.getJSONObject(i).get("startDate").toString();
                float bursary = Float.parseFloat(jsonArray.getJSONObject(i).get("bursary").toString());
                String email = jsonArray.getJSONObject(i).get("email").toString();


                // add the name and email of the current student to the students array
                students[i] = name+"\n"+email;

                //Create new student object and assign each object its details
                Student student = new Student(name,gender,dob,address,postcode,stuId,courseTitle,startDate,bursary,email);
                allStudent.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // An array adapter to do all the hard work just tell it the (context, where?, what?)
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, students);
        // Connect array to our listView in activity
        studentList.setAdapter(arrayAdapter);

        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // declare a new intent and give it the context and
                // specify which activity you want to open/start
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);

                // add/put the selected student object in to the intent which will
                // be passed over to the activity that is started
                // note we use a KEY:VALUE structure to pass variable/objects
                // between activities. Here the key is ‘student’ and the value is
                // the student object from the student array list using the position
                // which is specified by the ‘i’ variable.
                intent.putExtra("student", allStudent.get(i));
                // launch the activity
                startActivity(intent);
            } });

        // Delete student on long click on list view item
        studentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Gson gson = new Gson();
                final HashMap<String, String> params = new HashMap<>();
                int studentNumber = allStudent.get(i).getstuId();
                String idJson = gson.toJson(studentNumber);
                params.put("studentnumber", idJson);
                params.put("apikey", "13d0d2fcfa");
                allStudent.remove(i);
                String url = "http://radikaldesign.co.uk/sandbox/studentapi/delete.php";
                deleteStudent(url,params);
                onResume();
                return true;
            }
        });



        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewStudentActivity.class);
                startActivity(intent);
            }
        });


    }

    private String convertStreamToString(InputStream in) {
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String deleteStudent(String requestURL, HashMap<String, String> postDataParams) {
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
                Toast.makeText(this, "Student Deleted", Toast.LENGTH_SHORT).show();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            else {
                Toast.makeText(this, "Error deleting Student", Toast.LENGTH_SHORT).show();
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

    // Placing refresh button in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // refresh button on click behaviour
    @Override
    public  boolean onOptionsItemSelected(MenuItem menu){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        return super.onOptionsItemSelected(menu);
    }


}
