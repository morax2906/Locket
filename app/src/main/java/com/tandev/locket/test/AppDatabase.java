package com.tandev.locket.test;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MomentEntity.class, FriendEntity.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MomentDao momentDao();
    public abstract FriendDao friendDao();  // Thêm DAO cho FriendEntity
}
