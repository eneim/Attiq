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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.EventUtil;
import io.realm.Realm;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class BaseActivity extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  protected Realm mRealm;
  protected Profile mMyProfile;
  protected BaseState mState;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(lookupTheme(PrefUtil.getTheme()));
    super.onCreate(savedInstanceState);
    EventUtil.init(this);
    PrefUtil.registerOnSharedPreferenceChangeListener(this);
    initState();
    mRealm = Attiq.realm();
    mMyProfile = mRealm.where(Profile.class)
        .equalTo("token", PrefUtil.getCurrentToken()).findFirst();
    mState.isAuthorized = mMyProfile != null;
  }

  // placeholder for EventBus
  @SuppressWarnings("unused")
  public void onEvent(Event event) {
  }

  @Override protected void onResume() {
    super.onResume();
    // Active EventBus only when User could see the UI
    EventBus.getDefault().register(this);
  }

  @Override protected void onPause() {
    EventBus.getDefault().unregister(this);
    super.onPause();
  }

  @Override protected void onDestroy() {
    EventUtil.shutdown(this);
    PrefUtil.unregisterOnSharedPreferenceChangeListener(this);
    if (mRealm != null) {
      mRealm.close();
    }
    super.onDestroy();
  }

  protected void initState() {
    mState = new BaseState();
  }

  /**
   * This utility method handles Up navigation intents by searching for a parent activity and
   * navigating there if defined. When using this for an activity make sure to define both the
   * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
   * when the activity has a single parent activity. If the activity doesn't have a single parent
   * activity then don't define one and this method will use back button functionality. If "Up"
   * functionality is still desired for activities without parents then use
   * {@code syntheticParentActivity} to define one dynamically.
   * <p/>
   * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar
   * in Material Design guidelines.
   *
   * @param currentActivity         Activity in use when navigate Up action occurred.
   * @param syntheticParentActivity Parent activity to use when one is not already configured.
   */
  public static void navigateUpOrBack(Activity currentActivity,
                                      Class<? extends Activity> syntheticParentActivity) {
    // Retrieve parent activity from AndroidManifest.
    Intent intent = NavUtils.getParentActivityIntent(currentActivity);

    // Synthesize the parent activity when a natural one doesn't exist.
    if (intent == null && syntheticParentActivity != null) {
      try {
        intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (intent == null) {
      // No parent defined in manifest. This indicates the activity may be used by
      // in multiple flows throughout the app and doesn't have a strict parent. In
      // this case the navigation up button should act in the same manner as the
      // back button. This will result in users being forwarded back to other
      // applications if currentActivity was invoked from another application.
      currentActivity.onBackPressed();
    } else {
      if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
        // Need to synthesize a backstack since currentActivity was probably invoked by a
        // different app. The preserves the "Up" functionality within the app according to
        // the activity hierarchy defined in AndroidManifest.xml via parentActivity
        // attributes.
        TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
      } else {
        // Navigate normally to the manifest defined "Up" activity.
        NavUtils.navigateUpTo(currentActivity, intent);
      }
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      navigateUpOrBack(this, null);
//      overridePendingTransition(
//          R.anim.activity_in, R.anim.activity_out
//      );
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private static final String TAG = "BaseActivity";

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Log.d(TAG, "onSharedPreferenceChanged: " + getClass());
    if (PrefUtil.PREF_APP_THEME.equals(key)) {
      if (!getClass().getSimpleName().equals(SettingsActivity.class.getSimpleName())) {
        recreate();
      } else {
        startActivity(getIntent());
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
      }
      // Intent intent = new Intent(this, getClass());
    }
  }

  /**
   * An inner class store Activity's states, for example: login states, following states...
   */
  protected static class BaseState {

    // Activities should always know that a User is authorized or not
    protected boolean isAuthorized;
  }

  protected static class StateEvent<T extends BaseState> extends Event {

    protected final T state;

    @Deprecated
    public StateEvent(boolean success, @Nullable Error error, T state) {
      this(null, success, error, state);
    }

    public StateEvent(@Nullable String tag, boolean success, @Nullable Error error, T state) {
      super(tag, success, error);
      this.state = state;
    }
  }

  protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ?
        R.style.Attiq_Theme_Dark_NoActionBar : R.style.Attiq_Theme_Light_NoActionBar;
  }
}
