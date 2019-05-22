package mobilecrowdsourceStudy.nctu.minuku_2;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.model.DataRecord.FinalAnswerDataRecord;
import labelingStudy.nctu.minuku.service.BackgroundScreenRecorderService;
import labelingStudy.nctu.minuku.service.NotificationListenService;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.controller.VideoResult;
import mobilecrowdsourceStudy.nctu.minuku_2.controller.stopRecording;
import mobilecrowdsourceStudy.nctu.minuku_2.controller.study;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.AnswersActivity;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.QuestionActivity;

import static labelingStudy.nctu.minuku.config.SharedVariables.allMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.allMCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.allNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.allNCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.canFillQuestionnaire;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNoti;
import static labelingStudy.nctu.minuku.config.SharedVariables.canSentNotiMC;
import static labelingStudy.nctu.minuku.config.SharedVariables.extraForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;
import static labelingStudy.nctu.minuku.config.SharedVariables.pageRecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.questionaireType;
import static labelingStudy.nctu.minuku.config.SharedVariables.relatedId;
import static labelingStudy.nctu.minuku.config.SharedVariables.startAppHour;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCountString;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCountString;

public class MainActivity extends AppCompatActivity {
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final String TAG = "MainActivity";
    //private TextView compensationMessage;
    private AtomicInteger loadingProcessCount = new AtomicInteger(0);
    private ProgressDialog loadingProgressDialog;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    String SrcPath="";
    //WifiReceiver mWifiReceiver;
    IntentFilter intentFilter;

    private int dayCount = 0;
    private int mYear, mMonth, mDay;

    public static String task = "PART"; //default is PART
    ArrayList viewList;
    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public final int DRAW_OVER_OTHER_APP_PERMISSION = 3;
    public static View timerview, recordview, deviceIdview;

    public static android.support.design.widget.TabLayout mTabs;
    public static ViewPager mViewPager;
    private static final int QUESTIONNAIRE_REQUEST = 2018;
    private CardView resultButton;
    private CardView questionnaireButton;
    private CardView infoButton;
    private CardView record;
    // for recording
    private static final int RECORD_REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private BackgroundScreenRecorderService mRecorder;
    private NetworkStateChecker mNetworkStateChecker;
    private String device_id;
    private TextView user_id;
    private SharedPreferences sharedPrefs;
    private boolean firstTimeOrNot = true;
    private Integer checkIfStoreRelatedId = -1;
    // for response data record
    private Long startAnswerTime;
    private Long finishAnswerTime;
//    private TextView num_6_digit;

//    private TextView sleepingtime;
//
//    private ImageView tripStatus;
//    private ImageView surveyStatus;
//
//    private Button ohio_setting, ohio_annotate, startservice, tolinkList, go;
//    private String projName = "mobilecrowdsourcing";
//
//    private int requestCode_setting = 1;
//    private Bundle requestCode_annotate;
//
//    private boolean firstTimeToShowDialogOrNot;
//    private ScheduledExecutorService mScheduledExecutorService;
//    public static final int REFRESH_FREQUENCY = 15; //10s, 10000ms
//    public static final int BACKGROUND_RECORDING_INITIAL_DELAY = 0;

    private AlertDialog enableNotificationListenerAlertDialog;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
//    appDatabase db;
//
//    private TextView t;


    //private UserSubmissionStats mUserSubmissionStats;

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = getIntent();
        if(extraForQ!=null){
            Log.d("newAccess","extra : "+extraForQ);
        }else{
            Log.d("newAccess","extra null");
        }


        // 因為會有delay 所以先給

