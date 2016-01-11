package im.ene.lab.attiq.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.AccessToken;
import im.ene.lab.attiq.data.two.Profile;
import im.ene.lab.attiq.fragment.FeedListFragment;
import im.ene.lab.attiq.fragment.PublicStreamFragment;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileFetchedEvent;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  public static final int LOGIN_REQUEST_CODE = 0xa11d;
  public static final String EXTRA_AUTH_CALLBACK = "extra_auth_callback";
  // @Bind(R.id.header_account_background) View mHeaderBackground;
  @Bind(R.id.header_account_icon) ImageView mHeaderIcon;
  @Bind(R.id.header_account_name) TextView mHeaderName;
  @Bind(R.id.header_account_description) TextView mHeaderDescription;
  @Bind(R.id.header_auth_menu) ImageButton mAuthMenu;

  // No ButterKnife
  Toolbar mToolBar;
  View mContainer;
  ViewPager mViewPager;

  int mIconCornerRadius;
  int mIconBorderWidth;
  int mIconBorderColor;

  MenuItem mAuthMenuItem;
  private Realm mRealm;
  private View mHeaderView;
  private DrawerLayout mDrawerLayout;
  private TabLayout mMainTabs;
  private Fragment mFragment;
  private NavigationView mNavigationView;
  private Profile mMyProfile;

  @SuppressWarnings("unused")
  @OnClick(R.id.header_auth_menu) void toggleAuthMenu() {
    if (mAuthMenuItem != null) {
      if (mAuthMenuItem.isVisible()) {
        mAuthMenuItem.setVisible(false);
      } else {
        mAuthMenuItem.setVisible(true);
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mContainer = findViewById(R.id.container);
    mViewPager = (ViewPager) findViewById(R.id.view_pager);

    mIconCornerRadius = UIUtil.getDimen(this, R.dimen.header_icon_size_half);
    mIconBorderWidth = UIUtil.getDimen(this, R.dimen.dimen_unit);
    mIconBorderColor = UIUtil.getColor(this, R.color.colorPrimary);

    mToolBar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolBar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, mToolBar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    mDrawerLayout.setDrawerListener(toggle);
    toggle.syncState();

    mNavigationView = (NavigationView) findViewById(R.id.nav_actions);
    mAuthMenuItem = mNavigationView.getMenu().findItem(R.id.nav_login);
    mNavigationView.setNavigationItemSelectedListener(this);

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

    mRealm = Attiq.realm();

    mMyProfile = mRealm.where(Profile.class)
        .equalTo("token", PrefUtil.getCurrentToken()).findFirst();

    if (mMyProfile != null) {
      updateMasterUser(mMyProfile);
    }

    trySetupToolBarTabs(savedInstanceState);

    getMasterUser(PrefUtil.getCurrentToken());
  }

  private void updateMasterUser(@NonNull Profile user) {
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

  private void trySetupToolBarTabs(Bundle savedState) {
    if (savedState != null) {
      return;
    }

    trySetupToolBarTabs();
  }

  private void trySetupToolBarTabs() {
    if (mMainTabs != null) {
      mToolBar.removeView(mMainTabs);
    }

    if (UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
      // We need an user, so inflate auth menu
      mNavigationView.getMenu().setGroupVisible(R.id.group_auth, true);
      mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, false);
      mNavigationView.getMenu().setGroupVisible(R.id.group_post, false);

      if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
      }

      // There is no current active User, show Public Timeline Fragment
      mViewPager.setVisibility(View.GONE);
      mContainer.setVisibility(View.VISIBLE);
      // attach content
      mFragment = getSupportFragmentManager().findFragmentById(R.id.container);
      if (mFragment == null) {
        mFragment = PublicStreamFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, mFragment).commit();
      }
      return;
    }

    // Change menu visibility
    mNavigationView.getMenu().setGroupVisible(R.id.group_auth, false);
    mNavigationView.getMenu().setGroupVisible(R.id.group_navigation, true);
    mNavigationView.getMenu().setGroupVisible(R.id.group_post, true);

    // Check home button at startup
    mNavigationView.setCheckedItem(R.id.nav_home);

    mViewPager.setVisibility(View.VISIBLE);
    mContainer.setVisibility(View.GONE);
    // On first logging in, this Fragment has been initialized. We need to release it.
    if (mFragment != null) {
      getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
    }

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    mMainTabs = (TabLayout) LayoutInflater.from(mToolBar.getContext())
        .inflate(R.layout.toolbar_tab_layout, mToolBar, false);
    ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lp.gravity = GravityCompat.START;

    MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(pagerAdapter);
    mMainTabs.setupWithViewPager(mViewPager);

    mToolBar.addView(mMainTabs, lp);
  }

  private RealmAsyncTask mTransactionTask;

  private void getMasterUser(final String token) {
    ApiClient.me().enqueue(new Callback<Profile>() {
      @Override public void onResponse(final Response<Profile> response) {
        mMyProfile = response.body();
        if (mMyProfile != null) {
          mMyProfile.setToken(token);
          mTransactionTask = Attiq.realm().executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
              realm.copyToRealmOrUpdate(mMyProfile);
            }
          }, new Realm.Transaction.Callback() {
            @Override public void onSuccess() {
              super.onSuccess();
              EventBus.getDefault().post(new ProfileFetchedEvent(true, null, mMyProfile));
            }

            @Override public void onError(Exception e) {
              super.onError(e);
              EventBus.getDefault().post(new ProfileFetchedEvent(false,
                  new Event.Error(Event.Error.ERROR_UNKNOWN, e.getLocalizedMessage()), null));
            }
          });
        } else {
          EventBus.getDefault().post(new ProfileFetchedEvent(false,
              new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Throwable error) {
        EventBus.getDefault().post(new ProfileFetchedEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    });
  }

  @Override protected void onDestroy() {
    if (mTransactionTask != null && !mTransactionTask.isCancelled()) {
      mTransactionTask.cancel();
    }

    if (mRealm != null) {
      mRealm.close();
    }

    ButterKnife.unbind(this);
    super.onDestroy();
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if (id == R.id.nav_login) {
      login();
    } else if (id == R.id.nav_profile) {
      if (mMyProfile != null) {
        startActivity(ProfileActivity.createIntent(this, mMyProfile.getId()));
      }
    }

    mDrawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  private void login() {
    Intent intent = new Intent(this, WebViewActivity.class);
    startActivityForResult(intent, LOGIN_REQUEST_CODE);
  }

  private void logout() {
    // FIXME: 1/9/16
    Realm.deleteRealm(mRealm.getConfiguration());
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST_CODE && data != null) {
      String callback = data.getStringExtra(EXTRA_AUTH_CALLBACK);
      Uri callbackUri = Uri.parse(callback);
      final String code = callbackUri.getQueryParameter("code");
      ApiClient.accessToken(code).enqueue(new Callback<AccessToken>() {
        @Override public void onResponse(Response<AccessToken> response) {
          AccessToken accessToken = response.body();
          if (accessToken != null) {
            PrefUtil.setCurrentToken(accessToken.getToken());
            getMasterUser(accessToken.getToken());
          }
        }

        @Override public void onFailure(Throwable t) {

        }
      });
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
  public void onEventMainThread(final ProfileFetchedEvent event) {
    if (event.success) {
      if (event.profile != null && PrefUtil.getCurrentToken().equals(event.profile.getToken())) {
        if (PrefUtil.isFirstStart()) {
          PrefUtil.setFirstStart(false);
          mDrawerLayout.openDrawer(GravityCompat.START);
        }

        trySetupToolBarTabs();
        updateMasterUser(event.profile);

        mAuthMenuItem.setTitle(R.string.action_logout);
      } else {
        mAuthMenuItem.setTitle(R.string.action_login);
      }
    }
  }

  private static class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TITLES[] = {
        R.string.tab_home_public, R.string.tab_home_feed
    };

    public MainPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        return PublicStreamFragment.newInstance();
      } else if (position == 1) {
        return FeedListFragment.newInstance();
      }

      return PublicStreamFragment.newInstance();
    }

    @Override public int getCount() {
      return TITLES.length;
    }

    @Override public CharSequence getPageTitle(int position) {
      return Attiq.creator().getString(TITLES[position]);
    }
  }
}
