package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class TaskDetail extends AppCompatActivity {
    String nameAndEmail,menuText,summaryID;
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
            summaryID = bundle.getString("id");
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


        Fragment fragment2 = new Image();
        FragmentManager fm2 = getSupportFragmentManager();
        FragmentTransaction ft2 = fm2.beginTransaction();
        ft2.replace(R.id.fragmentImage, fragment2);
        ft2.commit();
        fragmentPicture = findViewById(R.id.fragmentImage );
        fragmentPicture.setVisibility(View.INVISIBLE);
        getDetail(TaskDetail.this);

    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(this, TaskSearch.class);
        mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
        mainActivity.putExtra("sub_menu",menuText);
        startActivity(mainActivity);
        finish();

    }
    public void onClickBackButton(View view){
        Intent mainActivity = new Intent(this, TaskSearch.class);
        mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
        mainActivity.putExtra("sub_menu",menuText);
        startActivity(mainActivity);
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
                        if(result.length()>0) {
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jo = result.getJSONObject(i);
                                TextView dataLeaveType = findViewById(R.id.TextViewLeaveType);
                                TextView dataReportedTo = findViewById(R.id.TextViewProject);
                                TextView dataDateFrom = findViewById(R.id.dateFrom);
                                TextView dataDateTo = findViewById(R.id.dateTo);
                                TextView dataNotes = findViewById(R.id.notes);
                                TextView status = findViewById(R.id.txtStatus);
                                TextView duration = findViewById(R.id.duration);

                                dataLeaveType.setText(jo.getString("leaveType"));
                                dataReportedTo.setText(jo.getString("projectName"));
                                dataDateFrom.setText(jo.getString("dateFrom"));
                                dataDateTo.setText(jo.getString("dateTo"));
                                dataNotes.setText(jo.getString("notes"));
                                status.setText(jo.getString("statusVal"));
                                duration.setText(jo.getString("duration") +" - "+"Days" );


                                View attachment = findViewById(R.id.linearLayoutAttachment);
                                if(jo.getString("file_data").isEmpty()){
                                    attachment.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    byte[] imageBytes = Base64.decode(jo.getString("file_data"), Base64.DEFAULT);
                                    bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    ImageView uploadedPic = findViewById(R.id.uploadedPicture);
                                    uploadedPic.setImageBitmap(bitmap);
                                }
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
                                TextView status = findViewById(R.id.txtStatus);


                                View attachment = findViewById(R.id.linearLayoutAttachment);
                                dataTitle.setText(jo.getString("title"));
                                dataProject.setText(jo.getString("projectName"));
                                dataClaimType.setText(jo.getString("claimType"));
                                dataClaimDate.setText(jo.getString("date"));
                                dataAmount.setText(jo.getString("amount"));
                                dataCurrency.setText(jo.getString("currency"));
                                dataAccount.setText(jo.getString("bank_account"));
                                dataNotes.setText(jo.getString("notes"));
                                status.setText(jo.getString("statusVal"));


                                byte[] imageBytes = Base64.decode(jo.getString("file_data"), Base64.DEFAULT);
                                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                ImageView uploadedPic = findViewById(R.id.uploadedPicture);
                                uploadedPic.setImageBitmap(bitmap);
                            }
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
                    params.put("summaryID",summaryID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetDetailLeaveAndClaimTaskMenu, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context);
        ae.execute();
    }

    public void onClickApproveAndRejectButton(View view){
        String remarks = ((TextView)findViewById(R.id.remarks)).getText().toString();
        if(view == findViewById(R.id.buttonReject)){
            if(!remarks.isEmpty()){
                updateContent(TaskDetail.this,"9",remarks);
            }
            else{
                utilHelper.createPopUpDialog("Missing Required field","Please fill the remarks");
            }
        }
        else{
            updateContent(TaskDetail.this,"7",remarks);
        }
    }

    public void updateContent(Context context,String statusCode,String remarks){
        class updateDatabase extends AsyncTask<Void,Void,String> {
            Context context;
            String statusCode,remarks;
            updateDatabase(Context context,String statusCode,String remarks){
                this.context = context;
                this.statusCode = statusCode;
                this.remarks = remarks;
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
                    if(output.getString("value").equalsIgnoreCase("1") ){
                        utilHelper.createPopUpDialogCloseActivity("Success",output.getString("message"));
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
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("menu",menuText);
                    params.put("summaryID",summaryID);
                    params.put("statusCode",statusCode);
                    params.put("remarks",remarks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.UpdateLeaveAndClaimSummary, params);
                return res;
            }
        }

        updateDatabase ae = new updateDatabase(context,statusCode,remarks);
        ae.execute();
    }

}
