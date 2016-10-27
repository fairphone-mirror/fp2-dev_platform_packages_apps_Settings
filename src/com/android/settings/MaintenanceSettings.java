package com.android.settings;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

/**
 * Activity with the maintenance settings.
 */
public class MaintenanceSettings extends SettingsPreferenceFragment {

    // Preference categories
    private static final String CHECKUP_PREFERENCE_SCREEN =
            "checkup_settings";

    private static final String PROXIMITY_SENSOR_PREFERENCE_SCREEN =
            "proximity_sensor_settings";

    private static final String HICCUP_PREFERENCE_SCREEN =
            "hiccup_settings";

    // Preference controls.
    private PreferenceScreen mCheckupPreferenceScreen;
    private PreferenceScreen mProximitySensorPreferenceScreen;
    private PreferenceScreen mHiccupPreferenceScreen;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.maintenance_prefs);
    }
}
