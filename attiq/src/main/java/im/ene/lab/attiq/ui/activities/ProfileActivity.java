package im.ene.lab.attiq.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
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

import com.squareup.picasso.RequestCreator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.DocumentCallback;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.model.local.RProfile;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.data.model.two.User;
import im.ene.lab.attiq.ui.fragment.DummyFragment;
import im.ene.lab.attiq.ui.fragment.UserItemsFragment;
import im.ene.lab.attiq.ui.fragment.UserStockedItemsFragment;
import im.ene.lab.attiq.ui.fragment.UserTagsFragment;
import im.ene.lab.attiq.ui.widgets.NotBadImageButton;
import im.ene.lab.attiq.ui.widgets.RoundedTransformation;
import im.ene.lab.attiq.ui.widgets.UserInfoRowTextView;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.WebUtil;
import im.ene.lab.attiq.util.event.DocumentEvent;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ProfileFetchedEvent;
import im.ene.lab.attiq.util.event.ProfileUpdatedEvent;
import im.ene.lab.support.widget.AlphaForegroundColorSpan;
import im.ene.lab.support.widget.AppBarLayout;
import im.ene.lab.support.widget.CollapsingToolbarLayout;
import im.ene.lab.support.widget.MathUtils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Iterator;
import java.util.List;

public class ProfileActivity extends BaseActivity implements RealmChangeListener {

  private static final int MESSAGE_ACTION_FOLLOW = 1;

  private static final int MESSAGE_DATA_UPDATE = 1 << 1;

  private static final String TAG = "ProfileActivity";
  // Used to keep track of index
  private static final int WEBSITE_BUTTON_INDEX = 0;
  private static final int FACEBOOK_BUTTON_INDEX = 1;
  private static final int TWITTER_BUTTON_INDEX = 2;
  private static final int GITHUB_BUTTON_INDEX = 3;
  private static final int LINKEDIN_BUTTON_INDEX = 4;

  private static final int HANDLER_DELAY = 200; // To prevent stress events

