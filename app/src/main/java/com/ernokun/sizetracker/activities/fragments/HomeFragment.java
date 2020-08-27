package com.ernokun.sizetracker.activities.fragments;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.recycleradapters.WeightAdapter;
import com.ernokun.sizetracker.viewmodels.WeightViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private WeightAdapter weightAdapter;

    // Gets data from repository and displays it in recycler view.
    private WeightViewModel weightViewModel;

    private GraphView graph;

    private boolean shouldBeKilograms = true;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.weight_recycler_view_id);
        graph = v.findViewById(R.id.graph_id);

        prepareRecyclerView(shouldBeKilograms);
        prepareViewModel();
        prepareSwipeToDelete();

        return v;
    }

    private void prepareRecyclerView(boolean shouldBeKilograms) {
        weightAdapter = new WeightAdapter(shouldBeKilograms);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(weightAdapter);

    }

    private void prepareViewModel() {
        // Gets reference to the instance of the view model.
        weightViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(
                        this.getActivity().getApplication())).get(WeightViewModel.class);

        // Automatically updates the recycler view when changes happen in the database.
        weightViewModel.getAllWeights().observe(this, new Observer<List<Weight>>() {
            @Override
            public void onChanged(List<Weight> weights) {
                // update RecyclerView
                weightAdapter.submitList(weights);
                setGraphData();
            }
        });
    }

    private void setGraphData() {
        LineGraphSeries<DataPoint> dataPoints = getDataPoints();

        dataPoints.setAnimated(true);

        graph.addSeries(dataPoints);
        graph.setTitle("Your weight history");
        graph.setTitleTextSize(78);

    }

    private LineGraphSeries<DataPoint> getDataPoints() {
        // How many data points we will need in the array.
        int weightCount = weightAdapter.getWeightCount();

        DataPoint[] dataPoints = new DataPoint[weightCount];

        // Create data points from the weight data.
        int currentIndex = 0;
        for (int i = weightCount - 1; i >= 0; i--) {
            Weight currentWeight = weightAdapter.getWeightAt(i);

            double weight;
            if (shouldBeKilograms)
                weight = currentWeight.getWeight_kg();
            else
                weight = currentWeight.getWeight_lbs();

            DataPoint currentDatapoint = new DataPoint(currentIndex + 1, weight);

            dataPoints[currentIndex++] = currentDatapoint;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);

        return series;
    }

    private void prepareSwipeToDelete() {
        // Adds the swipe to delete functionality.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete weight")
                        .setMessage("Are you sure you want to delete this weight?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                weightViewModel.delete(weightAdapter.getWeightAt(viewHolder.getAdapterPosition()));
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }


    public void changeUnit() {
        shouldBeKilograms = !shouldBeKilograms;
    }

    public void saveWeightsToFile(String filePath) {
        List<Weight> currentList = weightAdapter.getCurrentList();

        MyFileSaver.saveToFile(filePath, currentList);
    }


    private static class MyFileSaver {

        private MyFileSaver() {}

        private static PrintWriter getFileWriter(String filePath) {
            try { return new PrintWriter(new BufferedWriter(new FileWriter(filePath, false))); }
            catch (Exception e) { return null; }
        }

        public static void saveToFile(String filePath, List<Weight> currentList) {
            PrintWriter fileWriter = getFileWriter(filePath);

            for (Weight weight : currentList)
                fileWriter.println(weight);

            fileWriter.close();
        }
    }
}