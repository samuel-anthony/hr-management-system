package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminLeave extends AppCompatActivity {
    String statusSelected,typeSelected;
    Bundle bundle;
    JSONObject output;
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;
    ArrayList<HashMap<String,String>> completeTypeDate = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completeStatusData = new ArrayList<HashMap<String,String>>();
    JSONArray result;
    TextView exporter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_leave);
        exporter = findViewById(R.id.export);
        exporter.setVisibility(TextView.GONE);
        utilHelper  = new UtilHelper(this);
        scrollViewLayout = findViewById(R.id.search_result_scroll);
        initializeData();
    }

    public void onClickBackButton(View view){
        finish();
    }

    public void onClickClearButton(View view){
        scrollViewLayout.removeAllViews();
    }

    public void onClickSearchButton(View view){
        scrollViewLayout.removeAllViews();
        String status = "";
        if(!statusSelected.equalsIgnoreCase("0")){
            status = statusSelected;
        }
        String type = "";
        if(!typeSelected.equalsIgnoreCase("0")){
            type = typeSelected;
        }
        getLeaveData(this,((EditText)findViewById(R.id.name)).getText().toString(), type, status);
    }

    public void getLeaveData(Context context,String employeeName, String type, String status){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            ProgressDialog loading;
            String employeeName,type,status;
            retrieveDataDB(Context context,String employeeName,String type,String status){
                this.context = context;
                this.employeeName = employeeName;
                this.type = type;
                this.status = status;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AdminLeave.this,"Getting employee's data...","Please wait...",false,false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    result = output.getJSONArray("leave");
                    if(result.length()>0){
                        exporter.setVisibility(TextView.VISIBLE);
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f);
                            LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false);
                            RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(false,5.0f,false);

                            //id-name
                            TextView firstRow =  utilHelper.createTextView(jo.getString("id") + " - " + jo.getString("name"));
                            TextView secondRow = utilHelper.createTextView(jo.getString("dateFrom") + " Until " + jo.getString("dateTo"));

                            //type
                            LinearLayout subContainer =utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelType = utilHelper.createTextView("Type",3.0f);
                            TextView dataType = utilHelper.createTextView(jo.getString("type"),4.0f);
                            subContainer.addView(labelType);
                            subContainer.addView(dataType);
                            //status
                            LinearLayout subContainer1 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelStatus = utilHelper.createTextView("Status",3.0f);
                            TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                            subContainer1.addView(labelStatus);
                            subContainer1.addView(dataStatus);

                            leftSubContainer.addView(firstRow);
                            leftSubContainer.addView(secondRow);
                            leftSubContainer.addView(subContainer);
                            leftSubContainer.addView(subContainer1);

                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,50,50);
                            editButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(AdminLeave.this, AdminDetail.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("id"));
                                        detailActivity.putExtra("name",jo.getString("name"));
                                        detailActivity.putExtra("summaryId",jo.getString("leaveid"));
                                        detailActivity.putExtra("submenu","Leave");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                    finish();
                                }
                            });
                            leftSubContainer.setBackground(getDrawable(R.drawable.rounded_rec));
                            if (i % 2 == 0){
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d6e5fa")));
                            }else{
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eafbea")));
                            }

                            rightSubContainer.addView(editButton);
                            container.addView(leftSubContainer);
                            container.addView(rightSubContainer);
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
                params.put("employee_id","");
                params.put("name",employeeName);
                params.put("type",type);
                params.put("status",status);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchLeaveDataEmployee, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context,employeeName,type,status);
        ae.execute();
    }


    public void initializeData(){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AdminLeave.this,"Retrieving employee's data...","Please wait...",false,false);
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
                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(AdminLeave.this,android.R.layout.simple_selectable_list_item, arrayListStatus);
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminLeave.this,android.R.layout.simple_selectable_list_item, arrayListType);
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("employee_id","");
                params.put("sub_menu","LeaveAdmin");
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetTypeAndStatusReportMenu, params);
                return res;
            }
        }
        retrieveDataDB ae = new retrieveDataDB();
        ae.execute();

    }

    public void exportExcel(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }

        Workbook wb=new HSSFWorkbook();
        Cell cell=null;
        Sheet sheet =null;
        sheet = wb.createSheet("Leave Data");
        Font headerfont = wb.createFont();
        headerfont.setBold(true);
        headerfont.setFontHeightInPoints((short)14);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(headerfont);

        String[] header ={"Employee ID", "Employee Name","Project Reported","Leave Type","Start Date","End Date", "Status"};
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i<header.length ; i++){
            cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }

        try {
            if(result.length()>0){
                for(int i = 0; i<=result.length() ; i++) {
                    final JSONObject jo = result.getJSONObject(i);
                    Row row = sheet.createRow(i+1);
                    cell = row.createCell(0);
                    cell.setCellValue(jo.getString("id"));
                    cell = row.createCell(1);
                    cell.setCellValue(jo.getString("name"));
                    cell = row.createCell(2);
                    cell.setCellValue(jo.getString("project"));
                    cell = row.createCell(3);
                    cell.setCellValue(jo.getString("type"));
                    cell = row.createCell(4);
                    cell.setCellValue(jo.getString("dateFrom"));
                    cell = row.createCell(5);
                    cell.setCellValue(jo.getString("dateTo"));
                    cell = row.createCell(6);
                    cell.setCellValue(jo.getString("status"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String filename = "Leave Data - " + utilHelper.getTimeStamp() + ".xls" ;
        File file = new File(getExternalFilesDir(null),filename);
        FileOutputStream outputStream =null;

        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(getApplicationContext(),"File Exported Successfully",Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),"Upss.. ",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
