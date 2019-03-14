package labelingStudy.nctu.minuku.model.DataRecord;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import labelingStudy.nctu.minukucore.model.DataRecord;

@Entity(tableName = "answer_choices")
public class QuestionWithAnswersDataRecord implements DataRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "creationTime")
    public Long creationTime;
    @ColumnInfo(name = "detected_time")
    public String detectedTime;


    @ColumnInfo(name = "question_id")
    public String questionId;
    @ColumnInfo(name = "ans_choice")
    public String answerChoice;
    @ColumnInfo(name = "ans_choice_pos")
    public String answerChoicePosition;
    @ColumnInfo(name = "ans_choice_id")
    public String answerChoiceId;
    @ColumnInfo(name = "ans_choice_state")
    public String answerChoiceState;
    @ColumnInfo(name = "related_id")
    public Integer relatedId;

    public void setcreationIme(Long creationTime){
        this.creationTime = creationTime;
    }
    public void setdetectedTime(String detectedTime) {this.detectedTime = detectedTime;}
    public void setRelatedId(Integer related){
        this.relatedId = related;
    }
    public int getRelatedId(){
        return this.relatedId;
    }
    public String getAnswerChoiceId()
    {
        return answerChoiceId;
    }


    public void setAnswerChoiceId(String answerChoiceId)
    {
        this.answerChoiceId = answerChoiceId;
    }

    public String getAnswerChoiceState()
    {
        return answerChoiceState;
    }

    public void setAnswerChoiceState(String answerChoiceState)
    {
        this.answerChoiceState = answerChoiceState;
    }



    public String getAnswerChoicePosition()
    {
        return answerChoicePosition;
    }

    public void setAnswerChoicePosition(String answerChoicePosition)
    {
        this.answerChoicePosition = answerChoicePosition;
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

    public String getAnswerChoice()
    {
        return answerChoice;
    }

    public void setAnswerChoice(String answerChoice)
    {
        this.answerChoice = answerChoice;
    }
    public long getdetectedTime(Long detectedTime) {return detectedTime;}
    @Override
    public long getCreationTime() {
        return 0;
    }
}
