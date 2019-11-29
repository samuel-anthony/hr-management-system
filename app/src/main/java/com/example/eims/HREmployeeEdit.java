package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class HREmployeeEdit extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{
    View datePickerView;
    SimpleDateFormat dateFormat;
    UtilHelper utilHelper;
    int selectedIDRadioButton = 0;
    Bundle bundle;
    String nameAndEmail,employeeID;
    boolean firstTimeCheckBoxClicked = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_edit);
        bundle = getIntent().getExtras();
        nameAndEmail = bundle.getString("name");
        employeeID = bundle.getString("id");
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        utilHelper = new UtilHelper(this);

       /* CheckBox checkBox = findViewById(R.id.checkboxIsPM);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked){
                    if(firstTimeCheckBoxClicked)
                        utilHelper.createPopUpDialog("PM is checked","By Checking the PM, then this employee will automatically become user(not admin)");
                    firstTimeCheckBoxClicked = false;
                    ((CheckBox)findViewById(R.id.checkboxIsUser)).setChecked(true);
                }
            }
        });*/
        getData(this);
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
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()  || isDateEmpty) {
            if (firstName.isEmpty()) {
                ((EditText)findViewById(R.id.firstName)).setError("first name is required");
            }
            if (lastName.isEmpty()){
                ((EditText)findViewById(R.id.lastName)).setError("last name is required");
            }
            if (email.isEmpty()){
                ((EditText)findViewById(R.id.email)).setError("email is required");
            }
            if(isDateEmpty){
                utilHelper.createPopUpDialog("Error Input","Hired date is required");
            }
        }
        else{
            String hiredDate = ((TextView)findViewById(R.id.hiredDate)).getText().toString();
            String gender = ((RadioButton)findViewById(((RadioGroup)findViewById(R.id.radioGroupGender)).getCheckedRadioButtonId())).getText().toString();
            String employeeTag = "";
            if(selectedIDRadioButton!=0){
                 employeeTag = ((RadioButton)findViewById(selectedIDRadioButton)).getText().toString();
            }
            CheckBox isPM = findViewById(R.id.checkboxIsPM);
            String pmTag,userFlag;
            pmTag = isPM.isChecked() ? "1" : "0";
            //userFlag = "1";
            submitHREmployee(this,firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag);
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

    public void submitHREmployee(final Context context, String firstName, String lastName, String email, String address, String hiredDate, String phone, String gender, String employeeTag, String pmTag){
        class insertToDB extends AsyncTask<Void,Void,String> {
            String firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag;
            Context context;
            ProgressDialog loading;
            public insertToDB(Context context,String firstName,String lastName,String email, String address, String hiredDate,String phone,String gender, String employeeTag, String pmTag){
                this.firstName = firstName;
                this.lastName = lastName;
                this.email = email;
                this.address = address;
                this.hiredDate = hiredDate;
                this.phone = phone;
                this.gender = gender;
                this.employeeTag = employeeTag;
                this.pmTag = pmTag;
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Inserting employee's data...","Please wait...",false,false);
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
                params.put("employee_id",employeeID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.EditEmployee, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context,firstName,lastName,email,address,hiredDate,phone,gender,employeeTag,pmTag);
        ae.execute();
    }

    public void getData(Context context){
        class getDataFromDB extends AsyncTask<Void,Void,String> {
            Context context;
            ProgressDialog loading;
            public getDataFromDB(Context context){
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Getting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray result = output.getJSONArray("employee");
                    JSONObject jo = result.getJSONObject(0);//ini 0 soalnya data nya cman 1 dong
                    EditText firstName = findViewById(R.id.firstName);
                    EditText lastName = findViewById(R.id.lastName);
                    EditText email = findViewById(R.id.email);
                    TextView hiredDate = findViewById(R.id.hiredDate);
                    EditText address = findViewById(R.id.address);

                    firstName.setText(jo.getString("first_name"));
                    lastName.setText(jo.getString("last_name"));
                    email.setText(jo.getString("email"));
                    address.setText(jo.getString("address"));

                    String[] date = jo.getString("hired_date").split("-");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(date[0]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(date[1])-1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));

                    String currentDateString = dateFormat.format(calendar.getTime());
                    hiredDate.setText(currentDateString);
                    ((RadioGroup)findViewById(R.id.radioGroup2)).clearCheck();
                    ((RadioGroup)findViewById(R.id.radioGroup1)).clearCheck();
                    if(jo.getString("employee_tag").contains("Full")){
                        ((RadioButton)findViewById(R.id.radioEmployeeFullTime)).setChecked(true);
                    }else if (jo.getString("employee_tag").contains("Inte")){
                        ((RadioButton)findViewById(R.id.radioEmployeeIntern)).setChecked(true);
                    }else if(jo.getString("employee_tag").contains("Cont")){
                        ((RadioButton)findViewById(R.id.radioEmployeeContract)).setChecked(true);
                    }
                    if(jo.getString("gender").contains("Fe")){
                        ((RadioGroup)findViewById(R.id.radioGroupGender)).clearCheck();
                        ((RadioButton)findViewById(R.id.radioEmployeeFemale)).setChecked(true);
                    }
                    if(jo.getString("isPM").equalsIgnoreCase("1"))
                        ((CheckBox)findViewById(R.id.checkboxIsPM)).setChecked(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("employee_id",employeeID);
                params.put("employee_name","");

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeForAdmin, params);
                return res;
            }
        }

        getDataFromDB ae = new getDataFromDB(context);
        ae.execute();
    }
}
