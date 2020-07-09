package com.ernokun.sizetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;

import java.util.ArrayList;
import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightHolder> {

    List<Weight> weights = new ArrayList<>();

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
        notifyDataSetChanged();
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
        Weight currentWeight = weights.get(position);

        String date = currentWeight.getDate();
        String weight_text = String.valueOf(currentWeight.getWeight_kg());

        holder.day_month_textview.setText("ADD LATER");
        holder.year_textview.setText("ADD LATER");
        holder.weight_textview.setText(weight_text);
    }

    @Override
    public int getItemCount() {
        return weights.size();
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