        if (canFillQuestionnaire) {
//            if(checkIfStoreRelatedId == -1){
//                checkIfStoreRelatedId = relatedId;
//                appDatabase db = appDatabase.getDatabase(getApplicationContext());
//                db.repsonseDataRecordDao().insertAll(responseDataRecord);
//                responseDataRecord= new ResponseDataRecord(getReadableTime(qGenerateTime),relatedId,questionaireType);
//
//            }else{
//                if(checkIfStoreRelatedId != relatedId){
//                    responseDataRecord = new ResponseDataRecord(getReadableTime(new Date().getTime()),relatedId,questionaireType);
//                    appDatabase db = appDatabase.getDatabase(getApplicationContext());
//                    db.repsonseDataRecordDao().insertAll(responseDataRecord);
//                    checkIfStoreRelatedId = relatedId;
//                }
//            }

            questionnaireButton.setCardBackgroundColor(Color.WHITE);
            TextView text = (TextView)findViewById(R.id.control_questionnaire);
            text.setText(R.string.have_questionnaire);
            ImageView img = (ImageView)findViewById(R.id.control_questionnaire_image);
            img.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
            questionnaireButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultButton.setClickable(false);

                    Intent questions = new Intent(MainActivity.this, QuestionActivity.class);
                    //you have to pass as an extra the json string.

                    if (questionaireType == 0) {
                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example.json"));
                    }
                    else if(questionaireType == 1){
                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example3.json"));

                    }
                    else if(questionaireType == 2){
                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example3.json"));
                    }
                    questions.putExtra("extraInfo",extraForQ);
                    questions.putExtra("relatedIdForQ",relatedId);
                    startAnswerTime  = new Date().getTime();
                    pageRecord.clear();
                    MainActivity.this.startActivityForResult(questions, QUESTIONNAIRE_REQUEST);
                }
            });
        } else {
            questionnaireButton.setCardBackgroundColor(Color.LTGRAY);
            TextView text = (TextView)findViewById(R.id.control_questionnaire);
            text.setText(R.string.no_questionnaire);
            questionnaireButton.setClickable(false);
            resultButton.setClickable(true);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE);
        pref.edit()
                .putLong("state_main", System.currentTimeMillis() / 1000L)
                .apply();
//        Amplitude.getInstance().initialize(this, "357d2125a984bc280669e6229646816c").enableForegroundTracking(getApplication());

        Log.d(TAG, "Creating Main activity");
        device_id = getDeviceid();

        // TODO: Use your own attributes to track content views in your app
        //  Answers.getInstance().logContentView(new ContentViewEvent().putContentName("create mainactivity").putCustomAttribute("device_id", device_id));


        mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);


        int sdk_int = Build.VERSION.SDK_INT;
        if (sdk_int >= 23) {
            checkAndRequestPermissions();
        } else {
            startServiceWork();
        }

//        startService(new Intent(getBaseContext(), ExpSampleMethodService.class));
//        startService(new Intent(getBaseContext(), CheckpointAndReminderService.class));


           logUser();



        setContentView(R.layout.activity_home);
        Button but = (Button) findViewById(R.id.device);
        but.setText(Constants.copy_right+" "+Constants.DEVICE_ID);
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);


        questionnaireButton = (CardView) findViewById(R.id.begin_to_response);
        resultButton = (CardView) findViewById(R.id.result_of_response);
        infoButton = (CardView) findViewById(R.id.info_about_study);
        record = (CardView) findViewById(R.id.recording);

//        Intent intent = getIntent();
//        Boolean canFill  = intent.getBooleanExtra("canFill",false);
//        // 因為會有delay 所以先給
//        if(canFill){
//            canFillQuestionnaire = true;
//        }

        // 測試
        //怕id值一直被改 只看當下



//        if (canFillQuestionnaire) {
//            questionnaireButton.setCardBackgroundColor(Color.WHITE);
//            TextView text = (TextView)findViewById(R.id.control_questionnaire);
//            text.setText(R.string.have_questionnaire);
//            ImageView img = (ImageView)findViewById(R.id.control_questionnaire_image);
//            img.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
//            questionnaireButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    resultButton.setClickable(false);
//
//                    Intent questions = new Intent(MainActivity.this, QuestionActivity.class);
//                    //you have to pass as an extra the json string.
//
//                    if (questionaireType == 0) {
//                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example.json"));
//
//                    } else if(questionaireType == 1){
//                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example2.json"));
//
//                    } else if(questionaireType == 2){
//                        questions.putExtra("json_questions", MainActivity.this.loadQuestionnaireJson("questions_example3.json"));
//                    }
//
//                    sharedPrefs.edit().putString("startAnswerTime", getReadableTime(new Date().getTime())).apply();
//                    pageRecord.clear();
//                    MainActivity.this.startActivityForResult(questions, QUESTIONNAIRE_REQUEST);
//                }
//            });
//        } else {
//            questionnaireButton.setCardBackgroundColor(Color.LTGRAY);
//             TextView text = (TextView)findViewById(R.id.control_questionnaire);
//             text.setText(R.string.no_questionnaire);
//            questionnaireButton.setClickable(false);
//            resultButton.setClickable(true);
//        }

        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questions = new Intent(MainActivity.this, AnswersActivity.class);
                MainActivity.this.startActivity(questions);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Info = new Intent(MainActivity.this, study.class);
                MainActivity.this.startActivity(Info);

            }
        });
