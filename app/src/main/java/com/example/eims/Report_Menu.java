package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    String nameAndEmail;
    Bundle bundle;
    JSONObject output;
    String sub_menu_text;
    SimpleDateFormat dateFormat;
    UtilHelper utilHelper;
    View datePickerView;
    LinearLayout searchResult;
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
    }
    public void onClickBackButton(View view){
        finish();
    }

    public void showDatePicker(View view){
        DialogFragment datePicker = new DatePickerFragment();
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
                        if(!((TextView)findViewById(R.id.status)).getText().toString().isEmpty()){
                            status = ((TextView)findViewById(R.id.status)).getText().toString();
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
        else if(view == findViewById(R.id.searchLeave)){

        }
        else if(view == findViewById(R.id.searchClaim)){

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
                    for(int i = 0; i<result.length() ; i++){
                        JSONObject jo = result.getJSONObject(i);
                        LinearLayout container = new LinearLayout(context);
                        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                        container.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams paramContainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams paramTextView = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,4.0f);
                        //date
                        LinearLayout subContainer = new LinearLayout(context);
                        subContainer.setLayoutParams(paramContainer);
                        subContainer.setOrientation(LinearLayout.HORIZONTAL);
                        subContainer.setWeightSum(10.0f);
                        TextView labelDate = new TextView(context);
                        labelDate.setText("Date");
                        labelDate.setLayoutParams(paramTextView);
                        TextView dataDate = new TextView(context);
                        dataDate.setLayoutParams(paramTextView);
                        dataDate.setText(jo.getString("date"));
                        subContainer.addView(labelDate);
                        subContainer.addView(dataDate);
                        //clockin
                        LinearLayout subContainer2 = new LinearLayout(context);
                        subContainer2.setLayoutParams(paramContainer);
                        subContainer2.setOrientation(LinearLayout.HORIZONTAL);
                        subContainer2.setWeightSum(10.0f);
                        TextView labelClockIn = new TextView(context);
                        labelClockIn.setText("Clock-In");
                        labelClockIn.setLayoutParams(paramTextView);
                        TextView dataClockIn = new TextView(context);
                        dataClockIn.setText(jo.getString("clockIn"));
                        dataClockIn.setLayoutParams(paramTextView);
                        subContainer2.addView(labelClockIn);
                        subContainer2.addView(dataClockIn);
                        //clockout
                        LinearLayout subContainer3 = new LinearLayout(context);
                        subContainer3.setLayoutParams(paramContainer);
                        subContainer3.setOrientation(LinearLayout.HORIZONTAL);
                        subContainer3.setWeightSum(10.0f);

                        TextView labelClockOut = new TextView(context);
                        labelClockOut.setText("Clock-Out");
                        labelClockOut.setLayoutParams(paramTextView);
                        TextView dataClockOut = new TextView(context);
                        dataClockOut.setText(jo.getString("clockOut"));
                        dataClockOut.setLayoutParams(paramTextView);
                        subContainer3.addView(labelClockOut);
                        subContainer3.addView(dataClockOut);
                        //status
                        LinearLayout subContainer4 = new LinearLayout(context);
                        subContainer4.setLayoutParams(paramContainer);
                        subContainer4.setOrientation(LinearLayout.HORIZONTAL);
                        subContainer4.setWeightSum(10.0f);
                        TextView labelStatus = new TextView(context);
                        labelStatus.setText("Status");
                        labelStatus.setLayoutParams(paramTextView);
                        TextView dataStatus = new TextView(context);
                        dataStatus.setText(jo.getString("status"));
                        dataStatus.setLayoutParams(paramTextView);
                        subContainer4.addView(labelStatus);
                        subContainer4.addView(dataStatus);

                        container.addView(subContainer);
                        container.addView(subContainer2);
                        container.addView(subContainer3);
                        container.addView(subContainer4);
                        container.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle));
                        searchResult.addView(container);
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
}
