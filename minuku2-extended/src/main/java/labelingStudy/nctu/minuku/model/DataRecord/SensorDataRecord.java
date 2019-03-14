package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by Lawrence on 2017/7/22.
 */
@Entity(tableName = "SensorDataRecord")
public class SensorDataRecord implements DataRecord {


    @PrimaryKey(autoGenerate = true)
    public long _id;
    @ColumnInfo(name = "creationTime")
    public long creationTime;
    @ColumnInfo(name = "mAccele_str")
    public String mAccele_str;
    @ColumnInfo(name = "mGyroscope_str")
    public String mGyroscope_str;
    @ColumnInfo(name = "mLinearAcceleration_str")
    public String mLinearAcceleration_str;
    @ColumnInfo(name = "mRotationVector_str")
    public String mRotationVector_str;
    @ColumnInfo(name = "mProximity_str")
    public String mProximity_str;
    @ColumnInfo(name = "mLight_str")
    public String mLight_str;
    @ColumnInfo(name = "mPressure_str")
    public String mPressure_str;
    @ColumnInfo(name = "mRelativeHumidity_str")
    public String mRelativeHumidity_str;
    @ColumnInfo(name = "mAmbientTemperature_str")
    public String mAmbientTemperature_str;
    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;




    protected String _source;


    protected boolean isCopiedToPublicPool;

    public String mTimestring;
    @ColumnInfo(name = "sessionid")
    private String sessionid;



    public SensorDataRecord(String mAccele_str, String mGyroscope_str,  String mLinearAcceleration_str,
                            String mRotationVector_str, String mProximity_str,  String mLight_str,
                            String mPressure_str, String mRelativeHumidity_str, String mAmbientTemperature_str, String sessionid){

        this.creationTime = new Date().getTime();
        this.mAccele_str = mAccele_str;
        this.mGyroscope_str = mGyroscope_str;

        this.mLinearAcceleration_str = mLinearAcceleration_str;
        this.mRotationVector_str = mRotationVector_str;
        this.mProximity_str = mProximity_str;
        this.mLight_str = mLight_str;
        this.mPressure_str = mPressure_str;
        this.mRelativeHumidity_str = mRelativeHumidity_str;
        this.mAmbientTemperature_str = mAmbientTemperature_str;
        this.sessionid = sessionid;
        this.readable = getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;
    }

    public String getSessionid() {
        return sessionid;
    }



    public boolean isCopiedToPublicPool() {
        return isCopiedToPublicPool;
    }

    public void setIsCopiedToPublicPool(boolean isCopiedToPublicPool) {
        this.isCopiedToPublicPool = isCopiedToPublicPool;
    }

    public void setID(long id){
        _id = id;
    }

    public Long get_id() {
        return _id;
    }



    public String getSource(){
        return _source;
    }

    public void setSource(String source){
        _source = source;
    }


    @Override
    public long getCreationTime() {

        return creationTime;
    }

    public String getmAccele_str() {return this.mAccele_str;}

    public String getmGyroscope_str() {return this.mGyroscope_str;}


    public String getmLinearAcceleration_str() {return this.mLinearAcceleration_str;}

    public String mRotationVector_str() {return mRotationVector_str;}

    public String getmProximity_str() {return this.mProximity_str;}


    public String getmLight_str() {return mLight_str;}

    public String getmPressure_str() {return mPressure_str;}

    public String getmRelativeHumidity_str() {return mRelativeHumidity_str;}

    public String getmAmbientTemperature_str() {return mAmbientTemperature_str;}

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
