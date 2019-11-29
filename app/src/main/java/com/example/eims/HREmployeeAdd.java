package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HREmployeeAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    View datePickerView;
    SimpleDateFormat dateFormat;
    UtilHelper utilHelper;
    int selectedIDRadioButton;
    boolean firstTimeCheckBoxClicked = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_add);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        utilHelper = new UtilHelper(HREmployeeAdd.this);
        /*CheckBox checkBox = findViewById(R.id.checkboxIsPM);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked){
                    if(firstTimeCheckBoxClicked)
                        utilHelper.createPopUpDialog("PM is checked","By Checking the PM, Then this employee will automatically become user(not admin)");
                    firstTimeCheckBoxClicked = false;
                    ((CheckBox)findViewById(R.id.checkboxIsUser)).setChecked(true);
                }
            }
        });*/
    }

    public void onClickBackButton(View view){
        finish();
    }

    public void onClickSubmitButton(View view) {
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString();
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();
        String phone = ((EditText)findViewById(R.id.phone)).getText().toString();
        boolean isDateEmpty = ((TextView) findViewById(R.id.hiredDate)).getText().toString().isEmpty();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty() || isDateEmpty || phone.isEmpty() || address.isEmpty()) {
            if (firstName.isEmpty()) {
                ((EditText)findViewById(R.id.firstName)).setError("first name is required");
            }
            if (lastName.isEmpty()){
                ((EditText)findViewById(R.id.lastName)).setError("last name is required");
            }
            if (email.isEmpty()){
                ((EditText)findViewById(R.id.email)).setError("email is required");
            }
            if(phone.isEmpty()){
                ((EditText)findViewById(R.id.phone)).setError("Phone is required");
            }
            if(address.isEmpty()){
                ((EditText)findViewById(R.id.address)).setError("Address is required");
            }
            else if(isDateEmpty){
                utilHelper.createPopUpDialog("Error Input","Hired date is required");
            }

        }
        else{
            String hiredDate = ((TextView)findViewById(R.id.hiredDate)).getText().toString();
            String gender = ((RadioButton)findViewById(((RadioGroup)findViewById(R.id.radioGroupGender)).getCheckedRadioButtonId())).getText().toString();
            String employeeTag = ((RadioButton)findViewById(selectedIDRadioButton)).getText().toString();
            CheckBox isPM = findViewById(R.id.checkboxIsPM);
            //CheckBox isUser = findViewById(R.id.checkboxIsUser);
            String pmTag,userFlag;
            pmTag = isPM.isChecked() ? "1" : "0";
            userFlag = "1";//isUser.isChecked() ? "1" : "0";
            submitHREmployee(firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag,userFlag);
        }
    }

    public void onclickRadioButton(View view){
        if(view.getId() == R.id.radioEmployeeFullTime || view.getId() == R.id.radioEmployeeIntern){
            ((RadioGroup)findViewById(R.id.radioGroup2)).clearCheck();
        }
        else{
            ((RadioGroup)findViewById(R.id.radioGroup1)).clearCheck();
        }
        selectedIDRadioButton = view.getId();
    }

    public void showDatePicker(View view){
        String selectedDate = ((TextView)view).getText().toString();
        DialogFragment datePicker = new DatePickerFragment(false,true,selectedDate);
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



    public void submitHREmployee(String firstName,String lastName,String email,String address,String hiredDate,String phone,String gender, String employeeTag, String pmTag, String userFlag){
        class insertToDB extends AsyncTask<Void,Void,String> {
            String firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag,userFlag;
            ProgressDialog loading;
            public insertToDB(String firstName,String lastName,String email, String address, String hiredDate,String phone,String gender, String employeeTag, String pmTag, String userFlag){
                this.firstName = firstName;
                this.lastName = lastName;
                this.email = email;
                this.address = address;
                this.hiredDate = hiredDate;
                this.phone = phone;
                this.gender = gender;
                this.employeeTag = employeeTag;
                this.pmTag = pmTag;
                this.userFlag = userFlag;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HREmployeeAdd.this,"Inserting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1")){
                        //sukses submit..
                        utilHelper.createPopUpDialogCloseActivity("Success Message",output.getString("message"));
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
                params.put("firstName",firstName);
                params.put("lastName",lastName);
                params.put("email",email);
                params.put("address",address);
                params.put("hiredDate",hiredDate);
                params.put("phone",phone);
                params.put("gender",gender);
                params.put("employeeTag",employeeTag);
                params.put("pmFlag",pmTag);
                params.put("userFlag",userFlag);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.AddEmployee, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag,userFlag);
        ae.execute();
    }


}
