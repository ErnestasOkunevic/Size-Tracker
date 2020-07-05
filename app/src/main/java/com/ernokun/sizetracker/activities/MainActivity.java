package com.ernokun.sizetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.viewmodels.WeightViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeightViewModel weightViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weightViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(
                        this.getApplication())).get(WeightViewModel.class);


        // This might cause bugs because
        // "It only updates if the activity is in the foreground... ???"
        weightViewModel.getAllWeights().observe(this, new Observer<List<Weight>>() {
            @Override
            public void onChanged(List<Weight> weights) {
                // update RecyclerView
            }
        });
    }

}