package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.FinalAnswerDataRecord;

/**
 * Created by chiaenchiang on 21/11/2018.
 */
@Dao
public interface FinalAnswerDao

{
    @Query("SELECT * FROM FinalAnswer")
    List<FinalAnswerDataRecord> getAll();

    @Query("SELECT * FROM FinalAnswer WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Query("UPDATE FinalAnswer SET  ans_choice_state = :selectState WHERE question_id = :questionId AND answer_choice_pos =:optionId")
    void updateFAWithChoice(String selectState, String questionId, String optionId);

    @Query("UPDATE FinalAnswer SET  detected_time = :answerTime WHERE question_id = :questionId AND answer_choice_pos =:optionId")
    void updateFAWithDetectedTime(String answerTime, String questionId, String optionId);

    @Insert
     void insertAll(FinalAnswerDataRecord finalAnswerDataRecord);

    @Query("DELETE FROM FinalAnswer WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM FinalAnswer  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  FinalAnswer SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM FinalAnswer WHERE sycStatus = :status")
    void deleteSyncData( int status);
}
