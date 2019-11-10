package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Report_Menu extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String nameAndEmail,statusSelected,typeSelected;
    Bundle bundle;
    JSONObject output;
    String sub_menu_text;
    SimpleDateFormat dateFormat;
    UtilHelper utilHelper;
    View datePickerView;
    LinearLayout searchResult;
    ArrayList<HashMap<String,String>> completeTypeDate = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completeStatusData = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__menu);
        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
            //JSONObject temp = new JSONObject(bundle.getString("sub_menu"));
            sub_menu_text = bundle.getString("sub_menu");
        } catch (JSONException e) {
            e.printStackTrace();

        }
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);
        TextView sub_menu_name = findViewById(R.id.sub_menu_name);
        sub_menu_name.setText(sub_menu_text);

        Fragment fragment = null;
        if(sub_menu_text.equalsIgnoreCase("Attendance")){
            fragment = new Attendance_Report();
        }
        else if(sub_menu_text.equalsIgnoreCase("Leave")){
            fragment = new Leave_Report();
        }
        else if(sub_menu_text.equalsIgnoreCase("Claim")){
            fragment = new Claim_Report();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.sub_menu, fragment);
        ft.commit();
        utilHelper = new UtilHelper(Report_Menu.this);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        searchResult = findViewById(R.id.search_result_scroll);
        initializeData(Report_Menu.this,sub_menu_text);
    }
    public void onClickBackButton(View view){
        finish();
    }

    public void showDatePicker(View view){
        String selectedDate = ((TextView)view).getText().toString();
        DialogFragment datePicker = new DatePickerFragment(selectedDate);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
        datePickerView = view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = dateFormat.format(calendar.getTime());
        TextView a = (TextView) datePickerView;
        a.setText(currentDateString);

    }

    public void onClickSearchButton(View view){
        searchResult.removeAllViews();
        if(view == findViewById(R.id.searchAttendance)){
            if(!((TextView)(findViewById(R.id.dateFrom))).getText().toString().isEmpty() && !((TextView)(findViewById(R.id.dateTo))).getText().toString().isEmpty()){
                try {
                    Date dateFrom = dateFormat.parse(((TextView)(findViewById(R.id.dateFrom))).getText().toString());
                    Date dateTo = dateFormat.parse(((TextView)(findViewById(R.id.dateTo))).getText().toString());
                    String status = "";
                    if(dateFrom.compareTo(dateTo) > 0){
                        utilHelper.createPopUpDialog("Error Input","DateTo should be later than DateFrom");
                    }
                    else{
                        if(!statusSelected.equalsIgnoreCase("0")){
                            status = statusSelected;
                        }
                        getEmployeeAttendanceData(Report_Menu.this,((TextView)(findViewById(R.id.dateFrom))).getText().toString(),((TextView)(findViewById(R.id.dateTo))).getText().toString(),status);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                utilHelper.createPopUpDialog("Error input","please fill all of the data");
            }
        }
        else {
            String status = "";
            if(!statusSelected.equalsIgnoreCase("0")){
                status = statusSelected;
            }
            String type = "";
            if(!typeSelected.equalsIgnoreCase("0")){
                type = typeSelected;
            }
            if(view == findViewById(R.id.searchLeave)) {
                getEmployeeLeaveData(Report_Menu.this, type, status);
            }
            else if(view == findViewById(R.id.searchClaim)){
                getEmployeeClaimData(Report_Menu.this, type, status);
            }
        }
    }

    public void onClickClearButton(View view){
        searchResult.removeAllViews();
    }

    public void getEmployeeAttendanceData(Context context,String dateFrom,String dateTo,String status){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            String dateFrom,dateTo,status;
            retrieveDataDB(Context context,String dateFrom,String dateTo,String status){
                this.context = context;
                this.dateFrom = dateFrom;
                this.dateTo = dateTo;
                this.status = status;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("attendance");
                     if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(true,true);
                            //date
                            LinearLayout subContainer = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelDate = utilHelper.createTextView("Date",4.0f);
                            TextView dataDate = utilHelper.createTextView(jo.getString("date"),4.0f);
                            subContainer.addView(labelDate);
                            subContainer.addView(dataDate);
                            //clockin
                            LinearLayout subContainer2 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelClockIn = utilHelper.createTextView("Clock-In",4.0f);;
                            TextView dataClockIn = utilHelper.createTextView(jo.getString("clockIn"),4.0f);
                            subContainer2.addView(labelClockIn);
                            subContainer2.addView(dataClockIn);
                            //clockout
                            LinearLayout subContainer3 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelClockOut = utilHelper.createTextView("Clock-Out",4.0f);
                            TextView dataClockOut = utilHelper.createTextView(jo.getString("clockOut"),4.0f);
                            subContainer3.addView(labelClockOut);
                            subContainer3.addView(dataClockOut);
                            //status
                            LinearLayout subContainer4 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelStatus = utilHelper.createTextView("Status",4.0f);
                            TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                            subContainer4.addView(labelStatus);
                            subContainer4.addView(dataStatus);

                            container.addView(subContainer);
                            container.addView(subContainer2);
                            container.addView(subContainer3);
                            container.addView(subContainer4);
                            searchResult.addView(container);
                        }
                    }
                    else{
                        TextView dataStatus = utilHelper.createTextView("No Data Available");
                        searchResult.addView(dataStatus);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("dateFrom",dateFrom);
                    params.put("dateTo",dateTo);
                    params.put("status",status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchAttendanceDataEmployee, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,dateFrom,dateTo,status);
        ae.execute();
    }

    public void getEmployeeLeaveData(Context context,String type,String status){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            String type,status;
            retrieveDataDB(Context context,String type,String status){
                this.context = context;
                this.type = type;
                this.status = status;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("leave");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(true,true);
                            //type
                            LinearLayout subContainer = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelType = utilHelper.createTextView("Type",4.0f);
                            TextView dataType = utilHelper.createTextView(jo.getString("type"),4.0f);
                            subContainer.addView(labelType);
                            subContainer.addView(dataType);
                            //DateFrom
                            LinearLayout subContainer2 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelDateFrom = utilHelper.createTextView("Date-From",4.0f);
                            TextView dataDateFrom = utilHelper.createTextView(jo.getString("dateFrom"),4.0f);
                            subContainer2.addView(labelDateFrom);
                            subContainer2.addView(dataDateFrom);
                            //DateTo
                            LinearLayout subContainer3 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelDateTo = utilHelper.createTextView("Date-To",4.0f);
                            TextView dataDateTo = utilHelper.createTextView(jo.getString("dateTo"),4.0f);
                            subContainer3.addView(labelDateTo);
                            subContainer3.addView(dataDateTo);
                            //status
                            LinearLayout subContainer4 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelStatus = utilHelper.createTextView("Status",4.0f);
                            TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                            subContainer4.addView(labelStatus);
                            subContainer4.addView(dataStatus);

                            container.addView(subContainer);
                            container.addView(subContainer2);
                            container.addView(subContainer3);
                            container.addView(subContainer4);
                            searchResult.addView(container);
                        }
                    }
                    else{
                        TextView dataStatus = utilHelper.createTextView("No Data Available");
                        searchResult.addView(dataStatus);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("type",type);
                    params.put("status",status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchLeaveDataEmployee, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,type,status);
        ae.execute();
    }

    public void getEmployeeClaimData(Context context,String type,String status){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            String type,status;
            retrieveDataDB(Context context,String type,String status){
                this.context = context;
                this.type = type;
                this.status = status;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("claim");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(true,true);
                            //type
                            LinearLayout subContainer = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelType = utilHelper.createTextView("Type",4.0f);
                            TextView dataType = utilHelper.createTextView(jo.getString("type"),4.0f);
                            subContainer.addView(labelType);
                            subContainer.addView(dataType);
                            //DateProject
                            LinearLayout subContainer2 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelDateProject = utilHelper.createTextView("Date-Project",4.0f);
                            TextView dataDateProject = utilHelper.createTextView(jo.getString("date"),4.0f);
                            subContainer2.addView(labelDateProject);
                            subContainer2.addView(dataDateProject);
                            //Amount
                            LinearLayout subContainer3 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelAmount = utilHelper.createTextView("Amount",4.0f);
                            TextView dataAmount = utilHelper.createTextView(jo.getString("amount"),4.0f);
                            subContainer3.addView(labelAmount);
                            subContainer3.addView(dataAmount);
                            //status
                            LinearLayout subContainer4 = utilHelper.createLinearLayout(false,false,10.0f);

                            TextView labelStatus = utilHelper.createTextView("Status",4.0f);
                            TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                            subContainer4.addView(labelStatus);
                            subContainer4.addView(dataStatus);

                            container.addView(subContainer);
                            container.addView(subContainer2);
                            container.addView(subContainer3);
                            container.addView(subContainer4);
                            searchResult.addView(container);
                        }
                    }else{
                        TextView dataStatus = utilHelper.createTextView("No Data Available");
                        searchResult.addView(dataStatus);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("type",type);
                    params.put("status",status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchClaimDataEmployee, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,type,status);
        ae.execute();
    }

    public void initializeData(Context context, String sub_menu){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            Context context;
            String sub_menu;
            public retrieveDataDB(Context context,String sub_menu){
                this.context = context;
                this.sub_menu = sub_menu;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Retrieving employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray resultStatus = output.getJSONArray("status");
                    HashMap<String,String> data = new HashMap<>();
                    data.put("status_id","");
                    data.put("status_value","No Filter");
                    completeStatusData.add(data);
                    ArrayList<String> arrayListStatus = new ArrayList<String>();
                    arrayListStatus.add("No Filter");
                    for(int i = 0; i<resultStatus.length() ; i++){
                        JSONObject jo = resultStatus.getJSONObject(i);
                        data = new HashMap<>();
                        data.put("status_id",jo.getString("status_id"));
                        data.put("status_value",jo.getString("status_value"));
                        completeStatusData.add(data);
                        arrayListStatus.add(jo.getString("status_value"));
                    }
                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item, arrayListStatus);
                    arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerLeave = findViewById(R.id.status);
                    spinnerLeave.setAdapter(arrayAdapter2);
                    spinnerLeave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String statusName = parent.getItemAtPosition(position).toString();
                            statusSelected = completeStatusData.get(position).get("status_id");
                            Toast.makeText(parent.getContext(), "Selected: " + statusName, Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                            Toast.makeText(parent.getContext(), "Nothing Selected: ",    Toast.LENGTH_LONG).show();
                        }
                    });

                    if(!sub_menu.equalsIgnoreCase("Attendance")){
                        JSONArray resultType = output.getJSONArray("type");
                        data = new HashMap<>();
                        data.put("type_id","");
                        data.put("type_name","No Filter");
                        completeTypeDate.add(data);
                        ArrayList<String> arrayListType = new ArrayList<String>();
                        arrayListType.add("No Filter");
                        for(int i = 0; i<resultType.length() ; i++){
                            JSONObject jo = resultType.getJSONObject(i);
                            data = new HashMap<>();
                            data.put("type_id",jo.getString("type_id"));
                            data.put("type_name",jo.getString("type_name"));
                            completeTypeDate.add(data);
                            arrayListType.add(jo.getString("type_name"));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item, arrayListType);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinnerType = findViewById(R.id.type);
                        spinnerType.setAdapter(arrayAdapter);
                        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String typeName = parent.getItemAtPosition(position).toString();
                                typeSelected = completeTypeDate.get(position).get("type_id");
                                Toast.makeText(parent.getContext(), "Selected: " + typeName, Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                                Toast.makeText(parent.getContext(), "Nothing Selected: ",    Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("sub_menu",sub_menu);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetTypeAndStatusReportMenu, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,sub_menu);
        ae.execute();
    }
}
