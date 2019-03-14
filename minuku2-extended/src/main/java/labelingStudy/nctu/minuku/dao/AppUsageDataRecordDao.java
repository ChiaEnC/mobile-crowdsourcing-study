package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.AppUsageDataRecord;

/**
 * Created by Jimmy on 2017/8/8.
 */
@Dao
public interface AppUsageDataRecordDao {
    @Query("SELECT * FROM AppUsageDataRecord")
    List<AppUsageDataRecord> getAll();

    @Insert
    void insertAll(AppUsageDataRecord appUsageDataRecord);

    @Query("SELECT * FROM AppUsageDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Query("DELETE FROM AppUsageDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM AppUsageDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  AppUsageDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM AppUsageDataRecord WHERE sycStatus = :status")
    void deleteSyncData( int status);
}