/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.ui.activities;

import android.annotation.TargetApi;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.TaskUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.support.widget.MarkdownView;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupActionBar();
    if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
      getFragmentManager().beginTransaction()
          .replace(android.R.id.content, new GeneralPreferenceFragment())
          .commit();
    }
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private static final String TAG = "SettingsActivity";

  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
      new Preference.OnPreferenceChangeListener() {

        @Override public boolean onPreferenceChange(Preference preference, Object value) {
          Log.d(TAG, "onPreferenceChange() called with: " + "preference = [" + preference + "], " +
              "value = [" + value + "]");

          String stringValue = value.toString();

          if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
          } else if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
              // Empty values correspond to 'silent' (no ringtone).
              preference.setSummary(R.string.pref_ringtone_silent);
            } else {
              Ringtone ringtone =
                  RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

              if (ringtone == null) {
                // Clear the summary if there was a lookup error.
                preference.setSummary(null);
              } else {
                // Set the summary to reflect the new ringtone display
                // name.
                String name = ringtone.getTitle(preference.getContext());
                preference.setSummary(name);
              }
            }
          } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
          }
          return true;
        }
      };

  private static Preference.OnPreferenceClickListener sOnPreferenceClickListener =
      new Preference.OnPreferenceClickListener() {
        @Override public boolean onPreferenceClick(Preference preference) {
          if ("pref_open_source".equals(preference.getKey())) {
            final MarkdownView view = new MarkdownView(preference.getContext());
            new AlertDialog.Builder(preference.getContext()).setTitle(R.string.title_licenses)
                .setView(view)
                .create()
                .show();

            TaskUtil.load(IOUtil.LICENSES, new TaskUtil.Callback<String>() {
              @Override protected void onFinished(String markdown) {
                if (markdown != null) {
                  view.loadMarkdown(markdown, "file:///android_asset/html/css/github.css");
                }
              }
            });

            return true;
          } else if ("pref_other_resources".equals(preference.getKey())) {
            final MarkdownView view = new MarkdownView(preference.getContext());
            new AlertDialog.Builder(preference.getContext()).setTitle(R.string.title_resources)
                .setView(view)
                .create()
                .show();

            TaskUtil.load(IOUtil.RESOURCES, new TaskUtil.Callback<String>() {
              @Override protected void onFinished(String markdown) {
                if (markdown != null) {
                  view.loadMarkdown(markdown, "file:///android_asset/html/css/github.css");
                }
              }
            });

            return true;
          }
          return false;
        }
      };

  /**
   * This fragment shows general preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB) public static class GeneralPreferenceFragment
      extends PreferenceFragment {

    private Preference.OnPreferenceChangeListener mOnMathJaxTriggered =
        new Preference.OnPreferenceChangeListener() {
          @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof CheckBoxPreference) {
              String stringValue = newValue.toString();
              if (PrefUtil.PREF_TRIGGER_MATHJAX.equals(preference.getKey())) {
                Log.d(TAG, "onPreferenceChange() called with: "
                    + "preference = ["
                    + preference
                    + "], newValue = ["
                    + newValue
                    + "]");
                PrefUtil.setMathJaxEnable(Boolean.valueOf(stringValue));
              }
            }

            return true;
          }
        };

    private Preference.OnPreferenceChangeListener mOnThemeSettingClick =
        new Preference.OnPreferenceChangeListener() {

          @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
              // For list preferences, look up the correct display value in
              // the preference's 'entries' list.
              ListPreference listPreference = (ListPreference) preference;
              int index = listPreference.findIndexOfValue(stringValue);

              // Set the summary to reflect the new value.
              preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

              PrefUtil.setTheme(UIUtil.Themes.lookupByName(stringValue));
            }

            return true;
          }
        };

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_general);

      findPreference(PrefUtil.PREF_APP_THEME).setOnPreferenceChangeListener(mOnThemeSettingClick);
      mOnThemeSettingClick.onPreferenceChange(findPreference(PrefUtil.PREF_APP_THEME),
          PrefUtil.getTheme());

      findPreference(PrefUtil.PREF_TRIGGER_MATHJAX).setOnPreferenceChangeListener(
          mOnMathJaxTriggered);
      mOnMathJaxTriggered.onPreferenceChange(findPreference(PrefUtil.PREF_TRIGGER_MATHJAX),
          PrefUtil.isMathJaxEnabled());

      // bindPreferenceSummaryToValue(findPreference(PrefUtil.PREF_APP_THEME));
      bindPreferenceClickListener(findPreference("pref_open_source"));
      bindPreferenceClickListener(findPreference("pref_other_resources"));
    }
  }

  private static void bindPreferenceClickListener(Preference preference) {
    preference.setOnPreferenceClickListener(sOnPreferenceClickListener);
  }

  @Override protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ? R.style.Attiq_Theme_Dark_Setting
        : R.style.Attiq_Theme_Light_Setting;
  }
}
