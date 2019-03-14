package mobilecrowdsourceStudy.nctu.minuku_2.Manager;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.manager.MinukuDAOManager;
import labelingStudy.nctu.minuku.manager.MinukuSituationManager;
import labelingStudy.nctu.minuku.model.UserSubmissionStats;
import labelingStudy.nctu.minuku.streamgenerator.AccessibilityStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.ActivityRecognitionStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.AppUsageStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.BatteryStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.ConnectivityStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.LocationStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.MobileCrowdsourceStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.NotificationStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.RingerStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.SensorStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.TelephonyStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.TransportationModeStreamGenerator;

/**
 * Created by chiaenchiang on 26/10/2018.
 */

public class InstanceManager {
    private static InstanceManager instance = null;
    private Context mApplicationContext = null;
    private static Context mContext = null;
    private static Intent mintent;
    private UserSubmissionStats mUserSubmissionStats = null;
    private static String LOG_TAG = "InstanceManager";

    private InstanceManager(Context applicationContext) {
        this.mApplicationContext = applicationContext;
        initialize();
    }

    public static InstanceManager getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new InstanceManager(applicationContext);
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    private Context getApplicationContext() {
        return mApplicationContext;
    }

    public static void setContexttoActivityRecognitionservice(Context context) {
        mContext = context;
    }

    private void initialize() {
        // Add all initialization code here.
        // DAO initialization stuff

//        DBHelper dBHelper = new DBHelper(getApplicationContext());

        MinukuDAOManager daoManager = MinukuDAOManager.getInstance();


//        appDatabase db = Room.databaseBuilder(getApplicationContext(),appDatabase.class,"dataCollection")
//                .allowMainThreadQueries()
//                .build();

        //TODO build new StreamGenerator here.
        LocationStreamGenerator locationStreamGenerator =
                new LocationStreamGenerator(getApplicationContext());

        ActivityRecognitionStreamGenerator activityRecognitionStreamGenerator =
                new ActivityRecognitionStreamGenerator(getApplicationContext());

        TransportationModeStreamGenerator transportationModeStreamGenerator =
                new TransportationModeStreamGenerator(getApplicationContext());

        ConnectivityStreamGenerator connectivityStreamGenerator =
                new ConnectivityStreamGenerator(getApplicationContext());

        BatteryStreamGenerator batteryStreamGenerator =
                new BatteryStreamGenerator(getApplicationContext());

        RingerStreamGenerator ringerStreamGenerator =
                new RingerStreamGenerator(getApplicationContext());

        AppUsageStreamGenerator appUsageStreamGenerator =
                new AppUsageStreamGenerator(getApplicationContext());

        TelephonyStreamGenerator telephonyStreamGenerator =
                new TelephonyStreamGenerator(getApplicationContext());
        AccessibilityStreamGenerator accessibilityStreamGenerator =
                new AccessibilityStreamGenerator(getApplicationContext());
        SensorStreamGenerator SensorStreamGenerator =
                new SensorStreamGenerator(getApplicationContext());
        NotificationStreamGenerator notificationStreamGenerator =
                new NotificationStreamGenerator(getApplicationContext());
        MobileCrowdsourceStreamGenerator mobileCrowdsourceStreamGenerator =
                new MobileCrowdsourceStreamGenerator(getApplicationContext());


        // All situations must be registered AFTER the stream generators are registers.
        MinukuSituationManager situationManager = MinukuSituationManager.getInstance();





    }



    protected boolean areDatesEqual(long currentTime, long previousTime) {
        Log.d(LOG_TAG, "Checking if the both dates are the same");

        Calendar currentDate = Calendar.getInstance();
        Calendar previousDate = Calendar.getInstance();

        currentDate.setTimeInMillis(currentTime);
        previousDate.setTimeInMillis(previousTime);
        Log.d(LOG_TAG, "Current Year:" + currentDate.get(Calendar.YEAR) + " Previous Year:" + previousDate.get(Calendar.YEAR));
        Log.d(LOG_TAG, "Current Day:" + currentDate.get(Calendar.DAY_OF_YEAR) + " Previous Day:" + previousDate.get(Calendar.DAY_OF_YEAR));
        Log.d(LOG_TAG, "Current Month:" + currentDate.get(Calendar.MONTH) + " Previous Month:" + previousDate.get(Calendar.MONTH));

        boolean sameDay = (currentDate.get(Calendar.YEAR) == previousDate.get(Calendar.YEAR)) &&
                (currentDate.get(Calendar.DAY_OF_YEAR) == previousDate.get(Calendar.DAY_OF_YEAR)) &&
                (currentDate.get(Calendar.MONTH) == previousDate.get(Calendar.MONTH));

        if(sameDay)
            Log.d(LOG_TAG, "it is the same day, should not create a new object");
        else
            Log.d(LOG_TAG, "it is not the same day - a new day, should create a new object");
        return sameDay;
    }
}
