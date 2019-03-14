package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by chiaenchiang on 23/01/2019.
 */
@Entity(tableName = "ResponseDataRecord")
public class ResponseDataRecord implements DataRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "creationTime")
    public Long creationTime;

    @ColumnInfo(name = "related_id")
    public Integer relatedId;

    @ColumnInfo(name = "q_generate_time")
    public String questionGenerateTime;

    @ColumnInfo(name = "type")
    public Integer type;

    @ColumnInfo(name = "start_answer_time")
    public String startTime;
    @ColumnInfo(name = "finish_time")
    public String finishedTime;


    @ColumnInfo(name = "ifComplete")
    public Boolean ifComplete;
    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    public ResponseDataRecord(String questionGenerateTime,Integer relatedId,Integer type){
        this.creationTime = new Date().getTime();
        this.startTime = "Default";
        this.finishedTime = "Default";
        this.ifComplete = false;
        this.questionGenerateTime = questionGenerateTime;
        this.relatedId = relatedId;
        this.type = type;
        this.syncStatus = 0;
        this.readable = getReadableTimeLong(this.creationTime);

    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }

    public void setRelatedId(Integer relatedId){
        this.relatedId = relatedId;
    }
    public Integer getRelatedId(){
        return this.relatedId;
    }

    public void setQuestionGenerateTime(String questionGenerateTime){
        this.questionGenerateTime = questionGenerateTime;
    }
    public String getQuestionGenerateTime(){
        return this.questionGenerateTime;
    }

    public void setType(Integer type){
        this.type = type;
    }
    public Integer getType(){
        return this.type;
    }


    public void setStartAnswerTime(String startTime){
        this.startTime = startTime;
    }
    public String getStartAnswerTime(){
        return this.startTime;
    }

    public void setFinishedTime(String finishedTime){
        this.finishedTime = finishedTime;
    }
    public String getFinishedTime(){
        return this.finishedTime;
    }


    public void setIfComplete(Boolean ifComplete){
        this.ifComplete = ifComplete;
    }
    public Boolean getIfComplete(){
        return this.ifComplete;
    }

    public Long getReadable(){
        return this.readable;
    }

    public void setReadable(Long readable){
         this.readable = readable;
    }


}