//        record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
//
//                Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
//                startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
//
//            }
//        });
    record.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent videoresult = new Intent(MainActivity.this, VideoResult.class);
            MainActivity.this.startActivity(videoresult);

        }
    });

    }



    // for recording



    private boolean isNotificationServiceEnabled() {
        Log.d(TAG, "isNotificationServiceEnabled");
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_get_permission) {
            Context context = getApplicationContext();
            CharSequence text = "如果是小米、Asus、Oppo 請開啟自啟動管理";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            checkAndRequestPermissions();
            startpermission();
            return true;
        } else if (id == R.id.action_stop_recording) {
            Intent stop = new Intent(MainActivity.this, stopRecording.class);
            MainActivity.this.startActivity(stop);

            // mWebView.reload();
            return true;
        } else if (id == R.id.skip_questionnaire) {
            questionnaireButton.setCardBackgroundColor(Color.LTGRAY);
            TextView text = (TextView)findViewById(R.id.control_questionnaire);
            text.setText(R.string.no_questionnaire);
            questionnaireButton.setClickable(false);
            resultButton.setClickable(true);
            canFillQuestionnaire =  false;
            cancelNotification();

//            SharedPreferences pref = getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE);
//            String deviceId = pref.getString("device_id", Constants.DEVICE_ID);
//            Context context = getApplicationContext();
//            CharSequence text = "您的device_id : " + deviceId;
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleNotificationListenerService() {
        Log.d(TAG, "toggleNotificationListenerService");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationListenService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationListenService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("通知設定");
        alertDialogBuilder.setMessage("請開啟權限");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return (alertDialogBuilder.create());
    }


//    public void createShortCut() {
//        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        shortcutintent.putExtra("duplicate", false);
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
//        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.self_reflection); //TODO change the icon with the Ohio one.
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), MainActivity.class));
//        sendBroadcast(shortcutintent);
//    }

    public void getStartDate() {
        //get timzone
//        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH) + 1;
        int Day = cal.get(Calendar.DAY_OF_MONTH);

        int Hour = cal.get(Calendar.HOUR_OF_DAY);
        int Min = cal.get(Calendar.MINUTE);
        Log.d(TAG, "Year : " + Year + " Month : " + Month + " Day : " + Day + " Hour : " + Hour + " Min : " + Min);

        Constants.TaskDayCount = 0; //increase in checkfamiliarornotservice

//        Day++; //TODO start the task tomorrow.

        sharedPrefs.edit().putInt("StartYear", Year).apply();
        sharedPrefs.edit().putInt("StartMonth", Month).apply();
        sharedPrefs.edit().putInt("StartDay", Day).apply();

        sharedPrefs.edit().putInt("StartHour", Hour).apply();
        sharedPrefs.edit().putInt("StartMin", Min).apply();

        sharedPrefs.edit().putInt("TaskDayCount", Constants.TaskDayCount).apply();

        Log.d(TAG, "Start Year : " + Year + " Month : " + Month + " Day : " + Day + " TaskDayCount : " + Constants.TaskDayCount);

    }

    public void startpermission() {
        Log.d(TAG,"startpermission");
        //Maybe useless in this project.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//
//            //If the draw over permission is not available open the settings screen
//            //to grant the permission.
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
//        }

        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));  // 協助工具


        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)); //usage
        Log.d(TAG,"show usage");

