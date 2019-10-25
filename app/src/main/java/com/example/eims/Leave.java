package com.example.eims;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Leave extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String nameAndEmail,selectedProjectID,selectedLeaveID;
    Bundle bundle;
    JSONObject output;
    View datePickerView;
    Uri imageUri;
    Bitmap bitmap = null;
    TextView uploadPictureStat;
    FrameLayout fragmentPicture;
    private static final int PICK_IMAGE = 1;

    ArrayList<HashMap<String,String>> completeProjectData = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completeLeaveData = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uploadPictureStat = findViewById(R.id.uploadedPictureStatus);
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);
        fragmentPicture = findViewById(R.id.fragmentImageLeave);
        fragmentPicture.setVisibility(View.INVISIBLE);
        Fragment fragment = new Image();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentImageLeave, fragment);
        ft.commit();

        getLeaveEmployeeData();
    }

    public void showDatePicker(View view){
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");
        datePickerView = view;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateString = dateFormat.format(calendar.getTime());
        TextView a = (TextView) datePickerView;
        a.setText(currentDateString);

        }


    public void showUploadFragment(View view){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                ImageView uploadedPic = findViewById(R.id.uploadedPicture);
                uploadedPic.setImageBitmap(bitmap);
                uploadPictureStat.setText("picture selected, click to view");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
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

    public void onClickSubmitButton(){

    }

    public void onClickBackButton(View view){
        finish();
    }


    public void getLeaveEmployeeData(){
        class getLeaveEmployeeDataFromDB extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Leave.this,"Retrieving employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray resultProject = output.getJSONArray("project");
                    ArrayList<String> arrayListProject = new ArrayList<String>();
                    for(int i = 0; i<resultProject.length() ; i++){
                        JSONObject jo = resultProject.getJSONObject(i);
                        HashMap<String,String> data = new HashMap<>();
                        data.put("project_id",jo.getString("project_id"));
                        data.put("project_name",jo.getString("project_name"));
                        completeProjectData.add(data);
                        arrayListProject.add(jo.getString("project_name"));
                    }
                    ArrayList<String> arrayListLeave = new ArrayList<String>();
                    JSONArray resultLeaveType = output.getJSONArray("leave");
                    for(int i = 0; i<resultLeaveType.length() ; i++){
                        JSONObject jo = resultLeaveType.getJSONObject(i);
                        HashMap<String,String> data = new HashMap<>();
                        data.put("leave_id",jo.getString("leave_id"));
                        data.put("leave_name",jo.getString("leave_name"));
                        data.put("max_duration",jo.getString("max_duration"));
                        data.put("leave_balance",jo.getString("leave_balance"));
                        completeLeaveData.add(data);
                        arrayListLeave.add(jo.getString("leave_name")+", balance:"+jo.getString("leave_balance"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Leave.this,android.R.layout.simple_selectable_list_item, arrayListProject);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerProject = findViewById(R.id.spinnerProject);
                    spinnerProject.setAdapter(arrayAdapter);
                    spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String projectName = parent.getItemAtPosition(position).toString();
                            selectedProjectID = completeProjectData.get(position).get("project_id");
                            Toast.makeText(parent.getContext(), "Selected: " + projectName,    Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                            Toast.makeText(parent.getContext(), "Nothing Selected: ",    Toast.LENGTH_LONG).show();
                        }
                    });
                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(Leave.this,android.R.layout.simple_selectable_list_item, arrayListLeave);
                    arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner spinnerLeave = findViewById(R.id.spinnerLeaveType);
                    spinnerLeave.setAdapter(arrayAdapter2);
                    spinnerLeave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String leaveName = parent.getItemAtPosition(position).toString();
                            selectedLeaveID = completeLeaveData.get(position).get("leave_id");
                            Toast.makeText(parent.getContext(), "Selected: " + leaveName, Toast.LENGTH_LONG).show();
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
                try {
                    params.put("employee_id",output.getString("employee_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.GetLeavePageDataEmployee, params);
                return res;
            }
        }

        getLeaveEmployeeDataFromDB ae = new getLeaveEmployeeDataFromDB();
        ae.execute();
    }
}
