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

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.SuccessCallback;
import im.ene.lab.attiq.data.model.local.ReadArticle;
import im.ene.lab.attiq.data.model.local.StockArticle;
import im.ene.lab.attiq.data.model.two.AccessToken;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.ui.fragment.AuthorizedUserHomeFragment;
import im.ene.lab.attiq.ui.fragment.HistoryFragment;
import im.ene.lab.attiq.ui.fragment.PublicUserHomeFragment;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.AccessTokenEvent;
import im.ene.lab.attiq.util.event.ProfileEvent;
import im.ene.lab.support.widget.SmoothActionBarDrawerToggle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener, PublicUserHomeFragment.Callback,
    AuthorizedUserHomeFragment.Callback, HistoryFragment.Callback {

  private static final String TAG = "HomeActivity";

  public static final String EXTRA_AUTH_CALLBACK = "extra_auth_callback";

  /**
   * Request codes
   */
  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_SEARCH = 1 << 1;

  /**
   * Fragment names
   */
  private final String FRAGMENT_HOME_PUBLIC = "fragment_home_public";
  private final String FRAGMENT_HOME_AUTHORIZED = "fragment_home_authorized";
  private final String FRAGMENT_HISTORY = "fragment_history";

  /**
   * Header child views
   */
  @Bind(R.id.header_account_icon) ImageView mHeaderIcon;
  @Bind(R.id.header_account_name) TextView mHeaderName;
  @Bind(R.id.header_account_description) TextView mHeaderDescription;
  @Bind(R.id.header_auth_menu) ImageButton mAuthMenu;

  int mIconCornerRadius;
  int mIconBorderWidth;
  int mIconBorderColor;

  // No ButterKnife
  private Toolbar mToolBar;
  private MenuItem mAuthMenuItem;
  private MenuItem mMyPageMenuItem;
  private View mHeaderView;
  private DrawerLayout mDrawerLayout;
  private SmoothActionBarDrawerToggle mDrawerToggle;
  private TabLayout mMainTabs;
  private NavigationView mNavigationView;

  private Callback<AccessToken> mOnTokenCallback = new SuccessCallback<AccessToken>() {
    @Override public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
      AccessToken accessToken = response.body();
      if (accessToken != null) {
        EventBus.getDefault().post(new AccessTokenEvent(TAG, true, null, accessToken));
      }
    }
  };

  @SuppressWarnings("unused") @OnClick(R.id.header_auth_menu) void toggleAuthMenu() {
    if (mAuthMenuItem != null) {
      if (mAuthMenuItem.isVisible()) {
        mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_down);
        mAuthMenuItem.setVisible(false);
      } else {
        mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_up);
        mAuthMenuItem.setVisible(true);
      }
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    mIconCornerRadius = UIUtil.getDimen(this, R.dimen.header_icon_size_half);
    mIconBorderWidth = UIUtil.getDimen(this, R.dimen.dimen_unit);
    mIconBorderColor = ContextCompat.getColor(this, R.color.colorPrimary);

    mToolBar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolBar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolBar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
      @Override protected void onDrawerClosedByMenu(View drawerView, @NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
          case R.id.nav_login:
            if (UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
              login();
            } else {
              logout();
            }
            break;
          case R.id.nav_profile:
            if (mMyProfile != null) {
              startActivity(ProfileActivity.createIntent(HomeActivity.this, mMyProfile.getId()));
            }
            break;
          case R.id.nav_setting:
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            break;
          case R.id.nav_history:
            showHistory();
            break;
          case R.id.nav_home:
            showHome();
            break;
          case R.id.nav_about:
            // jump to Github
            String url = "https://github.com/eneim/Attiq";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            break;
        }
      }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);
    mDrawerToggle.syncState();

    mNavigationView = (NavigationView) findViewById(R.id.nav_actions);
    mAuthMenuItem = mNavigationView.getMenu().findItem(R.id.nav_login);
    mMyPageMenuItem = mNavigationView.getMenu().findItem(R.id.nav_profile);
    mNavigationView.setNavigationItemSelectedListener(this);

    if (mState.isAuthorized) {
      mMyPageMenuItem.setEnabled(true);
    } else {
      mMyPageMenuItem.setEnabled(false);
    }

    if (mNavigationView.getHeaderCount() > 0) {
      mHeaderView = mNavigationView.getHeaderView(0);
      // update padding top by status bar height.
      // we expect 24dp, but in some on devices, it was 25dp.
      if (mHeaderView != null) {
        mHeaderView.setPadding(mHeaderView.getPaddingLeft(),
            mHeaderView.getPaddingTop() + UIUtil.getStatusBarHeight(this),
            mHeaderView.getPaddingRight(), mHeaderView.getPaddingBottom());
      }
    }

    if (mHeaderView != null) {
      ButterKnife.bind(this, mHeaderView);
    }

    updateMasterUserInfo(mMyProfile);

    if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
      mNavigationView.setCheckedItem(R.id.nav_home);
      updateMasterUserData(mMyProfile);
    }
  }

  private void login() {
    Intent intent = new Intent(this, AuthActivity.class);
    startActivityForResult(intent, REQUEST_CODE_LOGIN);
  }

  private void logout() {
    // Show a dialog
    new AlertDialog.Builder(this).setMessage(R.string.logout_confirm)
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            if (dialog != null) {
              dialog.dismiss();
            }
          }
        })
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            if (dialog != null) {
              dialog.dismiss();
            }

            mRealm.beginTransaction();
            mRealm.clear(Profile.class);
            mRealm.clear(FeedItem.class);
            mRealm.clear(StockArticle.class);
            mRealm.clear(ReadArticle.class);
            mRealm.commitTransaction();

            mMyProfile = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              CookieManager.getInstance().removeAllCookies(null);
            } else {
              CookieManager.getInstance().removeAllCookie();
            }

            PrefUtil.setCurrentToken(null);
            PrefUtil.setFirstStart(true);
            EventBus.getDefault()
                .post(new ProfileEvent(HomeActivity.class.getSimpleName(), true, null, null));
          }
        })
        .create()
        .show();
  }

  private Fragment mHomeFragment;
  private Fragment mHistoryFragment;
  private String mCurrentFragmentName;

  @NonNull private String setHomeFragmentName(Profile profile) {
    if (profile != null) {
      return FRAGMENT_HOME_AUTHORIZED;
    } else {
      return FRAGMENT_HOME_PUBLIC;
    }
  }

  private void updateMasterUserData(Profile user) {
    final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // 1. detach History fragment if there is
    if ((mHistoryFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_HISTORY))
        != null) {
      transaction.detach(mHistoryFragment);
    }

    // 2. attach or create home fragment
    mCurrentFragmentName = setHomeFragmentName(user);
    if ((mHomeFragment = getSupportFragmentManager()  //
        .findFragmentByTag(mCurrentFragmentName)) != null) {
      transaction.attach(mHomeFragment);
    } else {
      if (FRAGMENT_HOME_AUTHORIZED.equals(mCurrentFragmentName)) {
        mHomeFragment = AuthorizedUserHomeFragment.newInstance(user.getId());
      } else {
        mHomeFragment = PublicUserHomeFragment.newInstance();
      }

      transaction.replace(R.id.container, mHomeFragment, mCurrentFragmentName);
    }

    // 3. commit transaction
    transaction.commit();
  }

  private void showHistory() {
    final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // 1. detach current Home fragment if there is
    mCurrentFragmentName = setHomeFragmentName(mMyProfile);
    if ((mHomeFragment = getSupportFragmentManager()  //
        .findFragmentByTag(mCurrentFragmentName)) != null) {
      transaction.detach(mHomeFragment);
    }

    // 2. attach or create History fragment
    if ((mHistoryFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_HISTORY))
        != null) {
      transaction.attach(mHistoryFragment);
    } else {
      mHistoryFragment = HistoryFragment.newInstance();
      transaction.replace(R.id.container, mHistoryFragment, FRAGMENT_HISTORY);
    }

    // 3. commit transaction
    transaction.commit();
  }

  private void showHome() {
    updateMasterUserData(mMyProfile);
  }

  private void updateMasterUserInfo(@Nullable Profile user) {
    if (user == null) {
      if (mHeaderName != null) {
        mHeaderName.setText(R.string.text_welcome);
      }

      if (mHeaderDescription != null) {
        mHeaderDescription.setText(R.string.text_attiq_intro);
      }

      if (mHeaderIcon != null) {
        mHeaderIcon.setImageResource(R.drawable.blank_profile_icon_large);
      }
    } else {
      if (mHeaderName != null) {
        mHeaderName.setText(user.getName());
      }

      if (mHeaderDescription != null) {
        mHeaderDescription.setText(user.getId());
      }

      if (mHeaderIcon != null && !UIUtil.isEmpty(user.getProfileImageUrl())) {
        Attiq.picasso()
            .load(user.getProfileImageUrl())
            .placeholder(R.drawable.blank_profile_icon_large)
            .fit()
            .centerInside()
            .transform(
                new RoundedTransformation(mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
            .into(mHeaderIcon);
      }
    }
  }

  @Override protected void onDestroy() {
    mOnTokenCallback = null;
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  @Override protected void initState() {
    mState = new State();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (R.id.action_search == item.getItemId()) {
      // get the icon's location on screen to pass through to the search screen
      View searchMenuView = mToolBar.findViewById(R.id.action_search);
      int[] loc = new int[2];
      searchMenuView.getLocationOnScreen(loc);
      startActivityForResult(
          SearchActivity.createStartIntent(this, loc[0], loc[0] + (searchMenuView.getWidth() / 2)),
          REQUEST_CODE_SEARCH, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody") @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    mDrawerToggle.closeDrawerUsingMenu(mDrawerLayout, GravityCompat.START, item);
    return true;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_CODE_SEARCH:
        // reset the search icon which we hid
        View searchMenuView = mToolBar.findViewById(R.id.action_search);
        if (searchMenuView != null) {
          searchMenuView.setAlpha(1.f);
        }
        break;
      case REQUEST_CODE_LOGIN:
        if (resultCode == RESULT_OK && data != null) {
          String callback = data.getStringExtra(EXTRA_AUTH_CALLBACK);
          Uri callbackUri = Uri.parse(callback);
          final String code = callbackUri.getQueryParameter("code");
          ApiClient.accessToken(code).enqueue(mOnTokenCallback);
        }
        break;
      default:
        break;
    }
  }

  @Override public void onBackPressed() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("unused") public void onEventMainThread(final ProfileEvent event) {
    if (event.success) {
      updateMasterUserInfo(event.profile);
      updateMasterUserData(event.profile);

      if (event.profile != null && PrefUtil.getCurrentToken().equals(event.profile.getToken())) {
        if (PrefUtil.isFirstStart()) {
          PrefUtil.setFirstStart(false);
          Toast.makeText(HomeActivity.this, "おはようございます", Toast.LENGTH_SHORT).show();
          mDrawerLayout.openDrawer(GravityCompat.START);
        }

        mState.isAuthorized = true;
      } else {
        mState.isAuthorized = false;
      }

      EventBus.getDefault().post(new StateEvent<>(getClass().getSimpleName(), true, null, mState));
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @SuppressWarnings("unused") public void onEventMainThread(StateEvent event) {
    if (event.state.isAuthorized) {
      mAuthMenuItem.setTitle(R.string.action_logout);
      mMyPageMenuItem.setEnabled(true);
    } else {
      mAuthMenuItem.setTitle(R.string.action_login);
      mMyPageMenuItem.setEnabled(false);
    }
  }

  @Override public void onUserHomeCreated(ViewPager viewPager) {
    if (mMainTabs != null) {
      mToolBar.removeView(mMainTabs);
    }
    // Change menu visibility
    mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_down);
    mNavigationView.getMenu().setGroupVisible(R.id.group_auth, false);
    mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, true);
    mNavigationView.getMenu().setGroupVisible(R.id.group_post, true);
    mAuthMenuItem.setTitle(R.string.action_logout);
    mMyPageMenuItem.setEnabled(true);

    // Check home button at startup

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    mMainTabs = (TabLayout) LayoutInflater.from(mToolBar.getContext())
        .inflate(R.layout.toolbar_tab_layout, mToolBar, false);
    mMainTabs.setupWithViewPager(viewPager);

    ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.gravity = GravityCompat.START;

    mToolBar.addView(mMainTabs, params);
  }

  @Override public void onTimelineCreated(View root) {
    if (mMainTabs != null) {
      mToolBar.removeView(mMainTabs);
    }
    // We need an user, so inflate auth menu
    mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_up);
    mNavigationView.getMenu().setGroupVisible(R.id.group_auth, true);
    mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, false);
    mNavigationView.getMenu().setGroupVisible(R.id.group_post, false);
    mAuthMenuItem.setTitle(R.string.action_login);
    mMyPageMenuItem.setEnabled(false);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(R.string.app_name);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
  }

  @Override public void onHistoryShown(View root) {
    if (mMainTabs != null) {
      mToolBar.removeView(mMainTabs);
    }

    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(R.string.menu_item_history);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
  }

  private static class State extends BaseState {
    public State() {
      super();
    }
  }

  @Override protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ? R.style.Attiq_Theme_Dark_NoActionBar
        : R.style.Attiq_Theme_Light_NoActionBar;
  }
}
