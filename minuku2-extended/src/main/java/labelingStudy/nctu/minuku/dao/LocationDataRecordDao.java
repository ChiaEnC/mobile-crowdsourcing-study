package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.LocationDataRecord;

/**
 * Created by tingwei on 2018/3/28.
 */

@Dao
public interface LocationDataRecordDao {

    @Query("SELECT * FROM LocationDataRecord")
    List<LocationDataRecord> getAll();

    @Query("SELECT * FROM LocationDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    long insertAll(LocationDataRecord locationDataRecord);

    @Query("DELETE FROM LocationDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM LocationDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  LocationDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM LocationDataRecord WHERE sycStatus = :status")
    void deleteSyncData( int status);

}

