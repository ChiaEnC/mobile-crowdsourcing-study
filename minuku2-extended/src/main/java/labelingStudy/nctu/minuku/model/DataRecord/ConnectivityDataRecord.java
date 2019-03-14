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

@Entity(tableName = "ConnectivityDataRecord")
public class ConnectivityDataRecord implements DataRecord{

    @PrimaryKey(autoGenerate = true)
    public long _id;


    @ColumnInfo(name = "creationTime")
    public long creationTime;


    //network connectivity


    @ColumnInfo(name = "NetworkType")
    public String NetworkType = "NA";
    @ColumnInfo(name = "IsNetworkAvailable")
    public boolean IsNetworkAvailable = false;
    @ColumnInfo(name = "IsConnected")
    public boolean IsConnected = false;
    @ColumnInfo(name = "IsWifiAvailable")
    public boolean IsWifiAvailable = false;
    @ColumnInfo(name = "IsMobileAvailable")
    public boolean IsMobileAvailable = false;
    @ColumnInfo(name = "IsWifiConnected")
    public boolean IsWifiConnected = false;
    @ColumnInfo(name = "IsMobileConnected")
    public boolean IsMobileConnected = false;
    @ColumnInfo(name = "sessionid")
    public String sessionid;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    @ColumnInfo(name = "readable")
    public Long readable;


    public ConnectivityDataRecord(String NetworkType,boolean IsNetworkAvailable, boolean IsConnected, boolean IsWifiAvailable,
                                  boolean IsMobileAvailable, boolean IsWifiConnected, boolean IsMobileConnected, String sessionid){
        this.creationTime = new Date().getTime();
//        this.taskDayCount = Constants.TaskDayCount;
//        this.hour = getmillisecondToHour(creationTime);
        this.NetworkType = NetworkType;
        this.IsNetworkAvailable = IsNetworkAvailable;
        this.IsConnected = IsConnected;
        this.IsWifiAvailable = IsWifiAvailable;
        this.IsMobileAvailable = IsMobileAvailable;
        this.IsWifiConnected = IsWifiConnected;
        this.IsMobileConnected = IsMobileConnected;
        this.sessionid = sessionid;
        this.readable =getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;
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
    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    private long getmillisecondToHour(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        long mhour = calendar.get(Calendar.HOUR_OF_DAY);

        return mhour;

    }

    public long get_id() {
        return _id;
    }

    public Long getReadable(){
        return this.readable;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public String getNetworkType(){
        return NetworkType;
    }

    public boolean getIsNetworkAvailable(){
        return IsNetworkAvailable;
    }

    public boolean getIsConnected(){
        return IsConnected;
    }

    public boolean getIsWifiAvailable(){
        return IsWifiAvailable;
    }

    public boolean getIsMobileAvailable(){
        return IsMobileAvailable;
    }

    public boolean getIsWifiConnected(){
        return IsWifiConnected;
    }

    public boolean getIsMobileConnected(){
        return IsMobileConnected;
    }

}
