package org.odk.collect.android.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.odk.collect.android.R;

public class SettingsFragment extends PreferenceFragment {
    public static final String SURVEY_SOURCE_URL_KEY = "survey_source_url";
    public static final String SURVEY_CHOSEN_TYPE_KEY = "survey_chosen_type";

    protected SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(SURVEY_SOURCE_URL_KEY)) {
                    Preference sourcePreference = findPreference(key);
                    sourcePreference.setSummary(sharedPreferences.getString(key, ""));
                }
                if (key.equals(SURVEY_CHOSEN_TYPE_KEY)) {
                    Preference sourcePreference = findPreference(key);
                    sourcePreference.setSummary(sharedPreferences.getString(key, ""));
                }
            }
        };

        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}
