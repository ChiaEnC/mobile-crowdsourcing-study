package mobilecrowdsourceStudy.nctu.minuku_2.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.config.SharedVariables;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.manager.MobilityManager;
import labelingStudy.nctu.minuku.manager.SessionManager;
import labelingStudy.nctu.minuku.model.Session;
import labelingStudy.nctu.minuku.receiver.AlarmReceiver;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.AllUtils.Utils;
import mobilecrowdsourceStudy.nctu.minuku_2.Manager.InstanceManager;
import mobilecrowdsourceStudy.nctu.minuku_2.NetworkStateChecker;
import mobilecrowdsourceStudy.nctu.minuku_2.controller.Dispatch;
import mobilecrowdsourceStudy.nctu.minuku_2.receiver.RestarterBroadcastReceiver;

import static labelingStudy.nctu.minuku.config.Constants.DATA_SAVED_BROADCAST;
import static labelingStudy.nctu.minuku.config.SharedVariables.RANDOMSURVEYALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.RESET;
import static labelingStudy.nctu.minuku.config.SharedVariables.SURVEYALARM;
import static labelingStudy.nctu.minuku.config.SharedVariables.hour;
import static labelingStudy.nctu.minuku.config.SharedVariables.min;
import static labelingStudy.nctu.minuku.config.SharedVariables.nextTimeRadomAlarmNumber;
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


/**
 * Created by chiaenchiang on 11/11/2018.
 */

public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";

    final static String CHECK_RUNNABLE_ACTION = "checkRunnable";
    final static String CHECK_RESET_ALARM = "checkResetAlarm";
    final static String CHECK_SURVEY_ALARM = "checkSurveyAlarm";
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //WifiReceiver mWifiReceiver;
    IntentFilter intentFilter;
    NetworkStateChecker mNetworkStateChecker;

    MinukuStreamManager streamManager;

    private ScheduledExecutorService mScheduledExecutorService;
    ScheduledFuture<?> mScheduledFuture, mScheduledFutureIsAlive;

    private int ongoingNotificationID = 42;
    private String ongoingNotificationText = Constants.RUNNING_APP_DECLARATION;

    NotificationManager mNotificationManager;

    public static boolean isBackgroundServiceRunning = false;
    public static boolean isBackgroundRunnableRunning = false;

    private SharedPreferences sharedPrefs;

    public BackgroundService() {
        super();

    }

    @Override
    public void onCreate() {

        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        isBackgroundServiceRunning = false;
        isBackgroundRunnableRunning = false;

        streamManager = MinukuStreamManager.getInstance();
     //   NotificationListenService NLService = new NotificationListenService();

        mScheduledExecutorService = Executors.newScheduledThreadPool(Constants.MAIN_THREAD_SIZE);

        mNetworkStateChecker = new NetworkStateChecker();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        intentFilter.addAction(Constants.ACTION_CONNECTIVITY_CHANGE);
        registerReceiver(mNetworkStateChecker,intentFilter);
        IntentFilter checkRunnableFilter = new IntentFilter(CHECK_RUNNABLE_ACTION);
        // registerReceiver(CheckRunnableReceiver, checkRunnableFilter);

//        IntentFilter alarmFilter = new IntentFilter();
//        alarmFilter.addAction(CHECK_RESET_ALARM);
//        alarmFilter.addAction(CHECK_SURVEY_ALARM);
        //registerReceiver(CheckAlarmReceiver, alarmFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(CheckRunnableReceiver,checkRunnableFilter);
       // LocalBroadcastManager.getInstance(this).registerReceiver(CheckAlarmReceiver,alarmFilter);


        Log.i("AlarmHelper", "Alarm set " + "in background ");
        //  mWifiReceiver = new WifiReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundServiceRunning ? "+isBackgroundServiceRunning);
        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundRunnableRunning ? "+isBackgroundRunnableRunning);

        String onStart = "BackGround, start service";
//        CSVHelper.storeToCSV(CSVHelper.CSV_ESM, onStart);
//        CSVHelper.storeToCSV(CSVHelper.CSV_CAR, onStart);


        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Intent nintent = new Intent(getApplicationContext(), NotificationListenService.class);
//        getApplicationContext().startService(nintent);

        createNotificationChannel(Constants.ONGOING_CHANNEL_NAME, Constants.ONGOING_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        createNotificationChannel(Constants.SURVEY_CHANNEL_NAME, Constants.SURVEY_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);

        //make the WifiReceiver start sending availSite to the server.
        //   registerReceiver(mWifiReceiver, intentFilter);
        registerConnectivityNetworkMonitorForAPI21AndUp();



        Log.d("AlarmHelper","background service register");

        //building the ongoing notification to the foreground
        startForeground(ongoingNotificationID, getOngoingNotification(ongoingNotificationText));


        if (!isBackgroundServiceRunning) {

            Log.d(TAG, "Initialize the Manager");

            isBackgroundServiceRunning = true;

            CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Going to judge the condition is ? "+(!InstanceManager.isInitialized()));

            if (!InstanceManager.isInitialized()) {

                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Going to start the runnable.");

                InstanceManager.getInstance(this);
                SessionManager.getInstance(this);
                MobilityManager.getInstance(this);
            }
            // original
            updateNotificationAndStreamManagerThread();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(CHECK_RUNNABLE_ACTION));

//            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(CHECK_RESET_ALARM));
//            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(CHECK_SURVEY_ALARM));
        }

        if(!isBackgroundServiceRunning) {
            updateNotificationAndStreamManagerThread();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(CHECK_RUNNABLE_ACTION));
        }
        // read test file
