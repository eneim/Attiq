package im.ene.lab.attiq.activities;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
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

import org.jsoup.Jsoup;
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
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.Comment;
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.util.AnimUtils;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.WebUtil;
import im.ene.lab.attiq.util.event.DocumentEvent;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemCommentsEvent;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
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

  private static final String TAG = "ItemDetailActivity";

  private static final int MESSAGE_STOCK = 1;
  private static final int MESSAGE_UNSTOCK = 1 << 1;

  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (msg.what == MESSAGE_UNSTOCK) {
        ApiClient.unStockItem(mItemUuid).enqueue(mItemUnStockedResponse);
        return true;
      } else if (msg.what == MESSAGE_STOCK) {
        ApiClient.stockItem(mItemUuid).enqueue(mItemStockedResponse);
        return true;
      }

      return false;
    }
  };

  private final Handler mHandler = new Handler(mHandlerCallback);

  @Bind(R.id.content_container) CoordinatorLayout mContentContainer;
  @Bind(R.id.comments_header) TextView mCommentInfo;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.item_content_web) WebView mContentView;
  @Bind(R.id.item_comments_web) WebView mComments;
  @Bind(R.id.item_stocks) TextView mStockCount;
  @Bind(R.id.item_comments) TextView mCommentCount;
  @Bind(R.id.toolbar_overlay) View mOverLayView;
  @Bind(R.id.item_title) TextView mArticleName;
  @Bind(R.id.item_subtitle) TextView mArticleDescription;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.drawer_layout) DrawerLayout mMenuLayout;
  @Bind(R.id.html_headers_container) LinearLayout mMenuContainer;
  @Bind(R.id.loading_container) View mLoadingView;

  @BindDimen(R.dimen.header_depth_width) int mHeaderDepthWidth;
  @BindDimen(R.dimen.header_depth_gap) int mHeaderDepthGap;
  @BindDimen(R.dimen.app_bar_max_elevation) float mMaxAppbarElevation;
  @BindDimen(R.dimen.app_bar_min_elevation) float mMinAppbarElevation;

  // private Document mArticleDocument;
  private MenuItem mArticleHeaderMenu;

  private Realm mRealm;
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
            float fraction = Math.abs(verticalOffset) / maxOffset;
            mOverLayView.setAlpha(1.f - fraction);

            ViewCompat.setElevation(mAppBarLayout,
                mMaxAppbarElevation * fraction + mMinAppbarElevation * (1.f - fraction));
          }
        }
      };
  private String mItemUuid;
  private okhttp3.Callback mDocumentCallback;
  private Callback<Void> mItemUnStockedResponse = new Callback<Void>() {
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        mState.isStocked = false;
        int newStockCount = (Integer.parseInt(mState.stockCount) - 1);
        if (newStockCount < 0) {
          newStockCount = 0;
        }

        mState.stockCount = "" + newStockCount;
      }

      EventBus.getDefault().post(new StateEvent(true, null, mState));
    }

    @Override public void onFailure(Throwable t) {

    }
  };
  private Callback<Void> mStockStatusResponse = new Callback<Void>() {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        mState.isStocked = true;
      } else {
        mState.isStocked = false;
      }

      EventBus.getDefault().post(new StateEvent(true, null, mState));
    }

    @Override public void onFailure(Throwable t) {

    }
  };

  private Callback<Void> mItemStockedResponse = new Callback<Void>() {
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        mState.isStocked = true;
        mState.stockCount = "" + (1 + Integer.parseInt(mState.stockCount));
      }

      EventBus.getDefault().post(new StateEvent(true, null, mState));
    }

    @Override public void onFailure(Throwable t) {

    }
  };

  private State mState = new State();

  public static Intent createIntent(Context context, String uuid) {
    Intent intent = createIntent(context);
    Uri data = Uri.parse(context.getString(R.string.data_items_url, uuid));
    intent.setData(data);
    return intent;
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, ItemDetailActivity.class);
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
    mTitleColorSpan = new AlphaForegroundColorSpan(ContextCompat.getColor(this, titleColorId));

    Uri data = getIntent().getData();
    if (data != null) {
      List<String> paths = data.getPathSegments();
      if (!UIUtil.isEmpty(paths)) {
        Iterator<String> iterator = paths.iterator();
        while (iterator.hasNext()) {
          if ("items".equals(iterator.next())) {
            mItemUuid = iterator.next();
            break;
          }
        }
      }
    }

    mRealm = Attiq.realm();

    Article article = mRealm.where(Article.class).equalTo("id", mItemUuid).findFirst();
    if (article != null) {
      EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
    }

    ApiClient.isStocked(mItemUuid).enqueue(mStockStatusResponse);
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    mStockStatusResponse = null;
    mDocumentCallback = null;
    mHandler.removeCallbacksAndMessages(null);
    mHandlerCallback = null;
    super.onDestroy();
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

  @SuppressWarnings("unused")
  @OnClick(R.id.button_action_share) void shareArticle() {
    if (mArticle == null) {
      return;
    }

    boolean found = false;
    Intent share = new Intent(android.content.Intent.ACTION_SEND);
    share.setType("text/plain");

    String shareUrl = mArticle.getUrl();
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_SUBJECT, mArticle.getTitle());
    intent.putExtra(Intent.EXTRA_TEXT, "I want to share this URL: " + shareUrl);
    startActivity(intent);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.item_comments) void commentArticle() {

  }

  @SuppressWarnings("unused")
  @OnClick(R.id.item_stocks) void stockArticle() {
    mHandler.removeMessages(MESSAGE_STOCK);
    mHandler.removeMessages(MESSAGE_UNSTOCK);
    if (!mState.isStocked) {
      mHandler.sendEmptyMessageDelayed(MESSAGE_STOCK, 200);
    } else {
      mHandler.sendEmptyMessageDelayed(MESSAGE_UNSTOCK, 200);
    }
  }

  @Override public void onResponse(Response<Article> response) {
    Article article = response.body();
    if (article != null) {
      mRealm.beginTransaction();
      mRealm.copyToRealmOrUpdate(article);
      mRealm.commitTransaction();
      EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
    } else {
      EventBus.getDefault().post(new ItemDetailEvent(false,
          new Event.Error(response.code(), response.message()), null));
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.article;
    mArticle = article;
    String userName = null;
    if (article != null) {
      User user = article.getUser();

      mArticleName.setText(article.getTitle());
      mSpannableTitle = new SpannableString(article.getTitle());
      userName = user.getId();
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
        mContentView.loadDataWithBaseURL(article.getUrl(), result, null, null, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    final CharSequence subTitle;
    if (article != null && !UIUtil.isEmpty(userName)) {
      subTitle = Html.fromHtml(getString(R.string.item_user_info,
          userName, userName, TimeUtil.beautify(article.getCreatedAt())));
    } else {
      subTitle = getString(R.string.item_detail_subtitle, userName);
    }

    mArticleDescription.setText(subTitle);
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
            Snackbar.make(mContentContainer, R.string.item_detail_no_menu,
                Snackbar.LENGTH_LONG).show();
          }
          return true;
        }
      });
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(DocumentEvent event) {
    if (event.document != null) {
      mState.stockCount = event.document.getElementsByClass("js-stocksCount").first().text();
      EventBus.getDefault().post(new StateEvent(true, null, mState));
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ItemCommentsEvent event) {
    if (event.success && !UIUtil.isEmpty(event.comments)) {
      List<Comment> comments = event.comments;

      mCommentCount.setText(comments.size() + "");

      String info = comments.size() == 1 ?
          getString(R.string.comment_singular) : getString(R.string.comment_plural);
      mCommentInfo.setText(getString(R.string.article_comment, comments.size(), info));

      final String html;
      try {
        html = IOUtil.readAssets("html/comments.html");

        Document fullBody = Jsoup.parse(html);
        Element content = fullBody.getElementById("content");

        for (Comment comment : comments) {
          String commentHtml = IOUtil.readAssets("html/comment.html");
          commentHtml = commentHtml
              .replace("{user_icon_url}", comment.getUser().getProfileImageUrl())
              .replace("{user_name}", comment.getUser().getId())
              .replace("{comment_time}", TimeUtil.commentTime(comment.getUpdatedAt()))
              .replace("{article_uuid}", mItemUuid)
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
    } else {
      mCommentCount.setText("0");
      mCommentInfo.setText(
          getString(R.string.article_comment, 0, getString(R.string.comment_plural)));
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (mItemUuid != null) {
      ApiClient.itemDetail(mItemUuid).enqueue(this);

      final String baseUrl = "http://qiita.com/api/items/" + mItemUuid;

      mDocumentCallback = new DocumentCallback(baseUrl) {
        @Override public void onDocument(Document response) {
          if (response != null) {
            EventBus.getDefault().post(new DocumentEvent(true, null, response));
          }
        }
      };

      WebUtil.loadWeb(baseUrl).enqueue(mDocumentCallback);
    }
  }

  @Override public void onFailure(Throwable error) {
    EventBus.getDefault().post(new ItemDetailEvent(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_item_detail, menu);
    mArticleHeaderMenu = menu.findItem(R.id.action_item_menu);
    return true;
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(StateEvent event) {
    if (event.state != null) {
      mStockCount.setText(event.state.stockCount);
      if (event.state.isStocked) {
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mStockCount,
            ContextCompat.getDrawable(mStockCount.getContext(), R.drawable.ic_action_stocked),
            null, null, null
        );
      } else {
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mStockCount,
            ContextCompat.getDrawable(mStockCount.getContext(), R.drawable.ic_action_stock),
            null, null, null
        );
      }
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      navigateUpOrBack(this, null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private static class State {

    private boolean isStocked;

    private String stockCount;
  }

  private static class StateEvent extends Event {

    private final State state;

    public StateEvent(boolean success, @Nullable Error error, State state) {
      super(success, error);
      this.state = state;
    }
  }


}
