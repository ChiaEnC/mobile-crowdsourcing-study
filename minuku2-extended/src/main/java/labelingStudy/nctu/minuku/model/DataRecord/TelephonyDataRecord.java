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
@Entity(tableName = "TelephonyDataRecord")
public class TelephonyDataRecord implements DataRecord {

    @PrimaryKey(autoGenerate = true)
    public long _id;
    long get_id(){
        return _id;
    }
    void set_id(long id){
        this._id = id;
    }
    @ColumnInfo(name = "creationTime")
    public long creationTime;


    @ColumnInfo(name = "NetworkOperatorName")
    public String NetworkOperatorName = "NA";
    @ColumnInfo(name = "CallState")
    public int CallState = -9999;
    @ColumnInfo(name = "PhoneSignalType")
    public int PhoneSignalType = -9999;
    @ColumnInfo(name = "GsmSignalStrength")
    public int GsmSignalStrength = -9999;
    @ColumnInfo(name = "LTESignalStrength")
    public int LTESignalStrength = -9999;
    @ColumnInfo(name = "CdmaSignalStrengthLevel")
    public int CdmaSignalStrengthLevel = -9999;
    @ColumnInfo(name = "sessionid")
    public String sessionid;
    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;


    public TelephonyDataRecord(String NetworkOperatorName, int CallState, int PhoneSignalType
            , int GsmSignalStrength, int LTESignalStrength, int CdmaSignalStrengthLevel, String sessionid) {

        this.creationTime = new Date().getTime();
        this.NetworkOperatorName = NetworkOperatorName;
        this.CallState = CallState;
        this.PhoneSignalType = PhoneSignalType;
        this.GsmSignalStrength = GsmSignalStrength;
        this.LTESignalStrength = LTESignalStrength;
        this.CdmaSignalStrengthLevel = CdmaSignalStrengthLevel;
        this.sessionid = sessionid;
        this.readable =getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;
    }

    public String getSessionid() {
        return sessionid;
    }

    private long getmillisecondToHour(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        long mhour = calendar.get(Calendar.HOUR_OF_DAY);

        return mhour;

    }


    @Override
    public long getCreationTime() {

        return creationTime;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public int getCallState() {
        return CallState;
    }

    public int getPhoneSignalType() {
        return PhoneSignalType;
    }

    public int getGsmSignalStrength() {
        return GsmSignalStrength;
    }

    public int getLTESignalStrength() {
        return LTESignalStrength;
    }

    public int getCdmaSignalStrengthLevel() {
        return CdmaSignalStrengthLevel;
    }

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }

    public Integer getsyncStatus(){
        return this.syncStatus;
    }
    public Long getReadable(){
        return this.readable;
    }
}
