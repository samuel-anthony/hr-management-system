package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Report_Main extends AppCompatActivity {
    String nameAndEmail;
    Bundle bundle;
    JSONObject output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__main);

        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);

    }
    public void onclickChangeMenu(View view){
        Intent mainActivity = new Intent(this, Report_Menu.class);
        mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
        if(view == findViewById(R.id.menu_attendance)){
            mainActivity.putExtra("sub_menu","Attendance");
        }else if(view == findViewById(R.id.menu_leave)){
            mainActivity.putExtra("sub_menu","Leave");
        }else if(view == findViewById(R.id.menu_claim)){
            mainActivity.putExtra("sub_menu","Claim");
        }
        startActivity(mainActivity);
    }

    public void onClickBackButton(View view){
        finish();
    }

}
