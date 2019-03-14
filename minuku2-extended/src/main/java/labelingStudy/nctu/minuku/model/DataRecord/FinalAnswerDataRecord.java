package labelingStudy.nctu.minuku.model.DataRecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import labelingStudy.nctu.minukucore.model.DataRecord;

import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;

/**
 * Created by chiaenchiang on 21/11/2018.
 */

@Entity(tableName = "FinalAnswer")
public class FinalAnswerDataRecord implements DataRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "creationTime")
    public Long creationTime;

    @ColumnInfo(name = "related_id")
    public Integer relatedId;
    @ColumnInfo(name = "question_id")
    public String questionId;
    @ColumnInfo(name = "answer_id")
    public String answerId;
    @ColumnInfo(name = "answer_choice_pos")
    public String answerChoicePos;
    @ColumnInfo(name = "ans_choice_state")
    public String answerChoiceState;
    @ColumnInfo(name = "detected_time")
    public String detectedTime;
    @ColumnInfo(name = "ans_choice")
    public String answerChoice;

    @ColumnInfo(name = "readable")
    public Long readable;
    @ColumnInfo(name = "sycStatus")
    public Integer syncStatus;

    public void setsyncStatus(Integer syncStatus){
        this.syncStatus = syncStatus;
    }
    public Integer getsyncStatus(){
        return this.syncStatus;
    }

    public void setanswerId(String answerId){
        this.answerId = answerId;
    }
    public String getanswerId(){
        return this.answerId;
    }
    public void setcreationIme(Long creationTime){
        this.readable =getReadableTimeLong(creationTime);
        this.creationTime = creationTime;
    }

    public void setAnswerChoice(String answerChoice){
        this.answerChoice = answerChoice;

    }

    public void setdetectedTime(String detectedTime) {this.detectedTime = detectedTime;}
    public String getdetectedTime() {return detectedTime;}

    public String getAnswerChoiceState()
    {
        return answerChoiceState;
    }

    public void setAnswerChoiceState(String answerChoiceState)
    {
        this.answerChoiceState = answerChoiceState;
    }

    public void setRelatedId(int relatedId)
    {
        this.relatedId = relatedId;
    }



    public Long getReadable(){
        return this.readable;
    }
    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }

    public String getAnswerChoicePos()
    {
        return answerChoicePos;
    }

    public void setAnswerChoicePos(String answerChoice)
    {
        this.answerChoicePos = answerChoice;
    }
    public long getdetectedTime(Long detectedTime) {return detectedTime;}
    @Override
    public long getCreationTime() {
        return 0;
    }
}
