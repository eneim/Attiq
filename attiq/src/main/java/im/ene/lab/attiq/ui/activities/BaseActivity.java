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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.AccessTokenEvent;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileEvent;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 12/13/15.
 */
public abstract class BaseActivity extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  protected Realm mRealm;
  protected Profile mMyProfile;
  protected BaseState mState;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  protected GoogleApiClient mGoogleApiClient;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(lookupTheme(PrefUtil.getTheme()));
    super.onCreate(savedInstanceState);
    PrefUtil.registerOnSharedPreferenceChangeListener(this);
    initState();
    mRealm = Attiq.realm();

    if (!UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
      mMyProfile =
          mRealm.where(Profile.class).equalTo("token", PrefUtil.getCurrentToken()).findFirst();
    }

    mState.isAuthorized = mMyProfile != null;
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
  }

  // placeholder for EventBus
  @SuppressWarnings("unused") public void onEvent(Event event) {
  }

  @SuppressWarnings("unused") public void onEvent(AccessTokenEvent event) {
    if (event.success && event.token != null) {
      PrefUtil.setCurrentToken(event.token.getToken());
      getMasterUser(event.token.getToken());
    }
  }

  @Override protected void onStart() {
    super.onStart();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    mGoogleApiClient.connect();
  }

  @Override protected void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
  }

  @Override protected void onResume() {
    super.onResume();
    // Batch.onStart(this);
    // Active EventBus only when User could see the UI
    EventBus.getDefault().register(this);
    if (mMyProfile == null && !UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
      getMasterUser(PrefUtil.getCurrentToken());
    }
  }

  // Utils
  RealmAsyncTask mTransactionTask;

  protected void getMasterUser(final String token) {
    ApiClient.me().enqueue(new Callback<Profile>() {
      @Override public void onResponse(Call<Profile> call, final Response<Profile> response) {
        mMyProfile = response.body();
        if (mMyProfile != null) {
          mMyProfile.setToken(token);
          // save to Realm
          mTransactionTask = Attiq.realm().executeTransactionAsync(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
              realm.copyToRealmOrUpdate(mMyProfile);
            }
          }, new Realm.Transaction.OnSuccess() {
            @Override public void onSuccess() {
              EventBus.getDefault().post( //
                  new ProfileEvent(HomeActivity.class.getSimpleName(), true, null, mMyProfile));
            }
          }, new Realm.Transaction.OnError() {
            @Override public void onError(Throwable error) {
              EventBus.getDefault()
                  .post(new ProfileEvent(HomeActivity.class.getSimpleName(), false,
                      new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()),
                      null));
            }
          });
        } else {
          EventBus.getDefault()
              .post(new ProfileEvent(HomeActivity.class.getSimpleName(), false,
                  new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Call<Profile> call, Throwable error) {
        EventBus.getDefault()
            .post(new ProfileEvent(HomeActivity.class.getSimpleName(), false,
                new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    });
  }

  @Override protected void onPause() {
    // Batch.onStop(this);
    EventBus.getDefault().unregister(this);
    super.onPause();
  }

  @Override protected void onDestroy() {
    PrefUtil.unregisterOnSharedPreferenceChangeListener(this);

    if (mTransactionTask != null && !mTransactionTask.isCancelled()) {
      mTransactionTask.cancel();
    }

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

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      navigateUpOrBack(this, null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private static final String TAG = "BaseActivity";

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Log.d(TAG, "onSharedPreferenceChanged: " + getClass());
    if (PrefUtil.PREF_APP_THEME.equals(key)) {
      if (getClass().getSimpleName().equals(SettingsActivity.class.getSimpleName())) {
        // special deal for Setting Activity
        startActivity(getIntent());
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
      } else {
        recreate();
      }
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

    public StateEvent(@Nullable String tag, boolean success, @Nullable Error error, T state) {
      super(tag, success, error);
      this.state = state;
    }
  }

  protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ? R.style.Attiq_Theme_Dark_NoActionBar
        : R.style.Attiq_Theme_Light_NoActionBar;
  }
}
