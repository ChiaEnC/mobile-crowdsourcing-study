package mobilecrowdsourceStudy.nctu.minuku_2.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import labelingStudy.nctu.minuku.config.Constants;
import mobilecrowdsourceStudy.nctu.minuku_2.service.BackgroundService;


/**
 * Created by chiaenchiang on 01/11/2018.
 */

public class RestarterBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "RestarterBroadcastReceiver";




    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Constants.CHECK_SERVICE_ACTION)) {

            Log.d(TAG, "the RestarterBroadcastReceiver is going to start the BackgroundService");

            Intent intentToStartBackground = new Intent(context, BackgroundService.class);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intentToStartBackground);
//            } else {
//                context.startService(intentToStartBackground);
//            }
            context.startService(intentToStartBackground);




        }
    }
}