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

import java.util.HashMap;

public class AdminDetail extends AppCompatActivity {
    String id,idname,menuText,summaryID;
    Bundle bundle;
    JSONObject output;
    UtilHelper utilHelper;

    Bitmap bitmap = null;
    FrameLayout fragmentPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report_detail);

        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        idname = bundle.getString("id")+ " - " + bundle.getString("name");
        menuText = bundle.getString("submenu");
        summaryID =bundle.getString("summaryId");
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(idname);

        utilHelper = new UtilHelper(AdminDetail.this);

        Fragment fragment = null;
        if(menuText.equalsIgnoreCase("Leave")){
            fragment = new LeaveDetail();
            setVisibility();
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
        getDetail(AdminDetail.this);


    }

    public void onBackPressed() {
        if(menuText.equalsIgnoreCase("Leave")) {
            Intent mainActivity = new Intent(this, AdminLeave.class);
            startActivity(mainActivity);
            finish();
        }else if(menuText.equalsIgnoreCase("Claim")){
            Intent mainActivity = new Intent(this, AdminClaim.class);
            startActivity(mainActivity);
            finish();

        }
    }
    public void onClickBackButton(View view){
        if(menuText.equalsIgnoreCase("Leave")) {
            Intent mainActivity = new Intent(this, AdminLeave.class);
            startActivity(mainActivity);
            finish();
        }else if(menuText.equalsIgnoreCase("Claim")){
            Intent mainActivity = new Intent(this, AdminClaim.class);
            startActivity(mainActivity);
            finish();

        }
    }

    public void setVisibility(){
        View label = findViewById(R.id.labelRemarks);
        View editTextRemarks = findViewById(R.id.remarks);
        View btnApprove = findViewById(R.id.buttonApprove);
        View btnReject = findViewById(R.id.buttonReject);

        label.setVisibility(View.INVISIBLE);
        editTextRemarks.setVisibility(View.INVISIBLE);
        btnApprove.setVisibility(View.INVISIBLE);
        btnReject.setVisibility(View.INVISIBLE);
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

                                dataLeaveType.setText(jo.getString("leaveType"));
                                dataReportedTo.setText(jo.getString("projectName"));
                                dataDateFrom.setText(jo.getString("dateFrom"));
                                dataDateTo.setText(jo.getString("dateTo"));
                                dataNotes.setText(jo.getString("notes"));
                                status.setText(jo.getString("statusVal"));

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

                                int statusId = Integer.parseInt(jo.getString("statusId"));

                                if(statusId == 6 || statusId == 8 || statusId == 9) {
                                    setVisibility();
                                }
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

                params.put("employee_id",id);
                params.put("menu",menuText);
                params.put("summaryID",summaryID);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetDetailLeaveAndClaimTaskMenu, params);
                return res;
            }
        }

        retrieveDataDB ae = new retrieveDataDB(context);
        ae.execute();
    }

    public void onClickApproveAndRejectButtonAdm(View view){
        String remarks = ((TextView)findViewById(R.id.remarks)).getText().toString();
        if(view == findViewById(R.id.buttonReject)){
            if(!remarks.isEmpty()){
                updateContent(AdminDetail.this,"9",remarks);
            }
            else{
                utilHelper.createPopUpDialog("Missing Required field","Please fill the remarks");
            }
        }
        else{
            updateContent(AdminDetail.this,"8",remarks);
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
                params.put("employee_id",id);
                params.put("menu",menuText);
                params.put("summaryID",summaryID);
                params.put("statusCode",statusCode);
                params.put("remarks",remarks);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.UpdateLeaveAndClaimSummary, params);
                return res;
            }
        }

        updateDatabase ae = new updateDatabase(context,statusCode,remarks);
        ae.execute();
    }


}
