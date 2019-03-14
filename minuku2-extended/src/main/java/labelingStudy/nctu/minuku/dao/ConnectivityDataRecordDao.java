package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.ConnectivityDataRecord;

/**
 * Created by tingwei on 2018/3/27.
 */
@Dao
public interface ConnectivityDataRecordDao {

    @Query("SELECT * FROM ConnectivityDataRecord")
    List<ConnectivityDataRecord> getAll();

    @Query("SELECT * FROM ConnectivityDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(ConnectivityDataRecord connectivityDataRecord);

    @Query("DELETE FROM ConnectivityDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);


    @Query("SELECT * FROM ConnectivityDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  ConnectivityDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM ConnectivityDataRecord WHERE sycStatus = :status")
    void deleteSyncData( int status);

}
