package org.odk.collect.android.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.odk.collect.android.R;

public class SettingsFragment extends PreferenceFragment {
    public static final String SURVEY_FORM_DOWNLOADED_KEY = "survey_form_downloaded";
    public static final String SURVEY_SOURCE_URL_KEY = "survey_source_url";
    public static final String SURVEY_CHOSEN_TYPE_KEY = "survey_chosen_type";

    protected SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = null;
    protected Preference mSurveySourceURL = null;
    protected Preference mSurveyChosenType = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        mSurveySourceURL = findPreference(SURVEY_SOURCE_URL_KEY);
        mSurveySourceURL.setSummary(getPreferenceManager().getSharedPreferences().getString(SURVEY_SOURCE_URL_KEY, null));

        mSurveyChosenType = findPreference(SURVEY_CHOSEN_TYPE_KEY);
        mSurveyChosenType.setSummary(getPreferenceManager().getSharedPreferences().getString(SURVEY_CHOSEN_TYPE_KEY, null));

        mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
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
                .registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }
}
