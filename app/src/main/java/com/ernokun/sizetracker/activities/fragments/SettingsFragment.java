package com.ernokun.sizetracker.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.utils.MyResources;
import com.ernokun.sizetracker.utils.MySharedPreferences;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    // All available commands for the SettingsListener interface.
    public static final String COMMAND_CHANGE_UNIT_TO_KG = "COMMAND_CHANGE_UNIT_TO_KG";
    public static final String COMMAND_CHANGE_UNIT_TO_LBS = "COMMAND_CHANGE_UNIT_TO_LBS";
    public static final String COMMAND_SAVE_WEIGHTS_TO_FILE = "COMMAND_SAVE_WEIGHTS_TO_FILE";


    // Reference to the resource object.
    private MyResources myResources;


    // Buttons.
    private Button changeWeightUnitButton;
    private Button saveWeightsToFileButton;


    // Listener that is implemented in MainActivity
    // Used to call methods to change various settings.
    private SettingsListener listener;


    // Should the change weight unit show kg or lbs.
    private static String current_unit;


    // Kgs, lbs.
    private String[] weight_units;


    // -----------------------------------------------------------------------------------


    // Constructor that is used to
    // pass the MyResources object reference from MainActivity
    public SettingsFragment(MyResources myResources) {
        this.myResources = myResources;
    }


    // The interface that is implemented in MainActivity
    // used to change settings in the program, such as changing the current weight unit
    public interface SettingsListener {
        void changeSetting(String command);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Setup.
        setupResources();
        setupViews(v);
        setupOnClickListeners();


        // If the button currently has no text, set it to kg by default.
        setupDefaultUnit();

        return v;
    }


    // Sets up resource values.
    private void setupResources() {
        weight_units = myResources.getWeight_units();
    }


    // Sets up the views.
    private void setupViews(View v) {
        changeWeightUnitButton = v.findViewById(R.id.change_weight_unit_button);
        saveWeightsToFileButton = v.findViewById(R.id.save_to_file_button);
    }


    // Sets up the on click listeners in this fragment.
    private void setupOnClickListeners() {
        // What happens when the weight unit button is pressed.
        changeWeightUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_unit.equals(weight_units[0])) // kg
                    listener.changeSetting(COMMAND_CHANGE_UNIT_TO_KG);
                else                                      // lbs
                    listener.changeSetting(COMMAND_CHANGE_UNIT_TO_LBS);

                changeWeightUnit(changeWeightUnitButton.getText().toString());
            }
        });


        // What happens when the save weights to file button is pressed.
        saveWeightsToFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changeSetting(COMMAND_SAVE_WEIGHTS_TO_FILE);
            }
        });
    }


    // Sets the default weight unit.
    private void setupDefaultUnit() {
        // Loads unit from shared preferences.
        current_unit = MySharedPreferences.getWeight_unit(Objects.requireNonNull(getContext()));

        // If no weight unit is selected:
        if (current_unit == null)
            current_unit = weight_units[0];

        // Sets the default weight unit to be shown on the button.
        changeWeightUnitButton.setText(current_unit);
    }


    // Changes weight unit from kg to lbs and vice versa.
    private void changeWeightUnit(String currentText) {
        // if currently the weight unit is set to kg:
        if (currentText.equals(weight_units[0]))
            current_unit = weight_units[1]; // lbs
        else
            current_unit = weight_units[0]; // kg

        // Save unit to shared preferences.
        MySharedPreferences.saveWeight_unit(Objects.requireNonNull(getContext()), current_unit);

        // Sets the new weight unit to be shown on the button.
        changeWeightUnitButton.setText(current_unit);
    }


    // Checks whether the context that is using this fragment has
    // implemented its' interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SettingsListener)
            listener = (SettingsListener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement SettingsListener");
    }


    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }
}
