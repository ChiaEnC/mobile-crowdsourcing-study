package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.SensorDataRecord;

/**
 * Created by chiaenchiang on 07/03/2018.
 */
@Dao
public interface SensorDataRecordDao {
    @Query("SELECT * FROM SensorDataRecord")
    List<SensorDataRecord> getAll();

    @Insert
    void insertAll(SensorDataRecord sensorDataRecord);

    @Query("SELECT * FROM SensorDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Query("DELETE FROM SensorDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM SensorDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  SensorDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);
    @Query("DELETE FROM SensorDataRecord WHERE sycStatus = :status")
    void deleteSyncData(int status);

}
