package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminClaim extends AppCompatActivity {
    String statusSelected,typeSelected;
    Bundle bundle;
    JSONObject output;
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;
    ArrayList<HashMap<String,String>> completeTypeDate = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completeStatusData = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_claim);

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
        getClaimData(this,((EditText)findViewById(R.id.name)).getText().toString(), type, status);
    }

    public void getClaimData(Context context, String employeeName, String type, String status){
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
                loading = ProgressDialog.show(AdminClaim.this,"Getting employee's data...","Please wait...",false,false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("claim");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f);
                            LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false);
                            RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(false,5.0f,false);

                            //id-name
                            TextView firstRow =  utilHelper.createTextView(jo.getString("id")+" - "+jo.getString("name"));

                            //date
                            LinearLayout secondRow =utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelDate = utilHelper.createTextView("Date",3f);
                            TextView dataDate = utilHelper.createTextView(jo.getString("date"),4f);
                            secondRow.addView(labelDate);
                            secondRow.addView(dataDate);

                            //type
                            LinearLayout subContainer =utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelType = utilHelper.createTextView("Type",3.0f);
                            TextView dataType = utilHelper.createTextView(jo.getString("type"),4.0f);
                            subContainer.addView(labelType);
                            subContainer.addView(dataType);
                            LinearLayout subContainer1 =utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelAmount = utilHelper.createTextView("Amount" ,3f);
                            TextView dataAmount = utilHelper.createTextView(jo.getString("amount"),4f);
                            subContainer1.addView(labelAmount);
                            subContainer1.addView(dataAmount);
                            //status
                            LinearLayout subContainer2 = utilHelper.createLinearLayout(false,false,10.0f);
                            TextView labelStatus = utilHelper.createTextView("Status",3.0f);
                            TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                            subContainer2.addView(labelStatus);
                            subContainer2.addView(dataStatus);

                            leftSubContainer.addView(firstRow);
                            leftSubContainer.addView(secondRow);
                            leftSubContainer.addView(subContainer);
                            //leftSubContainer.addView(fourrow);
                            leftSubContainer.addView(subContainer1);
                            leftSubContainer.addView(subContainer2);
                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit_black_24dp,50,50);
                            editButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(AdminClaim.this, AdminDetail.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("id"));
                                        detailActivity.putExtra("name",jo.getString("name"));
                                        detailActivity.putExtra("summaryId",jo.getString("claimId"));
                                        detailActivity.putExtra("submenu","Claim");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                    finish();
                                }
                            });

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
                String res = rh.sendPostRequest(ConfigURL.SearchClaimDataEmployee, params);
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
                loading = ProgressDialog.show(AdminClaim.this,"Retrieving employee's data...","Please wait...",false,false);
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
                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(AdminClaim.this,android.R.layout.simple_selectable_list_item, arrayListStatus);
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminClaim.this,android.R.layout.simple_selectable_list_item, arrayListType);
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
}
