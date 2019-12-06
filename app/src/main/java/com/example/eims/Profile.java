package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Profile extends AppCompatActivity {
    Bundle bundle;
    JSONObject output;
    UtilHelper utilHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        utilHelper = new UtilHelper(this);
        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeData();

    }
    public void onClickBackButton(View view){
        finish();
    }

    public void onClickSubmitButton(View view){
        EditText oldpassword = (EditText) findViewById(R.id.oldPassword);
        EditText newpassword = (EditText) findViewById(R.id.newPassword);
        int newpasslengt =newpassword.getText().length();
        if(TextUtils.isEmpty(oldpassword.getText())){
            oldpassword.setError("Old Password is required");
        }
        else if(TextUtils.isEmpty(newpassword.getText())){
            newpassword.setError("New Password is required");
        }else if(newpasslengt<8){
            newpassword.setError("Invalid New Password, minimum length : 8 character!");
        }else {
            changePassword();
        }
    }

    public void changePassword(){
        class changePasswordtoDb extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Profile.this,"Updating your Password..","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1")){
                        Intent loginIntent = new Intent(Profile.this, Login.class);
                        startActivity(loginIntent);
                        finish();
                        Toast.makeText(Profile.this,output.getString("message"),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(Profile.this,output.getString("message"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                String oldPassword = ((EditText)findViewById(R.id.oldPassword)).getText().toString();
                String newPassword = ((EditText)findViewById(R.id.newPassword)).getText().toString();

                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("password",oldPassword);
                    params.put("newpass",newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.UpdatePassword, params);
                return res;
            }
        }

        changePasswordtoDb ae = new changePasswordtoDb();
        ae.execute();
    }

    public void initializeData(){
        class retrieveDataDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Profile.this,"Retrieving employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("employee");
                    JSONObject jo = result.getJSONObject(0);//ini 0 soalnya data nya cman 1 dong
                    TextView firstName = findViewById(R.id.firstName);
                    TextView lastName = findViewById(R.id.lastName);
                    TextView email = findViewById(R.id.email);
                    TextView address = findViewById(R.id.address);
                    TextView phone = findViewById(R.id.phone);
                    TextView hiredDate = findViewById(R.id.hiredDate);
                    TextView gender = findViewById(R.id.gender);

                    firstName.setText(jo.getString("first_name"));
                    lastName.setText(jo.getString("last_name"));
                    email.setText(jo.getString("email"));
                    address.setText(jo.getString("address"));
                    phone.setText(jo.getString("phone"));
                    hiredDate.setText(jo.getString("hired_date"));
                    gender.setText(jo.getString("gender"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                try {
                    params.put("employee_id",output.getString("employee_id"));
                    params.put("employee_name","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeForAdmin, params);
                return res;
            }
        }
        retrieveDataDB ae = new retrieveDataDB();
        ae.execute();

    }

}
