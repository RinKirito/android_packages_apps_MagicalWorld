/*
 * Copyright (C) 2018 MiracleDROID Project
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

package mx.mdroid.magicalworld.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import com.android.internal.util.mdroid.MDroidUtils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class Lockscreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Lockscreen";

    private static final String FP_CAT = "lockscreen_fp_category";
    private static final String LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR = "lock_screen_visualizer_custom_color";

    private FingerprintManager mFingerprintManager;
    private ColorPickerPreference mVisualizerColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();

        PreferenceCategory fingerprintCategory = (PreferenceCategory) prefSet.findPreference(FP_CAT);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        if (mFingerprintManager != null && !mFingerprintManager.isHardwareDetected()) {
            prefSet.removePreference(fingerprintCategory);
        }

        // Visualizer custom color
        mVisualizerColor = (ColorPickerPreference) findPreference(LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR);
        int visColor = Settings.System.getInt(resolver,
                Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, 0xff1976D2);
        String visColorHex = String.format("#%08x", (0xff1976D2 & visColor));
        mVisualizerColor.setSummary(visColorHex);
        mVisualizerColor.setNewPreviewColor(visColor);
        mVisualizerColor.setAlphaSliderEnabled(true);
        mVisualizerColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.MAGICAL_WORLD;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mVisualizerColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_CUSTOM_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }
}
