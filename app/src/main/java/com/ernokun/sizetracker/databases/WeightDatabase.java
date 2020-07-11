package com.ernokun.sizetracker.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeightDao weightDao;

        private PopulateDbAsyncTask(WeightDatabase db) {
            weightDao = db.weightDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 15000; i++)
                weightDao.insert(new Weight(i, i*1.5, "date"+i));

            return null;
        }
    }

}
