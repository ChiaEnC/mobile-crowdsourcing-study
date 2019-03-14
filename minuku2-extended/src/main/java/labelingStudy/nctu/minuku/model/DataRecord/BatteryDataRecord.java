package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by Lawrence on 2017/7/22.
 */
@Entity(tableName = "BatteryDataRecord")
public class BatteryDataRecord implements DataRecord{



    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "BatteryLevel")
    public int BatteryLevel;

    @ColumnInfo(name = "BatteryPercentage")
    public float BatteryPercentage;

    @ColumnInfo(name = "BatteryChargingState")
    private String BatteryChargingState = "NA";

    @ColumnInfo(name = "isCharging")
    public boolean isCharging;

    @ColumnInfo(name = "sessionid")
    private String sessionid;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    public BatteryDataRecord(int BatteryLevel, float BatteryPercentage, String BatteryChargingState, boolean isCharging, String sessionid, long creationTime){
        this.creationTime = new Date().getTime();
        this.BatteryLevel = BatteryLevel;
        this.BatteryPercentage = BatteryPercentage;
        this.BatteryChargingState = BatteryChargingState;
        this.isCharging = isCharging;
        this.sessionid = sessionid;
        this.syncStatus = 0;
        this.readable = getReadableTimeLong(this.creationTime);
    }

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }
    public String getSessionid() {
        return sessionid;
    }



    private String getmillisecondToDateWithTime(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mhour = calendar.get(Calendar.HOUR);
        int mMin = calendar.get(Calendar.MINUTE);
        int mSec = calendar.get(Calendar.SECOND);

        return addZero(mYear)+"/"+addZero(mMonth)+"/"+addZero(mDay)+" "+addZero(mhour)+":"+addZero(mMin)+":"+addZero(mSec);

    }

    private String addZero(int date){
        if(date<10)
            return String.valueOf("0"+date);
        else
            return String.valueOf(date);
    }


    public Long getReadable(){
        return this.readable;
    }
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public int getBatteryLevel(){
        return BatteryLevel;
    }

    public float getBatteryPercentage(){
        return BatteryPercentage;
    }

    public String getBatteryChargingState(){
        return BatteryChargingState;
    }

    public boolean getisCharging(){
        return isCharging;
    }
}
