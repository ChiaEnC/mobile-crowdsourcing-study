package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.RingerDataRecord;

/**
 * Created by tingwei on 2018/3/26.
 */

@Dao
public interface RingerDataRecordDao {
    @Query("SELECT * FROM RingerDataRecord")
    List<RingerDataRecord> getAll();

    @Query("SELECT * FROM RingerDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Insert
    void insertAll(RingerDataRecord ringerDataRecord);

    @Query("DELETE FROM RingerDataRecord WHERE creationTime BETWEEN :start AND :end")
    void deleteRecordBetweenTimes( long start, long end);

    @Query("SELECT * FROM RingerDataRecord  WHERE sycStatus =:notSyncInt and readable=:targetHour  GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt,Long targetHour);

    @Query("UPDATE  RingerDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);

    @Query("DELETE FROM RingerDataRecord WHERE sycStatus = :status")
    void deleteSyncData( int status);
}