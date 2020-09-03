package com.ernokun.sizetracker.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ernokun.sizetracker.R;

import com.ernokun.sizetracker.room.entities.Weight;

/***
 * RecyclerView adapter for the list of weights in HomeFragment.
 */
public class WeightAdapter extends ListAdapter<Weight, WeightAdapter.WeightHolder> {

    // The months of the year.
    public static String[] months;


    // What color should the weight be printed in (blue or purple).
    private boolean shouldBeBlue;


    // Should the weight be printed in kilograms or lbs.
    private boolean shouldBeKilograms;


    // Reference to resource class object.
    private MyResources myResources;


    // Used in ListAdapter, is needed for the list adapter to be able to recognize if the list has
    // changed or not.
    private static final DiffUtil.ItemCallback<Weight> DIFF_CALLBACK = new DiffUtil.ItemCallback<Weight>() {
        @Override
        public boolean areItemsTheSame(@NonNull Weight oldItem, @NonNull Weight newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Weight oldItem, @NonNull Weight newItem) {
            return oldItem.getDate().equals(newItem.getDate())
                    && oldItem.getWeight_kg() == newItem.getWeight_kg()
                    && oldItem.getWeight_lbs() == newItem.getWeight_lbs();
        }
    };


    // -----------------------------------------------------------------------------------


    // Passed in reference to the MyResources object and a boolean which represents whether
    // the weights should be shown as kg or lbs.
    public WeightAdapter(boolean shouldBeKilograms, MyResources myResources) {
        super(DIFF_CALLBACK);

        // Adapter gets reference to resource object for color and string resource access.
        this.myResources = myResources;

        // Saves whether the weight needs to be printed in kilograms or lbs.
        this.shouldBeKilograms = shouldBeKilograms;

        // The first weight is always purple.
        this.shouldBeBlue = false;

        months = myResources.getMonths();
    }


    // Used to find which weight should be deleted.
    public Weight getWeightAt(int position) {
        return getItem(position);
    }


    @NonNull
    @Override
    public WeightHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new WeightHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull WeightHolder holder, int position) {
        // Gets the current weight that is about to be displayed.
        Weight currentWeight = getItem(position);

        // Sets up the date in the list item view.
        setDate(holder, currentWeight.getDate());

        // Sets up the colors in the list item view.
        setColor(holder);

        // Sets up the weight in the list item view.
        setWeight(holder, currentWeight);
    }


    private void setColor(@NonNull WeightHolder holder) {
        if (shouldBeBlue)
            holder.weight_textview.setTextColor(myResources.getBlue());
        else
            holder.weight_textview.setTextColor(myResources.getPurple());

        shouldBeBlue = !shouldBeBlue;
    }


    // Sets up the date in the provided list view holder
    private void setDate(@NonNull WeightHolder holder, String date) {
        String[] currentDate = date.split("-");

        String currentMonth = months[Integer.parseInt(currentDate[1]) - 1];
        String currentDay = currentDate[2];

        holder.day_month_textview.setText(currentDay + " of " + currentMonth);
        holder.year_textview.setText(currentDate[0]);
    }


    // Sets up the weight in the provided list view holder
    private void setWeight(@NonNull WeightHolder holder, Weight currentWeight) {
        if (shouldBeKilograms) {
            double weight_kg = currentWeight.getWeight_kg();
            weight_kg = round(weight_kg, 1);

            holder.weight_textview.setText(weight_kg + " kg");
        } else {
            double weight_lbs = currentWeight.getWeight_lbs();
            weight_lbs = round(weight_lbs, 1);

            holder.weight_textview.setText(weight_lbs + " lbs");
        }
    }


    // Method to help round the double values.
    private static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    // The list item view.
    static class WeightHolder extends RecyclerView.ViewHolder {
        private TextView day_month_textview;
        private TextView year_textview;
        private TextView weight_textview;

        public WeightHolder(@NonNull View itemView) {
            super(itemView);

            day_month_textview = itemView.findViewById(R.id.day_month_id);
            year_textview = itemView.findViewById(R.id.year_id);
            weight_textview = itemView.findViewById(R.id.weight_id);
        }
    }
}
