package com.android.settings;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.android.internal.logging.MetricsLogger;

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
    protected int getMetricsCategory() {
        return MetricsLogger.MAINTENANCE;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.maintenance_prefs);
    }
}
