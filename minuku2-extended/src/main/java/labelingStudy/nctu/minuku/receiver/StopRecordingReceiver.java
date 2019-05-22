package labelingStudy.nctu.minuku.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import labelingStudy.nctu.minuku.service.NotificationListenService;

import static labelingStudy.nctu.minuku.config.Constants.DELETE;
import static labelingStudy.nctu.minuku.config.Constants.STOP;
import static labelingStudy.nctu.minuku.config.Constants.STOP_RECORDING;

/**
 * Created by chiaenchiang on 28/04/2019.
 */

public class StopRecordingReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(STOP)){
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(STOP_RECORDING);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);
        }else if(intent.getAction().equals(DELETE)){
            NotificationListenService.cancelNotification(context,200);
        }

    }
}
