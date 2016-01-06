package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.zero.Profile;
import im.ene.lab.attiq.fragment.PublicStreamFragment;
import im.ene.lab.attiq.fragment.UserItemsFragment;
import im.ene.lab.attiq.fragment.UserStockedItemsFragment;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileFetchedEvent;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AnimationUtils;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import io.realm.Realm;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

  private static Intent createIntent(Context context) {
    return new Intent(context, ProfileActivity.class);
  }

  private static final String EXTRA_USER_NAME = "attiq_profile_user_name";

  public static Intent createIntent(Context context, String userName) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_USER_NAME, userName);
    return intent;
  }

  @Bind(R.id.view_pager) ViewPager mViewPager;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.toolbar_overlay) ImageView mOverLayView;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.tab_layout) TabLayout mTabLayout;
  @Bind(R.id.fab) FloatingActionButton mFab;

  private ProfileViewPagerAdapter mPagerAdapter;
  // Title support
  private AlphaForegroundColorSpan mTitleColorSpan;
  private SpannableString mSpannableTitle;
  private SpannableString mSpannableSubtitle;
  private int mToolbarLayoutOffset;
  private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener =
      new AppBarLayout.OnOffsetChangedListener() {
        @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
          mToolbarLayoutOffset = verticalOffset;
          float maxOffset = mToolBarLayout.getHeight() -
              ViewCompat.getMinimumHeight(mToolBarLayout) - mToolBarLayout.getInsetTop();
          if (maxOffset > 0) {
            float offsetFraction = Math.abs(verticalOffset) / maxOffset;
            mOverLayView.setAlpha(1.f - offsetFraction);

            float fabScale =
                AnimationUtils.DECELERATE_INTERPOLATOR.getInterpolation(
                    1.f - 0.5f * offsetFraction
                );
            mFab.setScaleX(fabScale);
            mFab.setScaleY(fabScale);
          }
        }
      };

  private Realm mRealm;
  private Profile mProfile;
  private String mUserId; // actually the User name

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    ButterKnife.bind(this);

    setSupportActionBar(mToolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // empty title at start
    setTitle("");

    mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);
    TypedValue typedValue = new TypedValue();
    mToolbar.getContext().getTheme()
        .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
    int titleColorId = typedValue.resourceId;
    mTitleColorSpan = new AlphaForegroundColorSpan(UIUtil.getColor(this, titleColorId));

    mRealm = Attiq.realm();
    mUserId = getIntent().getStringExtra(EXTRA_USER_NAME);

    mProfile = mRealm.where(Profile.class).equalTo("id", mUserId).findFirst();

    mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), mUserId);
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
    mTabLayout.setupWithViewPager(mViewPager);
  }

  private Callback<Profile> mOnUserLoaded;

  @Override protected void onResume() {
    super.onResume();
    Attiq.picasso()
        .load("https://newevolutiondesigns.com/images/freebies" +
            "/google-material-design-wallpaper-2.jpg")
        .fit().centerCrop()
        .into(mOverLayView);

    if (mProfile != null) {
      EventBus.getDefault().post(new ProfileFetchedEvent(true, null, mProfile));
    }

    mOnUserLoaded = new Callback<Profile>() {
      @Override public void onResponse(Response<Profile> response) {
        Profile profile = response.body();
        if (profile != null) {
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
    };

    ApiClient.user(mUserId).enqueue(mOnUserLoaded);
  }

  @Override protected void onPause() {
    mOnUserLoaded = null;
    super.onPause();
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  private static final String TAG = "ProfileActivity";

  public void onEventMainThread(ProfileFetchedEvent event) {
    mProfile = event.profile;
    Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");

    if (mProfile != null) {
      mSpannableTitle = new SpannableString(mProfile.getId());
      updateTitle();
    }
  }

  private void updateTitle() {
    float titleAlpha =
        mToolBarLayout.shouldTriggerScrimOffset(mToolbarLayoutOffset) ? 1.f : 0.f;
    mTitleColorSpan.setAlpha(titleAlpha);
    // title
    mSpannableTitle.setSpan(mTitleColorSpan, 0, mSpannableTitle.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mToolbar.setTitle(mSpannableTitle);

    // subtitle
    if (mSpannableSubtitle != null) {
      mSpannableSubtitle.setSpan(mTitleColorSpan, 0, mSpannableSubtitle.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      mToolbar.setSubtitle(mSpannableSubtitle);
    }
  }

  private static class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String mUserId;

    public ProfileViewPagerAdapter(FragmentManager fm, String userId) {
      super(fm);
      this.mUserId = userId;
    }

    @Override public Fragment getItem(int position) {
      if (position == 0) {
        return UserItemsFragment.newInstance(mUserId);
      } else if (position == 1) {
        return UserStockedItemsFragment.newInstance(mUserId);
      }

      return PublicStreamFragment.newInstance();
    }

    @Override public int getCount() {
      return 3;
    }

    @Override public CharSequence getPageTitle(int position) {
      if (position == 0) {
        return "投稿リスト";
      } else if (position == 1) {
        return "ストック";
      }

      return "Tab: " + position;
    }
  }
}