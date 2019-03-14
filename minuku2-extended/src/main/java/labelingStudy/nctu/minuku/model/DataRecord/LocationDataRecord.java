/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by shriti on 7/15/16.
 */
@Entity(tableName = "LocationDataRecord")
public class LocationDataRecord implements DataRecord {

    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "sessionid")
    public String sessionid;

    @ColumnInfo(name = "latitude")
    public float latitude;

    @ColumnInfo(name = "longitude")
    public float longitude;
    @ColumnInfo(name = "Accuracy")
    public float Accuracy;
    @ColumnInfo(name = "Altitude")
    public float Altitude;
    @ColumnInfo(name = "Speed")
    public float Speed;
    @ColumnInfo(name = "Bearing")
    public float Bearing;
    @ColumnInfo(name = "Provider")
    public String Provider;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;












    public LocationDataRecord(float latitude, float longitude, float Accuracy, float Altitude, float Speed, float Bearing, String Provider, String sessionid) {
        this.creationTime = new Date().getTime();
        this.latitude = latitude;
        this.longitude = longitude;
        this.Accuracy = Accuracy;
        this.Altitude = Altitude;
        this.Speed = Speed;
        this.Bearing = Bearing;
        this.Provider = Provider;
        this.sessionid = sessionid;
        this.readable = getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;
    }



    private long getmillisecondToHour(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        long mhour = calendar.get(Calendar.HOUR_OF_DAY);

        return mhour;

    }

    public Long getReadable(){
        return this.readable;
    }
    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }

    public void setID(long id){
        _id = id;
    }

    public long get_id(){
        return _id;
    }

    public String getSessionid(){
        return sessionid;
    }

    public void setSessionid(String sessionid){
        this.sessionid = sessionid;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getAccuracy(){
        return Accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.Accuracy = accuracy;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public float getAltitude() {
        return Altitude;
    }

    public void setAltitude(float Altitude) {
        this.Altitude = Altitude;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float Speed) {
        this.Speed = Speed;
    }

    public float getBearing() {
        return Bearing;
    }

    public void setBearing(float Bearing) {
        this.Bearing = Bearing;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String Provider) {
        this.Provider = Provider;
    }

    @Override
    public String toString() {
        return "Loc:" + this.latitude + ":" + this.longitude;
    }
}
