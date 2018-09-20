/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
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

package im.ene.attiq.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import javax.inject.Inject;

/**
 * @author eneim (12/3/16).
 */
public abstract class BaseActivity extends AppCompatActivity {

  protected String TAG = "Attiq:" + getClass().getSimpleName();

  static {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  @SuppressWarnings("unused") @Inject protected BaseViewModel baseViewModel;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
  }

  @SuppressLint("Range") @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    Log.d(TAG, "onPostCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
  }

  @Override protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "onRestart() called");
  }

  @Override protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart() called");
  }

  @Override protected void onResume() {
    super.onResume();
    Log.d(TAG, "onStarted() called");
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    Log.d(TAG, "onPostResume() called");
  }

  @Override protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause() called");
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop() called");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy() called");
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(TAG, "onNewIntent() called with: intent = [" + intent + "]");
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState() called with: outState = [" + outState + "]");
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    Log.w(TAG,
        "onRestoreInstanceState() called with: savedInstanceState = [" + savedInstanceState + "]");
  }

  @Override public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
    super.onMultiWindowModeChanged(isInMultiWindowMode);
    Log.i(TAG, "onMultiWindowModeChanged() called with: isInMultiWindowMode = ["
        + isInMultiWindowMode
        + "]");
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.i(TAG, "onConfigurationChanged() called with: newConfig = [" + newConfig + "]");
  }

  @Override protected void onUserLeaveHint() {
    super.onUserLeaveHint();
    Log.d(TAG, "onUserLeaveHint() called");
  }

  @Override public void onActivityReenter(int resultCode, Intent data) {
    super.onActivityReenter(resultCode, data);
    Log.d(TAG, "onActivityReenter() called with: resultCode = ["
        + resultCode
        + "], data = ["
        + data
        + "]");
  }

  @Override public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    Log.d(TAG, "onAttachedToWindow() called");
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Log.d(TAG, "onDetachedFromWindow() called");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult() called with: requestCode = ["
        + requestCode
        + "], resultCode = ["
        + resultCode
        + "], data = ["
        + data
        + "]");
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
   * @param currentActivity Activity in use when navigate Up action occurred.
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

  @SuppressLint("RestrictedApi") @Override
  public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
    super.startActivityForResult(intent, requestCode, options);
  }
}
