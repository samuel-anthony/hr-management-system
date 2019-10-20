package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Attendance extends AppCompatActivity implements LocationListener{
    private LocationManager locationManager;
    String nameAndEmail;
    Bundle bundle;
    JSONObject output;
    Spinner spinner;
    TextView dateNow;
    String clockIn = "0";
    String selectedProjectId;
    String formattedDate;
    Location currentUserLocation;
    ArrayList<HashMap<String,String>> completeProjectUserData = new ArrayList<HashMap<String,String>>();
    ArrayList<String> projectNameOnlyForUser = new ArrayList<String>();
    Location officeLocation = new Location("");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView employee_data =  findViewById(R.id.nameAndEmailAttendance);
        employee_data.setText(nameAndEmail);

        checkProjectForUser();

        dateNow = findViewById(R.id.date_time_now);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                LocalDateTime myDateObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = myDateObj.format(myFormatObj);
                dateNow.setText(formattedDate);
                handler.postDelayed(this, delay);
            }
        }, delay);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1 );
            return;
        }

    }

    public void onClickBackButton(View view){
        finish();
    }

    public void showCoordinate(View view){
        boolean flag = displayGpsStatus();//masi blom tau bakal kepake atau engga
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1 );
                return;
             }
        currentUserLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if(officeLocation.distanceTo(currentUserLocation) > 270.00){
            Toast.makeText(this, officeLocation.distanceTo(currentUserLocation)+" meters", Toast.LENGTH_LONG).show();
        }
        else {
            checkAttendanceUser();
        }
    }

    private boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    public void checkAttendanceUser(){
        class checkAttendanceToDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Attendance.this,"Mengambil data pegawai...","Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1") ){
                        if(clockIn.equalsIgnoreCase("0")){

                            clockIn = "1";
                        }
                        else{
                            clockIn = "2";
                        }
                    }
                    Toast.makeText(Attendance.this,output.getString("message"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    formattedDate = myDateObj.format(myFormatObj);
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("time",formattedDate);
                    params.put("is_clockIn",clockIn);
                    params.put("project_id",selectedProjectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.CheckAttendanceEmployee, params);
                return res;
            }
        }



        checkAttendanceToDB ae = new checkAttendanceToDB();
        ae.execute();
    }

    public void checkProjectForUser(){
        class checkProjectForUserToDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Attendance.this,"Mengambil data project...","Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String output) {
                super.onPostExecute(output);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(output);
                    JSONArray result = jsonObject.getJSONArray("result");
                    if(jsonObject.getString("clockin").isEmpty()){
                        LinearLayout m = findViewById(R.id.layout_clock_out_attendance);
                        m.setVisibility(LinearLayout.GONE);
                        clockIn = "1";
                    }
                    if(jsonObject.getString("clockout").isEmpty() && !jsonObject.getString("clockin").isEmpty()){
                        LinearLayout n = findViewById(R.id.layout_clock_in_attendance);
                        n.setVisibility(LinearLayout.GONE);
                        clockIn = "2";
                    }
                    if(!jsonObject.getString("clockin").isEmpty() && !jsonObject.getString("clockout").isEmpty()){
                        LinearLayout m = findViewById(R.id.layout_clock_out_attendance);
                        m.setVisibility(LinearLayout.GONE);
                        LinearLayout n = findViewById(R.id.layout_clock_in_attendance);
                        n.setVisibility(LinearLayout.GONE);
                        LinearLayout o = findViewById(R.id.layout_project_attendance);
                        o.setVisibility(LinearLayout.GONE);
                    }
                    TextView clockInTextView= findViewById(R.id.clock_in_attendance);
                    clockInTextView.setText(jsonObject.getString("clockin"));
                    TextView clockOutTextView= findViewById(R.id.clock_out_attendance);
                    clockOutTextView.setText(jsonObject.getString("clockout"));
                    for(int i = 0; i<result.length(); i++){
                        JSONObject jo = result.getJSONObject(i);
                        String project_id = jo.getString("project_id");
                        String project_name = jo.getString("project_name");
                        String latitude = jo.getString("latitude");
                        String longitude = jo.getString("longitude");

                        HashMap<String,String> data = new HashMap<>();
                        data.put("project_id",project_id);
                        data.put("project_name",project_name);
                        data.put("latitude",latitude);
                        data.put("longitude",longitude);
                        completeProjectUserData.add(data);
                        projectNameOnlyForUser.add(project_name);

                    }
                    loading.dismiss();
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Attendance.this,android.R.layout.simple_selectable_list_item, projectNameOnlyForUser);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner = findViewById(R.id.spinnerProjectAttendance);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String projectName = parent.getItemAtPosition(position).toString();
                            officeLocation.setLongitude(Double.valueOf(completeProjectUserData.get(position).get("longitude")));
                            officeLocation.setLatitude(Double.valueOf(completeProjectUserData.get(position).get("latitude")));
                            selectedProjectId = completeProjectUserData.get(position).get("project_id");
                            Toast.makeText(parent.getContext(), "Selected: " + projectName,    Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                            Toast.makeText(parent.getContext(), "Nothing Selected: ",    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.CheckProjectEmployee, params);
                return res;
            }
        }

        checkProjectForUserToDB ae = new checkProjectForUserToDB();
        ae.execute();
    }
}

