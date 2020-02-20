package com.Example.IRCTCWhereismyTrain.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.fragment.PNRStatusListFragment;

public class ActivityMainPNRStatus extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.act_main_pnr_status);
        openFragment(new PNRStatusListFragment());
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frame, fragment);
        beginTransaction.addToBackStack(fragment.toString());
        beginTransaction.commit();
    }

    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("==>>>");
        stringBuilder.append(backStackEntryCount);
        Log.e("onBackPressed", stringBuilder.toString());
        if (backStackEntryCount == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
