package com.Example.IRCTCWhereismyTrain.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.widget.GpsTracker;
import com.Example.IRCTCWhereismyTrain.widget.GpsUtils;
import com.Example.IRCTCWhereismyTrain.widget.GpsUtils.onGpsListener;

public class ActivityMain extends AppCompatActivity {
    GpsTracker gpsTracker;
    boolean isBack = false;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        CheckGps();
    }

    private void CheckGps() {
        this.gpsTracker = new GpsTracker(this);
        if (!this.gpsTracker.isGPSEnable()) {
            new GpsUtils(this).turnGPSOn(new onGpsListener() {
                public void gpsStatus(boolean z) {
                }
            });
        }
    }

    @OnClick({R.id.tv_train_ticket, R.id.cv_find_train})
    public void onClickbookTrain() {
        startActivity(new Intent(this, ActivityMainTicketBook.class));

    }

    @OnClick({R.id.ll_live_status, R.id.cv_live_status})
    public void onClickLiveStatus() {
        startActivity(new Intent(this, ActivityMainStatusLive.class));
    }

    @OnClick({R.id.tv_train_schedule, R.id.cv_train_schedule})
    public void onClickTrainSchd() {
        startActivity(new Intent(this, ActivityMainTrainSchedule.class));
    }

    @OnClick({R.id.tv_live_station, R.id.cv_live_station})
    public void onClickLiveStation() {
        startActivity(new Intent(this, ActivityMainStationLive.class));

    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    @OnClick({R.id.tv_pnr_status, R.id.cv_pnr_status})
    public void onClickPnrStatus() {
        startActivity(new Intent(this, ActivityMainPNRStatus.class));

    }

    @OnClick({R.id.tv_setting})
    public void onClickSetting() {
        startActivity(new Intent(this, ActivitySettings.class));

    }
}
