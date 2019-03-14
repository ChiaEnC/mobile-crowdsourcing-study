package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by Lawrence on 2017/7/22.
 */
@Entity(tableName = "AppUsageDataRecord")
public class AppUsageDataRecord implements DataRecord{

    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;


    @ColumnInfo(name = "Screen_Status")
    public String Screen_Status;
    @ColumnInfo(name = "Latest_Used_App")
    public String Latest_Used_App;
    @ColumnInfo(name = "Latest_Used_App_Time")
    public String Latest_Used_App_Time;
    @ColumnInfo(name = "sessionid")
    public String sessionid;
    //private String Users;
    @ColumnInfo(name = "Latest_Foreground_Activity")
    private String Latest_Foreground_Activity;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;




    //screen on and off
//    private static final String STRING_SCREEN_OFF = "Screen_off";
//    private static final String STRING_SCREEN_ON = "Screen_on";
//    private static final String STRING_INTERACTIVE = "Interactive";
//    private static final String STRING_NOT_INTERACTIVE = "Not_Interactive";
//
//    private PowerManager mPowerManager;


    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }

    public void setLatest_Foreground_Activity(String Latest_Foreground_Activity){
        this.Latest_Foreground_Activity = Latest_Foreground_Activity;
    }
    public String getLatest_Foreground_Activity(){
        return this.Latest_Foreground_Activity;
    }

    @Ignore
    private Context context;

    @Ignore
    protected JSONObject jSONObject;


//    public AppUsageDataRecord(){
//        this.creationTime = new java.util.Date().getTime();
//    }

    public AppUsageDataRecord(String Screen_Status, String Latest_Used_App, String Latest_Foreground_Activity,String Latest_Used_App_Time) {
        this.creationTime = new java.util.Date().getTime();
        this.Screen_Status = Screen_Status;
        this.Latest_Used_App = Latest_Used_App;
        this.Latest_Foreground_Activity = Latest_Foreground_Activity;
        this.Latest_Used_App_Time = Latest_Used_App_Time;
        this.syncStatus = 0;
        this.readable = getReadableTimeLong(this.creationTime);
        //this.Users = Users;
    }

//    public AppUsageDataRecord(String Screen_Status, String Latest_Used_App, String Latest_Used_App_Time, String Recent_Apps) {
//        this.creationTime = new java.util.Date().getTime();
//        this.Screen_Status = Screen_Status;
//        this.Latest_Used_App = Latest_Used_App;
//        this.Latest_Used_App_Time = Latest_Used_App_Time;
//        this.Recent_Apps = Recent_Apps;
//        //this.Users = Users;
//    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long t){this.creationTime = t;}

//    public String getScreenStatus() {
//        Log.e(TAG, "GetScreenStatus called.");
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//
//            //use isInteractive after api 20
//            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
//            for (Display display : dm.getDisplays()) {
//                if (display.getState() != Display.STATE_OFF) {
//                    Screen_Status = STRING_INTERACTIVE;
//                }
//                else
//                    Screen_Status = STRING_SCREEN_OFF;
//            }
//
//
//
//        }
//        //before API20, we use screen on or off
//        else {
//            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
//            if(mPowerManager.isScreenOn())
//                Screen_Status = STRING_SCREEN_ON;
//            else
//                Screen_Status = STRING_SCREEN_OFF;
//
//        }
//
//        Log.e(TAG, "test source being requested [testing app] SCREEN:  " + Screen_Status);
//
//        return Screen_Status;
//    }
    public Long getReadable(){
        return this.readable;
    }
    public String getScreen_Status(){


        return Screen_Status;
    }

    public String getLatestUsedApp() {

        return Latest_Used_App;
    }

    public String getLatestUsedAppTime() {

        return Latest_Used_App_Time;
    }



//      public String getUsers() {
//        return Users;
//    }



//    public static void setCurrentForegroundActivityAndPackage(String curForegroundActivity) {
//
//        Latest_Foreground_Activity=curForegroundActivity;
//
//
//        Log.d(TAG, "[setCurrentForegroundActivityAndPackage] the current running package mIs " + Latest_Foreground_Activity );
//    }

    public String getLatestForegroundActivity() {
        return Latest_Foreground_Activity;
    }

    public JSONObject getData() {
        return jSONObject;
    }

    public void setData(JSONObject data) {
        this.jSONObject = data;
    }


    /**get the current time in milliseconds**/
    public static long getCurrentTimeInMillis(){
        //get timzone
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        long t = cal.getTimeInMillis();
        return t;
    }


}
