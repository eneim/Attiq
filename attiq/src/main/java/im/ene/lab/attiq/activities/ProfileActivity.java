package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Profile;
import im.ene.lab.attiq.fragment.PublicStreamFragment;
import im.ene.lab.attiq.fragment.UserItemsFragment;
import im.ene.lab.attiq.fragment.UserStockedItemsFragment;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.DocumentEvent;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileFetchedEvent;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AnimationUtils;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import im.ene.support.design.widget.FabImageView;
import io.realm.Realm;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Random;

public class ProfileActivity extends BaseActivity {

  private static final String EXTRA_USER_NAME = "attiq_profile_user_name";
  private static final Random sRandom = new Random();
  private static final String TAG = "ProfileActivity";
  // Used to keep track of index
  private static final int WEBSITE_BUTTON_INDEX = 0;
  private static final int FACEBOOK_BUTTON_INDEX = 1;
  private static final int TWITTER_BUTTON_INDEX = 2;
  private static final int GITHUB_BUTTON_INDEX = 3;
  private static final int LINKEDIN_BUTTON_INDEX = 4;
  private final int[] BACKGROUNDS = {
      R.drawable.gradient_fresh_turboscent,
      R.drawable.gradient_lizard,
      R.drawable.gradient_nightwalk,
      R.drawable.gradient_turpoise_flow,
      R.drawable.gradient_vine
  };
  @Bind(R.id.view_pager) ViewPager mViewPager;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.toolbar_overlay) View mOverlayContainer;
  @Bind(R.id.toolbar_overlay_image) ImageView mOverlayView;
  @Bind({R.id.divider_1, R.id.divider_2}) List<View> mOverlayDividers;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.tab_layout) TabLayout mTabLayout;
  @Bind(R.id.fab) FabImageView mProfileFabImage;
  @Bind(R.id.profile_social_buttons) LinearLayout mSocialButtonContainer;

  @Bind(R.id.profile_name) TextView mProfileName;
  @Bind(R.id.profile_description) TextView mProfileDescription;

  // Others
  // @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
  @BindDimen(R.dimen.dimen_unit) int mImageBorderWidth;
  @BindColor(R.color.colorAccent) int mImageBorderColor;
  @BindDimen(R.dimen.profile_image_size) int mProfileImageSize;
  @BindDimen(R.dimen.profile_image_size_half) int mProfileImageSizeHalf;
  @Bind({
      R.id.profile_social_website,
      R.id.profile_social_facebook,
      R.id.profile_social_twitter,
      R.id.profile_social_github,
      R.id.profile_social_linkedin
  }) ImageButton[] mSocialButtons;
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
            mOverlayContainer.setAlpha(1.f - offsetFraction);
            float fabScale =
                AnimationUtils.DECELERATE_INTERPOLATOR.getInterpolation(
                    1.f - 0.5f * offsetFraction
                );
            mProfileFabImage.setScaleX(fabScale);
            mProfileFabImage.setScaleY(fabScale);
            for (View divider : mOverlayDividers) {
              divider.setAlpha(1 - fabScale);
            }
          }
        }
      };
  private Realm mRealm;
  private Profile mProfile;
  private String mUserId; // actually the User name
  private Callback<Profile> mOnUserLoaded;

  public static Intent createIntent(Context context, String userName) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_USER_NAME, userName);
    return intent;
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, ProfileActivity.class);
  }

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

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    mOverlayView.setBackgroundResource(BACKGROUNDS[sRandom.nextInt(BACKGROUNDS.length)]);

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

  public void onEventMainThread(ProfileFetchedEvent event) {
    mProfile = event.profile;
    Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");

    if (mProfile != null) {
      mProfileName.setText(mProfile.getId() + " | " + mProfile.getItemsCount() + "投稿");

      StringBuilder description = new StringBuilder();

      if (!UIUtil.isEmpty(mProfile.getName())) {
        description.append(mProfile.getName()).append(", ");
      }

      if (!UIUtil.isEmpty(mProfile.getLocation())) {
        description.append(mProfile.getLocation()).append(System.lineSeparator());
      }

      if (!UIUtil.isEmpty(mProfile.getOrganization())) {
        description.append(mProfile.getOrganization()).append(", ");
      }

      if (!UIUtil.isEmpty(mProfile.getDescription())) {
        description.append(System.lineSeparator())
            .append(mProfile.getDescription());
      }

      mProfileDescription.setText(description.toString());

      Attiq.picasso()
          .load(mProfile.getProfileImageUrl())
          .placeholder(R.mipmap.ic_launcher)
          .error(R.mipmap.ic_launcher)
          .resize(mProfileImageSize, 0)
          .transform(new RoundedTransformation(
              mImageBorderWidth, mImageBorderColor, mProfileImageSizeHalf))
          .into(mProfileFabImage);

      mSpannableTitle = new SpannableString(mProfile.getId());
      if (!UIUtil.isEmpty(mProfile.getDescription())) {
        mSpannableSubtitle = new SpannableString(mProfile.getDescription());
      }
      updateTitle();
      updateSocialButtons();
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

  private void updateSocialButtons() {
    if (mProfile == null || mSocialButtonContainer == null) {
      return;
    }

    for (View button : mSocialButtons) {
      button.setVisibility(View.GONE);
    }

    if (!UIUtil.isEmpty(mProfile.getWebsiteUrl())) {
      mSocialButtons[WEBSITE_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getFacebookId())) {
      mSocialButtons[FACEBOOK_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getTwitterScreenName())) {
      mSocialButtons[TWITTER_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getGithubLoginName())) {
      mSocialButtons[GITHUB_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getLinkedinId())) {
      mSocialButtons[LINKEDIN_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }
  }

  @OnClick(R.id.profile_social_website) void openWebsite() {
    if (mProfile != null) {
      UIUtil.openWebsite(this, mProfile.getWebsiteUrl());
    }
  }

  @OnClick(R.id.profile_social_facebook) void openFacebook() {
    if (mProfile != null) {
      UIUtil.openFacebookUser(this, mProfile.getFacebookId());
    }
  }

  @OnClick(R.id.profile_social_twitter) void openTwitter() {
    if (mProfile != null) {
      UIUtil.openTwitterUser(this, mProfile.getTwitterScreenName());
    }
  }

  @OnClick(R.id.profile_social_github) void openGithub() {
    if (mProfile != null) {
      UIUtil.openGithubUser(this, mProfile.getGithubLoginName());
    }
  }

  @OnClick(R.id.profile_social_linkedin) void openLinkedin() {
    if (mProfile != null) {
      UIUtil.openLinkedinUser(this, mProfile.getLinkedinId());
    }
  }

  public void onEventMainThread(DocumentEvent event) {
    Document document = event.document;
    if (document != null) {
      Log.d(TAG, "onEventMainThread: " + document.html());
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
      return 2;
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