//        FileHelper fileHelper = FileHelper.getInstance(getApplicationContext());
//        FileHelper.readTestFile();

        return START_REDELIVER_INTENT;

    }

    private void updateNotificationAndStreamManagerThread() {

        mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(
                updateStreamManagerRunnable,
                Constants.STREAM_UPDATE_DELAY,
                Constants.STREAM_UPDATE_FREQUENCY,
                TimeUnit.SECONDS);

        mScheduledFutureIsAlive = mScheduledExecutorService.scheduleAtFixedRate(
                isAliveRunnable,
                Constants.ISALIVE_UPDATE_DELAY,
                Constants.ISALIVE_UPDATE_FREQUENCY,
                TimeUnit.SECONDS
        );

    }

    Runnable isAliveRunnable = new Runnable() {
        @Override
        public void run() {

            Log.d(TAG, "sendingIsAliveData");

            CSVHelper.storeToCSV(CSVHelper.CSV_CHECK_ISALIVE, "sendingIsAliveData");

            Constants.DEVICE_ID = sharedPrefs.getString("DEVICE_ID",  Constants.DEVICE_ID);

            if(!Constants.DEVICE_ID.equals(Constants.INVALID_STRING_VALUE)) {

                // sendingIsAliveData();
            }
            checkIfResetAlarmAlive(getApplicationContext());
            Log.d("AlarmHelper", "isAliveRunnable check if reset ");
            checkIfSurveyAlarmAlive(getApplicationContext());
            Log.d("AlarmHelper", "isAliveRunnable check if survey ");
            checkIfRandomAlarmAlive(getApplicationContext());
            Log.d("AlarmHelper", "isAliveRunnable check if random ");
        }
    };

    Runnable updateStreamManagerRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundServiceRunning ? "+isBackgroundServiceRunning);
                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundRunnableRunning ? "+isBackgroundRunnableRunning);

                streamManager.updateStreamGenerators();
            }catch (Exception e){

                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Background, service update, stream, Exception");
                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, Utils.getStackTrace(e));
            }
        }
    };

    // for mobile crowdsourcing




    private Notification getOngoingNotification(String text) {

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(Constants.APP_NAME);
        bigTextStyle.bigText(text);

        Intent resultIntent = new Intent(this, Dispatch.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle(Constants.APP_FULL_NAME)
                .setContentText(text)
                .setStyle(bigTextStyle)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return noti
                    .setSmallIcon(getNotificationIcon(noti))
                    .setChannelId(Constants.ONGOING_CHANNEL_ID)
                    .build();
        } else {
            return noti
                    .setSmallIcon(getNotificationIcon(noti))
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        stopTheSessionByServiceClose();

        isBackgroundServiceRunning = false;
        isBackgroundRunnableRunning = false;

        String onDestroy = "BackGround, onDestroy";
//        CSVHelper.storeToCSV(CSVHelper.CSV_ESM, onDestroy);
//        CSVHelper.storeToCSV(CSVHelper.CSV_CAR, onDestroy);

        mNotificationManager.cancel(ongoingNotificationID);


        //checkingRemovedFromForeground();
        removeRunnable();

        sendBroadcastToStartService();
        unregisterReceiver(mNetworkStateChecker);
      //  LocalBroadcastManager.getInstance(this).unregisterReceiver(CheckAlarmReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(CheckRunnableReceiver);
        //  unregisterReceiver(mWifiReceiver);
    }

    @Override
    public void onTaskRemoved(Intent intent){
        super.onTaskRemoved(intent);

        mNotificationManager.cancel(ongoingNotificationID);

        isBackgroundServiceRunning = false;
        isBackgroundRunnableRunning = false;

        String onTaskRemoved = "BackGround, onTaskRemoved";
        CSVHelper.storeToCSV(CSVHelper.CSV_CheckService_alive, onTaskRemoved);


//        checkingRemovedFromForeground();
        removeRunnable();

        sendBroadcastToStartService();
    }

    private void registerConnectivityNetworkMonitorForAPI21AndUp() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the result page again
                //loadPages();
            }
        };
        Log.d(TAG,"register networkstate checker");
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities){
                        sendBroadcast(
                                getConnectivityIntent("onCapabilitiesChanged : "+networkCapabilities.toString())
                        );
                    }
                }
        );

    }

