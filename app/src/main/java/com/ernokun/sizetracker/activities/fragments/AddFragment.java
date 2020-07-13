package com.ernokun.sizetracker.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;

import java.time.LocalDate;

public class AddFragment extends Fragment {

    private static final String[] WEIGHT_UNITS = {"kg", "lbs"};

    private EditText weightEditText;
    private EditText dateEditText;

    private Button saveButton;
    private Button weightSelectionButton;

    private AddFragmentListener listener;

    public interface AddFragmentListener {
        void onWeightAdded(Weight weight);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        findViews(v);
        setOnClickListeners();

        LocalDate date = LocalDate.now();
        dateEditText.setText(date.toString());

        return v;
    }


    private void findViews(View v) {
        weightEditText = v.findViewById(R.id.weight_edittext_id);
        dateEditText = v.findViewById(R.id.date_edittext_id);

        saveButton = v.findViewById(R.id.save_button_id);
        weightSelectionButton = v.findViewById(R.id.kg_lbs_selection_button_id);
    }

    private void setOnClickListeners() {
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate date = LocalDate.now();
                dateEditText.setText(date.toString());

                // open calendar
                // TODO
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double weight_kg;
                double weight_lbs;

                String weightText = weightEditText.getText().toString();
                String date = dateEditText.getText().toString();

                if (weightText.equals("") || date.equals(""))
                    return;

                if (isGivenWeightInKilograms()) {
                    weight_kg = Double.parseDouble(weightText);
                    weight_lbs = weight_kg * 2.2;
                }
                else {
                    weight_lbs = Double.parseDouble(weightText);
                    weight_kg = weight_lbs / 2.2;
                }


                Weight weight = new Weight(weight_kg, weight_lbs, date);
                listener.onWeightAdded(weight);
            }
        });

        weightSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WEIGHT_UNITS[0].equals(weightSelectionButton.getText().toString()))
                    weightSelectionButton.setText(WEIGHT_UNITS[1]);
                else
                    weightSelectionButton.setText(WEIGHT_UNITS[0]);
            }
        });
    }

    private boolean isGivenWeightInKilograms() {
        // currently kg is selected
        if (WEIGHT_UNITS[0].equals(weightSelectionButton.getText().toString()))
            return true;

            // currently lbs is selected
        else
            return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof  AddFragmentListener)
            this.listener = (AddFragmentListener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement AddFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}
