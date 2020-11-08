package com.ernokun.sizetracker.activities.fragments;

import android.content.DialogInterface;
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
import com.ernokun.sizetracker.room.entities.Weight;
import com.ernokun.sizetracker.utils.MySharedPreferences;
import com.ernokun.sizetracker.utils.WeightAdapter;
import com.ernokun.sizetracker.utils.MyResources;
import com.ernokun.sizetracker.room.viewmodels.WeightViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    // How many weights can be shown on the graph.
    private static final int MAX_WEIGHT_AMOUNT = 12;



    // Reference to the resource object.
    private MyResources myResources;


    // Should the data be currently shown in kilograms or lbs.
    private boolean shouldbeKg;


    // Gets data from repository and displays it in recycler view.
    private WeightViewModel weightViewModel;


    // Recycler view and adapter:
    // used to display weight data to the user.
    private RecyclerView recyclerView;
    private WeightAdapter weightAdapter;


    // The graph.
    private BarChart graph;


    // -----------------------------------------------------------------------------------


    // Constructor that is used to
    // pass the MyResources object reference from MainActivity
    public HomeFragment(MyResources myResources) {
        this.myResources = myResources;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.weight_recycler_view_id);
        graph = v.findViewById(R.id.graph_id);

        setupWeightUnit();

        prepareRecyclerView(shouldbeKg);
        prepareViewModel();
        prepareSwipeToDelete();

        return v;
    }


    // Every time the kilograms or lbs setting is changed - a new
    // weight adapter with the required weight unit is created.
    private void prepareRecyclerView(boolean shouldBeKilograms) {
        weightAdapter = new WeightAdapter(shouldBeKilograms, myResources);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(weightAdapter);
    }


    // The View Model lets us use real-time data from the database.
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
                setupGraph();
            }
        });
    }


    // Checks whether the weight unit should set to kg or lbs.
    private void setupWeightUnit() {
        String[] weight_units = myResources.getWeight_units();

        String weight_unit = MySharedPreferences.getWeight_unit(Objects.requireNonNull(getContext()));

        if (weight_unit == null)
            shouldbeKg = true;
        else
            shouldbeKg = weight_unit.equals(weight_units[0]);
    }


    // Set the data for (and modifies the look of) the graph.
    private void setupGraph() {
        // All of the weights stored in the database.
        List<Weight> weightList = weightAdapter.getCurrentList();

        // How many weight records currently exist in the database.
        int weightCount = weightList.size();


        // Only the newest MAX_WEIGHT_AMOUNT number of weights should be shown on the graph.
        if (weightCount > MAX_WEIGHT_AMOUNT)
            weightCount = MAX_WEIGHT_AMOUNT;


        // The created data points for the graph.
        List<BarEntry> entries = getEntries(weightList, weightCount);


        BarData barData = getBarData(entries, weightCount);

        barData.setBarWidth(0.5f);

        graph.setData(barData);


        // Setting description for the graph.
        Description description = getDescription(weightList.size());

        graph.setDescription(description);


        // Customizing the look of the graph.
        customizeGraph();


        // Refreshes the data in the graph.
        graph.invalidate();
    }


    // Creates data points for the graph.
    private List<BarEntry> getEntries(List<Weight> weightList, int weightCount) {
        // The data points for the graph.
        List<BarEntry> entries = new ArrayList<>();

        // Add the last twelve (or less) weights to the graph.
        for (int i = 1; i <= weightCount; i++) {

            // Reverse order, because the newest weights are at the back of the list.
            Weight weight = weightList.get(weightCount - i);

            // What weight unit should be used.
            double weightAmount = (shouldbeKg) ?
                    weight.getWeight_kg() : weight.getWeight_lbs();

            // Creating the data point for the graph.
            BarEntry entry = new BarEntry(i, (float) weightAmount);

            // Adding the data point to the data point list.
            entries.add(entry);
        }

        return entries;
    }


    // Sets the description for the graph.
    private Description getDescription(int weightCount) {
//        String description_text = "You weighed yourself " + weightCount + " times";
        String description_text = "";

        Description description = new Description();

        description.setText(description_text);
        description.setTextColor(myResources.getWhite());

        return description;
    }


    // Bundles up data points and creates a data set for the graph.
    private BarData getBarData (List<BarEntry> entries, int usedWeightCount) {
        // Creating the data set.
        BarDataSet dataSet = new BarDataSet(entries, ("Your latest " + usedWeightCount + " sizes"));

        // Customising the look of the data set.
        dataSet.setColor(myResources.getBlue());

        dataSet.setValueTextColor(myResources.getWhite());
        dataSet.setHighLightColor(myResources.getPurple());


        // Bundling up the data and returning it.
        return new BarData(dataSet);
    }


    // Customizes the look of the graph.
    private void customizeGraph() {
        // Customizing the look of the graph.
        graph.getAxisLeft().setTextColor(myResources.getBlue());
        graph.getAxisRight().setTextColor(myResources.getPurple());
        graph.getXAxis().setTextColor(myResources.getWhite());

        graph.getAxisLeft().setTextSize(8);
        graph.getAxisRight().setTextSize(8);

        graph.getLegend().setTextColor(myResources.getWhite());

        graph.setNoDataText("Weigh yourself !");;
        graph.setNoDataTextColor(myResources.getWhite());

        graph.setDrawGridBackground(true);
        graph.setGridBackgroundColor(myResources.getBlack());

        graph.setDrawBorders(true);
        graph.setBorderColor(myResources.getBlack());
        graph.setBorderWidth(2);

        graph.setFitBars(true);
    }


    // Enables swiping to delete weights from the recycler view/
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
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                weightAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }


    // Gets called from by options through MainActivity,
    // changes kgs to lbs and vice versa.
    public void changeUnit(boolean shouldbeKg) {
        this.shouldbeKg = shouldbeKg;
    }


    // Gets called from by options through MainActivity,
    // saves weight data to a file.
    public void saveWeightsToFile(String filePath) {
        List<Weight> currentList = weightAdapter.getCurrentList();

        MyFileSaver.saveToFile(filePath, currentList);
    }


    // Class for saving data to files.
    // TODO change to AsyncTask later.
    private static class MyFileSaver {

        private MyFileSaver() {
        }

        private static PrintWriter getFileWriter(String filePath) {
            try {
                return new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
            } catch (Exception e) {
                return null;
            }
        }

        public static void saveToFile(String filePath, List<Weight> currentList) {
            PrintWriter fileWriter = getFileWriter(filePath);

            for (Weight weight : currentList)
                fileWriter.println(weight);

            fileWriter.close();
        }
    }
}