package mobilecrowdsourceStudy.nctu.minuku_2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.service.MobileAccessibilityService;
import labelingStudy.nctu.minuku.service.NotificationListenService;
import mobilecrowdsourceStudy.nctu.minuku_2.service.BackgroundService;

import static labelingStudy.nctu.minuku.config.Constants.LEN_PREFIX;
import static labelingStudy.nctu.minuku.config.Constants.VAL_PREFIX;
import static labelingStudy.nctu.minuku.config.Constants.everyDayMrecordString;
import static labelingStudy.nctu.minuku.config.Constants.everyDayNrecordString;
import static labelingStudy.nctu.minuku.config.SharedVariables.allMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.allMCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.allNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.allNCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.dayCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.dayCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.everyDayMrecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.everyDayNrecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCountString;

/**
 * Created by chiaenchiang on 07/11/2018.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";
   // private  static DBHelper dbhelper = null;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(TAG,"boot_complete in first");
            SharedPreferences pref = context.getSharedPreferences("edu.nctu.minuku", context.MODE_PRIVATE);
            pref.edit()
                    .putLong("state_bootcomplete", System.currentTimeMillis() / 1000L)
                    .apply();
            try{
//                dbhelper = new DBHelper(context);
//                dbhelper.getWritableDatabase();
                Log.d(TAG,"db is ok");

            }catch (Exception e){
                e.printStackTrace();

            }
            finally {

                Log.d(TAG, "Successfully receive reboot request");

//                here we start the service



                Intent bintent = new Intent(context, BackgroundService.class);
                context.startService(bintent);
                Log.d(TAG,"BackgroundService is ok");

                Intent nintent = new Intent(context, NotificationListenService.class);
                context.startService(nintent);
                Log.d(TAG,"NotificationListener is ok");

                Intent mintent = new Intent(context, MobileAccessibilityService.class);
                context.startService(mintent);
                Log.d(TAG,"MobileAccessibilityService is ok");

                everyDayNrecord  = getFromPrefs(everyDayNrecordString,context);
                everyDayMrecord = getFromPrefs(everyDayMrecordString,context);

                /*if(!InstanceManager.isInitialized()) {
                    InstanceManager.getInstance(context);
                }*/

                todayMCount = pref.getInt(todayMCountString,0);
                todayNCount = pref.getInt(todayNCountString,0);
                dayCount = pref.getInt(dayCountString,1);
                allMCount = pref.getInt(allMCountString,0);
                allNCount = pref.getInt(allNCountString,0);


            }


        }


    }
    public int[] getFromPrefs(String name,Context context){
        int[] ret;
        SharedPreferences prefs = context.getSharedPreferences(Constants.sharedPrefString, Context.MODE_PRIVATE);

        int count = prefs.getInt(LEN_PREFIX + name, 0);
        ret = new int[count];
        for (int i = 0; i < count; i++){
            ret[i] = prefs.getInt(VAL_PREFIX+ name + i, i);
        }
        return ret;
    }



}