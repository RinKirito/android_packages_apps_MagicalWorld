/*
 * Copyright (C) 2016 CarbonROM
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

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import java.util.Arrays;
import java.util.HashSet;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;
import mx.mdroid.magicalworld.preferences.CustomSeekBarPreference;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "System";
    private static final String SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String RINGTONE_FOCUS_MODE = "ringtone_focus_mode";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";

    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
    private static final String SCROLLINGCACHE_DEFAULT = "1";

    private static final String LONGPRESS_DELAY = "pref_longpress_delay";
    private static final String LONGPRESS_DELAY_PROP = "persist.sys.longpress.delay";
    private static final String LONGPRESS_DELAY_DEFAULT = "0";

    private ListPreference mScreenOffAnimation;
    private ListPreference mHeadsetRingtoneFocus;
    private CustomSeekBarPreference mScreenshotDelay;
    private ListPreference mScrollingCachePref;
    private ListPreference mLongpressDelayPref;

    private static FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        ContentResolver resolver = getActivity().getContentResolver();

        mFragmentManager = getFragmentManager();

        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION);
        int screenOffStyle = Settings.System.getInt(resolver,
                Settings.System.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(String.valueOf(screenOffStyle));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        mHeadsetRingtoneFocus = (ListPreference) findPreference(RINGTONE_FOCUS_MODE);
        int mHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.RINGTONE_FOCUS_MODE, 0);
        mHeadsetRingtoneFocus.setValue(Integer.toString(mHeadsetRingtoneFocusValue));
        mHeadsetRingtoneFocus.setSummary(mHeadsetRingtoneFocus.getEntry());
        mHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        mScreenshotDelay = (CustomSeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 1500);
        mScreenshotDelay.setValue(screenshotDelay / 1);
        mScreenshotDelay.setOnPreferenceChangeListener(this);

        mScrollingCachePref = (ListPreference) findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setOnPreferenceChangeListener(this);

        mLongpressDelayPref = (ListPreference) findPreference(LONGPRESS_DELAY);
        mLongpressDelayPref.setValue(SystemProperties.get(LONGPRESS_DELAY_PROP,
                SystemProperties.get(LONGPRESS_DELAY_PROP, LONGPRESS_DELAY_DEFAULT)));
        mLongpressDelayPref.setSummary(mLongpressDelayPref.getEntry());
        mLongpressDelayPref.setOnPreferenceChangeListener(this);
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

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();

        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mScreenOffAnimation) {
            Settings.System.putInt(resolver,
                    Settings.System.SCREEN_OFF_ANIMATION, Integer.valueOf((String) objValue));
            int valueIndex = mScreenOffAnimation.findIndexOfValue((String) objValue);
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[valueIndex]);
            return true;
        } else if (preference == mHeadsetRingtoneFocus) {
            int mHeadsetRingtoneFocusValue = Integer.valueOf((String) objValue);
            int index = mHeadsetRingtoneFocus.findIndexOfValue((String) objValue);
            mHeadsetRingtoneFocus.setSummary(
                    mHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(resolver, Settings.Global.RINGTONE_FOCUS_MODE,
                    mHeadsetRingtoneFocusValue);
            return true;
        } else if (preference == mScreenshotDelay) {
            int screenshotDelay = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREENSHOT_DELAY, screenshotDelay * 1);
            return true;
        } else if (preference == mScrollingCachePref) {
            if (objValue != null) {
                SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, (String) objValue);
            }
            return true;
        } else if (preference == mLongpressDelayPref) {
            if (objValue != null) {
                SystemProperties.set(LONGPRESS_DELAY_PROP, (String) objValue);
            }
            int valueIndex = mLongpressDelayPref.findIndexOfValue((String) objValue);
            mLongpressDelayPref.setSummary(mLongpressDelayPref.getEntries()[valueIndex]);
            confirmRebootChange();
            return true;
        }
        return false;
    }

    private static void confirmRebootChange() {
        DialogFragment newFragment = new confirmRebootChangeDialog();
        newFragment.show(mFragmentManager, "reboot");
    }
}
