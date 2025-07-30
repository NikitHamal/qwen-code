package com.codex.agent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Switch autoScrollSwitch;
    private Switch soundEnabledSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        autoScrollSwitch = findViewById(R.id.switch_auto_scroll);
        soundEnabledSwitch = findViewById(R.id.switch_sound_enabled);

        // Load saved preferences
        loadPreferences();

        // Set up listeners
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            // Apply theme change
            recreate();
        });

        autoScrollSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("auto_scroll", isChecked).apply();
        });

        soundEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("sound_enabled", isChecked).apply();
        });
    }

    private void loadPreferences() {
        darkModeSwitch.setChecked(preferences.getBoolean("dark_mode", false));
        autoScrollSwitch.setChecked(preferences.getBoolean("auto_scroll", true));
        soundEnabledSwitch.setChecked(preferences.getBoolean("sound_enabled", true));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}