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

public class SettingsFragment extends Fragment {

    // All available commands for the SettingsListener interface.
    public static final String COMMAND_CHANGE_UNIT = "CHANGE_WEIGHT_UNIT";
    public static final String COMMAND_SAVE_WEIGHTS_TO_FILE = "COMMAND_SAVE_WEIGHTS_TO_FILE";


    // Reference to the resource object.
    private MyResources myResources;


    // Buttons.
    private Button changeWeightUnitButton;
    private Button saveWeightsToFileButton;


    // Listener that is implemented in MainActivity
    // Used to call methods to change various settings.
    private SettingsListener listener;


    // TODO enhance with shared preferences
    // Should the change weight unit show kg or lbs.
    private static String current_button_text;


    // Kgs, lbs.
    private String[] weight_units;


    //-----------------------------------------------------------------------------------


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
        // TODO fix later with shared prefs to not be set to default value every time
        //  the app is launched.
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
                listener.changeSetting(COMMAND_CHANGE_UNIT);

                changeButtonText(changeWeightUnitButton.getText().toString());
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
        // If no weight unit is selected:
        if (current_button_text == null)
            current_button_text = weight_units[0];

        // Sets the default weight unit to be shown on the button.
        changeWeightUnitButton.setText(current_button_text);
    }


    // Changes weight unit from kg to lbs and vice versa.
    private void changeButtonText(String currentText) {
        // if currently the weight unit is set to kg:
        if (currentText.equals(weight_units[0]))
            current_button_text = weight_units[1]; // lbs
        else
            current_button_text = weight_units[0]; // kg

        // Sets the new weight unit to be shown on the button.
        changeWeightUnitButton.setText(current_button_text);
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
