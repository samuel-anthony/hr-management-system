package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity  {
    String nameAndEmail;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        try {
            JSONObject output = new JSONObject(bundle.getString("employee_data"));
            nameAndEmail = output.getString("first_name") + " " +output.getString("last_name") + ",\n" +output.getString("email");
            Fragment fragment = null;
            if(output.getString("is_pm").equalsIgnoreCase("1")){
                 fragment = new MainPM();
            }
            else{
                fragment = new MainEmployee();
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment);
            ft.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);

    }

    public void onclickChangeMenu(View view){
        if(view == findViewById(R.id.menu_attendance)){
            Intent mainActivity = new Intent(MainActivity.this, Attendance.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_leave)){
            Intent mainActivity = new Intent(MainActivity.this, Leave.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_claim)){
            Intent mainActivity = new Intent(MainActivity.this, Reimbursement.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_task)){
            Intent mainActivity = new Intent(MainActivity.this, TaskMain.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_report)){
            Intent mainActivity = new Intent(MainActivity.this, Report_Main.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_profile)){
            Intent mainActivity = new Intent(MainActivity.this, Profile.class);
            mainActivity.putExtra("employee_data",bundle.getString("employee_data"));
            startActivity(mainActivity);
        }
    }


    public void signOut(View view){
        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
