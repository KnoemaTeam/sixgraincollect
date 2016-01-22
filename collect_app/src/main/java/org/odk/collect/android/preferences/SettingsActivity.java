package org.odk.collect.android.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.odk.collect.android.R;

public class SettingsActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