//    private void checkingRemovedFromForeground(){
//
//        Log.d(TAG,"stopForeground");
//
//        stopForeground(true);
//
//        try {
//
//            unregisterReceiver(CheckRunnableReceiver);
//        }catch (IllegalArgumentException e){
//
//        }
//
//        mScheduledExecutorService.shutdown();
//    }

    private void stopTheSessionByServiceClose(){

        int ongoingSessionid = sharedPrefs.getInt("ongoingSessionid", Constants.INVALID_INT_VALUE);

        //if the background service is killed, set the end time of the ongoing trip (if any) using the current timestamp
//        if (SessionManager.getOngoingSessionIdList().size()>0){

        if(ongoingSessionid != Constants.INVALID_INT_VALUE){

            Session session = SessionManager.getSession(ongoingSessionid) ;

            //if we end the current session, we should update its time and set a long enough flag
            if (session.getEndTime()==0){
                long endTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
                session.setEndTime(endTime);
            }

            //end the current session
            SessionManager.endCurSession(session);

            //keep it when the service is gone to recover to the Arraylist
            sharedPrefs.edit().putInt("ongoingSessionid", Constants.INVALID_INT_VALUE).apply();
        }
    }

    private void removeRunnable(){

        mScheduledFuture.cancel(true);
        mScheduledFutureIsAlive.cancel(true);
    }

    private void sendBroadcastToStartService(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            sendBroadcast(new Intent(this, RestarterBroadcastReceiver.class).setAction(Constants.CHECK_SERVICE_ACTION));
        } else {

            Intent checkServiceIntent = new Intent(Constants.CHECK_SERVICE_ACTION);
            sendBroadcast(checkServiceIntent);
        }
    }

    private Intent getConnectivityIntent(String message) {

        Intent intent = new Intent();

        intent.setAction(Constants.ACTION_CONNECTIVITY_CHANGE);

        intent.putExtra("message", message);

        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel(String channelName, String channelID, int channelImportance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            int importance = channelImportance;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    private void sendingIsAliveData(){
//
//        final String postIsAliveUrl_insert = "http://18.219.118.106:5000/find_latest_and_insert?collection=isAlive&action=insert&id=";//&action=insert, search
//
//        String currentCondition = getResources().getString(labelingStudy.nctu.minuku.R.string.current_task);
//
//        //making isAlive
//        JSONObject dataInJson = new JSONObject();
//        try {
//            long currentTime = new Date().getTime();
//            String currentTimeString = ScheduleAndSampleManager.getReadableTimeLong(currentTime);
//
//            dataInJson.put("time", currentTime);
//            dataInJson.put("timeString", currentTimeString);
//            dataInJson.put("device_id", Constants.DEVICE_ID);
//            dataInJson.put("condition", currentCondition);
//
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "isAlive availSite uploading : " + dataInJson.toString());
//
//        String curr = Utils.getDateCurrentTimeZone(new Date().getTime());
//
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                new PostManager().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                        postIsAliveUrl_insert + Constants.DEVICE_ID,
//                        dataInJson.toString(),
//                        "isAlive",
//                        curr).get();
//            else
//                new PostManager().execute(
//                        postIsAliveUrl_insert + Constants.DEVICE_ID,
//                        dataInJson.toString(),
//                        "isAlive",
//                        curr).get();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//    }

    BroadcastReceiver CheckRunnableReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(CHECK_RUNNABLE_ACTION)) {

                Log.d(TAG, "[check runnable] going to check if the runnable is running");

                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "going to check if the runnable is running");
                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "is the runnable running ? " + isBackgroundRunnableRunning);

                if (!isBackgroundRunnableRunning) {

                    Log.d(TAG, "[check runnable] the runnable is not running, going to restart it.");

                    CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "the runnable is not running, going to restart it");

                    updateNotificationAndStreamManagerThread();

                    Log.d(TAG, "[check runnable] the runnable is restarted.");

                    CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "the runnable is restarted");
                }

                PendingIntent pi = PendingIntent.getBroadcast(BackgroundService.this, 0, new Intent(CHECK_RUNNABLE_ACTION), 0);

                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    alarm.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + Constants.PROMPT_SERVICE_REPEAT_MILLISECONDS,
                            pi);
                }else{

                    alarm.set(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + Constants.PROMPT_SERVICE_REPEAT_MILLISECONDS,
                            pi
                    );
                }


            }
        }
    };
    void checkIfResetAlarmAlive(Context context){
        Intent checkIfResetAlarmIntent = new Intent(context, AlarmReceiver.class);
        checkIfResetAlarmIntent.setAction(RESET);

        Log.d("AlarmHelper", "Reset checkIfResetAlarmAlive");
        boolean resetAlarmUp = (PendingIntent.getBroadcast(context, 10,checkIfResetAlarmIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        JSONObject object = new JSONObject();

        try {
            object.put("check_alarm_receiver_reset ", SharedVariables.getReadableTime(new Date().getTime()));
            object.put("reset_is_up ", resetAlarmUp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //CSVHelper.storeToCSV("CheckAlarm.csv",object.toString());

        if(resetFire){
           // CSVHelper.storeToCSV("CheckAlarm.csv","reset ready to set next time");
            setResetAlarm(context);
        }else {
            if (resetAlarmUp) {
                Log.d("AlarmHelper", "Alarm reset is already active");
              //  CSVHelper.storeToCSV("CheckAlarm.csv","Alarm reset is already active");
            } else {
                setResetAlarm(context);
               // Log.d("AlarmHelper", "Reset set ");
            }
        }

    }
    void checkIfRandomAlarmAlive(Context context){
        Intent checkIfRandomAlarmIntent = new Intent(context, AlarmReceiver.class);
        checkIfRandomAlarmIntent.setAction(RANDOMSURVEYALARM);

        Log.d("AlarmHelper", "random checkIfResetAlarmAlive");
        boolean randomAlarmUp = (PendingIntent.getBroadcast(context, 25,checkIfRandomAlarmIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

//        JSONObject object = new JSONObject();
//
//        try {
//            object.put("check_alarm_receiver_random ", SharedVariables.getReadableTime(new Date().getTime()));
//            object.put("random_is_up ", randomAlarmUp);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
       // CSVHelper.storeToCSV("CheckAlarm.csv",object.toString());

//        if(resetFire){
//            CSVHelper.storeToCSV("CheckAlarm.csv","reset ready to set next time");
//            setResetAlarm(context);
//        }else {
        if (randomAlarmUp) {
            Log.d("AlarmHelper", "Alarm random is already active");
           // CSVHelper.storeToCSV("CheckAlarm.csv","Alarm random is already active");
        } else {
            setRandomAlarm(context);
           // CSVHelper.storeToCSV("CheckAlarm.csv","nextTimeAlarmNumber : "+ nextTimeRadomAlarmNumber);
            Log.d("AlarmHelper", "random set ");
        }
       // }

    }



    void checkIfSurveyAlarmAlive(Context context) {
        for (int i = 0; i < 10; i++) {
            Log.d("AlarmHelper", "CHECK_SURVEY_ALARM register alarm i: " + i);
            Intent checkIfSurveyAlarmIntent = new Intent(context, AlarmReceiver.class);

//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            checkIfSurveyAlarmIntent.setAction(SURVEYALARM);
            boolean alarmUp = (PendingIntent.getBroadcast(context, 11 + i,   // 11 12 13 14 15 16 17 18 19 ...21
                    checkIfSurveyAlarmIntent,
                    PendingIntent.FLAG_NO_CREATE) != null);

            // 紀錄何時check survey
//            JSONObject object = new JSONObject();
//
//            try {
//                object.put("check_alarm_receiver_survey ", SharedVariables.getReadableTime(new Date().getTime()));
//                object.put("survey_number ", 11 + i);
//                object.put("survey_is_up ", alarmUp);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
           // CSVHelper.storeToCSV("CheckAlarm.csv", object.toString());


            Boolean surveyFire = targetFire(i);
            if (surveyFire) {
               // CSVHelper.storeToCSV("CheckAlarm.csv", "survey " + i + "ready to set next time");
                setSurveyAlarm(context, i + 11);
            } else {
                if (alarmUp) {
                    Log.d("AlarmHelper", "Alarm survey " + i + "is already active");
                   // CSVHelper.storeToCSV("CheckAlarm.csv", "Alarm survey " + (i + 11) + "is already active");
                } else {
                    setSurveyAlarm(context, i + 11);  // 0+11 1+11 2+11 3+11 4+11 5+11 6+11 7+11
                    Log.d("AlarmHelper", "Survey set " + (i + 11));
                    // startservice
                }
            }

//            else{
//                if (alarmUp) {
//                    Log.d("AlarmHelper", "Alarm survey " + i + "is already active");
//                    CSVHelper.storeToCSV("CheckAlarm.csv", "Alarm random survey is already active");
//                } else {
//                    setRandomAlarm(context, nextTimeAlarmRanMinStart,nextTimeAlarmRanMinStart);  // 0+11 1+11 2+11 3+11 4+11 5+11 6+11 7+11
//                    Log.d("AlarmHelper", "Survey set " + (i + 11));
//                    // startservice
//                }
//            }
        }
//        Intent checkIfSurveyRandomAlarmIntent = new Intent(context, AlarmReceiver.class);
//
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        checkIfSurveyRandomAlarmIntent.setAction(SURVEYALARM);
//        boolean alarmRandomUp = (PendingIntent.getBroadcast(context, 25,   // 11 12 13 14 15 16 17 18
//                checkIfSurveyRandomAlarmIntent,
//                PendingIntent.FLAG_NO_CREATE) != null);
//        if(!alarmRandomUp){
//            CSVHelper.storeToCSV("CheckAlarm.csv","Random survey ready to set next");
//            createRandomAlarm(context, nextTimeAlarmRanHourStart,nextTimeAlarmRanMinStart);
//        }else{
//            CSVHelper.storeToCSV("CheckAlarm.csv","Random survey is already active");
//        }


    }
    public void setRandomAlarm(Context context){
        int target_minute = 0;
        int target_hour = 0;
        Calendar currentTime = Calendar.getInstance();
        int currentHourIn24Format = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinInFormat = currentTime.get(Calendar.MINUTE);
        int minNum = 30;
        int maxNum = 60;
        int hour_start,min_start;
        int random = new Random().nextInt((maxNum - minNum) + 1) + minNum;
        // first time
        if(nextTimeRadomAlarmNumber == -1){
            hour_start = currentHourIn24Format;
            min_start = currentMinInFormat;
        }else{
            hour_start = hour[nextTimeRadomAlarmNumber];
            min_start = min[nextTimeRadomAlarmNumber];
        }

        if(hour_start+random >= 60){
            target_minute = hour_start+random - 60;
            target_hour = hour_start+1;
            target_hour = target_hour>=24? target_hour-24 : target_hour;
        }else{
            target_minute = min_start+random;
            target_hour = hour_start;
        }


        Calendar when = Calendar.getInstance();

        when.setTimeInMillis(System.currentTimeMillis());

        when.set(Calendar.HOUR_OF_DAY,target_hour);  // (8+0*2)8 (8+1*2)10 (8+2*2)12 (8+3*2)14 (8+4*2)16 (8+5*2)18 (8+6*2)10 (8+7*2)22
        when.set(Calendar.MINUTE, target_minute);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        CSVHelper.storeToCSV("randomAlarm.csv","not up Background new set : random "+random);
        CSVHelper.storeToCSV("randomAlarm.csv","not up Background new set : current hour "+currentHourIn24Format);
        CSVHelper.storeToCSV("randomAlarm.csv","not up Background new set : current min "+currentMinInFormat);
        CSVHelper.storeToCSV("randomAlarm.csv","not up Background new set : hour "+target_hour);
        CSVHelper.storeToCSV("randomAlarm.csv","not up Background new set : min "+target_minute);

        setAlarm(context, when, 25, RANDOMSURVEYALARM);
        nextTimeRadomAlarmNumber += 1;
        nextTimeRadomAlarmNumber = nextTimeRadomAlarmNumber>=10? 0 : nextTimeRadomAlarmNumber;

    }


    public Boolean targetFire(Integer i ){
        if(i == 0){
            return survey1Fire;
        }else if(i == 1){
            return survey2Fire;
        }else if(i == 2){
            return survey3Fire;
        }else if(i == 3){
            return survey4Fire;
        }else if(i == 4){
            return survey5Fire;
        }else if(i == 5){
            return survey6Fire;
        }else if(i == 6){
            return survey7Fire;
        }else if(i == 7){
            return survey8Fire;
        }else if(i == 8){
            return survey9Fire;
        }else if(i == 9){
            return survey10Fire;
        }
//        else if(i==10){
//            return survey11Fire;
//        }
        else return false;

    }
    public void setTargetFire(Integer i ){
        if(i == 0){
            survey1Fire = false;
        }else if(i == 1){
            survey2Fire = false;
        }else if(i == 2){
            survey3Fire = false;
        }else if(i == 3){
            survey4Fire = false;
        }else if(i == 4){
            survey5Fire = false;
        }else if(i == 5){
            survey6Fire = false;
        }else if(i == 6){
            survey7Fire = false;
        }else if(i == 7){
            survey8Fire = false;
        }else if(i == 8){
            survey9Fire = false;
        } else if(i == 9){
            survey10Fire = false;
        }
    }
    public  void setResetAlarm(Context context){
        Calendar currentTime = Calendar.getInstance();
        Calendar when = Calendar.getInstance();
        when.setTimeInMillis(System.currentTimeMillis());
        when.set(Calendar.HOUR_OF_DAY, 0);
        when.set(Calendar.MINUTE, 0);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (when.compareTo(currentTime) <= 0) {
            when.add(Calendar.DATE, 1);
            resetFire = false;
           // CSVHelper.storeToCSV("CheckAlarm.csv","*** reset set next time "+sdf.format(when.getTime()));
        }

        setAlarm(context,when,10,RESET);

        //request code default 0
    }
    public  void setSurveyAlarm(Context context,int alarmNumber){   // 11 12 13 14 15 16 17 18
        Calendar currentTime = Calendar.getInstance();
        Calendar when = Calendar.getInstance();
        when.setTimeInMillis(System.currentTimeMillis());

        when.set(Calendar.HOUR_OF_DAY,hour[alarmNumber-11]);  // (8+0*2)8 (8+1*2)10 (8+2*2)12 (8+3*2)14 (8+4*2)16 (8+5*2)18 (8+6*2)10 (8+7*2)22
        when.set(Calendar.MINUTE, min[alarmNumber-11]);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (when.compareTo(currentTime) <= 0) {
            when.add(Calendar.DATE, 1);
            setTargetFire(alarmNumber-11);
//            CSVHelper.storeToCSV("CheckAlarm.csv","*** survey set "+alarmNumber);
//            CSVHelper.storeToCSV("CheckAlarm.csv","*** survey set next time "+sdf.format(when.getTime()));
        }

        setAlarm(context, when, alarmNumber, SURVEYALARM); // 11 12 13 14 15 16 17 18


    }

    @SuppressLint("SimpleDateFormat")
    private void setAlarm(Context context, Calendar when ,int requestCode,String action) {

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(action);
        intent.putExtra("alarmNumber", requestCode-11);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG,"when : "+when.toString());
        Log.d(TAG,"action : "+action);
        Log.d(TAG,"requestCode  : "+requestCode);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        JSONObject object = new JSONObject();
//        try {
//            object.put("setAlarm",action);
//            object.put("when",sdf.format(when.getTime()));
//            object.put("requestCode",requestCode).toString();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

       // CSVHelper.storeToCSV("CheckAlarm.csv",object.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(when.getTimeInMillis(),alarmIntent),alarmIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);
        else
            am.set(AlarmManager.RTC, when.getTimeInMillis(), alarmIntent);


        Log.i(TAG, "Alarm set " + sdf.format(when.getTime()));

    }







}


