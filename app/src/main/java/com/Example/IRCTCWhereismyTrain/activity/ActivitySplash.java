package com.Example.IRCTCWhereismyTrain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.Example.IRCTCWhereismyTrain.R;

public class ActivitySplash extends AppCompatActivity {
    int pp_position;

    public void onBackPressed() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.act_splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                ActivitySplash activitySplash = ActivitySplash.this;
                activitySplash.startActivity(new Intent(activitySplash, ActivityMain.class));
                ActivitySplash.this.finish();
            }
        }, 1500);
    }
}
