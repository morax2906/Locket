package com.tandev.locket.test;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MomentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MomentEntity moment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MomentEntity> moments);

    @Query("SELECT * FROM moment_table ORDER BY dateSeconds DESC")
    LiveData<List<MomentEntity>> getAllMoments();

    @Query("DELETE FROM moment_table")
    void deleteAll();
}