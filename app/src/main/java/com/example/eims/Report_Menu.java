package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Report_Menu extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String nameAndEmail;
    Bundle bundle;
    JSONObject output;
    String sub_menu_text;
    SimpleDateFormat dateFormat;
    View datePickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__menu);
        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
            //JSONObject temp = new JSONObject(bundle.getString("sub_menu"));
            sub_menu_text = bundle.getString("sub_menu");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);
        TextView sub_menu_name = findViewById(R.id.sub_menu_name);
        sub_menu_name.setText(sub_menu_text);

        Fragment fragment = null;
        if(sub_menu_text.equalsIgnoreCase("Attendance")){
            fragment = new Attendance_Report();
        }
        else if(sub_menu_text.equalsIgnoreCase("Leave")){
            fragment = new Leave_Report();
        }
        else if(sub_menu_text.equalsIgnoreCase("Claim")){
            fragment = new Claim_Report();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.sub_menu, fragment);
        ft.commit();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }
    public void onClickBackButton(View view){
        finish();
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

        String currentDateString = dateFormat.format(calendar.getTime());
        TextView a = (TextView) datePickerView;
        a.setText(currentDateString);

    }


}
