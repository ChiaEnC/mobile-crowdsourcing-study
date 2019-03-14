package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by chiaenchiang on 08/03/2018.
 */
@Entity(tableName = "AccessibilityDataRecord")
public class AccessibilityDataRecord implements DataRecord {

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "pack")
    public String pack;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "extra")
    public String extra;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "mcid")
    public Integer mcid;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;



    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
//    public AccessibilityDataRecord(){
//        this.creationTime = new Date().getTime();
//    }

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }

    public AccessibilityDataRecord(String pack, String text, String type, String extra, String content,Integer mcid){
        this.creationTime = new Date().getTime();

        this.pack = pack;
        this.text = text;
        this.type = type;
        this.extra = extra;
        this.content = content;
        this.mcid = mcid;
        this.syncStatus = 0;
        this.readable = getReadableTimeLong(this.creationTime);
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



    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPack(){
        return pack;
    }

    public String getText(){
        return text;
    }

    public String getType(){
        return type;
    }

    public String getExtra(){
        return extra;
    }
    public String getContent (){return content;}
    public Integer getmcId(){return mcid; }
}

