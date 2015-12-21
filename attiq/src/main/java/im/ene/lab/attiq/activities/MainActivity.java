package im.ene.lab.attiq.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.api.open.Profile;
import im.ene.lab.attiq.data.api.v2.response.AccessToken;
import im.ene.lab.attiq.data.event.Event;
import im.ene.lab.attiq.data.event.ProfileFetchedEvent;
import im.ene.lab.attiq.fragment.PublicStreamFragment;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  public static final int LOGIN_REQUEST_CODE = 0xa11d;
  public static final String EXTRA_AUTH_CALLBACK = "extra_auth_callback";

  private Realm mRealm;
  private View mHeaderView;

  private DrawerLayout mDrawerLayout;

  private Fragment mFragment;

  @Bind(R.id.header_account_background) View mHeaderBackground;
  @Bind(R.id.header_account_icon) ImageView mHeaderIcon;
  @Bind(R.id.header_account_name) TextView mHeaderName;
  @Bind(R.id.header_account_description) TextView mHeaderDescription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    mDrawerLayout.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    NavigationMenuItemView test;

    mRealm = Attiq.realm();

    if (navigationView.getHeaderCount() > 0) {
      mHeaderView = navigationView.getHeaderView(0);
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

    Profile user = mRealm.where(Profile.class).findFirst();
    if (user == null) {
      user = mRealm.where(Profile.class)
          .equalTo("token", PrefUtil.getCurrentToken()).findFirst();
    }

    if (user != null) {
      updateMasterUser(user);
    }

    // attach content
    mFragment = getSupportFragmentManager().findFragmentById(R.id.container);
    if (mFragment == null) {
      mFragment = PublicStreamFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, mFragment).commit();
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

  private void login() {
    Intent intent = new Intent(this, WebViewActivity.class);
    startActivityForResult(intent, LOGIN_REQUEST_CODE);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
      login();
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    mDrawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST_CODE && data != null) {
      String callback = data.getStringExtra(EXTRA_AUTH_CALLBACK);
      Uri callbackUri = Uri.parse(callback);
      final String code = callbackUri.getQueryParameter("code");
      ApiClient.accessToken(code).enqueue(new Callback<AccessToken>() {
        @Override public void onResponse(Response<AccessToken> response, Retrofit retrofit) {
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

  private void getMasterUser(final String token) {
    ApiClient.me().enqueue(new Callback<Profile>() {
      @Override public void onResponse(Response<Profile> response, Retrofit retrofit) {
        Profile profile = response.body();
        if (profile != null) {
          profile.setToken(token);
          Realm realm = Attiq.realm();
          realm.beginTransaction();
          realm.copyToRealmOrUpdate(profile);
          realm.commitTransaction();
          realm.close();
          EventBus.getDefault().post(new ProfileFetchedEvent(true, null, profile));
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

  private void updateMasterUser(@NonNull Profile user) {
    if (mHeaderName != null) {
      mHeaderName.setText(user.getName());
    }

    if (mHeaderDescription != null) {
      mHeaderDescription.setText(user.getDescription());
    }

    if (mHeaderIcon != null && !UIUtil.isEmpty(user.getProfileImageUrl())) {
      Attiq.picasso()
          .load(user.getProfileImageUrl())
          .placeholder(android.R.drawable.sym_def_app_icon)
          .fit().centerInside()
          .into(mHeaderIcon);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    getMasterUser(PrefUtil.getCurrentToken());
  }

  public void onEventMainThread(final ProfileFetchedEvent event) {
    if (event.success) {
      if (PrefUtil.isFirstStart()) {
        PrefUtil.setFirstStart(false);
        mDrawerLayout.openDrawer(GravityCompat.START);
      }
      if (event.profile != null) {
        updateMasterUser(event.profile);
      }
    }
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  private static final String TAG = "MainActivity";

}
