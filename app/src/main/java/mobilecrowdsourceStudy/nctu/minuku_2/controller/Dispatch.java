package mobilecrowdsourceStudy.nctu.minuku_2.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.service.NotificationListenService;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.MainActivity;
import mobilecrowdsourceStudy.nctu.minuku_2.service.BackgroundService;

/**
 * Created by chiaenchiang on 26/10/2018.
 */

public class Dispatch extends Activity {

    private final String TAG = "Dispatch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.dispatcher);

        MultiDex.install(this);

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        Class<?> activityClass;

        boolean firstStartBackGround = sharedPrefs.getBoolean("firstStartBackGround", true);


        if(firstStartBackGround) {

            startBackgroundService();

            sharedPrefs.edit().putBoolean("firstStartBackGround", false).apply();
        }

        if(!firstStartBackGround && !BackgroundService.isBackgroundServiceRunning){

            startBackgroundService();
        }

        try {

            activityClass = Class.forName(
                    sharedPrefs.getString("lastActivity", MainActivity.class.getName()));
        } catch(ClassNotFoundException e) {

            activityClass = MainActivity.class;
        }

        Log.d(TAG, "Going to "+activityClass.getName());

        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        Dispatch.this.finish();
    }

    private void startBackgroundService(){


        Intent intentToStartBackground = new Intent(getBaseContext(), BackgroundService.class);
        Intent intentToStartNLService = new Intent(getBaseContext(), NotificationListenService.class);
        startService(intentToStartBackground);
        startService(intentToStartNLService);

//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(this,intentToStartNLService);
//
//        } else {
//            startService(intentToStartBackground);
//            startService(intentToStartNLService);
//        }
    }

}
