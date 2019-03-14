package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by chiaenchiang on 04/11/2018.
 */
@Entity(tableName = "MobileCrowdsourceDataRecord")
public class MobileCrowdsourceDataRecord implements DataRecord {

    @Override
    public long getCreationTime() {
        return 0;
    }

    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "App")
    public String App;
    @ColumnInfo(name = "ifClickedNoti")
    public boolean ifClickedNoti;
    @ColumnInfo(name = "startTasktime")
    public long startTasktime ;
    @ColumnInfo(name = "endTasktime")
    public long endTasktime ;
    @ColumnInfo(name = "userActions")
    public String userActions ;
    @ColumnInfo(name = "accessId")
    public Integer accessId ;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    public MobileCrowdsourceDataRecord( String App, boolean ifClickedNoti
            , long startTasktime, long endTasktime, String userActions, Integer accessId){
        this.creationTime = new Date().getTime();
//        this.taskDayCount = Constants.TaskDayCount;
//        this.hour = getmillisecondToHour(creationTime);

        this.App = App;
        this.ifClickedNoti = ifClickedNoti;
        this.startTasktime = startTasktime;
        this.endTasktime = endTasktime;
        this.userActions = userActions;
        this.accessId = accessId;
        this.readable = getReadableTimeLong(this.creationTime);
        this.syncStatus = 0;

    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public Long getReadable(){
        return this.readable;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }


    public String getApp() {
        return App;
    }

    public boolean getIfClickedNoti() {
        return ifClickedNoti;
    }

    public long getStartTasktime() {
        return startTasktime;
    }
    public long getEndTasktime() {
        return endTasktime;
    }

    public String getUserActions() {
        return userActions;
    }

    public Integer getaccessId(){return accessId; }

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }

    public Integer getsyncStatus(){
        return this.syncStatus;
    }




}
