package labelingStudy.nctu.minuku.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.R;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.ResponseDataRecord;
import labelingStudy.nctu.minuku.receiver.AlarmReceiver;
import labelingStudy.nctu.minuku.streamgenerator.NotificationStreamGenerator;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;

import static labelingStudy.nctu.minuku.config.Constants.Content;
import static labelingStudy.nctu.minuku.config.Constants.QUESTIONNAIRE_CHANNEL_ID;
import static labelingStudy.nctu.minuku.config.Constants.QUESTIONNAIRE_TITLE_CONTENT;
import static labelingStudy.nctu.minuku.config.SharedVariables.NotiInfoForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.SURVEYDELETEALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.canFillQuestionnaire;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNotiMC;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNotiMCNoti;
import static labelingStudy.nctu.minuku.config.SharedVariables.extraForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.hour;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifClickedNoti;
import static labelingStudy.nctu.minuku.config.SharedVariables.min;
import static labelingStudy.nctu.minuku.config.SharedVariables.nhandle_or_dismiss;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiPack;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiPackForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiPostedTimeForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiReason;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiSubText;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiText;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTextForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTickerText;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTitle;
import static labelingStudy.nctu.minuku.config.SharedVariables.notiTitleForRandom;
import static labelingStudy.nctu.minuku.config.SharedVariables.questionaireType;
import static labelingStudy.nctu.minuku.config.SharedVariables.relatedId;
import static labelingStudy.nctu.minuku.config.SharedVariables.timeForQ;
import static labelingStudy.nctu.minuku.service.MobileAccessibilityService.enterFlag;
import static labelingStudy.nctu.minuku.service.MobileCrowdsourceRecognitionService.matchAppCode;
import static labelingStudy.nctu.minuku.service.MobileCrowdsourceRecognitionService.matchAppName;

