package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.TransportationModeDataRecord;

/**
 * Created by tingwei on 2018/3/28.
 */

@Dao
public interface TransportationModeDataRecordDao {

    @Query("SELECT * FROM TransportationModeDataRecord")
    List<TransportationModeDataRecord> getAll();

    @Query("SELECT * FROM TransportationModeDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(TransportationModeDataRecord transportationModeDataRecord);

    @Query("DELETE FROM TransportationModeDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM TransportationModeDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  TransportationModeDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM TransportationModeDataRecord WHERE sycStatus = :status")
    void deleteSyncData(int status);
}
