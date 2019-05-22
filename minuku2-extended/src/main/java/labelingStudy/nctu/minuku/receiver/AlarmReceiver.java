package labelingStudy.nctu.minuku.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.service.NotificationListenService;

import static labelingStudy.nctu.minuku.config.Constants.QUESTIONNAIRE_TITLE_RANDOM_NOTI;
import static labelingStudy.nctu.minuku.config.Constants.VIDEO_DIRECTORY_PATH;
import static labelingStudy.nctu.minuku.config.SharedVariables.NSHasPulledDown;
import static labelingStudy.nctu.minuku.config.SharedVariables.RANDOMSURVEYALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.RESET;
import static labelingStudy.nctu.minuku.config.SharedVariables.SURVEYALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.SURVEYDELETEALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.canFillQuestionnaire;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNoti;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNotiMC;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNotiMCNoti;
import static labelingStudy.nctu.minuku.config.SharedVariables.dayCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.dayCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.extraForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.hour;
import static labelingStudy.nctu.minuku.config.SharedVariables.min;
import static labelingStudy.nctu.minuku.config.SharedVariables.nextTimeRadomAlarmNumber;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiPackForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiPostedTimeForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTextForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTitleForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.pullcontent;
import static labelingStudy.nctu.minuku.config.SharedVariables.resetFire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey10Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey1Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey2Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey3Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey4Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey5Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey6Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey7Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey8Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.survey9Fire;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCount;

