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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.Header;
import im.ene.lab.attiq.data.api.SuccessCallback;
import im.ene.lab.attiq.ui.fragment.TagItemsFragment;
import im.ene.lab.attiq.util.UIUtil;
import java.util.Iterator;
import java.util.List;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsActivity extends BaseActivity implements TagItemsFragment.Callback {

  private final int MESSAGE_FOLLOW_UNFOLLOW = 1;

  @Bind(R.id.toolbar) Toolbar mToolbar;

  private String mTagId; // actually the Tag name
  private View mBtnFollowContainer;
  private TextView mBtnFollow;
  private Callback<Void> mFollowUnFollowCallback = new SuccessCallback<Void>() {
    @Override public void onResponse(Call<Void> call, Response<Void> response) {
      Boolean currentState = ((State) mState).isFollowing;
      if (currentState != null && response.code() == 204) { // success
        ((State) mState).isFollowing = !currentState; // state changed
        EventBus.getDefault()
            .post(new StateEvent<>(getClass().getSimpleName(), true, null, mState));
      }
    }
  };
  private Handler mHandler = new Handler(new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (msg.what == MESSAGE_FOLLOW_UNFOLLOW) {
        // Follow/Unfollow current tag
        Boolean isFollowing = ((State) mState).isFollowing;
        if (isFollowing != null) {
          if (isFollowing) {
            ApiClient.unFollowTag(((State) mState).tagName).enqueue(mFollowUnFollowCallback);
          } else {
            ApiClient.followTag(((State) mState).tagName).enqueue(mFollowUnFollowCallback);
          }
        }
        return true;
      }
      return false;
    }
  });
  // Could not use ButterKnife for this action. This button is not included in View hierarchy
  // from the beginning
  private View.OnClickListener mOnFollowClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (mHandler != null) {
        // Prevent 'stress pressing' by using handler
        mHandler.removeMessages(MESSAGE_FOLLOW_UNFOLLOW);
        mHandler.sendEmptyMessageDelayed(MESSAGE_FOLLOW_UNFOLLOW, 200);
      }
    }
  };

  public static Intent createIntent(Context context, String tagName) {
    Intent intent = new Intent(context, TagItemsActivity.class);
    Uri data = Uri.parse(context.getString(R.string.data_tags_url, tagName));
    intent.setData(data);
    return intent;
  }

  private static final String TAG = "TagItemsActivity";

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleIntent(intent, false);
  }

  /**
   * Process current intent
   *
   * @param intent The Intent to process
   * @param coldStart first start of this Activity or be called from onNewIntent
   */
  private void handleIntent(Intent intent, boolean coldStart) {
    if (intent != null) {
      Uri data = intent.getData();
      if (data != null) {
        List<String> paths = data.getPathSegments();
        if (!UIUtil.isEmpty(paths)) {
          Iterator<String> iterator = paths.iterator();
          while (iterator.hasNext()) {
            if ("tags".equals(iterator.next())) {
              mTagId = iterator.next();
              break;
            }
          }
        }
      }

      ((State) mState).tagName = mTagId;
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, TagItemsFragment.newInstance(mTagId));
      // Add fragment to backstack only when that Fragment is added from current Activity
      if (!coldStart) {
        transaction.addToBackStack(null);
      }

      transaction.commit();
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_common_toolbar);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    getSupportFragmentManager().addOnBackStackChangedListener(mBackStackListener);

    handleIntent(getIntent(), true);
  }

  private FragmentManager.OnBackStackChangedListener mBackStackListener =
      new FragmentManager.OnBackStackChangedListener() {
        @Override public void onBackStackChanged() {
          if (mState != null) {
            ((State) mState).itemCount = 0;
            EventBus.getDefault()
                .post(new StateEvent<>(getClass().getSimpleName(), true, null, mState));
          }
        }
      };

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Back-press will change the fragment backstack.
    // But press navigate button should finish the activity
    if (item.getItemId() == android.R.id.home) {
      getSupportFragmentManager().popBackStackImmediate(null,
          FragmentManager.POP_BACK_STACK_INCLUSIVE);
      navigateUpOrBack(this, null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void addFollowButton() {
    if (mToolbar == null) {
      return;
    }

    mBtnFollowContainer = mToolbar.findViewById(R.id.tag_activity_action_follow);
    if (mBtnFollowContainer == null) {
      mBtnFollowContainer = LayoutInflater.from(mToolbar.getContext())
          .inflate(R.layout.button_follow, mToolbar, false);
      mBtnFollowContainer.setId(R.id.tag_activity_action_follow);
      ActionBar.LayoutParams params = new ActionBar.LayoutParams(
          UIUtil.getDimen(mToolbar.getContext(), R.dimen.follow_button_container_width),
          ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END);
      mToolbar.addView(mBtnFollowContainer, params);
    }

    mBtnFollow = (TextView) mBtnFollowContainer.findViewById(R.id.text_action_follow);
    mBtnFollow.setOnClickListener(mOnFollowClick);
  }

  @SuppressWarnings("unused") public void onEventMainThread(StateEvent<State> event) {
    if (event.state != null) {
      if (event.state.tagName != null) {
        setTitle(getString(R.string.title_activity_tag, event.state.tagName));
      }

      if (event.state.itemCount != null) {
        mToolbar.setSubtitle(getResources().getQuantityString(R.plurals.title_activity_tag_quantity,
            event.state.itemCount, event.state.itemCount));
      }

      if (event.state.isFollowing != null) {
        addFollowButton();
      }

      if (mBtnFollowContainer != null && mBtnFollow != null) {
        mBtnFollow.setEnabled(event.state.isFollowing != null);
        mBtnFollow.setClickable(event.state.isFollowing != null);
        mBtnFollowContainer.setVisibility(
            event.state.isFollowing != null ? View.VISIBLE : View.GONE);

        mBtnFollow.setText(
            event.state.isFollowing ? R.string.state_following : R.string.state_not_following);

        mBtnFollow.setBackgroundResource(
            event.state.isFollowing ? R.drawable.rounded_background_active
                : R.drawable.rounded_background_normal);
      }
    }
  }

  @Override protected void onDestroy() {
    getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackListener);
    mHandler.removeCallbacksAndMessages(null);
    mFollowUnFollowCallback = null;
    super.onDestroy();
  }

  /**
   * Get item-count header from API then update item count
   *
   * @param headers from API's response
   */
  @Override public void onResponseHeaders(Headers headers) {
    if (headers != null) {
      String itemCount = headers.get(Header.Response.TOTAL_COUNT);
      try { // catch NumberFormatException, in case their API returns something weird
        ((State) mState).itemCount = Integer.valueOf(itemCount);
        EventBus.getDefault()
            .post(new StateEvent<>(getClass().getSimpleName(), true, null, mState));
      } catch (NumberFormatException er) {
        er.printStackTrace();
      }
    }
  }

  @Override public void onTagFollowState(String tagName, Boolean isFollowing) {
    ((State) mState).isFollowing = isFollowing;
    ((State) mState).tagName = tagName;
    EventBus.getDefault().post(new StateEvent<>(getClass().getSimpleName(), true, null, mState));
  }

  @Override protected void initState() {
    mState = new State();
  }

  private static class State extends BaseState {

    // When null, it means that there is no status. May be it comes from un-authorized user.
    // We don't show follow button in that case
    @Nullable private Boolean isFollowing;

    @Nullable private Integer itemCount;

    @Nullable private String tagName;
  }
}