//                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS); //notification
//                    startActivity(intent);

        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //location



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QUESTIONNAIRE_REQUEST) {

            if (resultCode == RESULT_OK) {
//                appDatabase db = appDatabase.getDatabase(getApplicationContext());
                Boolean isMobileCrowdsource;

//                String ans = db.questionWithAnswersDao().isChecked("1", "0");
                if(questionaireType ==0 || questionaireType ==2){
                    isMobileCrowdsource = true;
                    canSentNotiMC =  false;
                }else{
                    isMobileCrowdsource = false;
                    canSentNoti = false;
                }
//                if (ans != null) {
//                    if (ans.contains("1")) isMobileCrowdsource = true;
//                    else isMobileCrowdsource = false;
//                }
                finishAnswerTime = new Date().getTime();
                canFillQuestionnaire = false;


                questionnaireButton.setCardBackgroundColor(Color.LTGRAY);
                TextView text = (TextView)findViewById(R.id.control_questionnaire);
                text.setText(R.string.no_questionnaire);
                resultButton.setClickable(true);
                questionnaireButton.setClickable(false);
                if(questionaireType == 0){
                    Toast.makeText(this, "感謝您的填答 !", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "提醒您，上傳影片 !", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(this, "感謝您的填答 !", Toast.LENGTH_LONG).show();
                }

               // cancelNotification();
                pageRecord.clear();
                extraForQ = "";
//                stopRecording();
                //因為已經完成問卷了 等到下一次在填


                //TODO 歸零everyDay

//if you added fragment via layout xml

                // totalCount +=1;
                if (isMobileCrowdsource == true) {
//                    int todayMCount = sharedPrefs.getInt("todayMCount", 0);
//                    int allMCount = sharedPrefs.getInt("todayMCount", 0);
                    todayMCount += 1;
                    allMCount += 1;
                    Log.d("CheckQ", "isMobile : " + true);
                    Log.d("CheckQ", "todayMCount : " + todayMCount);
//                    sharedPrefs.edit().putInt("todayMCount", todayMCount).apply();
//                    sharedPrefs.edit().putInt("allMCount", allMCount).apply();

                } else {
//                    int todayNCount = sharedPrefs.getInt("todayNCount", 0);
//                    int allNCount = sharedPrefs.getInt("allNCount", 0);
                    todayNCount += 1;
                    allNCount += 1;
                    Log.d("CheckQ", "isMobile : " + false);
                    Log.d("CheckQ", "todayNCount : " + todayNCount);
//                    sharedPrefs.edit().putInt("todayNCount", todayNCount).apply();
//                    sharedPrefs.edit().putInt("allNCount", allNCount).apply();

                }
                sharedPrefs.edit().putInt(todayMCountString,todayMCount).commit();
                sharedPrefs.edit().putInt(todayNCountString,todayNCount).commit();
                sharedPrefs.edit().putInt(allMCountString,allMCount).commit();
                sharedPrefs.edit().putInt(allNCountString,allNCount).commit();

//                sharedPrefs.edit().putInt("todayMCount", todayMCount);
//                sharedPrefs.edit().putInt("totalCount",totalCount);
               // canFillQuestionnaire = false;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        cancelNotification();
                        insertFinalAnswer();
                        updateResponse();
                    }
                });







            }
        } else if( requestCode == DRAW_OVER_OTHER_APP_PERMISSION){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Permission is not available. Display error text.

                }
            }

        }


    }
    public void updateResponse(){
        appDatabase db = appDatabase.getDatabase(getApplicationContext());
//        responseDataRecord.setStartAnswerTime(getReadableTime(startAnswerTime));
//        responseDataRecord.setFinishedTime(getReadableTime(finishAnswerTime));
//        responseDataRecord.setIfComplete(true);
        db.repsonseDataRecordDao().updateData(relatedId,getReadableTime(startAnswerTime),getReadableTime(finishAnswerTime),true);
        String str = "";
        str += "startTime : "+getReadableTime(startAnswerTime)+ " finishTime : "+getReadableTime(finishAnswerTime);
        CSVHelper.storeToCSV("response.csv","modify : "+str);
        // db.repsonseDataRecordDao().insertAll(responseDataRecord);
        relatedId++;
        startAnswerTime = Long.valueOf(0);
        finishAnswerTime = Long.valueOf(0);

    }
    public void insertFinalAnswer(){
        appDatabase db = appDatabase.getDatabase(getApplicationContext());
        Cursor transCursor = db.questionWithAnswersDao().getAllQuestionsWithChoices("0");
        int rows = transCursor.getCount();
        if(rows!=0) {
            transCursor.moveToFirst();
            for (int i = 0; i < rows; i++) {
                String detectedTime = transCursor.getString(2);
                String questionId = transCursor.getString(3);
                String optionpos = transCursor.getString(5);
                String answerChoice = transCursor.getString(4);
                String optionId = transCursor.getString(6);

                String answerChoiceState = transCursor.getString(7);
                Integer related = transCursor.getInt(8);

                FinalAnswerDataRecord finalAnswerDataRecord = new FinalAnswerDataRecord();
                finalAnswerDataRecord.setAnswerChoicePos(optionpos);
                finalAnswerDataRecord.setAnswerChoiceState(answerChoiceState);
                finalAnswerDataRecord.setanswerId(String.valueOf(optionId));
                finalAnswerDataRecord.setdetectedTime(detectedTime);
                finalAnswerDataRecord.setQuestionId(questionId);
                finalAnswerDataRecord.setsyncStatus(0);
                finalAnswerDataRecord.setRelatedId(related);
                finalAnswerDataRecord.setAnswerChoice(answerChoice);
                finalAnswerDataRecord.setcreationIme(new Date().getTime());
                db.finalAnswerDao().insertAll(finalAnswerDataRecord);
                transCursor.moveToNext();
            }
        }

    }





    protected void showToast(String aText) {
        Toast.makeText(this, aText, Toast.LENGTH_SHORT).show();
    }
    public void  cancelNotification(){
        NotificationListenService.cancelNotification(this,100);
        NotificationListenService.cancelNotification(this,102);
        NotificationListenService.cancelNotification(this,101);
    }


    private void checkAndRequestPermissions() {

        Log.e(TAG, "checkingAndRequestingPermissions");
        if (!isNotificationServiceEnabled()) {
            Log.d(TAG, "notification start!!");
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        } else {
            toggleNotificationListenerService();
        }
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionFineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionStatus = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();



        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

        } else {
            startServiceWork();
        }

    }



    public String getDeviceid() {

        TelephonyManager mngr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int permissionStatus = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            Constants.DEVICE_ID = mngr.getDeviceId();
            sharedPrefs = getSharedPreferences(getString(R.string.sharedPreference), MODE_PRIVATE);
            sharedPrefs.edit().putString("device_id", mngr.getDeviceId()).apply();

            Log.e(TAG, "DEVICE_ID" + Constants.DEVICE_ID + " : " + mngr.getDeviceId());
            return mngr.getDeviceId();

            /*if(projName.equals("Ohio")) {
               device_id=(TextView)findViewById(R.id.deviceid);
               device_id.setText("ID = " + Constants.DEVICE_ID);

            }*/

        }
        return "NA";
    }

    public void startServiceWork() {

        Log.d(TAG, "startServiceWork");

        getDeviceid();
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString,MODE_PRIVATE);
        firstTimeOrNot = sharedPrefs.getBoolean("firstTimeOrNot", true);
        Log.d(TAG,"firstTime : "+firstTimeOrNot);
        if (firstTimeOrNot) {
            startpermission();
            firstTimeOrNot = false;
             sharedPrefs.edit().putBoolean("firstTimeOrNot", false).apply();
             Long nowTime = new Date().getTime() ;
             Long startHour = getReadableTimeLong(nowTime);
             startAppHour = startHour;
             Log.d(TAG,"firsttime");
        }
    }

    private void logUser() {
        Crashlytics.setUserIdentifier(Constants.DEVICE_ID);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.SYSTEM_ALERT_WINDOW, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.BODY_SENSORS, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
                        android.util.Log.d("permission", "[permission test]all permission granted");
                        //permission_ok=1;
                        startServiceWork();
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "/"
                + String.valueOf(monthOfYear + 1) + "/"
                + String.valueOf(dayOfMonth);
    }


    public void updateTab() {

    }


    /*
        private void showSettingsScreen() {
            //showToast("Clicked settings");
            Intent preferencesIntent = new Intent(this, SettingsActivity.class);
            startActivity(preferencesIntent);
        }
    */
    @Override
    protected void onStart() {
        super.onStart();
    }


