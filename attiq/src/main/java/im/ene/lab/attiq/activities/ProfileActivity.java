package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.fragment.DummyFragment;
import im.ene.lab.attiq.fragment.UserItemsFragment;
import im.ene.lab.attiq.fragment.UserStockedItemsFragment;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileFetchedEvent;
import im.ene.lab.attiq.util.event.UserFetchedEvent;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import im.ene.lab.attiq.widgets.UserInfoRowTextView;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import im.ene.support.design.widget.FabImageButton;
import im.ene.support.design.widget.MathUtils;
import io.realm.Realm;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

  private static final int MESSAGE_ACTION_FOLLOW = 1;

  private static final String EXTRA_USER_NAME = "attiq_profile_user_name";
  private static final String TAG = "ProfileActivity";
  // Used to keep track of index
  private static final int WEBSITE_BUTTON_INDEX = 0;
  private static final int FACEBOOK_BUTTON_INDEX = 1;
  private static final int TWITTER_BUTTON_INDEX = 2;
  private static final int GITHUB_BUTTON_INDEX = 3;
  private static final int LINKEDIN_BUTTON_INDEX = 4;
  private static final int HANDLER_DELAY = 200;

  @Bind(R.id.view_pager) ViewPager mViewPager;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.toolbar_overlay) View mOverlayContainer;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.tab_layout) TabLayout mTabLayout;
  @Bind(R.id.profile_image) FabImageButton mProfileImage;
  @Bind(R.id.social_button_container) View mSocialButtonContainer;
  @Bind(R.id.profile_social_buttons) LinearLayout mSocialButtonView;
  @Bind(R.id.text_action_follow) TextView mBtnFollow;
  @Bind(R.id.profile_name) TextView mProfileName;
  @Bind(R.id.profile_description) TextView mProfileDescription;
  @Bind(R.id.description_container) LinearLayout mDescription;

  @Bind(R.id.user_item_count) TextView mItemCount;
  @Bind(R.id.user_item_count_quantity) TextView mItemQuantity;

  @Bind(R.id.user_follower_count) TextView mFollowerCount;
  @Bind(R.id.user_follower_count_quantity) TextView mFollowerQuantity;

  @Bind(R.id.user_following_count) TextView mFollowingCount;
  @Bind(R.id.user_following_count_quantity) TextView mFollowingQuantity;

  // Others
  // @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
  @BindDimen(R.dimen.item_padding_half) int mImageBorderWidth;
  @BindColor(android.R.color.white) int mImageBorderColor;
  @BindDimen(R.dimen.profile_image_size) int mProfileImageSize;
  @BindDimen(R.dimen.item_padding) int mProfileImageRadius;
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
          float maxOffset = mToolBarLayout.getHeight() - mToolBarLayout.getScrimOffsetBound();
          if (maxOffset > 0) {
            float offsetFraction = Math.abs(verticalOffset) / maxOffset;
            offsetFraction = MathUtils.constrain(offsetFraction, 0.f, 1.f);
            mOverlayContainer.setAlpha(1.f - offsetFraction);
            mProfileImage.setAlpha(1.f - offsetFraction);
          }
        }
      };
  private Realm mRealm;
  private Profile mRefUser;
  private User mUser;
  private State mState = new State();
  private String mUserId; // actually the User name
  private Callback<Void> mOnFollowStateCallback;
  private Callback<Void> mOnUnFollowStateCallback;
  private Callback<User> mOnUserCallback;
  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (msg.what == MESSAGE_ACTION_FOLLOW) {
        if (!mState.isFollowing) {
          mState.isFollowing = true;
          EventBus.getDefault().post(new StateEvent(true, null, mState));
          ApiClient.followUser(mUserId).enqueue(mOnFollowStateCallback);
        } else {
          mState.isFollowing = false;
          EventBus.getDefault().post(new StateEvent(true, null, mState));
          ApiClient.unFollowUser(mUserId).enqueue(mOnUnFollowStateCallback);
        }

        return true;
      }
      return false;
    }
  };
  private final Handler mHandler = new Handler(mHandlerCallback);

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

    mRefUser = mRealm.where(Profile.class).equalTo("token", PrefUtil.getCurrentToken()).findFirst();
    mUser = mRealm.where(User.class).equalTo("id", mUserId).findFirst();

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
    // setup
    mOnFollowStateCallback = new Callback<Void>() {
      @Override public void onResponse(Response<Void> response) {
        mState.isFollowing = response != null && response.code() == 204;
        EventBus.getDefault().post(new StateEvent(true, null, mState));
      }

      @Override public void onFailure(Throwable t) {
        EventBus.getDefault().post(new StateEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
      }
    };

    mOnUnFollowStateCallback = new Callback<Void>() {
      @Override public void onResponse(Response<Void> response) {
        mState.isFollowing = response != null && !(response.code() == 204);
      }

      @Override public void onFailure(Throwable t) {
        EventBus.getDefault().post(new StateEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
      }
    };

    mOnUserCallback = new Callback<User>() {
      @Override public void onResponse(Response<User> response) {
        User user = response.body();
        if (user != null) {
          Realm realm = Attiq.realm();
          realm.beginTransaction();
          realm.copyToRealmOrUpdate(user);
          realm.commitTransaction();
          realm.close();
          EventBus.getDefault().post(new UserFetchedEvent(true, null, user));
        } else {
          EventBus.getDefault().post(new UserFetchedEvent(false,
              new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Throwable error) {
        EventBus.getDefault().post(new ProfileFetchedEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    };

    // update UI
    if (mUser != null) {
      EventBus.getDefault().post(new UserFetchedEvent(true, null, mUser));
    }

    ApiClient.isFollowing(mUserId).enqueue(mOnFollowStateCallback);
    ApiClient.user(mUserId).enqueue(mOnUserCallback);
  }

  @Override protected void onPause() {
    mOnUserCallback = null;
    mOnFollowStateCallback = null;
    mOnUnFollowStateCallback = null;
    mHandler.removeCallbacksAndMessages(null);
    mHandlerCallback = null;
    super.onPause();
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(StateEvent event) {
    mBtnFollow.setEnabled(mRefUser != null && !UIUtil.isEmpty(mRefUser.getToken()));
    mBtnFollow.setClickable(mRefUser != null && !UIUtil.isEmpty(mRefUser.getToken()));
    mBtnFollow.setVisibility(
        mRefUser != null && mUserId.equals(mRefUser.getId()) ? View.GONE : View.VISIBLE
    );

    if (event.state != null) {
      mBtnFollow.setText(
          event.state.isFollowing ? R.string.state_following : R.string.state_not_following
      );

      mBtnFollow.setBackgroundResource(
          event.state.isFollowing ?
              R.drawable.rounded_background_active : R.drawable.rounded_background
      );
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(UserFetchedEvent event) {
    mUser = event.user;
    Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
    if (mUser != null) {
      mProfileName.setText(mUser.getId());

      StringBuilder description = new StringBuilder();

      boolean willBreakLine = false;
      boolean willSeparate = false;

      if (!UIUtil.isEmpty(mUser.getName())) {
        description.append(mUser.getName());
        willBreakLine = true;
      }

      if (!UIUtil.isEmpty(mUser.getLocation())) {
        if (willSeparate) {
          description.append(", ");
        }

        if (willBreakLine) {
          description.append(System.lineSeparator());
        }

        description.append(mUser.getLocation());
      }

      mProfileDescription.setText(description.toString());

      Attiq.picasso()
          .load(mUser.getProfileImageUrl())
          .placeholder(R.mipmap.ic_launcher)
          .error(R.mipmap.ic_launcher)
          .resize(mProfileImageSize, 0)
          .transform(new RoundedTransformation(
              mImageBorderWidth, mImageBorderColor, mProfileImageRadius))
          .into(mProfileImage);

      mSpannableTitle = new SpannableString(mUser.getId());
      if (!UIUtil.isEmpty(mUser.getName())) {
        mSpannableSubtitle = new SpannableString(mUser.getName());
      }

      updateTitle();
      updateDescription();
      updateQuantities();
      updateSocialButtons();
    }
  }

  private void updateDescription() {
    if (mUser == null || mDescription == null) {
      return;
    }

    mDescription.removeAllViews();
    final Context context = mDescription.getContext();

    if (!UIUtil.isEmpty(mUser.getDescription())) {
      TextView description = (TextView) LayoutInflater.from(context)
          .inflate(R.layout.widget_info_textview, mDescription, false);
      description.setText(UIUtil.beautify(mUser.getDescription()));
      mDescription.addView(description);
    }

    if (!UIUtil.isEmpty(mUser.getOrganization())) {
      UserInfoRowTextView view = new UserInfoRowTextView(mDescription.getContext());
      view.setText(UIUtil.beautify(mUser.getOrganization()));
      view.setIcon(R.drawable.ic_organization);

      mDescription.addView(view);
    }
  }

  private void updateQuantities() {
    if (mUser == null || isFinishing()) {
      return;
    }

    final Resources res = getResources();
    mItemCount.setText(mUser.getItemsCount() + "");
    mItemQuantity.setText(res.getQuantityString(R.plurals.user_items, mUser.getItemsCount()));

    mFollowerCount.setText(mUser.getFollowersCount() + "");
    mFollowerQuantity.setText(
        res.getQuantityString(R.plurals.user_followers, mUser.getFollowersCount()));

    mFollowingCount.setText(mUser.getFolloweesCount() + "");
    mFollowingQuantity.setText(
        res.getQuantityString(R.plurals.user_following, mUser.getFolloweesCount()));
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
    if (mUser == null || mSocialButtonView == null || mSocialButtonContainer == null) {
      return;
    }

    boolean hasSocialButton = false;

    for (View button : mSocialButtons) {
      button.setVisibility(View.GONE);
    }

    if (!UIUtil.isEmpty(mUser.getWebsiteUrl())) {
      hasSocialButton = true;
      mSocialButtons[WEBSITE_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mUser.getFacebookId())) {
      hasSocialButton = true;
      mSocialButtons[FACEBOOK_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mUser.getTwitterScreenName())) {
      hasSocialButton = true;
      mSocialButtons[TWITTER_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mUser.getGithubLoginName())) {
      hasSocialButton = true;
      mSocialButtons[GITHUB_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mUser.getLinkedinId())) {
      hasSocialButton = true;
      mSocialButtons[LINKEDIN_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    mSocialButtonContainer.setVisibility(hasSocialButton ? View.VISIBLE : View.GONE);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_website) void openWebsite() {
    if (mUser != null) {
      UIUtil.openWebsite(this, mUser.getWebsiteUrl());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_facebook) void openFacebook() {
    if (mUser != null) {
      UIUtil.openFacebookUser(this, mUser.getFacebookId());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_twitter) void openTwitter() {
    if (mUser != null) {
      UIUtil.openTwitterUser(this, mUser.getTwitterScreenName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_github) void openGithub() {
    if (mUser != null) {
      UIUtil.openGithubUser(this, mUser.getGithubLoginName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_linkedin) void openLinkedin() {
    if (mUser != null) {
      UIUtil.openLinkedinUser(this, mUser.getLinkedinId());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.text_action_follow) void followUnFollow() {
    mHandler.removeMessages(MESSAGE_ACTION_FOLLOW);
    mHandler.sendEmptyMessageDelayed(MESSAGE_ACTION_FOLLOW, HANDLER_DELAY);
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

      // fallback
      return DummyFragment.newInstance();
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
      if (position == 0) {
        return Attiq.creator().getString(R.string.tab_title_items);
      } else if (position == 1) {
        return Attiq.creator().getString(R.string.tab_title_stocks);
      } else if (position == 2) {
        return Attiq.creator().getString(R.string.tab_title_tags);
      }

      // fallback
      return "Tab: " + position;
    }
  }

  private static class State {

    private boolean isFollowing;
  }

  private static class StateEvent extends Event {

    private final State state;

    public StateEvent(boolean success, @Nullable Error error, State state) {
      super(success, error);
      this.state = state;
    }
  }
}