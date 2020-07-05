package com.ernokun.sizetracker.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.repositories.WeightRepository;

import java.util.List;

public class WeightViewModel extends AndroidViewModel {
    private WeightRepository repository;
    private LiveData<List<Weight>> allWeights;

    public WeightViewModel(@NonNull Application application) {
        super(application);

        repository = new WeightRepository(application);

        allWeights = repository.getAllWeights();
    }

    public void insert(Weight weight) {
        repository.insert(weight);
    }

    public void update(Weight weight) {
        repository.update(weight);
    }

    public void delete(Weight weight) {
        repository.delete(weight);
    }

    public LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

}
