package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import labelingStudy.nctu.minuku.model.DataRecord.VideoDataRecord;

/**
 * Created by chiaenchiang on 07/12/2018.
 */
@Dao
public interface VideoDataRecordDao {
    @Insert
    void insertAll(VideoDataRecord videoDataRecordDao);

    @Query("SELECT * FROM VideoDataRecord  WHERE sycStatus =:notSyncInt  GROUP BY creationTime")
    Cursor getUnsyncedData(int notSyncInt);
    @Query("DELETE FROM VideoDataRecord WHERE sycStatus = :status")
    void deleteSyncData(int status);
    @Query("UPDATE  VideoDataRecord SET sycStatus = :status WHERE creationTime = :creationT")
    void updateDataStatus(long creationT, int status);
    @Query("UPDATE  VideoDataRecord SET sycStatus = :status WHERE file_name = :fileName")
    void updateDataStatusByFileName(String fileName, int status);
    @Query("SELECT * From VideoDataRecord  WHERE sycStatus = :status GROUP BY creationTime")
    Cursor getSyncVideoData(int status);


}
