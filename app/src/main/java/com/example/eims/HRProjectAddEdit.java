package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HRProjectAddEdit extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {
    EditText searchBox,projectName,addressEditText;
    TextView longitudeTextView,latitudeTextView, dateFrom, dateTo;
    String selectedEmployeeID,projectID;
    SimpleDateFormat dateFormat;
    View datePickerView;
    UtilHelper utilHelper;
    ArrayList<HashMap<String,String>> completeEmployeeData = new ArrayList<HashMap<String,String>>();
    private static final String TAG = "HRProjectAddEdit";
    GoogleMap map;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        map = googleMap;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrproject_add_edit);
        utilHelper = new UtilHelper(this);
        Bundle bundle;

        bundle = getIntent().getExtras();
        projectID = bundle.getString("id");
        if(bundle.getString("sub_menu").equalsIgnoreCase("edit")){
            ((TextView)findViewById(R.id.title)).setText("Edit Form");
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        projectName = findViewById(R.id.projectName);
        addressEditText = findViewById(R.id.address);
        searchBox = findViewById(R.id.search_bar_edit_text);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateTo);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                    return true;
                }

                return false;
            }
        });

        initMap();
        getData(this,projectID);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = dateFormat.format(calendar.getTime());
        TextView a = (TextView) datePickerView;
        a.setText(currentDateString);
    }

    public void showDatePicker(View view){
        String selectedDate = ((TextView)view).getText().toString();
        DialogFragment datePicker = new DatePickerFragment(selectedDate);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
        datePickerView = view;
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(HRProjectAddEdit.this);
    }
    public void geoLocate(){
        String searchString = searchBox.getText().toString();
        Geocoder geocoder = new Geocoder(HRProjectAddEdit.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG,address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),15f,address.getAddressLine(0));
        }
    }

    public void moveCamera(LatLng latLng,float zoom,String title){
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        map.addMarker(options);
        addressEditText.setText(title);
        longitudeTextView.setText(String.valueOf(latLng.longitude));
        latitudeTextView.setText(String.valueOf(latLng.latitude));
    }

    public void onClickBackButton(View view){
        finish();
    }

    public void onClickSubmitButton(View view){
        String tempLong = longitudeTextView.getText().toString();
        String tempLat = latitudeTextView.getText().toString();
        String address = addressEditText.getText().toString();
        String projectName = ((EditText)findViewById(R.id.projectName)).getText().toString();
        if(!((TextView)(findViewById(R.id.dateFrom))).getText().toString().isEmpty() && !((TextView)(findViewById(R.id.dateTo))).getText().toString().isEmpty()  || projectName.isEmpty() || tempLong.isEmpty() || tempLat.isEmpty() || address.isEmpty()){
            try {
                Date dateFrom = dateFormat.parse(((TextView)(findViewById(R.id.dateFrom))).getText().toString());
                Date dateTo = dateFormat.parse(((TextView)(findViewById(R.id.dateTo))).getText().toString());
                String status = "";
                if(dateFrom.compareTo(dateTo) > 0){
                    utilHelper.createPopUpDialog("Error Input","DateTo should be later than DateFrom");
                }else if(tempLong.isEmpty() || tempLat.isEmpty() || address.isEmpty()){
                    utilHelper.createPopUpDialog("Map is not selected","please select the project Location");
                }else if(projectName.isEmpty()){
                    ((EditText) findViewById(R.id.login_email)).setError("Project Name is Requires");
                }
                else{
                    submitProject(this,projectID,projectName,address,tempLong,tempLat,((TextView)(findViewById(R.id.dateFrom))).getText().toString(),((TextView)(findViewById(R.id.dateTo))).getText().toString());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            utilHelper.createPopUpDialog("Error input","please fill the date from and date to");
        }

        /*if(tempLong.isEmpty() || tempLat.isEmpty() || address.isEmpty()){
            utilHelper.createPopUpDialog("Map is not selected","please select the project Location");
        }
        else{
            submitProject(this,projectID,projectName.getText().toString(),address,tempLong,tempLat);
        }*/
    }
    public boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HRProjectAddEdit.this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HRProjectAddEdit.this,available,9001);
        }
        else{
            Toast.makeText(this,"You can't make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void getData(Context context,String projectID){
        class getDataFromDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            Context context;
            String projectID;
            public getDataFromDB(Context context,String projectID){
                this.context = context;
                this.projectID = projectID;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Retrieving data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray resultProject = output.getJSONArray("projectManager");
                    ArrayList<String> arrayListProject = new ArrayList<String>();
                    if(resultProject.length()>0){
                        for(int i = 0; i<resultProject.length() ; i++){
                            JSONObject jo = resultProject.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("employee_id",jo.getString("employee_id"));
                            data.put("employee_name",jo.getString("employee_name"));
                            completeEmployeeData.add(data);
                            arrayListProject.add(jo.getString("employee_name"));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item, arrayListProject);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinnerProject = findViewById(R.id.spinnerProjectManager);
                        spinnerProject.setAdapter(arrayAdapter);
                        spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String projectName = parent.getItemAtPosition(position).toString();
                                selectedEmployeeID = completeEmployeeData.get(position).get("employee_id");
                                Toast.makeText(parent.getContext(), "Selected: " + projectName,    Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                                Toast.makeText(parent.getContext(), "Nothing Selected: ",    Toast.LENGTH_LONG).show();
                            }
                        });
                        if(!projectID.isEmpty()){
                            JSONArray projectData = output.getJSONArray("projectData");
                            JSONObject jo = projectData.getJSONObject(0);
                            projectName.setText(jo.getString("project_name"));
                            addressEditText.setText(jo.getString("address"));
                            moveCamera(
                                    new LatLng(Double.parseDouble(jo.getString("latitude")),Double.parseDouble(jo.getString("longitude"))),
                                    15f,
                                    jo.getString("address"));
                            longitudeTextView.setText(jo.getString("longitude"));
                            latitudeTextView.setText(jo.getString("latitude"));
                            dateFrom.setText(jo.getString("startDate"));
                            dateTo.setText(jo.getString("endDate"));
                        }
                    }
                    else {//brti ga ada projectManager yang bisa di assign or edit
                        utilHelper.createPopUpDialogCloseActivity("Lack number of PM","There is no available project manager. Please assign new project manager to be hired!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("project_id",projectID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetProjectManagerList, params);
                return res;
            }
        }

        getDataFromDB ae = new getDataFromDB(context,projectID);
        ae.execute();
    }

    public void submitProject(Context context,String projectID,String projectName,String address,String longitude,String latitude, String dateFrom,String dateTo){
        class insertToDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            String projectID,projectName,address,longitude,latitude, dateTo, dateFrom;
            public insertToDB(Context context,String projectID,String projectName,String address,String longitude,String latitude, String dateFrom,String dateTo){
                this.context = context;
                this.projectID = projectID;
                this.projectName = projectName;
                this.address = address;
                this.longitude = longitude;
                this.latitude = latitude;
                this.dateFrom=dateFrom;
                this.dateTo=dateTo;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Updating employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1")){
                        //sukses submit..
                        utilHelper.createPopUpDialogCloseActivity("Success Message",output.getString("message"));
                        //addResultToView(id,actionString);
                    }
                    else{
                        utilHelper.createPopUpDialog("Ooopsss",output.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("project_id",projectID);
                params.put("project_name",projectName);
                params.put("project_manager_id",selectedEmployeeID);
                params.put("address",address);
                params.put("longitude",longitude);
                params.put("latitude",latitude);
                params.put("dateF",dateFrom);
                params.put("dateT",dateTo);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.AddEditProject, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context, projectID, projectName, address, longitude, latitude,dateFrom,dateTo);
        ae.execute();
    }
}
