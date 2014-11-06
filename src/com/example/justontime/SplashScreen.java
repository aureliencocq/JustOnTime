package com.example.justontime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreen extends Activity {

	// timer
    private static int SPLASH_TIME_OUT = 2000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
 
        new Handler().postDelayed(new Runnable() {
 
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, FirstPage.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
