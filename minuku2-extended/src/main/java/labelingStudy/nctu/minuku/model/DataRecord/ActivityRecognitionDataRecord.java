package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.location.DetectedActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;


/**
 * Created by tingwei on 2018/3/15.
 */
@Entity(tableName = "ActivityRecognitionDataRecord")
public class ActivityRecognitionDataRecord implements DataRecord {

    public static String TAG = "ActivityRecognitionDataRecord";

    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "MostProbableActivity")
    public String MostProbableActivityString;

    @ColumnInfo(name = "ProbableActivities")
    public String ProbableActivitiesString;

    @ColumnInfo(name = "Detectedtime")
    public long Detectedtime;

    @ColumnInfo(name = "_timestamp")
    public long _timestamp;
    @ColumnInfo(name = "mTimestring")
    public String mTimestring;

    @ColumnInfo (name = "session_id")
    public String session_id;
    @ColumnInfo (name = "data")
    public String data;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    @Ignore
    private static DetectedActivity MostProbableActivity;
    @Ignore
    private List<DetectedActivity> mProbableActivities;

    @Ignore
    private String sessionid;

    @Ignore protected JSONObject mData;



//    public ActivityRecognitionDataRecord(long detectedtime){
//        this.creationTime = detectedtime;
//    }
//
//    public ActivityRecognitionDataRecord(DetectedActivity MostProbableActivity, List<DetectedActivity> mProbableActivities){
//        this.creationTime = new Date().getTime();
//        this.MostProbableActivity = MostProbableActivity.toString();
//        this.ProbableActivitiesNonString = mProbableActivities;
//        this.ProbableActivities = ProbableActivities.toString();
//
//    }
    public void setsyncStatus(Integer syncStatus){
    this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
    return this.syncStatus;
    }


    public ActivityRecognitionDataRecord(){

    }

//    public ActivityRecognitionDataRecord(long detectedtime){
//        this.creationTime = detectedtime;
//    }
//
//    public ActivityRecognitionDataRecord(DetectedActivity MostProbableActivity, List<DetectedActivity> mProbableActivities){
//        this.creationTime = new Date().getTime();
//        this.MostProbableActivity = MostProbableActivity;
//        this.mProbableActivities = mProbableActivities;
//
//    }
//    public ActivityRecognitionDataRecord(DetectedActivity mostProbableActivity, List<DetectedActivity> mProbableActivities,long detectedtime){
//        this.creationTime = new Date().getTime();
//        this.MostProbableActivity = mostProbableActivity;
//        this.mProbableActivities = mProbableActivities;
//        this.Detectedtime = detectedtime;
//
//    }

    public ActivityRecognitionDataRecord(DetectedActivity MostProbableActivity,long Detectedtime){
        this.creationTime = new Date().getTime();
        this.MostProbableActivity = MostProbableActivity;
        this.Detectedtime = Detectedtime;
        this.syncStatus = 0;
        this.readable = getReadableTimeLong(this.creationTime);
    }



    public ActivityRecognitionDataRecord(DetectedActivity mostProbableActivity, List<DetectedActivity> mProbableActivities,long detectedtime, String sessionid){
        this.creationTime = new Date().getTime();
        this.MostProbableActivity = mostProbableActivity;
        if(mostProbableActivity!=null) this.MostProbableActivityString = mostProbableActivity.toString();
        this.mProbableActivities = mProbableActivities;
        if(mProbableActivities!=null) this.ProbableActivitiesString = mProbableActivities.toString();
        this.Detectedtime = detectedtime;
        this.sessionid = sessionid;
        this.readable = getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;
    }

    public String getSessionid() {
        return sessionid;
    }

    public DetectedActivity getMostProbableActivity(){return MostProbableActivity;}

    public void setProbableActivities(List<DetectedActivity> probableActivities) {
        mProbableActivities = probableActivities;

    }

    public void setMostProbableActivity(DetectedActivity mostProbableActivity) {
        MostProbableActivity = mostProbableActivity;

    }

    public void setDetectedtime(long detectedtime){
        Detectedtime = detectedtime;

    }

    private long getmillisecondToHour(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        long mhour = calendar.get(Calendar.HOUR_OF_DAY);

        return mhour;

    }

    public void setID(long id){
        _id = id;
    }

    public long getID(){
        return _id;
    }

    public long getDetectedtime(){return Detectedtime;}

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public void setTimestamp(long t){
        _timestamp = t;
    }

    public long getTimestamp(){
        return _timestamp;
    }

    public JSONObject getData() {
        return mData;
    }

    public Long getReadable(){
        return this.readable;
    }

    public void setData(JSONObject data) {
        this.mData = data;
    }

    public String getTimeString(){

        SimpleDateFormat sdf_now = new SimpleDateFormat(Constants.DATE_FORMAT_NOW);
        mTimestring = sdf_now.format(_timestamp);

        return mTimestring;
    }

    public List<DetectedActivity> getProbableActivities() {
        return mProbableActivities;
    }


}
