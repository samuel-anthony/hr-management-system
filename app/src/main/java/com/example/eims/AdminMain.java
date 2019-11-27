package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
    }

    public void onclickChangeMenu(View view){
        if(view == findViewById(R.id.menu_employee)){
            Intent mainActivity = new Intent(this, HREmployeeMain.class);
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_project)){
            Intent mainActivity = new Intent(this, HRProjectMain.class);
            startActivity(mainActivity);

        }
        else if(view == findViewById(R.id.menu_attendance)){
            Intent mainActivity = new Intent(this,AdminAttendance.class);
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_leave)){
            Intent mainActivity = new Intent(this,AdminLeave.class);
            startActivity(mainActivity);
        }
        else if(view == findViewById(R.id.menu_claim)){
            Intent mainActivity = new Intent(this,AdminClaim.class);
            startActivity(mainActivity);
        }
    }

    public void signOut(View view){
        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
