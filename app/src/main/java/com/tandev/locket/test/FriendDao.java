package com.tandev.locket.test;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FriendEntity friend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FriendEntity> friends);

    @Query("SELECT * FROM friend_table ORDER BY first_name ASC")
    LiveData<List<FriendEntity>> getAllFriends();  // Lấy tất cả bạn bè

    @Query("DELETE FROM friend_table")
    void deleteAll();  // Xóa tất cả bạn bè
}
