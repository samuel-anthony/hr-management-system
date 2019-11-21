package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HREmployeeMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_main);
    }

    public void onClickAddNewButton(View view){
        Intent mainActivity = new Intent(this, HREmployeeAdd.class);
        startActivity(mainActivity);
    }
    public void onClickBackButton(View view){
        finish();
    }
}