/*
    @Subscribe
    public void assertEligibilityAndPopulateCompensationMessage(
            UserSubmissionStats userSubmissionStats) {
        Log.d(TAG, "Attempting to update compesnation message");
        if(userSubmissionStats != null && isEligibleForReward(userSubmissionStats)) {
            Log.d(TAG, "populating the compensation message");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    compensationMessage.setText("You are now eligible for today's reward!");
                    compensationMessage.setVisibility(View.VISIBLE);
                    compensationMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onCheckCreditPressed(v);
                        }
                    });

                }});
        } else {
                compensationMessage.setText("");
                compensationMessage.setVisibility(View.INVISIBLE);
        }
    }
*/


    // because of loadingProgressDialog
/*
    private void maybeRemoveProgressDialog(Integer loadingCount) {
        if(loadingCount <= 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingProgressDialog.hide();
                }
            });
        }
    }
*/
    /*
    @Subscribe
    public boolean isEligibleForReward(UserSubmissionStats userSubmissionStats) {
        return getRewardRelevantSubmissionCount(userSubmissionStats) >= ApplicationConstants.MIN_REPORTS_TO_GET_REWARD;
    }

    public void onCheckCreditPressed(View view) {
        Intent displayCreditIntent = new Intent(MainActivity.this, DisplayCreditActivity.class);
        startActivity(displayCreditIntent);
    }*/

    public class TimerOrRecordPagerAdapter extends PagerAdapter {
        private List<View> mListViews;
        private Context mContext;

        public TimerOrRecordPagerAdapter() {
        }

        ;

        public TimerOrRecordPagerAdapter(List<View> mListViews, Context mContext) {
            this.mListViews = mListViews;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }


        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


    }

    //json stored in the assets folder. but you can get it from wherever you like.
    private String loadQuestionnaireJson(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


//    private void uploadVideo(Uri fileuri) {
//        // TODO 加description of the video
//        dayCount = sharedPrefs.getInt("dayCount",0);
//        String description = "DeviceID : "+ Constants.DEVICE_ID+" Day : "+dayCount + "time : "+getTimeString(new Date().getTime());
//        File originalFile = mobilecrowdsourceStudy.nctu.minuku_2.AllUtils.FileUtils.getFile(MainActivity.this, fileuri);
//        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, description);
//
//        // get video mime type
//        ContentResolver cR = MainActivity.this.getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        String type = mime.getExtensionFromMimeType(cR.getType(fileuri));
//
//        RequestBody filePart = RequestBody.create(MediaType.parse(type),
//                originalFile);
//
//        MultipartBody.Part file = MultipartBody.Part.createFormData("video", originalFile.getName(), filePart);
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(URL_SAVE_VIDEO)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();
//
//        UserClient client = retrofit.create(UserClient.class);
//        Call<RequestBody> call = client.uploadVideo(descriptionPart, file);
//
//        call.enqueue(new Callback<RequestBody>() {
//            @Override
//            public void onResponse(Call<RequestBody> call, ResponseResult<RequestBody> response) {
//                Toast.makeText(MainActivity.this, "uploaded", Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onFailure(Call<RequestBody> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "uploaded failed", Toast.LENGTH_LONG);
//
//            }
//        });
//
//    }
    // You'll SUFFER just to create a file on SD on Android 5, see below
//
//    public void stopRecording(){
//
//        Intent broadCastIntent = new Intent();
//        broadCastIntent.setAction(STOP_RECORDING);
////        sendBroadcast(broadCastIntent);
//        Log.d("checkBroadcast","stopRecording");
//        Toast.makeText(this,"stopRecording",Toast.LENGTH_LONG);
//        sendBroadcast(broadCastIntent);
//    }





}