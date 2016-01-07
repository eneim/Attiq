package im.ene.lab.attiq.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.one.Post;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.Comment;
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.data.zero.FeedItem;
import im.ene.lab.attiq.data.zero.PublicItem;
import im.ene.lab.attiq.util.AnimUtils;
import im.ene.lab.attiq.util.WebUtil;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemCommentsEvent;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import im.ene.lab.attiq.widgets.drawable.ThreadedCommentDrawable;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import io.realm.Realm;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ItemDetailActivity extends BaseActivity implements Callback<Article> {

  private static final String EXTRA_DETAIL_ITEM_ID = "attiq_item_detail_extra_id";

  private static final String EXTRA_DETAIL_ITEM_UUID = "attiq_item_detail_extra_uuid";

  private static final String TAG = "ItemDetailActivity";

  @Bind(R.id.sliding_layout) SlidingUpPanelLayout mSlidingLayout;
  @Bind(R.id.content_container) CoordinatorLayout mContentContainer;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.item_content_web) WebView mContentView;
  @Bind(R.id.item_comments) WebView mComments;
  @Bind(R.id.toolbar_overlay) View mOverLayView;
  @Bind(R.id.item_title) TextView mArticleName;
  @Bind(R.id.item_subtitle) TextView mArticleDescription;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.drawer_layout) DrawerLayout mMenuLayout;
  @Bind(R.id.html_headers_container) LinearLayout mMenuContainer;
  @Bind(R.id.loading_container) View mLoadingView;
  // @Bind(R.id.detail_author_icon) ImageButton mAuthorIcon;
  // !IMPORTANT Bottom Toolbar, setup specially for an Article
  @Bind(R.id.actions_bar) Toolbar mArticleBar;

  @BindDimen(R.dimen.item_icon_size_small) int mIconSize;
  @BindDimen(R.dimen.item_icon_size_small_half) int mIconCornerRadius;
  @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
  @BindColor(R.color.colorPrimary) int mIconBorderColor;

  @BindDimen(R.dimen.header_depth_width) int mHeaderDepthWidth;
  @BindDimen(R.dimen.header_depth_gap) int mHeaderDepthGap;

  private Toolbar.OnMenuItemClickListener mArticleBarItemClickListener =
      new Toolbar.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem item) {
          int id = item.getItemId();
          switch (id) {
            case R.id.action_item_share:
              shareArticle();
              return true;
            case R.id.action_item_comment:
              commentArticle();
              return true;
            case R.id.action_item_stock:
              stockArticle();
              return true;
            default:
              return false;
          }
        }
      };

  private MenuItem mArticleHeaderMenu;

  private Realm mRealm;
  private PublicItem mPublicItem;
  private Article mArticle;
  private boolean mIsFirstTimeLoaded = false;
  private Element mMenuAnchor;
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
          }
        }
      };
  private String mItemUuid;

  public static Intent createIntent(Context context, Long itemId, String itemUuid) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, itemId);
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, itemUuid);
    return intent;
  }

  public static Intent createIntent(Context context, @NonNull PublicItem item) {
    return createIntent(context, item.getId(), item.getUuid());
  }

  public static Intent createIntent(Context context, @NonNull Post item) {
    return createIntent(context, item.getId(), item.getUuid());
  }

  public static Intent createIntent(Context context, @NonNull FeedItem item) {
    return createIntent(context, null, item.getMentionedObjectUuid());
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, ItemDetailActivity.class);
  }

  public static Intent createIntent(Context context, String uuid) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, uuid);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    mArticleBar.inflateMenu(R.menu.menu_item_detail_actions);
    mArticleBar.setOnMenuItemClickListener(mArticleBarItemClickListener);

    // empty title at start
    setTitle("");

    trySetupMenuDrawerLayout();

    trySetupContentView();

    trySetupCommentView();

    mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);

    mArticleDescription.setClickable(true);
    mArticleDescription.setMovementMethod(LinkMovementMethod.getInstance());
    // dynamically update padding
    mArticleName.setPadding(
        mArticleName.getPaddingLeft(),
        mArticleName.getPaddingTop() + UIUtil.getStatusBarHeight(this),
        mArticleName.getPaddingRight(),
        mArticleName.getPaddingBottom()
    );

    TypedValue typedValue = new TypedValue();
    mToolbar.getContext().getTheme()
        .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
    int titleColorId = typedValue.resourceId;
    mTitleColorSpan = new AlphaForegroundColorSpan(UIUtil.getColor(this, titleColorId));

    mItemUuid = getIntent().getStringExtra(EXTRA_DETAIL_ITEM_UUID);
    mRealm = Attiq.realm();
    mPublicItem = mRealm.where(PublicItem.class).equalTo("uuid", mItemUuid).findFirst();

  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    mArticleBarItemClickListener = null;
    super.onDestroy();
  }

  private void shareArticle() {
    if (mArticle == null) {
      return;
    }

    String shareUrl = mArticle.getUrl();
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_SUBJECT, mArticle.getTitle());
    intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
    startActivity(intent);
  }

  private void commentArticle() {

  }

  private void stockArticle() {

  }

  private void trySetupMenuDrawerLayout() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mMenuLayout, null,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    mMenuLayout.setDrawerListener(toggle);
    // !IMPORTANT Don't call this.
    // It will change Toolbar's navi icon position, which is not what I want to do
    // toggle.syncState();
  }

  private void trySetupContentView() {
    mContentView.setVerticalScrollBarEnabled(false);
    mContentView.setHorizontalScrollBarEnabled(false);
    mContentView.getSettings().setJavaScriptEnabled(true);

    mContentView.setWebChromeClient(new WebChromeClient() {
      @Override public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        Log.d(TAG, "newProgress = [" + newProgress + "]");
      }
    });

    mContentView.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          return true;
        } else {
          return false;
        }
      }

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (!mIsFirstTimeLoaded && mLoadingView != null) {
          mLoadingView.setAlpha(1.f);
          mLoadingView.setVisibility(View.VISIBLE);
        }
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mIsFirstTimeLoaded = true;
        if (mLoadingView != null) {
          mLoadingView.animate().alpha(0.f).setDuration(300)
              .setListener(new AnimUtils.AnimationEndListener() {
                @Override public void onAnimationEnd(Animator animation) {
                  if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                  }
                }
              }).start();
        }
      }
    });

    mContentView.setFindListener(new WebView.FindListener() {
      @Override
      public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean
          isDoneCounting) {
        if (mMenuLayout != null) {
          mMenuLayout.closeDrawer(GravityCompat.END);
        }
        if (numberOfMatches > 0 && mMenuAnchor != null && mContentView != null) {
          // mContentView.clearMatches();
          // FIXME Doesn't work now, because WebView is staying inside ScrollView
          mContentView.loadUrl("javascript:scrollToElement(\"" + mMenuAnchor.text() + "\");");
        }
      }
    });

  }

  private void trySetupCommentView() {
    mComments.setVerticalScrollBarEnabled(true);
    mComments.setHorizontalScrollBarEnabled(false);
  }

  @Override public void onResponse(Response<Article> response) {
    Article article = response.body();
    if (article != null) {
      EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
    } else {
      EventBus.getDefault().post(new ItemDetailEvent(false,
          new Event.Error(response.code(), response.message()), null));
    }
  }

  public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.article;
    mArticle = article;
    if (article != null) {
      User user = article.getUser();
      final RequestCreator requestCreator;
      if (!UIUtil.isEmpty(user.getProfileImageUrl())) {
        requestCreator = Attiq.picasso().load(user.getProfileImageUrl());
      } else {
        requestCreator = Attiq.picasso().load(R.drawable.blank_profile_icon_medium);
      }

      requestCreator
          .placeholder(R.drawable.blank_profile_icon_medium)
          .error(R.drawable.blank_profile_icon_medium)
          .resize(mIconSize, 0)
          .transform(new RoundedTransformation(
              mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
          .into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
              mArticleBar.setNavigationIcon(
                  new BitmapDrawable(mArticleBar.getContext().getResources(), bitmap));
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
              mArticleBar.setNavigationIcon(R.mipmap.ic_launcher);
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
              mArticleBar.setNavigationIcon(R.drawable.blank_profile_icon_small);
            }
          });

      mArticleBar.setTitle(user.getId());
      mArticleBar.setSubtitle(user.getItemsCount() + "");

      mArticleName.setText(article.getTitle());
      mSpannableTitle = new SpannableString(article.getTitle());
      String userName = user.getId();
      final CharSequence subTitle;

      if (mPublicItem != null) {
        subTitle = Html.fromHtml(getString(R.string.item_user_info,
            userName, userName, TimeUtil.beautify(article.getCreatedAt())));
      } else {
        subTitle = getString(R.string.item_detail_subtitle, userName);
      }

      mArticleDescription.setText(subTitle);
      mSpannableSubtitle = new SpannableString(userName);

      updateTitle();

      buildArticleComments(article);

      buildArticleMenu(article);

      final String html;
      try {
        html = IOUtil.readAssets("html/article.html");

        Document doc = Jsoup.parse(html);
        Element elem = doc.getElementById("content");
        elem.append(article.getRenderedBody());

        String result = doc.outerHtml();
        mContentView.loadDataWithBaseURL(
            article.getUrl(), result, null, null, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void updateTitle() {
    float titleAlpha = mToolBarLayout.shouldTriggerScrimOffset(mToolbarLayoutOffset) ? 1.f : 0.f;
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

  private void buildArticleComments(@NonNull final Article article) {
    ApiClient.itemComments(article.getId()).enqueue(new Callback<List<Comment>>() {
      @Override public void onResponse(Response<List<Comment>> response) {
        if (response.code() == 200) {
          EventBus.getDefault().post(new ItemCommentsEvent(true, null, response.body()));
        } else {
          EventBus.getDefault().post(new ItemCommentsEvent(false,
              new Event.Error(response.code(), response.message()), null));
        }
      }

      @Override public void onFailure(Throwable error) {
        EventBus.getDefault().post(new ItemCommentsEvent(false,
            new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
      }
    });
  }

  private void buildArticleMenu(@NonNull Article article) {
    String articleHtml = article.getRenderedBody();
    Elements headers = Jsoup.parse(articleHtml).select("h0, h1, h2, h3, h4, h5, h6");

    mMenuContainer.removeAllViews();
    final LayoutInflater inflater = LayoutInflater.from(mMenuContainer.getContext());
    if (!UIUtil.isEmpty(headers)) {
      // 1. Find the top level (lowest level)
      Iterator<Element> items = headers.iterator();
      int topLevel = WebUtil.getHeaderLevel(items.next().tagName());
      while (items.hasNext()) {
        int level = WebUtil.getHeaderLevel(items.next().tagName());
        if (topLevel > level) {
          topLevel = level;
        }
      }

      Log.e(TAG, "buildArticleMenu: " + topLevel);
      // 2. Build the menu for headers
      for (final Element item : headers) {
        View menuItemView = inflater.inflate(R.layout.item_detail_menu_row, mMenuContainer, false);
        CheckedTextView menuContent =
            (CheckedTextView) menuItemView.findViewById(R.id.header_content);
        menuContent.setText(item.text());

        int currentLevel = WebUtil.getHeaderLevel(item.tagName());
        if (currentLevel - topLevel > 0) {
          menuContent.setCompoundDrawablesWithIntrinsicBounds(new ThreadedCommentDrawable(
              mHeaderDepthWidth, mHeaderDepthGap, currentLevel - topLevel
          ), null, null, null);
        }

        menuItemView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            mMenuAnchor = item;
            mContentView.clearMatches();
            mContentView.findAllAsync(item.text());
          }
        });

        mMenuContainer.addView(menuItemView);
      }
      mMenuLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      mArticleHeaderMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem item) {
          mMenuLayout.openDrawer(GravityCompat.END);
          return true;
        }
      });
    } else {
      mMenuLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
      mArticleHeaderMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem item) {
          if (!isFinishing() && mContentContainer != null) {
            Snackbar.make(mContentContainer, "メニューがありません!", Snackbar.LENGTH_LONG).show();
          }
          return true;
        }
      });
    }
  }

  public void onEventMainThread(ItemCommentsEvent event) {
    if (event.success && !UIUtil.isEmpty(event.comments)) {
      List<Comment> comments = event.comments;
      final String html;
      try {
        html = IOUtil.readAssets("html/comments.html");

        Document fullBody = Jsoup.parse(html);
        Element content = fullBody.getElementById("content");

        // TODO support comment ordering
        for (Comment comment : comments) {
          String commentHtml = IOUtil.readAssets("html/comment.html");
          commentHtml = commentHtml
              .replace("{user_name}", comment.getUser().getId())
              .replace("{comment_time}", TimeUtil.commentTime(comment.getUpdatedAt()))
              .replace("{article_uuid}", mPublicItem.getUuid())
              .replace("{comment_id}", comment.getId());

          Document commentDoc = Jsoup.parse(commentHtml);
          Element eComment = commentDoc.getElementsByClass("comment-box").first();
          eComment.getElementsByClass("message").first().append(comment.getRenderedBody());
          content.appendChild(eComment);
        }

        String result = fullBody.outerHtml();
        mComments.loadDataWithBaseURL(
            "http://qiita.com/", result, null, null, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
      mSlidingLayout.setTouchEnabled(true);
    } else {
      mSlidingLayout.setTouchEnabled(false);
    }
  }

  @Override public void onFailure(Throwable error) {
    EventBus.getDefault().post(new ItemDetailEvent(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
  }

  @Override public void onBackPressed() {
    if (mSlidingLayout != null
        && mSlidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
      mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    } else {
      super.onBackPressed();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (mItemUuid != null) {
      ApiClient.itemDetail(mItemUuid).enqueue(this);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_item_detail, menu);
    mArticleHeaderMenu = menu.findItem(R.id.action_item_menu);
    return true;
  }

}
