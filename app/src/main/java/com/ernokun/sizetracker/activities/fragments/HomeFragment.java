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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Recycler view and adapter:
    // used to display weight data to the user.
    private RecyclerView recyclerView;
    private WeightAdapter weightAdapter;

    // Gets data from repository and displays it in recycler view.
    private WeightViewModel weightViewModel;

    // The graph.
    private LineChart graph;

    // TODO Makes this variable get its' value from shared preferences.
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
                setGraphData();
            }
        });
    }


    // Set the data for (and modifies the look of) the graph.
    private void setGraphData() {

        // All of the weights stored in the database.
        List<Weight> weightList = weightAdapter.getCurrentList();

        // Used multiple times so value is saved to a variable.
        int my_white_color = Color.parseColor(MyColors.MY_WHITE);
        int my_dark_color = Color.parseColor(MyColors.MY_DARK);
        int my_purple_color = Color.parseColor(MyColors.MY_PURPLE);
        int my_blue_color = Color.parseColor(MyColors.MY_BLUE);
        int my_black_color = Color.parseColor(MyColors.MY_BLACK);

        List<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor(MyColors.MY_PURPLE));
        colors.add(Color.parseColor(MyColors.MY_BLUE));

        // TODO write the code for the graph data
        List<Entry> entries = new ArrayList<>();

        int weightCount = weightList.size();

        final int MAX_WEIGHT_AMOUNT = 12;

        if (weightCount > MAX_WEIGHT_AMOUNT)
            weightCount = MAX_WEIGHT_AMOUNT;

        for (int i = 1; i <= weightCount; i++) {

            // Reverse order.
            Weight weight = weightList.get(weightCount - i);

            double weightAmount = (shouldBeKilograms) ?
                    weight.getWeight_kg() : weight.getWeight_lbs();

            Entry entry = new Entry(i, (float) weightAmount);

            entries.add(entry);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Your latest " + weightCount + " sizes");

        dataSet.setColor(my_blue_color);
        dataSet.setCircleColor(my_black_color);
        dataSet.setValueTextColor(my_white_color);
        dataSet.setHighLightColor(my_black_color);
        dataSet.setCircleHoleColor(my_white_color);

        LineData lineData = new LineData(dataSet);

        graph.setData(lineData);

        String description_text = "You weighed yourself " + weightList.size() + " times";

        Description description = new Description();

        description.setText(description_text);
        description.setTextColor(my_white_color);

        graph.getAxisLeft().setTextColor(my_blue_color);
        graph.getAxisRight().setTextColor(my_purple_color);
        graph.getXAxis().setTextColor(my_white_color);

        graph.getAxisLeft().setTextSize(8);
        graph.getAxisRight().setTextSize(8);

        graph.getLegend().setTextColor(my_white_color);

        graph.setDescription(description);

        graph.setNoDataText("Weigh yourself !");;
        graph.setNoDataTextColor(my_white_color);

        graph.setDrawGridBackground(true);
        graph.setGridBackgroundColor(my_dark_color);

        graph.setDrawBorders(true);
        graph.setBorderColor(my_black_color);
        graph.setBorderWidth(2);

        // Refreshes the data in the graph.
        graph.invalidate();
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
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }


    // Gets called from options.
    // Changes kgs to lbs and vice versa.
    public void changeUnit() {
        shouldBeKilograms = !shouldBeKilograms;
    }


    // Gets called from options.
    // Saves weight data to a file.
    public void saveWeightsToFile(String filePath) {
        List<Weight> currentList = weightAdapter.getCurrentList();

        MyFileSaver.saveToFile(filePath, currentList);
    }


    // Class for saving data to files.
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