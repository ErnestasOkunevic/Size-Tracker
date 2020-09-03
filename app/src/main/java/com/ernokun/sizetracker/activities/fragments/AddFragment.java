package com.ernokun.sizetracker.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.utils.MyResources;

import java.time.LocalDate;

public class AddFragment extends Fragment {

    // Reference to the resource object.
    private MyResources myResources;


    // Kgs, lbs.
    private String[] weight_units;


    // EditTexts.
    private EditText weightEditText;
    private EditText dateEditText;


    // Buttons.
    private Button saveButton;
    private Button weightUnitButton;


    // Listener that is implemented in MainActivity
    // Used to call method to add new weight to database.
    private AddFragmentListener listener;


    // -----------------------------------------------------------------------------------


    // Constructor that is used to
    // pass the MyResources object reference from MainActivity
    public AddFragment(MyResources myResources) {
        this.myResources = myResources;
    }


    // The interface that is implemented in MainActivity
    // used to add inputted weight to database.
    public interface AddFragmentListener {
        void onWeightAdded(Weight weight);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        setupResources();
        setupViews(v);
        setupOnClickListeners();
        setupCurrentDate();

        return v;
    }


    // Sets up resource values.
    private void setupResources() {
        weight_units = myResources.getWeight_units();
    }


    // Sets up the views.
    private void setupViews(View v) {
        weightEditText = v.findViewById(R.id.weight_edittext_id);
        dateEditText = v.findViewById(R.id.date_edittext_id);

        saveButton = v.findViewById(R.id.save_button_id);
        weightUnitButton = v.findViewById(R.id.kg_lbs_selection_button_id);
    }


    // Sets up the on click listeners in this fragment.
    private void setupOnClickListeners() {
        // What happens when the date is pressed.
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Currently only the current date is availabe...
                setupCurrentDate();

                // TODO make custom dates available via use of the calendar
                // code
                // code
            }
        });


        // What happens when the save button is pressed.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the inputted weight and date.
                String weightText = weightEditText.getText().toString();
                String date = dateEditText.getText().toString();

                // Check if the values needed arent't empty.
                if (weightText.equals("") || date.equals(""))
                    return;

                // Both weight units are needed in order to save a weight.
                double weight_kg;
                double weight_lbs;

                // If the weight inputted is in kilograms.
                if (isGivenWeightInKilograms()) {
                    weight_kg = Double.parseDouble(weightText);
                    weight_lbs = weight_kg * 2.2;
                } else { // the weight inputted is in lbs.
                    weight_lbs = Double.parseDouble(weightText);
                    weight_kg = weight_lbs / 2.2;
                }

                // Creating the new weight with the data given.
                Weight weight = new Weight(weight_kg, weight_lbs, date);

                // Call the method in MainActivity.
                listener.onWeightAdded(weight);
            }
        });


        // What happens when the weight unit button is pressed.
        weightUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight_units[0].equals(weightUnitButton.getText().toString()))
                    weightUnitButton.setText(weight_units[1]);
                else
                    weightUnitButton.setText(weight_units[0]);
            }
        });
    }


    // Sets the current date as the default.
    private void setupCurrentDate() {
        // Get the current date.
        LocalDate date = LocalDate.now();

        // Default date.
        dateEditText.setText(date.toString());
    }


    // Returns whether the weight unit button is set to kilograms or lbs.
    private boolean isGivenWeightInKilograms() {
        // True - set to kg
        // False - set to lbs
        boolean setToKg = weight_units[0].equals(weightUnitButton.getText().toString());

        return setToKg;
    }


    // Checks whether the context that is using this fragment has
    // implemented its' interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AddFragmentListener)
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
