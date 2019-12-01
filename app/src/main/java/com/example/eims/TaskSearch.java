package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TaskSearch extends AppCompatActivity {
    String nameAndEmail,menuText;
    Bundle bundle;
    JSONObject output;
    LinearLayout searchResult;
    UtilHelper utilHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_search);

        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
            menuText = bundle.getString("sub_menu");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);
        searchResult = findViewById(R.id.search_result_scroll);
        utilHelper = new UtilHelper(TaskSearch.this);
        getEmployeeData(TaskSearch.this);
    }

    public void onClickBackButton(View view){
        finish();
    }

    public void getEmployeeData(Context context){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            ProgressDialog loading;

            retrieveDataDB(Context context){
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Retrieving employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject output = new JSONObject(s);
                    if(menuText.equalsIgnoreCase("Leave")){

                        JSONArray result = output.getJSONArray("leave");
                        if(result.length()>0){
                            for(int i = 0; i<result.length() ; i++){
                                final JSONObject jo = result.getJSONObject(i);

                                LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f);
                                LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,false,15.0f,false);
                                RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(false,5.0f,false);

                                TextView employeeName = utilHelper.createTextView(jo.getString("employeeName"));

                                TextView dateLeave = utilHelper.createTextView(jo.getString("dateFrom") + " - " +jo.getString("dateTo"));

                                LinearLayout textViewContainer = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelLeaveType = utilHelper.createTextView("Type",3.0f);
                                TextView dataLeaveType = utilHelper.createTextView(jo.getString("leaveType"),4.0f);
                                textViewContainer.addView(labelLeaveType);
                                textViewContainer.addView(dataLeaveType);

                                LinearLayout textViewContainer2 = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelStatus = utilHelper.createTextView("Status",3.0f);
                                TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                                textViewContainer2.addView(labelStatus);
                                textViewContainer2.addView(dataStatus);

                                leftSubContainer.addView(employeeName);
                                leftSubContainer.addView(dateLeave);
                                leftSubContainer.addView(textViewContainer);
                                leftSubContainer.addView(textViewContainer2);

                                ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,50,50);
                                editButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        // Do some job here
                                        Intent detailActivity = new Intent(TaskSearch.this, TaskDetail.class);
                                        detailActivity.putExtra("employee_data",bundle.getString("employee_data"));
                                        detailActivity.putExtra("sub_menu",menuText);
                                        try {
                                            detailActivity.putExtra("id",jo.getString("id"));
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
                                searchResult.addView(container);
                            }
                        }
                        else {
                            TextView dataStatus = utilHelper.createTextView("No Data Available");
                            searchResult.addView(dataStatus);
                        }
                    }
                    else if(menuText.equalsIgnoreCase("Claim")){
                        JSONArray result2 = output.getJSONArray("claim");
                        if(result2.length()>0){
                            for(int i = 0; i<result2.length() ; i++){
                                final JSONObject jo = result2.getJSONObject(i);
                                LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f);
                                LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false);
                                RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(false,5.0f,false);

                                TextView employeeName = utilHelper.createTextView(jo.getString("employeeName"));

                                LinearLayout textViewContainer = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelDate = utilHelper.createTextView("Date",3.0f);
                                TextView dataDate = utilHelper.createTextView(jo.getString("date"),4.0f);
                                textViewContainer.addView(labelDate);
                                textViewContainer.addView(dataDate);

                                LinearLayout textViewContainer2 = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelClaim = utilHelper.createTextView("Type",3.0f);
                                TextView dataClaim = utilHelper.createTextView(jo.getString("claimType"),4.0f);
                                textViewContainer2.addView(labelClaim);
                                textViewContainer2.addView(dataClaim);

                                LinearLayout textViewContainer3 = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelAmount = utilHelper.createTextView("Amount",3.0f);
                                TextView dataAmount = utilHelper.createTextView(jo.getString("amount"),4.0f);
                                textViewContainer3.addView(labelAmount);
                                textViewContainer3.addView(dataAmount);

                                LinearLayout textViewContainer4 = utilHelper.createLinearLayout(false,false,10.0f);
                                TextView labelStatus = utilHelper.createTextView("Status",3.0f);
                                TextView dataStatus = utilHelper.createTextView(jo.getString("status"),4.0f);
                                textViewContainer4.addView(labelStatus);
                                textViewContainer4.addView(dataStatus);

                                leftSubContainer.addView(employeeName);
                                leftSubContainer.addView(textViewContainer);
                                leftSubContainer.addView(textViewContainer2);
                                leftSubContainer.addView(textViewContainer3);
                                leftSubContainer.addView(textViewContainer4);

                                ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,50,50);
                                editButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        // Do some job here
                                        Intent detailActivity = new Intent(TaskSearch.this, TaskDetail.class);
                                        detailActivity.putExtra("employee_data",bundle.getString("employee_data"));
                                        detailActivity.putExtra("sub_menu",menuText);
                                        try {
                                            detailActivity.putExtra("id",jo.getString("id"));
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

                                searchResult.addView(container);
                            }
                        }
                        else{
                            TextView dataStatus = utilHelper.createTextView("No Data Available");
                            searchResult.addView(dataStatus);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("menu",menuText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetLeaveAndClaimTaskMenu, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context);
        ae.execute();
    }


}
