package com.android.settings.maintenance;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

/**
 * Activity with the proximity sensor settings.
 */
public class ProximitySensorSettings extends SettingsPreferenceFragment {

    // Preference categories
    private static final String PROXIMITY_SENSOR_CATEGORY = "proximity_sensor_category";

    private static final String SENSOR_CALIBRATION_PREFERENCE_SCREEN =
            "sensor_calibration_settings";

    // Preference controls.
    private PreferenceCategory mProximitySensorCategory;

    private PreferenceScreen mProximityCalibrationPreferenceScreen;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.proximity_sensor_settings);
    }
}
