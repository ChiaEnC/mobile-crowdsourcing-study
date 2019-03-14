package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by chiaenchiang on 18/11/2018.
 */
@Entity
public class NotificationDataRecord implements DataRecord {
    @Override
    public long getCreationTime() {
        return this.creationTime;
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }


    @PrimaryKey(autoGenerate = true)
    public long _id;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "NotificaitonTitle")
    public String NotificaitonTitle ;

    @ColumnInfo(name = "NotificaitonText")
    public String NotificaitonText ;

    @ColumnInfo(name = "NotificaitonSubText")
    public String NotificaitonSubText ;

    @ColumnInfo(name = "NotificationTickerText")
    public String NotificationTickerText ;

    @ColumnInfo(name = "NotificaitonPackageName")
    public String NotificaitonPackageName ;

    @ColumnInfo(name = "accessid")
    public Integer accessid ;
    @ColumnInfo(name = "reason")
    public String reason ;


    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    public String getNotificaitonTitle(){
        return NotificaitonTitle;
    }
    public String getNotificaitonText(){
        return NotificaitonText;
    }
    public String getNotificaitonSubText(){
        return NotificaitonSubText;
    }
    public String getNotificationTickerText(){
        return NotificationTickerText;
    }
    public String getNotificaitonPackageName(){
        return NotificaitonPackageName;
    }
    public long getcreationTime(){
        return creationTime;
    }
    public Integer getaccessid(){
        return accessid;
    }
    public void setAccessid(Integer accessid){
        this.accessid = accessid;
    }
    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }
    public void setReason(String reason){
        this.reason = reason;
    }
    public String getReason(){
        return this.reason;
    }


    public NotificationDataRecord(String NotificaitonTitle, String NotificaitonText, String NotificaitonSubText
            , String NotificationTickerText, String NotificaitonPackageName, Integer accessid,String reason){
        this.creationTime = new Date().getTime();
//        this.taskDayCount = Constants.TaskDayCount;
//        this.hour = getmillisecondToHour(creationTime);
        this.NotificaitonTitle = NotificaitonTitle;
        this.NotificaitonText = NotificaitonText;
        this.NotificaitonSubText = NotificaitonSubText;
        this.NotificationTickerText = NotificationTickerText;
        this.NotificaitonPackageName = NotificaitonPackageName;
        this.readable = getReadableTimeLong(this.creationTime);
        this.accessid = accessid;
        this.reason = reason;
        this.syncStatus = 0;

    }
    public Long getReadable(){
        return this.readable;
    }
}

