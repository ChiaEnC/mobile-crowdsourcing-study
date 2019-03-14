package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.BatteryDataRecord;

/**
 * Created by Lawrence on 2017/8/22.
 */
@Dao
public interface BatteryDataRecordDao {
    @Query("SELECT * FROM BatteryDataRecord")
    List<BatteryDataRecord> getAll();

    @Query("SELECT * FROM BatteryDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(BatteryDataRecord batteryDataRecord);

    @Query("DELETE FROM BatteryDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM BatteryDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  BatteryDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM BatteryDataRecord WHERE sycStatus = :status")
    void deleteSyncData( int status);

}