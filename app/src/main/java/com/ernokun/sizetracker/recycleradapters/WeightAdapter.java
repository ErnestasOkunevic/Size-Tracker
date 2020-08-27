package com.ernokun.sizetracker.recycleradapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.utils.MyColors;


public class WeightAdapter extends ListAdapter<Weight, WeightAdapter.WeightHolder> {
    public WeightAdapter(boolean shouldBeKilograms) {
        super(DIFF_CALLBACK);

        shouldBeCyan = false;

        this.shouldBeKilograms = shouldBeKilograms;
    }

    private boolean shouldBeCyan;
    private boolean shouldBeKilograms;

    public static String[] MONTHS = {
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
    };

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


    public Weight getWeightAt(int position) {
        return getItem(position);
    }

    public int getWeightCount() {
        return getItemCount();
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
        Weight currentWeight = getItem(position);

        setDate(holder, currentWeight.getDate());
        setColor(holder);

        setWeight(holder, currentWeight);
    }

    private void setColor(@NonNull WeightHolder holder) {
        if (shouldBeCyan)
            holder.weight_textview.setTextColor(Color.parseColor(MyColors.MY_BLUE));
        else
            holder.weight_textview.setTextColor(Color.parseColor(MyColors.MY_PURPLE));

        shouldBeCyan = !shouldBeCyan;
    }

    private void setDate(@NonNull WeightHolder holder, String date) {
        String[] currentDate = date.split("-");

        String currentMonth = MONTHS[Integer.parseInt(currentDate[1]) - 1];
        String currentDay = currentDate[2];

        holder.day_month_textview.setText(currentDay + " of " + currentMonth);
        holder.year_textview.setText(currentDate[0]);
    }

    private void setWeight(@NonNull WeightHolder holder, Weight currentWeight) {
        if (shouldBeKilograms) {
            double weight_kg = currentWeight.getWeight_kg();
            weight_kg = round(weight_kg, 1);

            holder.weight_textview.setText(weight_kg + " kg");
        }
        else {
            double weight_lbs = currentWeight.getWeight_lbs();
            weight_lbs = round(weight_lbs, 1);

            holder.weight_textview.setText(weight_lbs + " lbs");
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    class WeightHolder extends RecyclerView.ViewHolder {
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
