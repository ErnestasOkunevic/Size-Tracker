package com.ernokun.sizetracker.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ernokun.sizetracker.daos.WeightDao;
import com.ernokun.sizetracker.entities.Weight;

@Database(entities = {Weight.class}, version = 1)
public abstract class WeightDatabase extends RoomDatabase {
    private static WeightDatabase instance;

    public abstract WeightDao weightDao();

    public static synchronized WeightDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WeightDatabase.class, "weight_database")
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }
}
