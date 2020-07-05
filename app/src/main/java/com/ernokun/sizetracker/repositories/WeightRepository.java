package com.ernokun.sizetracker.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ernokun.sizetracker.daos.WeightDao;
import com.ernokun.sizetracker.databases.WeightDatabase;
import com.ernokun.sizetracker.entities.Weight;

import java.util.List;

public class WeightRepository {
    private WeightDao weightDao;
    private LiveData<List<Weight>> allWeights;

    public WeightRepository(Application application) {
        // Creates and gets instance of database.
        WeightDatabase weightDatabase = WeightDatabase.getInstance(application);

        // Interface methods get created by ROOM.
        weightDao = weightDatabase.weightDao();

        // Getting the LiveData from the database.
        allWeights = weightDao.getAllWeights();
    }

    public void insert(Weight weight) {
        new InsertAsyncTask(weightDao).execute(weight);
    }

    public void update(Weight weight) {
        new UpdateAsyncTask(weightDao).execute(weight);
    }

    public void delete(Weight weight) {
        new DeleteAsyncTask(weightDao).execute(weight);
    }

    public LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

    private static class InsertAsyncTask extends AsyncTask<Weight, Void, Void> {
        private WeightDao weightDao;

        private InsertAsyncTask(WeightDao weightDao) {
            this.weightDao = weightDao;
        }

        @Override
        protected Void doInBackground(Weight... weights) {
            weightDao.insert(weights[0]);

            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Weight, Void, Void> {
        private WeightDao weightDao;

        private UpdateAsyncTask(WeightDao weightDao) {
            this.weightDao = weightDao;
        }

        @Override
        protected Void doInBackground(Weight... weights) {
            weightDao.update(weights[0]);

            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Weight, Void, Void> {
        private WeightDao weightDao;

        private DeleteAsyncTask(WeightDao weightDao) {
            this.weightDao = weightDao;
        }

        @Override
        protected Void doInBackground(Weight... weights) {
            weightDao.delete(weights[0]);

            return null;
        }
    }
}