  @Bind(R.id.view_pager) ViewPager mViewPager;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.toolbar_overlay) View mOverlayContainer;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.tab_layout) TabLayout mTabLayout;
  @Bind(R.id.profile_image) NotBadImageButton mProfileImage;
  @Bind(R.id.social_button_container) View mSocialButtonContainer;
  @Bind(R.id.profile_social_buttons) LinearLayout mSocialButtonView;
  @Bind(R.id.text_action_follow_container) View mBtnFollowContainer;
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
  int mImageBorderColor;
  @BindDimen(R.dimen.profile_image_size) int mProfileImageSize;
  @BindDimen(R.dimen.item_padding) int mProfileImageRadius;
  @Bind({
      R.id.profile_social_website,
      R.id.profile_social_facebook,
      R.id.profile_social_twitter,
      R.id.profile_social_github,
      R.id.profile_social_linkedin
  }) ImageButton[] mSocialButtons;

  // private int mFollowTextPositive;
  // private int mFollowTextNegative;

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
  private RProfile mProfile;
  private Profile mRefUser;
  // private User mUser;
  private State mState = new State();
  private String mUserId; // actually the User name
  private Callback<Void> mOnFollowStateCallback;
  private Callback<Void> mOnUnFollowStateCallback;
  private Callback<User> mOnUserCallback;
  private DocumentCallback mDocumentCallback;
  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (msg.what == MESSAGE_ACTION_FOLLOW) {
        if (!mState.isFollowing) {
          mState.isFollowing = true;
          EventBus.getDefault().post(
              new StateEvent<>(ProfileActivity.class.getSimpleName(), true, null, mState));
          ApiClient.followUser(mUserId).enqueue(mOnFollowStateCallback);
        } else {
          mState.isFollowing = false;
          EventBus.getDefault().post(
              new StateEvent<>(ProfileActivity.class.getSimpleName(), true, null, mState));
          ApiClient.unFollowUser(mUserId).enqueue(mOnUnFollowStateCallback);
        }

        return true;
      } else if (msg.what == MESSAGE_DATA_UPDATE) {
        EventBus.getDefault().post(
            new ProfileUpdatedEvent(ProfileActivity.class.getSimpleName(), true, null, mProfile));
      }

      return false;
    }
  };
  private final Handler mHandler = new Handler(mHandlerCallback);

  public static Intent createIntent(Context context, String userName) {
    Intent intent = new Intent(context, ProfileActivity.class);
    Uri data = Uri.parse(context.getString(R.string.data_users_url, userName));
    intent.setData(data);
    return intent;
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
    mTitleColorSpan = new AlphaForegroundColorSpan(ContextCompat.getColor(this, titleColorId));

    typedValue = new TypedValue();
    getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
    int accentColorId = typedValue.resourceId;
    mImageBorderColor = ContextCompat.getColor(this, accentColorId);

    mRealm = Attiq.realm();
    mRealm.addChangeListener(this);

    Uri data = getIntent().getData();
    if (data != null) {
      List<String> paths = data.getPathSegments();
      if (!UIUtil.isEmpty(paths)) {
        Iterator<String> iterator = paths.iterator();
        while (iterator.hasNext()) {
          if ("users".equals(iterator.next())) {
            mUserId = iterator.next();
            break;
          }
        }
      }
    }

    mRefUser = mRealm.where(Profile.class).equalTo("token", PrefUtil.getCurrentToken()).findFirst();

    mProfile = mRealm.where(RProfile.class).equalTo(RProfile.FIELD_USER_NAME, mUserId).findFirst();
    if (mProfile == null) {
      mRealm.beginTransaction();
      mProfile = mRealm.createObject(RProfile.class);
      mProfile.setUserName(mUserId);
      mRealm.commitTransaction();
    }

    if (getSupportFragmentManager().findFragmentById(R.id.profile_info_tags) == null) {
      mTagFragment = UserTagsFragment.newInstance(mUserId);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.profile_info_tags, mTagFragment).commit();
    }

    mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), mUserId);
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
    mTabLayout.setupWithViewPager(mViewPager);

    // find a local user, if there is one, update current profile
    User user = mRealm.where(User.class).equalTo("id", mUserId).findFirst();
    if (user != null) {
      EventBus.getDefault().post(
          new ProfileFetchedEvent(getClass().getSimpleName(), true, null, user));
    }
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.removeChangeListener(this);
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
        EventBus.getDefault().post(new StateEvent<>(ProfileActivity.class.getSimpleName(),
            true, null, mState));
      }

      @Override public void onFailure(Throwable t) {
        EventBus.getDefault().post(new StateEvent<>(ProfileActivity.class.getSimpleName(), false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, t.getLocalizedMessage()), null));
      }
    };

    mOnUnFollowStateCallback = new Callback<Void>() {
      @Override public void onResponse(Response<Void> response) {
        mState.isFollowing = response != null && !(response.code() == 204);
      }

      @Override public void onFailure(Throwable t) {
        EventBus.getDefault().post(new StateEvent<>(ProfileActivity.class.getSimpleName(), false,
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
          EventBus.getDefault().post(new ProfileFetchedEvent(ProfileActivity.class.getSimpleName(),
              true, null, user));
        } else {
          EventBus.getDefault().post(new ProfileFetchedEvent(ProfileActivity.class.getSimpleName(),
              false, new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Throwable error) {
        EventBus.getDefault().post(new ProfileFetchedEvent(ProfileActivity.class.getSimpleName(),
            false, new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    };

    final String baseUrl = "http://qiita.com/" + mUserId;

    mDocumentCallback = new DocumentCallback(baseUrl) {
      @Override public void onDocument(Document response) {
        if (response != null) {
          EventBus.getDefault().post(new DocumentEvent(
              ProfileActivity.class.getSimpleName(), true, null, response));
        }
      }
    };

    WebUtil.loadWeb(baseUrl).enqueue(mDocumentCallback);

    // update UI
    if (mProfile != null) {
      EventBus.getDefault().post(new ProfileUpdatedEvent(ProfileActivity.class.getSimpleName(),
          true, null, mProfile));
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
  public void onEventMainThread(StateEvent<State> event) {
    mBtnFollow.setEnabled(mRefUser != null && !UIUtil.isEmpty(mRefUser.getToken()));
    mBtnFollow.setClickable(mRefUser != null && !UIUtil.isEmpty(mRefUser.getToken()));
    mBtnFollowContainer.setVisibility(
        mRefUser != null && mUserId.equals(mRefUser.getId()) ? View.GONE : View.VISIBLE
    );

    if (event.state != null) {
      mBtnFollow.setText(
          event.state.isFollowing ? R.string.state_following : R.string.state_not_following
      );

      mBtnFollow.setBackgroundResource(
          event.state.isFollowing ?
              R.drawable.rounded_background_active : R.drawable.rounded_background_normal
      );

      mRealm.beginTransaction();
      mProfile.setContributionCount(event.state.contribution);
      mRealm.commitTransaction();
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ProfileUpdatedEvent event) {
    mProfileName.setText(mProfile.getUserName());
    mProfileDescription.setText(mProfile.getBrief());

    final RequestCreator profileImageRequest;
    if (!UIUtil.isEmpty(mProfile.getProfileImageUrl())) {
      profileImageRequest = Attiq.picasso().load(mProfile.getProfileImageUrl());
    } else {
      profileImageRequest = Attiq.picasso().load(R.drawable.blank_profile_icon_large);
    }

    profileImageRequest
        .placeholder(R.drawable.blank_profile_icon_large)
        .error(R.drawable.blank_profile_icon_large)
        .resize(mProfileImageSize, 0)
        .transform(new RoundedTransformation(
            mImageBorderWidth, mImageBorderColor, mProfileImageRadius))
        .into(mProfileImage);

    mSpannableTitle = new SpannableString(mUserId);
    if (!UIUtil.isEmpty(mProfile.getFullName())) {
      mSpannableSubtitle = new SpannableString(mProfile.getFullName());
    }

    updateTitle();
    updateDescription();
    updateQuantities();
    updateSocialButtons();
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ProfileFetchedEvent event) {
    Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
    if (event.user != null) {
      mRealm.beginTransaction();

      mProfile.setProfileImageUrl(event.user.getProfileImageUrl());

      StringBuilder description = new StringBuilder();

      boolean willBreakLine = false;
      boolean willSeparate = false;

      if (!UIUtil.isEmpty(event.user.getName())) {
        description.append(event.user.getName());
        willBreakLine = true;
      }

      if (!UIUtil.isEmpty(event.user.getLocation())) {
        if (willSeparate) {
          description.append(", ");
        }

        if (willBreakLine) {
          description.append(System.lineSeparator());
        }

        description.append(event.user.getLocation());
      }

      mProfile.setBrief(description.toString());

      mProfile.setDescription(event.user.getDescription());
      mProfile.setOrganization(event.user.getOrganization());

      mProfile.setItemCount(event.user.getItemsCount());
      mProfile.setFollowerCount(event.user.getFollowersCount());
      mProfile.setFollowingCount(event.user.getFolloweesCount());

      mProfile.setWebsite(event.user.getWebsiteUrl());
      mProfile.setFacebookName(event.user.getFacebookId());
      mProfile.setTwitterName(event.user.getTwitterScreenName());
      mProfile.setGithubName(event.user.getGithubLoginName());
      mProfile.setLinkedinName(event.user.getLinkedinId());

      mRealm.commitTransaction();
    }
  }

  private void updateDescription() {
    if (mProfile == null || mDescription == null) {
      return;
    }

    // mDescription.removeAllViews();
    final Context context = mDescription.getContext();

    if (mProfile.getContributionCount() != null) {
      TextView contribution = (TextView) mDescription.findViewById(R.id.profile_info_contribution);
      if (contribution == null) {
        contribution = (TextView) LayoutInflater.from(context)
            .inflate(R.layout.widget_info_textview, mDescription, false);
        contribution.setId(R.id.profile_info_contribution);
        mDescription.addView(contribution, 0);
      }

      contribution.setText(getResources().getQuantityString(
          R.plurals.user_contribution_quantity,
          mProfile.getContributionCount(), mProfile.getContributionCount()
      ));
    }

    if (!UIUtil.isEmpty(mProfile.getDescription())) {
      TextView description = (TextView) mDescription.findViewById(R.id.profile_info_description);
      if (description == null) {
        description = (TextView) LayoutInflater.from(context)
            .inflate(R.layout.widget_info_textview, mDescription, false);
        description.setId(R.id.profile_info_description);
        mDescription.addView(description);
      }
      description.setText(UIUtil.beautify(mProfile.getDescription()));
    }

    if (!UIUtil.isEmpty(mProfile.getOrganization())) {
      UserInfoRowTextView organization =
          (UserInfoRowTextView) mDescription.findViewById(R.id.profile_info_organization);
      if (organization == null) {
        organization = new UserInfoRowTextView(mDescription.getContext());
        organization.setId(R.id.profile_info_organization);
        mDescription.addView(organization);
      }

      organization.setText(UIUtil.beautify(mProfile.getOrganization()));
      organization.setIcon(R.drawable.ic_organization);
    }
  }

  private UserTagsFragment mTagFragment;

  private void updateQuantities() {
    if (mProfile == null || isFinishing()) {
      return;
    }

    final Resources res = getResources();
    mItemCount.setText(mProfile.getItemCount() + "");
    mItemQuantity.setText(res.getQuantityString(R.plurals.user_items, mProfile.getItemCount()));

    mFollowerCount.setText(mProfile.getFollowerCount() + "");
    mFollowerQuantity.setText(
        res.getQuantityString(R.plurals.user_followers, mProfile.getFollowerCount()));

    mFollowingCount.setText(mProfile.getFollowingCount() + "");
    mFollowingQuantity.setText(
        res.getQuantityString(R.plurals.user_following, mProfile.getFollowingCount()));
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
    if (mProfile == null || mSocialButtonView == null || mSocialButtonContainer == null) {
      return;
    }

    boolean hasSocialButton = false;

    for (View button : mSocialButtons) {
      button.setVisibility(View.GONE);
    }

    if (!UIUtil.isEmpty(mProfile.getWebsite())) {
      hasSocialButton = true;
      mSocialButtons[WEBSITE_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getFacebookName())) {
      hasSocialButton = true;
      mSocialButtons[FACEBOOK_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getTwitterName())) {
      hasSocialButton = true;
      mSocialButtons[TWITTER_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getGithubName())) {
      hasSocialButton = true;
      mSocialButtons[GITHUB_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    if (!UIUtil.isEmpty(mProfile.getLinkedinName())) {
      hasSocialButton = true;
      mSocialButtons[LINKEDIN_BUTTON_INDEX].setVisibility(View.VISIBLE);
    }

    mSocialButtonContainer.setVisibility(hasSocialButton ? View.VISIBLE : View.GONE);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_website) void openWebsite() {
    if (mProfile != null) {
      UIUtil.openWebsite(this, mProfile.getWebsite());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_facebook) void openFacebook() {
    if (mProfile != null) {
      UIUtil.openFacebookUser(this, mProfile.getFacebookName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_twitter) void openTwitter() {
    if (mProfile != null) {
      UIUtil.openTwitterUser(this, mProfile.getTwitterName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_github) void openGithub() {
    if (mProfile != null) {
      UIUtil.openGithubUser(this, mProfile.getGithubName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.profile_social_linkedin) void openLinkedin() {
    if (mProfile != null) {
      UIUtil.openLinkedinUser(this, mProfile.getLinkedinName());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.text_action_follow) void followUnFollow() {
    mHandler.removeMessages(MESSAGE_ACTION_FOLLOW);
    mHandler.sendEmptyMessageDelayed(MESSAGE_ACTION_FOLLOW, HANDLER_DELAY);
  }

  @Override public void onChange() {
    mHandler.removeMessages(MESSAGE_DATA_UPDATE);
    mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, HANDLER_DELAY);
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

  @SuppressWarnings("unused")
  public void onEventMainThread(DocumentEvent event) {
    if (event.document != null) {
      Elements stats = event.document.getElementsByClass("userActivityChart_stats");
      Element statBlock;
      if (!UIUtil.isEmpty(stats) && (statBlock = stats.first()) != null) {
        Elements statElements = statBlock.children();
        Integer contribution = null;
        for (Element element : statElements) {
          String unit = element.getElementsByClass("userActivityChart_statUnit").text();
          if ("Contribution".equals(unit.trim())) {
            try {
              contribution = Integer.valueOf(
                  element.getElementsByClass("userActivityChart_statCount").text()
              );
            } catch (NumberFormatException er) {
              er.printStackTrace();
            }

            break;
          }
        }

        if (contribution != null) {
          mState.contribution = contribution;
          EventBus.getDefault().post(
              new StateEvent<>(getClass().getSimpleName(), true, null, mState));
        }
      }
    }
  }

  private static class State extends BaseState {

    private boolean isFollowing;

    private Integer contribution;
  }

  @Override protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ?
        R.style.Attiq_Theme_Dark_NoActionBar_Profile :
        R.style.Attiq_Theme_Light_NoActionBar_Profile;
  }
}