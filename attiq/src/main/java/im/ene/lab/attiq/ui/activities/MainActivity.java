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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.SuccessCallback;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.two.AccessToken;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.data.model.zero.FeedItem;
import im.ene.lab.attiq.services.ParseUserService;
import im.ene.lab.attiq.ui.fragment.FeedListFragment;
import im.ene.lab.attiq.ui.fragment.HistoryFragment;
import im.ene.lab.attiq.ui.fragment.PublicStreamFragment;
import im.ene.lab.attiq.ui.fragment.UserStockedItemsFragment;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileEvent;
import im.ene.lab.support.widget.SmoothActionBarDrawerToggle;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  public static final String EXTRA_AUTH_CALLBACK = "extra_auth_callback";

  /**
   * Request codes
   */
  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_SEARCH = 1 << 1;

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
  private View mMainContainer;
  private ViewPager mViewPager;
  private View mHeaderView;
  private DrawerLayout mDrawerLayout;
  private SmoothActionBarDrawerToggle mDrawerToggle;
  private TabLayout mMainTabs;
  private Fragment mFragment;
  private NavigationView mNavigationView;

  // Utils
  private RealmAsyncTask mTransactionTask;
  private Callback<AccessToken> mOnTokenCallback = new SuccessCallback<AccessToken>() {
    @Override public void onResponse(Response<AccessToken> response) {
      AccessToken accessToken = response.body();
      if (accessToken != null) {
        PrefUtil.setCurrentToken(accessToken.getToken());
        getMasterUser(accessToken.getToken());
      }
    }
  };

  @SuppressWarnings("unused")
  @OnClick(R.id.header_auth_menu) void toggleAuthMenu() {
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mMainContainer = findViewById(R.id.container);
    mViewPager = (ViewPager) findViewById(R.id.view_pager);
    mViewPager.setOffscreenPageLimit(3);

    mIconCornerRadius = UIUtil.getDimen(this, R.dimen.header_icon_size_half);
    mIconBorderWidth = UIUtil.getDimen(this, R.dimen.dimen_unit);
    mIconBorderColor = ContextCompat.getColor(this, R.color.colorPrimary);

    mToolBar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolBar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerToggle = new SmoothActionBarDrawerToggle(
        this, mDrawerLayout, mToolBar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close) {
      @Override protected void onDrawerClosedByMenu(View drawerView, @NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_login) {
          if (UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
            login();
          } else {
            logout();
          }
        } else if (id == R.id.nav_profile) {
          if (mMyProfile != null) {
            startActivity(ProfileActivity.createIntent(MainActivity.this, mMyProfile.getId()));
          }
        } else if (id == R.id.nav_setting) {
          startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
        mHeaderView.setPadding(
            mHeaderView.getPaddingLeft(),
            mHeaderView.getPaddingTop() + UIUtil.getStatusBarHeight(this),
            mHeaderView.getPaddingRight(),
            mHeaderView.getPaddingBottom()
        );
      }
    }

    if (mHeaderView != null) {
      ButterKnife.bind(this, mHeaderView);
    }

    if (mMyProfile != null) {
      updateMasterUser(mMyProfile);
      mState.isAuthorized = true;
    } else {
      mState.isAuthorized = false;
    }

    EventBus.getDefault().post(new StateEvent<>(true, null, mState));

    trySetupToolBarTabs(savedInstanceState);
    getMasterUser(PrefUtil.getCurrentToken());
  }

  private void login() {
    Intent intent = new Intent(this, AuthActivity.class);
    startActivityForResult(intent, REQUEST_CODE_LOGIN);
  }

  private void logout() {
    // Show a dialog
    new AlertDialog.Builder(this)
        .setMessage(R.string.logout_confirm)
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
            mRealm.commitTransaction();

            mMyProfile = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              CookieManager.getInstance().removeAllCookies(null);
            } else {
              CookieManager.getInstance().removeAllCookie();
            }

            // Logout from Parse
            ParseUser.logOutInBackground(new LogOutCallback() {
              @Override public void done(ParseException e) {
                Log.d("Main#logout()", "done() called with: " + "e = [" + e + "]");
                // Re-anonymous this app
                if (ParseUser.getCurrentUser() == null) {
                  ParseUser.enableAutomaticUser();
                }
              }
            });
            PrefUtil.setCurrentToken(null);
            EventBus.getDefault().post(new ProfileEvent(true, null, null));
          }
        }).create().show();
  }

  private void updateMasterUser(Profile user) {
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
            .fit().centerInside()
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
            .into(mHeaderIcon);
      }
    }
  }

  private void trySetupToolBarTabs(Bundle savedState) {
    if (savedState != null) {
      return;
    }

    trySetupToolBarTabs();
  }

  private void getMasterUser(final String token) {
    ApiClient.me().enqueue(new Callback<Profile>() {
      @Override public void onResponse(final Response<Profile> response) {
        mMyProfile = response.body();
        if (mMyProfile != null) {
          mMyProfile.setToken(token);
          // save to Realm
          mTransactionTask = Attiq.realm().executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
              realm.copyToRealmOrUpdate(mMyProfile);
            }
          }, new Realm.Transaction.Callback() {
            @Override public void onSuccess() {
              super.onSuccess();
              EventBus.getDefault().post(new ProfileEvent(true, null, mMyProfile));
            }

            @Override public void onError(Exception e) {
              super.onError(e);
              EventBus.getDefault().post(new ProfileEvent(false,
                  new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), null));
            }
          });
        } else {
          EventBus.getDefault().post(new ProfileEvent(false,
              new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Throwable error) {
        EventBus.getDefault().post(new ProfileEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    });
  }

  private void trySetupToolBarTabs() {
    if (mMainTabs != null) {
      mToolBar.removeView(mMainTabs);
    }

    if (UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
      // We need an user, so inflate auth menu
      mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_up);
      mNavigationView.getMenu().setGroupVisible(R.id.group_auth, true);
      mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, false);
      mNavigationView.getMenu().setGroupVisible(R.id.group_post, false);

      if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
      }

      // There is no current active User, show Public Timeline Fragment
      mViewPager.setVisibility(View.GONE);
      mMainContainer.setVisibility(View.VISIBLE);
      // attach content
      mFragment = getSupportFragmentManager().findFragmentById(R.id.container);
      if (mFragment == null) {
        mFragment = PublicStreamFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, mFragment).commit();
      }
    } else {
      // Change menu visibility
      mAuthMenu.setImageResource(R.drawable.ic_arrow_drop_down);
      mNavigationView.getMenu().setGroupVisible(R.id.group_auth, false);
      mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, true);
      mNavigationView.getMenu().setGroupVisible(R.id.group_post, true);

      // Check home button at startup
      mNavigationView.setCheckedItem(R.id.nav_home);

      // On first logging in, this Fragment has been initialized. We need to release it.
      if (mFragment != null) {
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
      }
      mMainContainer.setVisibility(View.GONE);
      mViewPager.setVisibility(View.VISIBLE);

      if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
      }

      MainPagerAdapter pagerAdapter = new MainPagerAdapter(
          mMyProfile != null ? mMyProfile.getId() : null,
          getSupportFragmentManager()
      );
      mViewPager.setAdapter(pagerAdapter);

      mMainTabs = (TabLayout) LayoutInflater.from(mToolBar.getContext())
          .inflate(R.layout.toolbar_tab_layout, mToolBar, false);
      mMainTabs.setupWithViewPager(mViewPager);

      ActionBar.LayoutParams params = new ActionBar.LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      params.gravity = GravityCompat.START;

      mToolBar.addView(mMainTabs, params);
    }
  }

  @Override protected void onDestroy() {
    if (mTransactionTask != null && !mTransactionTask.isCancelled()) {
      mTransactionTask.cancel();
    }

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
      startActivityForResult(SearchActivity.createStartIntent(this, loc[0], loc[0] +
              (searchMenuView.getWidth() / 2)), REQUEST_CODE_SEARCH,
          ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override public boolean onNavigationItemSelected(MenuItem item) {
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

  @Override
  public void onBackPressed() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(final ProfileEvent event) {
    if (event.success) {
      trySetupToolBarTabs();
      updateMasterUser(event.profile);

      if (event.profile != null && PrefUtil.getCurrentToken().equals(event.profile.getToken())) {
        // Update User to Parse
        ParseUserService.Argument authArgument = new ParseUserService.Argument(event.profile);
        Intent authService = new Intent(this, ParseUserService.class);
        authService.putExtras(authArgument.getArguments());
        startService(authService);

        if (PrefUtil.isFirstStart()) {
          PrefUtil.setFirstStart(false);
          Toast.makeText(MainActivity.this, "おはようございます", Toast.LENGTH_SHORT).show();
          mDrawerLayout.openDrawer(GravityCompat.START);
        }

        mState.isAuthorized = true;
      } else {
        mState.isAuthorized = false;
      }

      EventBus.getDefault().post(new StateEvent<>(true, null, mState));
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(StateEvent event) {
    if (event.state.isAuthorized) {
      mAuthMenuItem.setTitle(R.string.action_logout);
      mMyPageMenuItem.setEnabled(true);
    } else {
      mAuthMenuItem.setTitle(R.string.action_login);
      mMyPageMenuItem.setEnabled(false);
    }
  }

  private static class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TITLES[] = {
        R.string.tab_home_public,
        R.string.tab_home_feed,
        R.string.tab_title_stocks,
        R.string.tab_title_history
    };
    private final String mUserId;

    public MainPagerAdapter(String userId, FragmentManager fm) {
      super(fm);
      mUserId = userId;
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        return PublicStreamFragment.newInstance();
      } else if (position == 1) {
        return FeedListFragment.newInstance();
      } else if (position == 2) {
        return UserStockedItemsFragment.newInstance(mUserId);
      } else if (position == 3) {
        return HistoryFragment.newInstance();
      }

      return PublicStreamFragment.newInstance();
    }

    @Override public int getCount() {
      if (mUserId != null) {
        return TITLES.length;
      } else {
        return TITLES.length - 2;
      }
    }

    @Override public CharSequence getPageTitle(int position) {
      // Directly use Application Context here. There is no style or theme here so we don't care
      return Attiq.creator().getString(TITLES[position]);
    }
  }

  private static class State extends BaseActivity.BaseState {
    public State() {
      super();
    }
  }

}
