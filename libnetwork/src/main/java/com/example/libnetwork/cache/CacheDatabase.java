package com.example.libnetwork.cache;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.libcommon.AppGlobals;
@Database(entities = {Cache.class},version = 1,exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase database;

    static {
         database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "ppjoke_cache")
                .allowMainThreadQueries()
                .build();
    }

    public static CacheDatabase get() {
        return database;
    }
}
