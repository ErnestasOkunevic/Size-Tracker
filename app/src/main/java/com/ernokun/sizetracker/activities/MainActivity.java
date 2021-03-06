package com.ernokun.sizetracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.activities.fragments.AddFragment;
import com.ernokun.sizetracker.activities.fragments.HomeFragment;
import com.ernokun.sizetracker.activities.fragments.SettingsFragment;
import com.ernokun.sizetracker.room.entities.Weight;
import com.ernokun.sizetracker.utils.MyResources;
import com.ernokun.sizetracker.room.viewmodels.WeightViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AddFragment.AddFragmentListener,
        SettingsFragment.SettingsListener, DatePickerDialog.OnDateSetListener {

    // The bottom navigation bar.
    private BottomNavigationView bottomNav;


    // Fragments
    private AddFragment addFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;


    // Used to access and modify the weight database.
    private WeightViewModel weightViewModel;


    // Path for saving all of the weights to a file.
    private String filePath;


    // Resource reference for the fragments and their components.
    private MyResources myResources;


    // -----------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (myResources == null)
            myResources = new MyResources(getResources());

        // Gets reference to the instance of the view model.
        weightViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(
                        MainActivity.this.getApplication())).get(WeightViewModel.class);

        // Sets the filePath for the "Save to file" functionality.
        filePath = getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/saved_weights.txt";

        // Sets up the bottom navigation bar.
        prepareBottomNavigationBar();

        // Opens the home fragment by default.
        openHomeFragment();
    }


    // Opens the homefragment by default and sets up a listener for the nav bar.
    private void prepareBottomNavigationBar() {
        bottomNav = findViewById(R.id.bottom_navigation_bar_id);

        homeFragment = new HomeFragment(myResources);

        // On click listener for bottom navigation bar.
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                // Which fragment should get shown.
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        if (homeFragment == null)
                            homeFragment = new HomeFragment(myResources);

                        selectedFragment = homeFragment;
                        break;


                    case R.id.nav_add:
                        addFragment = new AddFragment(myResources);

                        selectedFragment = addFragment;
                        break;


                    case R.id.nav_settings:
                        if (settingsFragment == null)
                            settingsFragment = new SettingsFragment(myResources);

                        selectedFragment = settingsFragment;
                        break;
                }


                // Changes the current fragment to the selected one.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();


                // True - should highlight the selected fragment in
                // the bottom navigation bar.
                return true;
            }
        });
    }


    // What happens when the add button is pressed in the add fragment.
    @Override
    public void onWeightAdded(Weight weight) {
        weightViewModel.insert(weight);

        openHomeFragment();
    }


    // Opens the home fragment when a new weight is inserted in the add fragment.
    private void openHomeFragment() {
        // Opens the home fragment screen when the app is launched.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();


        // Highlights the Home selection in the bottom navigation bar.
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
    }


    // Implemented interface for SettingsFragment
    @Override
    public void changeSetting(String command) {
        switch (command) {
            // When the change weight unit button is pressed for kgs.
            case SettingsFragment.COMMAND_CHANGE_UNIT_TO_KG:
                // Create new adapter with the corresponding weight unit.
                homeFragment.changeUnit(true);
                break;


            // When the change weight unit button is pressed for lbs.
            case SettingsFragment.COMMAND_CHANGE_UNIT_TO_LBS:
                // Create new adapter with the corresponding weight unit.
                homeFragment.changeUnit(false);
                break;


            // When the save weight to file button is pressed.
            case SettingsFragment.COMMAND_SAVE_WEIGHTS_TO_FILE:
                // Shows a toast message with the filePath for the saved file.
                showToast("Saved to: " + filePath);

                // Saves all weights to the given filePath.
                homeFragment.saveWeightsToFile(filePath);
                break;
        }
    }


    // Used to show messages to the user.
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    // When a date is picked - make a calendar and send it to the AddFragment.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (addFragment != null)
            addFragment.setDateEditText(calendar);
    }
}