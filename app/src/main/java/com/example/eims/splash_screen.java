package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    //nanti kasi validasi uda login atau belum, klo uda lari ke mainpage, else lari ke login register
                    startActivity(new Intent(splash_screen.this, Login.class));
                    finish();
                }
            }
        };
        thread.start();
    }
}
