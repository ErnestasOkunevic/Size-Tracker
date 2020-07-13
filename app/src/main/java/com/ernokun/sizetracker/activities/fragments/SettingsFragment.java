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

public class SettingsFragment extends Fragment {

    private Button changeWeightUnitButton;

    private SettingsListener listener;

    private static String currentButton_text = "";

    private static final String[] BUTTON_TEXT = {"kg", "lbs"};

    public static final String COMMAND_CHANGE_UNIT = "CHANGE_WEIGHT_UNIT";


    public interface SettingsListener {
        void changeSetting(String command);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        changeWeightUnitButton = v.findViewById(R.id.change_weight_unit_button);

        if(!currentButton_text.equals(""))
            changeWeightUnitButton.setText(currentButton_text);

        changeWeightUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changeSetting(COMMAND_CHANGE_UNIT);

                changeButtonText(changeWeightUnitButton.getText().toString());
            }
        });

        return v;
    }

    private void changeButtonText(String currentText) {
        if (currentText.equals(BUTTON_TEXT[0]))
            changeWeightUnitButton.setText(BUTTON_TEXT[1]);
        else
            changeWeightUnitButton.setText(BUTTON_TEXT[0]);

        currentButton_text = changeWeightUnitButton.getText().toString();
    }

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
