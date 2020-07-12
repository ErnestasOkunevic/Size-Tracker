package com.ernokun.sizetracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.ernokun.sizetracker.R;
import com.ernokun.sizetracker.activities.fragments.AddFragment;
import com.ernokun.sizetracker.activities.fragments.HomeFragment;
import com.ernokun.sizetracker.activities.fragments.SettingsFragment;
import com.ernokun.sizetracker.entities.Weight;
import com.ernokun.sizetracker.recycleradapters.WeightAdapter;
import com.ernokun.sizetracker.viewmodels.WeightViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddFragment.AddFragmentListener {

    // The bottom navigation bar.
    private BottomNavigationView bottomNav;

    private AddFragment addFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;

    private WeightViewModel weightViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets reference to the instance of the view model.
        weightViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(
                        MainActivity.this.getApplication())).get(WeightViewModel.class);

        // Self-explanatory.
        prepareBottomNavigationBar();
    }

    // Self-explanatory.
    private void prepareBottomNavigationBar() {
        bottomNav = findViewById(R.id.bottom_navigation_bar_id);

        homeFragment = new HomeFragment();

        // On click listener for bottom navigation bar.
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                // Which fragment should get shown.
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        if (homeFragment == null)
                            homeFragment = new HomeFragment();

                        selectedFragment = homeFragment;
                        break;

                    case R.id.nav_add:
                        addFragment = new AddFragment();

                        selectedFragment = addFragment;
                        break;

                    case R.id.nav_settings:
                        if (settingsFragment == null)
                            settingsFragment = new SettingsFragment();

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

        // open the home fragment by default.
        openHomeFragment();
    }

    @Override
    public void onWeightAdded(Weight weight) {
        weightViewModel.insert(weight);

        openHomeFragment();
    }

    private void openHomeFragment() {
        // Opens the home fragment screen when the app is launched.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();


        // Highlights the Home selection in the bottom navigation bar.
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
    }
}