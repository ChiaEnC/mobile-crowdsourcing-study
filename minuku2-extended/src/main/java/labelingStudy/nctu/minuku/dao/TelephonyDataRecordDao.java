package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.TelephonyDataRecord;

/**
 * Created by tingwei on 2018/3/26.
 */

@Dao
public interface TelephonyDataRecordDao {
    @Query("SELECT * FROM TelephonyDataRecord")
    List<TelephonyDataRecord> getAll();

    @Query("SELECT * FROM TelephonyDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(TelephonyDataRecord telephonyDataRecord);



    @Query("DELETE FROM TelephonyDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM TelephonyDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  TelephonyDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM TelephonyDataRecord WHERE sycStatus = :status")
    void deleteSyncData(int status);
}