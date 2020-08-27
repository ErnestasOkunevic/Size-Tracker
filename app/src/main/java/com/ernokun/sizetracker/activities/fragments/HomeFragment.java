package com.ernokun.sizetracker.activities.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
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
import com.ernokun.sizetracker.utils.MyColors;
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


    // The graph.
    private GraphView graph;


    // Should the data be currently shown in kilograms or lbs.
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

    // Every time the kilograms or lbs setting is changed - a new
    // weight adapter with the required weight unit is created.
    private void prepareRecyclerView(boolean shouldBeKilograms) {
        weightAdapter = new WeightAdapter(shouldBeKilograms);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(weightAdapter);
    }

    // The viewmodel lets us use livedata from the database.
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
        // How many data points we will need in the array.
        int weightCount = weightAdapter.getWeightCount();

        // The maximum amount of weights in the graph.
        final int MAX_WEIGHTS = weightCount;

//        final int MAX_WEIGHTS = 12;
//
//        if (weightCount > MAX_WEIGHTS)
//            weightCount = MAX_WEIGHTS;

        LineGraphSeries<DataPoint> dataPoints = getDataPoints(weightCount);


        int my_white_color = Color.parseColor(MyColors.MY_WHITE);

        dataPoints.setDrawBackground(true);
        dataPoints.setAnimated(true);
        dataPoints.setColor(my_white_color);
        dataPoints.setBackgroundColor(Color.parseColor(MyColors.MY_DARK));

        graph.addSeries(dataPoints);

        graph.setTitle("Your " + weightCount + " weights");
        graph.setTitleTextSize(78);
        graph.setTitleColor(my_white_color);

        graph.getGridLabelRenderer().setGridColor(my_white_color);

        graph.getGridLabelRenderer().setLabelVerticalWidth(75);
        graph.getGridLabelRenderer().setLabelHorizontalHeight(35);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(MAX_WEIGHTS + 1);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(my_white_color);
        graph.getGridLabelRenderer().setVerticalLabelsColor(my_white_color);

        graph.getGridLabelRenderer().setVerticalAxisTitleColor(my_white_color);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(my_white_color);

        String verticalAxisTitle = (shouldBeKilograms) ?  "kg" : "lbs";
        graph.getGridLabelRenderer().setVerticalAxisTitle(verticalAxisTitle);

        String horizontalAxisTitle = "week";
        graph.getGridLabelRenderer().setHorizontalAxisTitle(horizontalAxisTitle);
    }

    private LineGraphSeries<DataPoint> getDataPoints(int weightCount) {
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

            DataPoint currentDatapoint = new DataPoint(currentIndex+1, weight);

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