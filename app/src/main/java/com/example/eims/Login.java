package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onclickButtonLogin(View view){
        EditText login = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);
        if(TextUtils.isEmpty(login.getText())){
            login.setError("email is required");
        }
        else if(TextUtils.isEmpty(password.getText())){
            password.setError("password is required");
        }else {
            checkLogin();
        }
    }

    public void checkLogin(){
        class checkLoginToDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this,"Mencoba Login...","Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1")){
                        Intent mainActivity = new Intent(Login.this, MainActivity.class);
                        mainActivity.putExtra("employee_data",s);
                        startActivity(mainActivity);
                        finish();
                    }
                    Toast.makeText(Login.this,output.getString("message"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
                String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

                params.put("email",email);
                params.put("password",password);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.Login, params);
                return res;
            }
        }

        checkLoginToDB ae = new checkLoginToDB();
        ae.execute();
    }


}
