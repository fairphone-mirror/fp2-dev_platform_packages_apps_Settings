/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.display;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import com.android.settings.DropDownPreference;
import android.widget.Switch;
import android.widget.TimePicker;

import com.android.internal.app.NightDisplayController;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;

import com.android.internal.logging.MetricsConstants;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;

/**
 * Settings screen for Night display.
 */
  public class NightDisplaySettings extends SettingsPreferenceFragment implements
        NightDisplayController.Callback,
        DropDownPreference.Callback,
        Preference.OnPreferenceClickListener,
        SwitchBar.OnSwitchChangeListener {

    private static final String KEY_NIGHT_DISPLAY_AUTO_MODE = "night_display_auto_mode";
    private static final String KEY_NIGHT_DISPLAY_START_TIME = "night_display_start_time";
    private static final String KEY_NIGHT_DISPLAY_END_TIME = "night_display_end_time";

    private static final int DIALOG_START_TIME = 0;
    private static final int DIALOG_END_TIME = 1;

    private NightDisplayController mController;
    private DateFormat mTimeFormatter;

    private DropDownPreference mAutoModePreference;
    private Preference mStartTimePreference;
    private Preference mEndTimePreference;

    private SwitchBar mSwitchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getContext();
        mController = new NightDisplayController(context);

        mTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
        mTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Load the preferences from xml.
        addPreferencesFromResource(R.xml.night_display_settings);

        mAutoModePreference = (DropDownPreference) findPreference(KEY_NIGHT_DISPLAY_AUTO_MODE);
        mStartTimePreference = findPreference(KEY_NIGHT_DISPLAY_START_TIME);
        mEndTimePreference = findPreference(KEY_NIGHT_DISPLAY_END_TIME);

        mAutoModePreference.addItem(getString(R.string.night_display_auto_mode_never),String.valueOf(NightDisplayController.AUTO_MODE_DISABLED));
        mAutoModePreference.addItem(getString(R.string.night_display_auto_mode_custom),String.valueOf(NightDisplayController.AUTO_MODE_CUSTOM));
        mAutoModePreference.addItem(getString(R.string.night_display_auto_mode_twilight),String.valueOf(NightDisplayController.AUTO_MODE_TWILIGHT));

        mAutoModePreference.setCallback(this);
        mStartTimePreference.setOnPreferenceClickListener(this);
        mEndTimePreference.setOnPreferenceClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwitchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Listen for changes only while visible.
        mController.setListener(this);

        // Update the current state since it have changed while not visible.
        onActivated(mController.isActivated());
        onAutoModeChanged(mController.getAutoMode());
        onCustomStartTimeChanged(mController.getCustomStartTime());
        onCustomEndTimeChanged(mController.getCustomEndTime());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Stop listening for state changes.
        mController.setListener(null);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mStartTimePreference) {
            showDialog(DIALOG_START_TIME);
            return true;
        } else if (preference == mEndTimePreference) {
            showDialog(DIALOG_END_TIME);
            return true;
        }
        return false;
    }

    @Override
    public Dialog onCreateDialog(final int dialogId) {
        if (dialogId == DIALOG_START_TIME || dialogId == DIALOG_END_TIME) {
            final NightDisplayController.LocalTime initialTime;
            if (dialogId == DIALOG_START_TIME) {
                initialTime = mController.getCustomStartTime();
            } else {
                initialTime = mController.getCustomEndTime();
            }

            final Context context = getContext();
            final boolean use24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
            return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    final NightDisplayController.LocalTime time =
                            new NightDisplayController.LocalTime(hourOfDay, minute);
                    if (dialogId == DIALOG_START_TIME) {
                        mController.setCustomStartTime(time);
                    } else {
                        mController.setCustomEndTime(time);
                    }
                }
            }, initialTime.hourOfDay, initialTime.minute, use24HourFormat);
        }
        return super.onCreateDialog(dialogId);
    }

    @Override
    public void onActivated(boolean activated) {
        mSwitchBar.setChecked(activated);
    }

    @Override
    public void onAutoModeChanged(int autoMode) {
        mAutoModePreference.setSelectedValue(String.valueOf(autoMode));

        final boolean showCustomSchedule = autoMode == NightDisplayController.AUTO_MODE_CUSTOM;
        mStartTimePreference.setEnabled(showCustomSchedule);
        mEndTimePreference.setEnabled(showCustomSchedule);
    }

    private String getFormattedTimeString(NightDisplayController.LocalTime localTime) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(mTimeFormatter.getTimeZone());
        c.set(Calendar.HOUR_OF_DAY, localTime.hourOfDay);
        c.set(Calendar.MINUTE, localTime.minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return mTimeFormatter.format(c.getTime());
    }

    @Override
    public void onCustomStartTimeChanged(NightDisplayController.LocalTime startTime) {
        mStartTimePreference.setSummary(getFormattedTimeString(startTime));
    }

    @Override
    public void onCustomEndTimeChanged(NightDisplayController.LocalTime endTime) {
        mEndTimePreference.setSummary(getFormattedTimeString(endTime));
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        // Attempt to update the NIGHT_DISPLAY_ACTIVATED setting if necessary.
        final boolean isActivated = mController.isActivated();
        if (isActivated != isChecked) {
            if (mController.setActivated(isChecked)) {
                switchView.setChecked(isActivated);
            }
        }
    }

    @Override
    public boolean onItemSelected(int pos, Object newValue) {
        Log.d("NightDisplaySettings", "Set mode to " + newValue);
        return mController.setAutoMode(Integer.parseInt((String) newValue));
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsConstants.DISPLAY;
    }
}