/**
 * Created by chiaenchiang on 18/11/2018.
 */

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListenService extends NotificationListenerService {
    private static final String TAG = "NotificationListener";

    private NotificationManager mManager;
    //    private String deviceId;
    private String title;
    private String text;
    private String subText;
    private String tickerText;
    public String pack;
    int notificationCode ;
    Long postedTime;
    String haveShownApp = "";
    int countDown;
    int countDownForNoti;
    String lastTimePostContent ="";
    String lastTimeRemovedContent = "";
    int noti_id;
    public int requestCode = 123;
    private ArrayList<String> haveSentMCNoti = new ArrayList<>();
    //    private String app;
//    private Boolean send_form;
//    private String  last_title;
//    private Boolean skip_form;
//    private Intent intent;

    // JSONObject dataInJson = new JSONObject();
    private static NotificationStreamGenerator notificationStreamGenerator;
    public NotificationListenService(NotificationStreamGenerator notiStreamGenerator){
        try {
            Log.d(TAG,"call notificationlistener service2");
            this.notificationStreamGenerator = (NotificationStreamGenerator) MinukuStreamManager.getInstance().getStreamGeneratorFor(NotificationDataRecord.class);
        } catch (StreamNotFoundException e) {
            this.notificationStreamGenerator = notiStreamGenerator;
            Log.d(TAG,"call notificationlistener service3");}
    }

    public NotificationListenService(){
        super();
    }

    public class repeatTask {
        Handler mHandler = new Handler();
        int interval = 1000; // 1000 * 30

        Runnable mHandlerTask = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                countDown -= interval;
                mHandler.postDelayed(mHandlerTask, interval);
                if (countDown > 0) {
                    Log.d(TAG,"CountDown in if = "+countDown);
//                    SharedPreferences pref = getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE);
//                    Integer nhandle_or_dismiss = pref.getInt("nhandle_or_dismiss", -1);
                    //已經handle 而且超過十分鐘 停止偵測
                    if(enterFlag) {
                        stopRepeatingTask();
                    }

                }else{   //十分鐘之後沒有按
                    // trigger notification 為什麼沒有做
                    // 送的時候當下有沒有亮和unlock
                    Log.d(TAG,"CountDown in else = "+countDown);
//                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                    if (MobileCrowdsourceRecognitionService.ifScreenLight(pm)) {
//                        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                        if (!MobileCrowdsourceRecognitionService.ifScreenLock(myKM)) {
//                            // trigger notification
//                            //triggerNotifications(pack,"noti_true_user_false");
//                            stopRepeatingTask();
//                        }
//                    }else{  //要送noti時沒有亮，就放棄送ESM
//                        stopRepeatingTask();
//                    }
                    if(canSentNotiMC) {

                        String post =getReadableTime(postedTime) ;
                        //String[] split_line = NotiInfoForQ.split("::");

                        extraForQ =Content+" : "+NotiInfoForQ;
                        triggerNotifications( haveShownApp, post ,2);
                        //storeToNotificationDataRecord(split_line[0],split_line[1],split_line[1]," ",haveShownApp,"MC_noti_QId "+relatedId);

                        canSentNotiMC = false;
                    }
                    stopRepeatingTask();
                    //relatedId++;
                }
            }
        };

        void startRepeatingTask() {
            postedTime = System.currentTimeMillis();
            countDown = 20*60*1000;//ten minutes // 10*60*1000
            Log.d(TAG,"startCountdown");
            haveShownApp = matchAppName(notificationCode);
            CSVHelper.storeToCSV("randomAlarm.csv","startCountingDown");
            mHandlerTask.run();
        }

        void stopRepeatingTask() {
            NotiInfoForQ = "";
            CSVHelper.storeToCSV("randomAlarm.csv","stopCountingDown");
            postedTime = Long.valueOf(0);
            mHandler.removeCallbacks(mHandlerTask);
            countDown = -1;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public  void setRandomNotiAndSentNoti(){
        StatusBarNotification[]sbn = this.getActiveNotifications();
        if(sbn!=null) {
//            for(StatusBarNotification s : sbn){
//                Log.d("checkRandomNoti","packageName : "+s.getPackageName());
//                Notification noti = s.getNotification();
//                Log.d("checkRandomNoti","title : "+noti.extras.get("android.title").toString());
//                Log.d("checkRandomNoti","text : "+noti.extras.get("android.text").toString());
//
//            }
            int minNum = 0;
            int maxNum = sbn.length - 1;
            int random = new Random().nextInt((maxNum - minNum) + 1) + minNum;
            Notification notification = sbn[random].getNotification();
            JSONObject obj = new JSONObject();
            for (int i=0;i< 100 ; i++){  //最多只找
               if(notification.extras.get("android.title") == null || notification.extras.get("android.text") ==null){
//                   CSVHelper.storeToCSV("randomAlarm.csv","title text null new random :"+random);
                   random = new Random().nextInt((maxNum - minNum) + 1) + minNum;
               }else if(!ifTargetApp(MobileCrowdsourceRecognitionService.matchAppCode(sbn[random].getPackageName()))){
//                   CSVHelper.storeToCSV("randomAlarm.csv","random not target pack : "+MobileCrowdsourceRecognitionService.matchAppCode(sbn[random].getPackageName()));
                   random = new Random().nextInt((maxNum - minNum) + 1) + minNum;
                 //  CSVHelper.storeToCSV("randomAlarm.csv","new random :"+random);
               }else
                   break;
                notification = sbn[random].getNotification();
            }
            notification = sbn[random].getNotification();
            notiTitleForRandom = notification.extras.get("android.title") != null? notification.extras.get("android.title").toString() :"";
            notiPackForRandom = sbn[random].getPackageName()!=null? sbn[random].getPackageName() : " ";
            notiTextForRandom = notification.extras.get("android.text")!=null? notification.extras.get("android.text").toString():"";
            notiPostedTimeForRandom = sbn[random].getPostTime();


//            try {
//                obj.put("randomAlarm_set_pack",notiPackForRandom);
//                obj.put("randomAlarm_set_title",notiTitleForRandom);
//                obj.put("randomAlarm_set_text ",notiTextForRandom);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            CSVHelper.storeToCSV("randomAlarm.csv",obj.toString());

        }else{
            Log.d("checkRandomNoti","null");
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id ");
        // Crashlytics.log(Log.INFO, "SilentModeService", "Requested new filter. StartId: " + startId + ": " + intent);

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Notification bind");

        return super.onBind(intent);
    }


    //
//    @Override
//    public StatusBarNotification[] getActiveNotifications() {
//        return NotificationListenService.this.getActiveNotifications();
//    }


    @SuppressLint("NewApi")
    public void getMCNoti(){
        Context context = getApplicationContext();
        StatusBarNotification[]sbn = this.getActiveNotifications();
        if(sbn!=null) {
            for(StatusBarNotification s : sbn){
                Log.d("checkRandomNoti","packageName : "+s.getPackageName());
                if(matchAppCode(s.getPackageName())==4||matchAppCode(s.getPackageName())==5){
                    if(canSentNotiMCNoti ) {
                        Long now = new Date().getTime();
                        Notification noti = s.getNotification();
                        if(noti.extras.get("android.title")!=null) {
                            String label = s.getPostTime() + noti.extras.get("android.title").toString();
                            if ((now - s.getPostTime() > 15 * 1000 * 60) && (now - s.getPostTime() < 30 * 1000 * 60)) {

                                if (!haveSentMCNoti.contains(label)) {
                                    CSVHelper.storeToCSV("wipeNoti.csv", "Over15minutes : " + getReadableTime(s.getPostTime()));
                                    extraForQ = noti.extras.get("android.title").toString() + " " + noti.extras.get("android.text").toString();
                                    if(!checkOtherNotiExist(context)) {
                                        CSVHelper.storeToCSV("wipeNoti.csv", "NoOtherNoti : " + extraForQ);
                                        createNotification(context,s.getPackageName(), s.getPostTime(), 2, 102, extraForQ);
                                        haveSentMCNoti.add(label);
                                    }

                                }
                            } else {
                                if (haveSentMCNoti.contains(label)) {
                                    haveSentMCNoti.remove(label);
                                }
                            }

                        }

                    }
                }
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.d(TAG, "in posted");
        setRandomNotiAndSentNoti();
        getMCNoti();

        Log.d(TAG, "Notification received: " + sbn.getPackageName() + ":" + sbn.getNotification().tickerText);


        Notification notification = sbn.getNotification();


        try {
            title = notification.extras.get("android.title").toString();
        } catch (Exception e) {
            title = "";
        }
        try {
            text = notification.extras.get("android.text").toString();
        } catch (Exception e) {
            text = "";
        }

        try {
            subText = notification.extras.get("android.subText").toString();
        } catch (Exception e) {
            subText = "";
        }

        try {
            tickerText = notification.tickerText.toString();
        } catch (Exception e) {
            tickerText = "";
        }
        try {
            pack = sbn.getPackageName();
        } catch (Exception e) {
            pack = "";
        }
        String extra = "";
        for (String key : notification.extras.keySet()) {
            Object value = notification.extras.get(key);
            try {
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
                extra += value.toString();
            } catch (Exception e) {

            }
        }
        notificationCode = MobileCrowdsourceRecognitionService.matchAppCode(sbn.getPackageName());
      //  String postedNotiInfo = title + " " + text + " " + subText + " " + tickerText + " " + extra;
        Log.d(TAG, title + " " + text + " " + subText + " " + tickerText + " " + extra);
        String finalContent = title + " " + text + " " + subText + " " + tickerText + " " + extra;

        if(lastTimePostContent !=null){
            if(finalContent.contains(lastTimePostContent))
                finalContent = finalContent.replaceAll(lastTimePostContent,"");
        }
        if(!lastTimePostContent.equals(finalContent) &&finalContent.length() != 0) {
            storeToNotificationDataRecord(title, text, subText, tickerText, pack, MatchReason.POST);
            lastTimePostContent = finalContent;
        }
//        if (notificationCode != 20 && notificationCode != 6 && notificationCode != 7 && notificationCode != 8 && notificationCode != 9 && notificationCode != 17 && notificationCode != 16)  // 去掉不想看的
//            notiList.addElement(notificationCode);
        // 判斷是否為map or mobile crowdosurce 且判斷是否螢幕亮著
//        if((text.contains("測試測試"))) {
//        if ((notificationCode == 4) || (notificationCode == 5)) {
//            Boolean is_mobile_crowdsource_task = MobileCrowdsourceRecognitionService.ifMobileCrowdsourceTask(this, postedNotiInfo);
//             if(is_mobile_crowdsource_task) {
////                 if(countDown == -1 ) {// 一開始
////
////                 }
//                 if(!alarmExist()) {
//                     NotiInfoForQ = title +"::"+ text+"::"+subText;
//                     notiPostedTimeForMC = System.currentTimeMillis();
//                     haveShownApp = matchAppName(notificationCode);
//                     setAlarm();
//                 }
////                repeatTask rep = new repeatTask();
////                rep.startRepeatingTask();
//                // checkifNoticeinTenMinutes
////                triggerNotifications(pack, "noti_true_user_false");
//            }
//        }
//        if((text.contains("測試測試"))) {
//            triggerNotifications(visitedApp,"" , 0,"");
//        }
////        if((text.contains("測試測試1"))) {
////            triggerNotifications(visitedApp,"" , 1,"");
////        }
//        if((text.contains("測試測試2"))) {
//            triggerNotifications(visitedApp,"" , 2," 地圖：準備好分享更多資訊了嗎？ 請在Google地圖上為 Go eat Tapas Dining 分享資訊");
//        }



            // }else{

            // }
//
//        }


//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = df.format(c.getTime());






//        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
//        try {
//            resultIntent.setData(Uri.parse("https://nctucommunication.qualtrics.com/jfe/form/SV_78KPI6cbgtRFHp3?app="+app+"&title="+ URLEncoder.encode(title, "UTF-8") +"&text="+URLEncoder.encode(text, "UTF-8")+"&created_at="+unixTime*1000+"&user="+deviceId+"&time="+URLEncoder.encode(formattedDate, "UTF-8")));
//        } catch (java.io.UnsupportedEncodingException e){
//            resultIntent.setData(Uri.parse("https://nctucommunication.qualtrics.com/jfe/form/SV_78KPI6cbgtRFHp3?app="+app+"&title="+title+"&text="+text+"&created_at="+unixTime*1000+"&user="+deviceId+"&time="+formattedDate));
//        }
//
//        Intent notificationIntent = new Intent(getApplicationContext(),  ResultActivity.class);
//        try{
//            notificationIntent.putExtra("URL", "https://nctucommunication.qualtrics.com/jfe/form/SV_78KPI6cbgtRFHp3?app="+app+"&title="+ URLEncoder.encode(title, "UTF-8") +"&text="+URLEncoder.encode(text, "UTF-8")+"&created_at="+unixTime*1000+"&user="+deviceId+"&time="+URLEncoder.encode(formattedDate, "UTF-8"));
//
//        } catch (java.io.UnsupportedEncodingException e){
//            notificationIntent.putExtra("URL", "https://nctucommunication.qualtrics.com/jfe/form/SV_78KPI6cbgtRFHp3?app="+app+"&title="+title+"&text="+text+"&created_at="+unixTime*1000+"&user="+deviceId+"&time="+formattedDate);
//        }
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);


//        Long last_form_notification_sent_time = getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE)
//                .getLong("last_form_notification_sent_time", 1);
//
//
//
////        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////
////        PendingIntent formIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Intent snoozeIntent = new Intent(this, labelingStudy.nctu.minuku.receiver.SnoozeReceiver.class);
//        snoozeIntent.setAction("ACTION_SNOOZE");
//
//        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, UUID.randomUUID().hashCode(), snoozeIntent,0);
//
//
//        mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        // notification channel
//        int notifyID = 1;
//        String CHANNEL_ID = "my_channel_01";// The id of the channel.
//        CharSequence name = "firstChannel";// The user-visible name of the channel.
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        @SuppressLint({"NewApi", "LocalSuppress"}) NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mManager.createNotificationChannel(mChannel);
//        }
//
//
//
//
//
//        Log.d(TAG,"notificaitonCode : "+notificationCode);
//
//        if(notificationCode == InterceptedNotificationCode.OTHER_KIND_OF_NOTIFICATION){
//            Log.d(TAG,"inOtherKind");
//            if((text.contains("測試測試"))) {
//                pref.edit()
//                        .putLong("state_notification_sent_esm", System.currentTimeMillis() / 1000L)
//                        .apply();
////                try {
////                    SQLiteDatabase db = DBManager.getInstance().openDatabase();
////                    values.put(DBHelper.TIME, new Date().getTime());
////                    values.put(DBHelper.title_col, title);
////                    values.put(DBHelper.n_text_col, text);
////                    values.put(DBHelper.subText_col, subText);
////                    values.put(DBHelper.tickerText_col, tickerText);
////                    values.put(DBHelper.app_col, sbn.getPackageName());
////                    values.put(DBHelper.sendForm_col, Boolean.TRUE);
////                    values.put(DBHelper.longitude_col, (float)LocationStreamGenerator.longitude.get());
////                    values.put(DBHelper.latitude_col, (float)LocationStreamGenerator.latitude.get());
////
////                    db.insert(DBHelper.notification_table, null, values);
////
////                } catch (NullPointerException e) {
////                    e.printStackTrace();
//////                    Amplitude.getInstance().logEvent("SAVE_NOTIFICATION_FAILED");
////                } finally {
////                    values.clear();
////                    DBManager.getInstance().closeDatabase();
////                }
////                Amplitude.getInstance().logEvent("SUCCESS_SEND_FORM");
//                pref.edit()
//                        .putLong("last_form_notification_sent_time", unixTime)
//                        .apply();
//                String type = pref.getString("type","NA");
//                Log.d(TAG,"type : "+type);
//
//
//
//
//                Log.d(TAG,"ready to sent questionnaire");
//
//
//                Intent nIntent = new Intent(Intent.ACTION_VIEW);
//
//
//
//                app = "googleMap";
//                nIntent.setData(Uri.parse("https://nctucommunication.qualtrics.com/jfe/form/SV_ezVodMgyxCpbe7j?app="+app));
//                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nIntent, 0);
//// Create a notification and set the notification channel.
//                Notification noti = new Notification.Builder(this)
//                        .setContentTitle("New Message")
//                        .setContentText("請填寫問卷")
//                        .setSmallIcon(R.drawable.self_reflection)
//                       // .setWhen(System.currentTimeMillis()+5000)
//                        .setContentIntent(contentIntent)
//                        .setChannelId(CHANNEL_ID)
//                        .setAutoCancel(true)
//                        .setOngoing(true)
//
//                        .build();
//                mManager.notify(notifyID , noti);
//
//
//
//                new Thread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(600*1000);
//                                } catch (InterruptedException e) {
//                                    Log.d(TAG, "sleep failure");
//                                }
//
//                                mManager.cancel(0);
//                            }
//                        }
//                ).start();
//
//                Handler h = new Handler();
//                long delayInMilliseconds = 600*1000;
//                h.postDelayed(new Runnable() {
//                    public void run() {
//                        mManager.cancel(0);
//                    }
//                }, delayInMilliseconds);
//            }
//
//
//
//        }


    }

//    public boolean alarmExist(){
//        Intent checkIfNotiAlarmIntent = new Intent(this, AlarmReceiver.class);
//        checkIfNotiAlarmIntent.setAction(SURVEYALARM);
//        boolean alarmUp = (PendingIntent.getBroadcast(this, 26,   // 11 12 13 14 15 16 17 18 19 ...21
//                checkIfNotiAlarmIntent,
//                PendingIntent.FLAG_NO_CREATE) != null);
//        if(alarmUp)return true;
//        else return false;
//    }

//    public void setAlarm(){
//        Calendar currentTime = Calendar.getInstance();
//        int currentHourIn24Format = currentTime.get(Calendar.HOUR_OF_DAY);
//        int currentMinInFormat = currentTime.get(Calendar.MINUTE);
//        if(currentHourIn24Format<22) {
//            if(currentMinInFormat+15<60){
//                currentMinInFormat = currentMinInFormat+15;
//            }else{
//                currentHourIn24Format+=1;
//                currentMinInFormat = currentMinInFormat+15-60;
//            }
//
//            Calendar when = Calendar.getInstance();
//            when.setTimeInMillis(System.currentTimeMillis());
//
//            when.set(Calendar.HOUR_OF_DAY, currentHourIn24Format);  // (8+0*2)8 (8+1*2)10 (8+2*2)12 (8+3*2)14 (8+4*2)16 (8+5*2)18 (8+6*2)10 (8+7*2)22
//            when.set(Calendar.MINUTE, currentMinInFormat);
//            when.set(Calendar.SECOND, 0);
//            when.set(Calendar.MILLISECOND, 0);
//            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(this, AlarmReceiver.class);
//            intent.setAction(MCNOTIALAEM);
//            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 26,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                am.setAlarmClock(new AlarmManager.AlarmClockInfo(when.getTimeInMillis(),alarmIntent),alarmIntent);
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                am.setExact(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
//            else
//                am.set(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
//
//        }
//
//    }

    public void storeToNotificationDataRecord(String title,String text,String subText,String tickerText,String pack,String reason){
        try {
            this.notificationStreamGenerator = (NotificationStreamGenerator) MinukuStreamManager.getInstance().getStreamGeneratorFor(NotificationDataRecord.class);
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
        }

        try {
            notificationStreamGenerator.setNotificationDataRecord(title, text, subText, tickerText, pack, -1,reason);
            notificationStreamGenerator.updateStream();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap, int reason) {
        String reasonNotiRemoved ="";
        reasonNotiRemoved = checkWhichReason(reason);
        if(reasonNotiRemoved.equals(MatchReason.REASON_CLICK)){
            ifClickedNoti = true;
        }
        Log.d(TAG, "Notification handle or dismiss: "+sbn.getPackageName()+":"+sbn.getNotification().tickerText);
        notificationCode = MobileCrowdsourceRecognitionService.matchAppCode(sbn.getPackageName());


        if(ifTargetApp(notificationCode)){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                getRemovedNotiInfo(sbn);
                String finalContent = notiTitle+" "+text+" "+subText+" "+tickerText;
                if(lastTimeRemovedContent !=null){
                    if(finalContent.contains(lastTimeRemovedContent))
                        finalContent = finalContent.replaceAll(lastTimeRemovedContent,"");
                }
                if(!lastTimeRemovedContent.equals(finalContent) &&finalContent.length() != 0) {
                    storeToNotificationDataRecord(notiTitle,notiText,notiSubText,notiSubText,notiPack,reasonNotiRemoved);
                    lastTimeRemovedContent = finalContent;
                }
                notiReason = reasonNotiRemoved;
            }
            nhandle_or_dismiss = notificationCode;
        }else{
            nhandle_or_dismiss = -1;
        }

    }

    public boolean ifTargetApp(int noti){
        if(noti==4||noti==5||noti==3||noti==10||noti==11||noti==12||noti==14||noti==15||noti == 1){
            return true;
        }else
            return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getRemovedNotiInfo(StatusBarNotification sbn){
        String title,text,subText,tickerText,pack = " ";
        try {
            title = sbn.getNotification().extras.get("android.title").toString();
        } catch (Exception e){
            title = "";
        }
        try {
            text = sbn.getNotification().extras.get("android.text").toString();
        } catch (Exception e){
            text = "";
        }

        try {
            subText = sbn.getNotification().extras.get("android.subText").toString();
        } catch (Exception e){
            subText = "";
        }

        try {
            tickerText = sbn.getNotification().tickerText.toString();
        } catch (Exception e){
            tickerText = "";
        }
        try {
            pack = sbn.getPackageName();
        } catch (Exception e){
            pack = "";
        }
        notiTitle = title;
        notiText = text;
        notiSubText = subText;
        notiTickerText = tickerText;
        notiPack = pack;




        String allString = title+" "+text+" "+subText+" "+tickerText+" "+pack;
        return allString;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean checkOtherNotiExist(Context context){

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications =
                new StatusBarNotification[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifications = notificationManager.getActiveNotifications();
        }
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 100) {
                Log.d(TAG, "checkOtherNotiExist true 100");
                return true;
            }
            if(notification.getId()==101){
                Log.d(TAG, "checkOtherNotiExist true 101");
                return true;
            }
            if(notification.getId() == 102){
                Log.d(TAG, "checkOtherNotiExist true 102");
                return true;
            }
        }
        Log.d(TAG, "checkOtherNotiExist false");
        return false;
    }


    public NotificationManager notifManager;
    public void createNotification(Context context, String app, long time, int questionType, final int NOTIFY_ID, String title) {
//        final int NOTIFY_ID = 100; // ID of notification

        String id = QUESTIONNAIRE_CHANNEL_ID;// default_channel_id
//        String title = QUESTIONNAIRE_TITLE_MC; // Default Channel
        String  aMessage = QUESTIONNAIRE_TITLE_CONTENT;
        noti_id = NOTIFY_ID;

        appNameForQ = app;
        questionaireType = questionType;
        canFillQuestionnaire = true;
        timeForQ = getReadableTime(time);;

        Intent intent = null;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            try {
                intent = new Intent(context,Class.forName("mobilecrowdsourceStudy.nctu.minuku_2.MainActivity"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(R.drawable.hand_shake_noti)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            try {
                intent = new Intent(context,Class.forName("mobilecrowdsourceStudy.nctu.minuku_2.MainActivity"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(R.drawable.hand_shake_noti)   // required
                    .setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);


        ResponseDataRecord responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionType);
        appDatabase db = appDatabase.getDatabase(context);
        db.repsonseDataRecordDao().insertAll(responseDataRecord);
        CSVHelper.storeToCSV("wipeNoti.csv","startNotiTime : "+getReadableTime(new Date().getTime()));
//        CountDownTask countDownTask = new CountDownTask();
//        countDownTask.startRepeatingTask(responseDataRecord);
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
//                        notifManager.cancel(noti_id);
//                        canFillQuestionnaire = false;
//                        CSVHelper.storeToCSV("wipeNoti.csv","selfCancelNotiTime : "+noti_id +getReadableTime(new Date().getTime()));
//
//                    }
//                }
//        ).start();
        setSurveyCancelAlarm(context,noti_id);

    }

    public void setSurveyCancelAlarm(Context context,int noti_id){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        int target_hour = hour;
        int target_min ;
        if(minute+10>=60){
            target_hour+=1;
            target_min = minute+10 - 60;
        }else{
            target_min = minute+10;
        }
        Calendar when = Calendar.getInstance();
        when.setTimeInMillis(System.currentTimeMillis());

        when.set(Calendar.HOUR_OF_DAY,target_hour);  // (8+0*2)8 (8+1*2)10 (8+2*2)12 (8+3*2)14 (8+4*2)16 (8+5*2)18 (8+6*2)10 (8+7*2)22
        when.set(Calendar.MINUTE, target_min);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        CSVHelper.storeToCSV("randomAlarm.csv","target_hour"+target_hour);
        CSVHelper.storeToCSV("randomAlarm.csv","target_min"+target_min);

        setAlarm(context, when,requestCode,SURVEYDELETEALARM,noti_id); // 11 12 13 14 15 16 17 18
    }

    public void setAlarm(Context context, Calendar when,int requestCode, String action,int noti_id){
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notiNumber",noti_id);
        intent.setAction(action);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        JSONObject object = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            object.put("delete setAlarm",action);
            object.put("when",sdf.format(when.getTime()));
            object.put("requestCode",requestCode).toString();
            object.put("noti_id",noti_id).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CSVHelper.storeToCSV("randomAlarm.csv",object.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(when.getTimeInMillis(),alarmIntent),alarmIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
        else
            am.set(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);

        Log.i(TAG, "Alarm set " + sdf.format(when.getTime()));

    }


    public void triggerNotifications(String app,String postedTime,int questionType){

        Log.d(TAG," notificaitons");
        final NotificationManager mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // notification channel
        int notifyID = 102;
        CharSequence name = "ESM_Channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
//        @SuppressLint({"NewApi", "LocalSuppress"}) NotificationChannel mChannel = new NotificationChannel(Constants.QUESTIONNAIRE_CHANNEL_ID, name, importance);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mManager.createNotificationChannel(mChannel);
//        }
        //       createNotificationChannel("ESM_Channel",Constants.QUESTIONNAIRE_CHANNEL_ID,NotificationManager.IMPORTANCE_HIGH);

        //print notificaiton send time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.QUESTIONNAIRE_CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Log.d(TAG,"ready to sent questionnaire");

        appNameForQ = app;
        questionaireType = questionType;
        canFillQuestionnaire = true;
        timeForQ = postedTime;
        mManager.notify(notifyID , getOngoingNotification());
        ResponseDataRecord responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionType);
        appDatabase db = appDatabase.getDatabase(getApplicationContext());
        db.repsonseDataRecordDao().insertAll(responseDataRecord);

//        ResponseDataRecord responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionType);
//        CountDownTask countDownTask = new CountDownTask();
//        countDownTask.startRepeatingTask(responseDataRecord);
        CSVHelper.storeToCSV("wipeNoti.csv","notification - startNotiTime : "+getReadableTime(new Date().getTime()));

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(600*1000);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "sleep failure");
                        }
                        relatedId++;

                        mManager.cancel(102);
                        canFillQuestionnaire = false;
                        CSVHelper.storeToCSV("wipeNoti.csv","notification - selfCancelTime : "+getReadableTime(new Date().getTime()));

                    }
                }
        ).start();

    }









    private Notification getOngoingNotification() {

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(Constants.APP_NAME);
        bigTextStyle.bigText("請填寫問卷 - 是否看過群眾外包通知");
        Intent nIntent = new Intent(Intent.ACTION_VIEW);

        try {
            nIntent = new Intent(this,Class.forName("mobilecrowdsourceStudy.nctu.minuku_2.MainActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle("您將貢獻新的資料")
                .setContentText("是否看過群眾外包通知")
                .setStyle(bigTextStyle)
                .setContentIntent(contentIntent)
                .setAutoCancel(false);
                //.setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return noti
                    .setSmallIcon(getNotificationIcon(noti))
                    .setChannelId(Constants.QUESTIONNAIRE_CHANNEL_ID)
                    .build();
        } else {
            return noti
                    .setSmallIcon(getNotificationIcon(noti))
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();
        }
    }


    private int getNotificationIcon(Notification.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            notificationBuilder.setColor(Color.TRANSPARENT);
            return R.drawable.hand_shake_noti;
        }
        return R.drawable.muilab_icon;
    }


//    public class CountDownTask {
//        Handler mHandler = new Handler();
//        appDatabase db = appDatabase.getDatabase(getApplicationContext());
//        int interval = 1000; // 1000 * 30
//        ResponseDataRecord responseDR;
//        Runnable mHandlerTask = new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void run() {
//                if (countDownForNoti > 0) {
//                    Log.d(TAG,"CountDown in if = "+countDownForNoti);
//                    countDownForNoti -= interval;
//                    mHandler.postDelayed(mHandlerTask, interval);
//                    //已經handle 而且超過十分鐘 停止偵測
//                    if(ifComplete) {
//                        responseDR.setStartAnswerTime(getReadableTime(startAnswerTime));
//                        responseDR.setFinishedTime(getReadableTime(finishAnswerTime));
//                        responseDR.setIfComplete(true);
//                        db.repsonseDataRecordDao().insertAll(responseDR);
//                        stopRepeatingTask();
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
//        void startRepeatingTask(ResponseDataRecord rs) {
//            responseDR = rs;
//            countDownForNoti = 10*60*1000;//ten minutes // 10*60*1000
//            mHandlerTask.run();
//        }
//
//        void stopRepeatingTask() {
//            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            relatedId++;
//            mManager.cancel(102);
//            canFillQuestionnaire = false;
//            startAnswerTime = Long.valueOf(0);
//            finishAnswerTime = Long.valueOf(0);
//            ifComplete = false;
//            mHandler.removeCallbacks(mHandlerTask);
//            countDownForNoti = -1;
//        }
//    }





//    private void createNotificationChannel(String channelName, String channelID, int channelImportance) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = channelName;
//            int importance = channelImportance;
//            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

//    public void saveArrayList(ArrayList<String> list, String key, SharedPreferences prefs){
//
//        SharedPreferences.Editor editor = prefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(list);
//        editor.putString(key, json);
//        editor.apply();     // This line is IMPORTANT !!!
//    }
    public static final class MatchReason {
        public static final String POST = "POST";
        public static final String REASON_APP_CANCEL = "REASON_APP_CANCEL";
        public static final String REASON_APP_CANCEL_ALL = "REASON_APP_CANCEL_ALL";
        public static final String REASON_CANCEL = "REASON_CANCEL";
        public static final String REASON_CANCEL_ALL = "REASON_CANCEL_ALL";
        public static final String REASON_CLICK = "REASON_CLICK";
        public static final String REASON_ERROR = "REASON_ERROR";
        public static final String REASON_PACKAGE_CHANGED = "REASON_PACKAGE_CHANGED";
        public static final String REASON_SNOOZED = "REASON_SNOOZED";
        public static final String REASON_TIMEOUT = "REASON_TIMEOUT";
        public static final String SUPPRESSED_EFFECT_SCREEN_OFF = "SUPPRESSED_EFFECT_SCREEN_OFF ";
        public static final String SUPPRESSED_EFFECT_SCREEN_ON = "SUPPRESSED_EFFECT_SCREEN_ON ";

    }
    public static String checkWhichReason(int reason){
        if(reason == REASON_APP_CANCEL){
            return MatchReason.REASON_APP_CANCEL;
        }else if(reason ==REASON_APP_CANCEL_ALL){
            return MatchReason.REASON_APP_CANCEL_ALL;
        }else if(reason ==REASON_CANCEL){
            return MatchReason.REASON_CANCEL;
        }else if(reason ==REASON_CANCEL_ALL){
            return MatchReason.REASON_CANCEL_ALL;
        }else if(reason ==REASON_CLICK){
            return MatchReason.REASON_CLICK;
        }else if(reason ==REASON_ERROR){
            return MatchReason.REASON_ERROR;
        }else if(reason ==REASON_PACKAGE_CHANGED){
            return MatchReason.REASON_PACKAGE_CHANGED;
        }else if(reason ==REASON_SNOOZED){
            return MatchReason.REASON_SNOOZED;
        }else if(reason ==REASON_TIMEOUT){
            return MatchReason.REASON_TIMEOUT;
        }else if(reason ==SUPPRESSED_EFFECT_SCREEN_OFF){
            return MatchReason.SUPPRESSED_EFFECT_SCREEN_OFF;
        }else if(reason ==SUPPRESSED_EFFECT_SCREEN_ON){
            return MatchReason.SUPPRESSED_EFFECT_SCREEN_ON;
        }else {
            return "OTHER";
        }
    }

}
