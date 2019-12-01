package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class AdminAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String statusSelected;
    Bundle bundle;
    JSONObject output;
    SimpleDateFormat dateFormat;
    UtilHelper utilHelper;
    View datePickerView;
    LinearLayout scrollViewLayout;
    ArrayList<HashMap<String,String>> completeStatusData = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        utilHelper  = new UtilHelper(this);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        scrollViewLayout = findViewById(R.id.search_result_scroll);
        initializeData();
    }

    public void onClickBackButton(View view){
        finish();
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
        DialogFragment datePicker = new DatePickerFragment(false,true,selectedDate);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
        datePickerView = view;
    }

    public void onClickClearButton(View view){
        scrollViewLayout.removeAllViews();
    }

    public void onClickSearchButton(View view){
        scrollViewLayout.removeAllViews();
        String employeeName = ((EditText)findViewById(R.id.name)).getText().toString();
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
                    getEmployeeAttendanceData(this,((EditText)findViewById(R.id.name)).getText().toString() ,((TextView)(findViewById(R.id.dateFrom))).getText().toString(),((TextView)(findViewById(R.id.dateTo))).getText().toString(),status);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            utilHelper.createPopUpDialog("Error input","please fill the date from and date to");
        }
    }

    public void getEmployeeAttendanceData(Context context,String employeeName, String dateFrom,String dateTo,String status){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            String employeeName,dateFrom,dateTo,status;
            retrieveDataDB(Context context,String employeeName, String dateFrom,String dateTo,String status){
                this.context = context;
                this.employeeName = employeeName;
                this.dateFrom = dateFrom;
                this.dateTo = dateTo;
                this.status = status;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AdminAttendance.this,"Getting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("attendance");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(true,true);
                            //id-name
                            LinearLayout subContainer0 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelRow = utilHelper.createTextView("ID-Name",4.0f);
                            TextView dataRow =  utilHelper.createTextView(jo.getString("id") + " - " + jo.getString("name"),4.0f);
                            subContainer0.addView(labelRow);
                            subContainer0.addView(dataRow);

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

                            container.addView(subContainer0);
                            container.addView(subContainer);
                            container.addView(subContainer2);
                            container.addView(subContainer3);
                            container.addView(subContainer4);
                            scrollViewLayout.addView(container);
                        }
                    }
                    else{
                        TextView dataStatus = utilHelper.createTextView("No Data Available");
                        scrollViewLayout.addView(dataStatus);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",employeeName);
                params.put("dateFrom",dateFrom);
                params.put("dateTo",dateTo);
                params.put("status",status);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchAttendanceforAdmin, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,employeeName,dateFrom,dateTo,status);
        ae.execute();
    }

    public void initializeData(){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AdminAttendance.this,"Retrieving employee's data...","Please wait...",false,false);
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
                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(AdminAttendance.this,android.R.layout.simple_selectable_list_item, arrayListStatus);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("employee_id","");
                params.put("sub_menu","Attendance");
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetTypeAndStatusReportMenu, params);
                return res;
            }
        }
        retrieveDataDB ae = new retrieveDataDB();
        ae.execute();

    }
}