/**
 * Created by chiaenchiang on 20/11/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AlarmReceiver extends BroadcastReceiver {
    public String TAG  = "AlarmReceiver";
    appDatabase db;
    int  countDown;
    public NotificationListenService notificationListenService = new NotificationListenService();
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onReceive(Context context, Intent intent) {
        db = appDatabase.getDatabase(context);
        String action = intent.getAction();
        SharedPreferences  sharedPrefs = context.getSharedPreferences(Constants.sharedPrefString, context.MODE_PRIVATE);
        Integer alarmNumber = intent.getIntExtra("alarmNumber",-1);
        Integer notiNumber = intent.getIntExtra("notiNumber",-1);
         if(action.equals(RESET)){
             Log.d("AlarmHelper", "AlarmReceiver set reset ");
             todayMCount = 0;
             todayNCount = 0;

//            pref.edit().putInt("todayMCount", 0).apply();
//            pref.edit().putInt("todayNCount", 0).apply();
//            int dayCount = pref.getInt("dayCount",0);
            dayCount+=1;
             sharedPrefs.edit().putInt(dayCountString,dayCount).commit();
//            pref.edit().putInt("dayCount", dayCount).apply();
            deleteAllSyncVideo();
            deleteAllSyncData();

            // 整點叫你
    //        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    //        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            Toast.makeText(context,"整點歸零 delete all sync data",Toast.LENGTH_LONG).show();
//            JSONObject object = new JSONObject();
//
//             try {
//                 object.put("resetReceive", SharedVariables.getReadableTime(new Date().getTime()));
//             } catch (JSONException e) {
//                 e.printStackTrace();
//             }
//             CSVHelper.storeToCSV("CheckAlarm.csv",object.toString());
             resetFire = true;

        }else if(action.equals(SURVEYALARM)){
             Log.d("AlarmHelper", "AlarmReceiver set survey ");
             canSentNotiMC = true;
             canSentNotiMCNoti = true;
             NSHasPulledDown = false;
             pullcontent.clear();


             //Toast.makeText(context,"可送noti",Toast.LENGTH_LONG).show();
            // JSONObject object = new JSONObject();

//             try {
//                 object.put("surveyReceive",SharedVariables.getReadableTime(new Date().getTime()));
//             } catch (JSONException e) {
//                 e.printStackTrace();
//             }
//             CSVHelper.storeToCSV("CheckAlarm.csv",object.toString());
             if(alarmNumber%2==0){
                 canSentNoti = true;
             }
             if(alarmNumber == 0){
                // CSVHelper.storeToCSV("CheckAlarm.csv","survey1Fire set true");
                 survey1Fire = true;
             }else if(alarmNumber == 1){
                // CSVHelper.storeToCSV("CheckAlarm.csv","survey2Fire set true");
                 survey2Fire = true ;
             }else if(alarmNumber == 2){
                // CSVHelper.storeToCSV("CheckAlarm.csv","survey3Fire set true");
                 survey3Fire = true;
             }else if(alarmNumber == 3){
                 survey4Fire = true;
               //  CSVHelper.storeToCSV("CheckAlarm.csv","survey4Fire set true");
             }else if(alarmNumber == 4){
                 survey5Fire = true;
               //  CSVHelper.storeToCSV("CheckAlarm.csv","survey5Fire set true");
             }else if(alarmNumber == 5){
                 survey6Fire = true;
               //  CSVHelper.storeToCSV("CheckAlarm.csv","survey6Fire set true");
             }else if(alarmNumber == 6){
                 survey7Fire = true;
              //   CSVHelper.storeToCSV("CheckAlarm.csv","survey7Fire set true");
             }else if(alarmNumber == 7){
                 survey8Fire = true;
               //  CSVHelper.storeToCSV("CheckAlarm.csv","survey8Fire set true");
             }else if(alarmNumber == 8){
                 survey9Fire = true ;
                // CSVHelper.storeToCSV("CheckAlarm.csv","survey8Fire set true");
             }else if(alarmNumber == 9){
                 survey10Fire = true;
                 canSentNoti = false;
                 canSentNotiMC = false;
                 canSentNotiMCNoti = false;
             //    CSVHelper.storeToCSV("CheckAlarm.csv","survey8Fire set true");
             }
//             else if(alarmNumber == 10){
////                 survey11Fire = true;
//                 triggerNotifications(notiPackForRandom,notiPostedTimeForRandom,2,notiTitleForRandom+" "+notiTextForRandom,context);
//                 CSVHelper.storeToCSV("randomAlarm.csv","receive time"+ getReadableTime(System.currentTimeMillis()));
//                 CSVHelper.storeToCSV("randomAlarm.csv","receive content"+notiPackForRandom +" "+ notiPostedTimeForRandom + " "+notiTitleForRandom+" "+notiTextForRandom);
//
//             }
//             else{
//                 CSVHelper.storeToCSV("CheckAlarm.csv","alarm receive wrong number");
//             }
             if(alarmNumber>=0 && alarmNumber<9){
                 createRandomAlarm(context,alarmNumber);
             }
        }
        else if(action.equals(RANDOMSURVEYALARM)){
            if(canSentNoti) {
                extraForQ = notiTitleForRandom + " " + notiTextForRandom;
                if(!notificationListenService.checkOtherNotiExist(context)) {
                    notificationListenService.createNotification(context,notiPackForRandom,notiPostedTimeForRandom,1,101,QUESTIONNAIRE_TITLE_RANDOM_NOTI);
                   // triggerNotifications(notiPackForRandom, notiPostedTimeForRandom, 1, context);
                    CSVHelper.storeToCSV("randomAlarm.csv", "receive time" + getReadableTime(System.currentTimeMillis()));
                    CSVHelper.storeToCSV("randomAlarm.csv", "receive content" + notiPackForRandom + " " + notiPostedTimeForRandom + " " + notiTitleForRandom + " " + notiTextForRandom);
                    canSentNoti = false;
                }
            }
         }else if (action.equals(SURVEYDELETEALARM)){
             canFillQuestionnaire = false;
             cancelNotification(context, notiNumber);
         }


    }

    public void  cancelNotification(Context context,int noti_id){
        CSVHelper.storeToCSV("wipeNoti.csv","receive delete alarm : "+getReadableTime(new Date().getTime()));
        if(noti_id == 100){

        }else if(noti_id == 101){
            CSVHelper.storeToCSV("MCNoti_cancel.csv","receive delete alarm : "+getReadableTime(new Date().getTime()));
        }else if(noti_id == 102){
            CSVHelper.storeToCSV("randomAlarm.csv","receive delete alarm : "+getReadableTime(new Date().getTime()));
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications =
                new StatusBarNotification[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifications = notificationManager.getActiveNotifications();
        }
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == noti_id) {
                notificationManager.cancel(noti_id);
                if(noti_id == 100){

                }else if(noti_id == 101){
                    CSVHelper.storeToCSV("MCNoti_cancel.csv","find and delete alarm");
                }else if(noti_id == 102){
                    CSVHelper.storeToCSV("randomAlarm.csv","find and delete alarm");
                }
                // Do something.
                return;
            }
        }
        if(noti_id == 100){
            CSVHelper.storeToCSV("MCNoti_cancel.csv","alarm not found");
        }else if(noti_id == 101){
            CSVHelper.storeToCSV("MCNoti_cancel.csv","alarm not found");
        }else if(noti_id == 102){
            CSVHelper.storeToCSV("randomAlarm.csv","alarm not found");
        }

    }


//    public void triggerNotifications(String app,long enterTime,int questionType,Context context){
//        Log.d(TAG," notificaitons");
//        final NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // notification channel
//        int notifyID = 101;
//        CharSequence name = "ESM_Channel";// The user-visible name of the channel.
//        int importance = NotificationManager.IMPORTANCE_HIGH;
////        @SuppressLint({"NewApi", "LocalSuppress"}) NotificationChannel mChannel = new NotificationChannel(Constants.QUESTIONNAIRE_CHANNEL_ID, name, importance);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            mManager.createNotificationChannel(mChannel);
////        }
//        //       createNotificationChannel("ESM_Channel",Constants.QUESTIONNAIRE_CHANNEL_ID,NotificationManager.IMPORTANCE_HIGH);
//
//        //print notificaiton send time
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(Constants.QUESTIONNAIRE_CHANNEL_ID, name, importance);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//
//        Log.d(TAG,"ready to sent questionnaire");
//
////        Intent nIntent = new Intent(Intent.ACTION_VIEW);
////
////        //   nIntent.putExtra("URL", "https://nctucommunication.qualtrics.com/jfe/form/SV_78KPI6cbgtRFHp3?app="+app);//"&title="+ URLEncoder.encode(title, "UTF-8") +"&text="+URLEncoder.encode(text, "UTF-8")+"&created_at="+unixTime*1000+"&user="+deviceId+"&time="+URLEncoder.encode(formattedDate, "UTF-8"
////        try {
////            nIntent = new Intent(this,Class.forName("mobilecrowdsourceStudy.nctu.minuku_2.MainActivity"));
////        } catch (ClassNotFoundException e) {
////            e.printStackTrace();
////        }
//        appNameForQ = app;
//
//        questionaireType = questionType;
//
//        canFillQuestionnaire = true;
//        timeForQ = getReadableTime(enterTime);
//
////        //nIntent.setData(Uri.parse("https://nctucommunication.qualtrics.com/jfe/form/SV_ezVodMgyxCpbe7j?app="+app));
////        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nIntent, 0);
////        // Create a notification and set the notification channel.
////        Notification.Builder noti = new Notification.Builder(this)
////                .setContentTitle("您將貢獻新的資料")
////                .setContentText("請填寫問卷")
////                .setColor(Color.WHITE)
////                // .setWhen(System.currentTimeMillis()+5000)
////                .setContentIntent(contentIntent)
////                .setAutoCancel(true)
////                .setOngoing(true);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////           noti.setSmallIcon(getNotificationIcon(noti))
////                   .setChannelId(CHANNEL_ID);
////        } else {
////           noti.setSmallIcon(getNotificationIcon(noti));
////        }
//
//        // prepare for questionnaire
//
//
//        mManager.notify(notifyID , getOngoingNotification(context));
//
////        ResponseDataRecord responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionType);
////        CountDownTask countDownTask = new CountDownTask();
////        countDownTask.startRepeatingTask(responseDataRecord,context);
//        ResponseDataRecord responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionType);
//        CSVHelper.storeToCSV("wipeNoti.csv","alarm - startNotiTime : "+getReadableTime(new Date().getTime()));
//        appDatabase db = appDatabase.getDatabase(context.getApplicationContext());
//        db.repsonseDataRecordDao().insertAll(responseDataRecord);
//
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(600*1000);
//                        } catch (InterruptedException e) {
//                            Log.d(TAG, "sleep failure");
//                        }
//                        relatedId++;
//                        mManager.cancel(101);
//                        canFillQuestionnaire = false;
//                        CSVHelper.storeToCSV("wipeNoti.csv","alarm - selfCancelNotiTime : "+getReadableTime(new Date().getTime()));
//                    }
//                }
//        ).start();
//
//    }
//
//    private Notification getOngoingNotification(Context context) {
//
//        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
//        bigTextStyle.setBigContentTitle(Constants.APP_NAME);
//        bigTextStyle.bigText("請填寫問卷 - 是否看到通知");
//        Intent nIntent = new Intent(Intent.ACTION_VIEW);
//
//        try {
//            nIntent = new Intent(context,Class.forName("mobilecrowdsourceStudy.nctu.minuku_2.MainActivity"));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
////        nIntent.putExtra("canFill",true);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder noti = new Notification.Builder(context)
//                .setContentTitle("您將貢獻新的資料")
//                .setContentText("是否看到通知")
//                .setStyle(bigTextStyle)
//                .setContentIntent(contentIntent)
//                .setAutoCancel(false);
//                //.setOngoing(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return noti
//                    .setSmallIcon(getNotificationIcon(noti))
//                    .setChannelId(Constants.QUESTIONNAIRE_CHANNEL_ID)
//                    .build();
//        } else {
//            return noti
//                    .setSmallIcon(getNotificationIcon(noti))
//                    .setPriority(Notification.PRIORITY_MAX)
//                    .build();
//        }
//    }
//
//
//    private int getNotificationIcon(Notification.Builder notificationBuilder) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            notificationBuilder.setColor(Color.TRANSPARENT);
//            return R.drawable.hand_shake_noti;
//        }
//
//        return R.drawable.muilab_icon;
//    }

//    public class CountDownTask {
//        Handler mHandler = new Handler();
//        int interval = 1000; // 1000 * 30
//        ResponseDataRecord responseDR;
//        Context contextt;
//        Runnable mHandlerTask = new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void run() {
//                countDown -= interval;
//                mHandler.postDelayed(mHandlerTask, interval);
//                if (countDown > 0) {
//                    Log.d(TAG,"CountDown in if = "+countDown);
//
//                    //已經handle 而且超過十分鐘 停止偵測
//                    if(ifComplete) {
//                        responseDR.setStartAnswerTime(getReadableTime(startAnswerTime));
//                        responseDR.setFinishedTime(getReadableTime(finishAnswerTime));
//                        responseDR.setIfComplete(true);
//                        db.repsonseDataRecordDao().insertAll(responseDR);
//                        stopRepeatingTask();
//
//                    }
//
//                }else{   //十分鐘之後沒有按
//                    db.repsonseDataRecordDao().insertAll(responseDR);
//                    stopRepeatingTask();
//
//                }
//            }
//        };
//
//        void startRepeatingTask(ResponseDataRecord rs,Context context) {
//            responseDR = rs;
//            contextt = context;
//            countDown = 10*60*1000;//ten minutes // 10*60*1000
//            mHandlerTask.run();
//        }
//
//        void stopRepeatingTask() {
//            NotificationManager mManager = (NotificationManager) contextt.getSystemService(Context.NOTIFICATION_SERVICE);
//            relatedId++;
//            mManager.cancel(101);
//            canFillQuestionnaire = false;
//            startAnswerTime = Long.valueOf(0);
//            finishAnswerTime = Long.valueOf(0);
//            ifComplete = false;
//            mHandler.removeCallbacks(mHandlerTask);
//            countDown = -1;
//        }
//    }




    public static void createRandomAlarm(Context context,int alarmNumber){
        int minNum = 30;
        int maxNum = 60;
        int random = new Random().nextInt((maxNum - minNum) + 1) + minNum;
        int target_minute ;
        int target_hour = 0;
        if(min[alarmNumber]+random >= 60){
            target_minute = min[alarmNumber]+random - 60;
            target_hour = hour[alarmNumber]+1;
            target_hour = target_hour>=24? target_hour-24 : target_hour;
        }else{
            target_minute = min[alarmNumber]+random;
            target_hour = hour[alarmNumber];
        }
        Calendar currentTime = Calendar.getInstance();
        Calendar when = Calendar.getInstance();
        when.setTimeInMillis(System.currentTimeMillis());


        when.set(Calendar.HOUR_OF_DAY,target_hour);  // (8+0*2)8 (8+1*2)10 (8+2*2)12 (8+3*2)14 (8+4*2)16 (8+5*2)18 (8+6*2)10 (8+7*2)22
        when.set(Calendar.MINUTE, target_minute);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        CSVHelper.storeToCSV("randomAlarm.csv","random "+random);
        CSVHelper.storeToCSV("randomAlarm.csv","now hour "+hour[alarmNumber]);
        CSVHelper.storeToCSV("randomAlarm.csv","now min "+min[alarmNumber]);
        CSVHelper.storeToCSV("randomAlarm.csv","new Random hour "+(target_hour));
        CSVHelper.storeToCSV("randomAlarm.csv","new Random min "+(target_minute));

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(RANDOMSURVEYALARM);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 25,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(when.getTimeInMillis(),alarmIntent),alarmIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
        else
            am.set(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
        nextTimeRadomAlarmNumber = alarmNumber +1;
        nextTimeRadomAlarmNumber = nextTimeRadomAlarmNumber>=10? 0 : nextTimeRadomAlarmNumber;

    }




    public void deleteAllSyncData(){
        db.accessibilityDataRecordDao().deleteSyncData(1);
        db.transportationModeDataRecordDao().deleteSyncData(1);
        db.locationDataRecordDao().deleteSyncData(1);
        db.activityRecognitionDataRecordDao().deleteSyncData(1);
        db.ringerDataRecordDao().deleteSyncData(1);
        db.batteryDataRecordDao().deleteSyncData(1);
        db.appUsageDataRecordDao().deleteSyncData(1);
        db.telephonyDataRecordDao().deleteSyncData(1);
        db.sensorDataRecordDao().deleteSyncData(1);
        db.notificationDataRecordDao().deleteSyncData(1);
        db.mobileCrowdsourceDataRecordDao().deleteSyncData(1);
        db.finalAnswerDao().deleteSyncData(1);
        db.videoDataRecordDao().deleteSyncData(1);


    }
    public void deleteAllSyncVideo() {

        Cursor transCursor = db.videoDataRecordDao().getSyncVideoData(1);
        int rows = transCursor.getCount();

        if (rows != 0) {
            transCursor.moveToFirst();
            for (int i = 0; i < rows; i++) {
                String fileName = transCursor.getString(3);
                File file = new File(Environment.getExternalStorageDirectory()+VIDEO_DIRECTORY_PATH,
                        fileName);
                boolean deleted = file.delete();
                if(deleted){
                    Log.d("AlarmHelper", "fileName : "+ fileName +"has been deleted");
                    CSVHelper.storeToCSV("FileDelete.csv","fileName : "+ fileName +"has been deleted");
                }else{
                    Log.d(TAG,"file: "+fileName+" has not been deleted");
                    CSVHelper.storeToCSV("FileDelete.csv","fileName : "+ fileName +"has not been deleted");
                }
                transCursor.moveToNext();
            }
        }
    }

}
