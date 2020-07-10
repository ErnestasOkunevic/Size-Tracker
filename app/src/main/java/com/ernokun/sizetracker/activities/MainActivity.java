package com.ernokun.sizetracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.ernokun.sizetracker.viewmodels.WeightViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Gets data from repository and passes it on to the activity.
    private WeightViewModel weightViewModel;


    // The bottom navigation bar.
    private BottomNavigationView bottomNav;


    // On click listener for bottom navigation bar.
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            // Which fragment should get shown.
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_add:
                    selectedFragment = new AddFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets reference to the instance of the view model.
        weightViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(
                        this.getApplication())).get(WeightViewModel.class);


        // This might cause bugs because
        // "It only updates if the activity is in the foreground... ???"
        weightViewModel.getAllWeights().observe(this, new Observer<List<Weight>>() {
            @Override
            public void onChanged(List<Weight> weights) {
                // update RecyclerView
                Toast.makeText(MainActivity.this, "It worked", Toast.LENGTH_LONG)
                        .show();
            }
        });

        // Self-explanatory.
        prepareBottomNavigationBar();

    }

    // Self-explanatory.
    private void prepareBottomNavigationBar() {
        bottomNav = findViewById(R.id.bottom_navigation_bar_id);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        // Opens the home fragment screen when the app is launched.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();


        // Highlights the Home selection in the bottom navigation bar.
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

}