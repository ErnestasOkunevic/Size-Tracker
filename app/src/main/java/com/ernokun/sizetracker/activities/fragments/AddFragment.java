package com.ernokun.sizetracker.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ernokun.sizetracker.R;

public class AddFragment extends Fragment {

    private EditText weightEditText;
    private EditText dateEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        weightEditText = v.findViewById(R.id.weight_edittext_id);
        dateEditText = v.findViewById(R.id.date_edittext_id);


        return v;
    }
}
