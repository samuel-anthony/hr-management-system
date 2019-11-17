package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TaskDetail extends AppCompatActivity {
    String nameAndEmail,menuText;
    Bundle bundle;
    JSONObject output;
    UtilHelper utilHelper;

    Bitmap bitmap = null;
    FrameLayout fragmentPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
            menuText = bundle.getString("sub_menu");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);
        utilHelper = new UtilHelper(TaskDetail.this);
        Fragment fragment = null;
        if(menuText.equalsIgnoreCase("Leave")){
            fragment = new LeaveDetail();
        }
        else if(menuText.equalsIgnoreCase("Claim")){
            fragment = new ClaimDetail();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentDetail, fragment);
        ft.commit();
        fragmentPicture = findViewById(R.id.fragmentImage );
        getDetail(TaskDetail.this);

    }

    public void onClickBackButton(View view){
        finish();
    }


    public void showUploadedPicture(View view){
        if(bitmap != null){
            fragmentPicture.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Tap anywhere to dismiss",Toast.LENGTH_LONG).show();
        }
    }
    public void hideUploadedPicture(View view){
        fragmentPicture.setVisibility(View.INVISIBLE);
    }

    public void getDetail(Context context){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {
            Context context;
            retrieveDataDB(Context context){
                this.context = context;
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
                    if(menuText.equalsIgnoreCase("Leave")){
                        JSONArray result = output.getJSONArray("leave");
                        if(result.length()>0) {
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jo = result.getJSONObject(i);
                                TextView dataLeaveType = findViewById(R.id.TextViewLeaveType);
                                TextView dataReportedTo = findViewById(R.id.TextViewProject);
                                TextView dataDateFrom = findViewById(R.id.dateFrom);
                                TextView dataDateTo = findViewById(R.id.dateTo);
                                TextView dataNotes = findViewById(R.id.notes);

                                View attachment = findViewById(R.id.linearLayoutAttachment);

                            }
                        }
                    }
                    else{
                        JSONArray result = output.getJSONArray("claim");
                        if(result.length()>0) {
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jo = result.getJSONObject(i);
                                TextView dataTitle = findViewById(R.id.title);
                                TextView dataProject = findViewById(R.id.TextViewProject);
                                TextView dataClaimType = findViewById(R.id.TextViewClaimType);
                                TextView dataClaimDate = findViewById(R.id.date);
                                TextView dataCurrency = findViewById(R.id.TextViewCurrency);
                                TextView dataAmount = findViewById(R.id.amount);
                                TextView dataAccount = findViewById(R.id.account);
                                TextView dataNotes = findViewById(R.id.notes);

                                View attachment = findViewById(R.id.linearLayoutAttachment);
                            }
                        }
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
