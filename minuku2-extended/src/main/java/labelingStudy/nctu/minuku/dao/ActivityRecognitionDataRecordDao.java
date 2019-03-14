package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.ActivityRecognitionDataRecord;


/**
 * Created by Lawrence on 2017/5/22.
 */
@Dao
public interface ActivityRecognitionDataRecordDao  {

    @Query("SELECT * FROM ActivityRecognitionDataRecord")
    List<ActivityRecognitionDataRecord> getAll();
    @Query("SELECT * FROM ActivityRecognitionDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(ActivityRecognitionDataRecord activityRecognitionDataRecord);

    @Query("DELETE FROM ActivityRecognitionDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM ActivityRecognitionDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  ActivityRecognitionDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM ActivityRecognitionDataRecord WHERE sycStatus = :status")
    void deleteSyncData(int status);

}