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

package im.ene.lab.attiq.activities;

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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
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
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.DocumentCallback;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.two.Article;
import im.ene.lab.attiq.data.two.Comment;
import im.ene.lab.attiq.data.two.User;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.ImeUtils;
import im.ene.lab.attiq.util.SuccessCallback;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.util.WebUtil;
import im.ene.lab.attiq.util.event.DocumentEvent;
import im.ene.lab.attiq.util.event.Event;
import im.ene.lab.attiq.util.event.ItemCommentsEvent;
import im.ene.lab.attiq.util.event.ItemDetailEvent;
import im.ene.lab.attiq.widgets.CommentComposerView;
import im.ene.lab.attiq.widgets.NestedScrollableViewHelper;
import im.ene.lab.attiq.widgets.PanelSlideListenerAdapter;
import im.ene.lab.attiq.widgets.drawable.ThreadedCommentDrawable;
import im.ene.lab.support.widget.AlphaForegroundColorSpan;
import im.ene.lab.support.widget.AppBarLayout;
import im.ene.lab.support.widget.CollapsingToolbarLayout;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDetailActivity extends BaseActivity implements Callback<Article> {

  private static final String TAG = "ItemDetailActivity";

  private static final int MESSAGE_STOCK = 1;
  private static final int MESSAGE_UN_STOCK = 1 << 1;

  @Bind(R.id.content_container) CoordinatorLayout mContentContainer;
  @Bind(R.id.content_scrollview) NestedScrollView mCommentScrollView;
  @Bind(R.id.comments_header) TextView mCommentInfo;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.item_content_web) WebView mContentView;
  @Bind(R.id.item_comments_web) WebView mCommentsView;
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
  @Bind(R.id.sliding_layout) SlidingUpPanelLayout mSlidingPanel;
  @Bind(R.id.comment_composer_container) View mCommentComposerContainer;
  @Bind(R.id.comment_composer) CommentComposerView mCommentComposer;
  @Bind(R.id.comment_composer_tabs) TabLayout mComposerTabs;
  @BindDimen(R.dimen.header_depth_width) int mHeaderDepthWidth;
  @BindDimen(R.dimen.header_depth_gap) int mHeaderDepthGap;
  @BindDimen(R.dimen.app_bar_max_elevation) float mMaxAppbarElevation;
  @BindDimen(R.dimen.app_bar_min_elevation) float mMinAppbarElevation;

  private ArrayList<Comment> mComments = new ArrayList<>();
  private MenuItem mArticleHeaderMenu;
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
  private ViewPager.OnPageChangeListener mCommentComposerPageChange = new ViewPager
      .SimpleOnPageChangeListener() {
    @Override public void onPageSelected(int position) {
      super.onPageSelected(position);
      View currentView;
      if (mCommentComposer != null && mSlidingPanel != null &&
          (currentView = mCommentComposer.getCurrentView()) != null) {
        mSlidingPanel.setScrollableView(currentView);
      }
    }
  };
  private String mItemUuid;
  private okhttp3.Callback mDocumentCallback;

  private Callback<Void> mItemUnStockedResponse = new SuccessCallback<Void>() {
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        ((State) mState).isStocked = false;
        int newStockCount = (Integer.parseInt(((State) mState).stockCount) - 1);
        if (newStockCount < 0) {
          newStockCount = 0;
        }
        ((State) mState).stockCount = "" + newStockCount;
      }

      EventBus.getDefault().post(new StateEvent<>(true, null, mState));
    }
  };
  private Callback<Void> mStockStatusResponse = new SuccessCallback<Void>() {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        ((State) mState).isStocked = true;
      } else {
        ((State) mState).isStocked = false;
      }

      EventBus.getDefault().post(new StateEvent<>(true, null, mState));
    }
  };
  private Callback<Void> mItemStockedResponse = new SuccessCallback<Void>() {
    @Override public void onResponse(Response<Void> response) {
      if (response.code() == 204) {
        ((State) mState).isStocked = true;
        ((State) mState).stockCount = "" + (1 + Integer.parseInt(((State) mState).stockCount));
      }

      EventBus.getDefault().post(new StateEvent<>(true, null, mState));
    }
  };
  private Handler.Callback mHandlerCallback = new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      if (msg.what == MESSAGE_UN_STOCK) {
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

  private Callback<Comment> mCommentCallback = new SuccessCallback<Comment>() {
    @Override public void onResponse(Response<Comment> response) {
      Comment newComment = response.body();
      if (newComment != null) {
        mComments.add(0, newComment);
      }
      if (mCommentScrollView != null && mCommentInfo != null) {
        mCommentScrollView.scrollTo(mCommentInfo.getTop(), 0);
      }

      EventBus.getDefault().post(new ItemCommentsEvent(true, null, mComments));
    }
  };

  public static Intent createIntent(Context context, String uuid) {
    Intent intent = new Intent(context, ItemDetailActivity.class);
    Uri data = Uri.parse(context.getString(R.string.data_items_url, uuid));
    intent.setData(data);
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

    // To get Toolbar's height, there are several methods. But all of them depends on Toolbar's
    // theme or the host activity spec. In AppCompat, it's not guarantee that it contains a
    // custom Toolbar or default ActionBar/Toolbar, so we'd better get the real Toolbar's dimen.
    mToolbar.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            mToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            // Dynamically update top margin, depending on device's spec
            SlidingUpPanelLayout.LayoutParams lp =
                ((SlidingUpPanelLayout.LayoutParams) mCommentComposerContainer.getLayoutParams());
            if (lp != null) {
              lp.topMargin =
                  UIUtil.getStatusBarHeight(ItemDetailActivity.this) + mToolbar.getHeight();
              mCommentComposerContainer.setLayoutParams(lp);
            }
          }
        });

    mSlidingPanel.setScrollableViewHelper(new NestedScrollableViewHelper());
    mSlidingPanel.setPanelSlideListener(new PanelSlideListenerAdapter() {
      @Override public void onPanelCollapsed(View panel) {
        ImeUtils.hideIme(panel);
      }

      @Override public void onPanelAnchored(View panel) {
        ImeUtils.hideIme(panel);
      }

      @Override public void onPanelHidden(View panel) {
        ImeUtils.hideIme(panel);
      }
    });

    mCommentComposer.addOnPageChangeListener(mCommentComposerPageChange);
    mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    mComposerTabs.setupWithViewPager(mCommentComposer);

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

    Article article = mRealm.where(Article.class).equalTo("id", mItemUuid).findFirst();
    if (article != null) {
      EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
    }

    ApiClient.isStocked(mItemUuid).enqueue(mStockStatusResponse);
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
    // mContentView.getSettings().setJavaScriptEnabled(true);

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
          ViewCompat.animate(mLoadingView).alpha(0.f).setDuration(300)
              .setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override public void onAnimationEnd(View view) {
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
          // Suppose to jump to matched text
          // FIXME Doesn't work now, because WebView is staying inside ScrollView
          mContentView.loadUrl("javascript:scrollToElement(\"" + mMenuAnchor.text() + "\");");
        }
      }
    });
  }

  private void trySetupCommentView() {
    mCommentsView.setVerticalScrollBarEnabled(true);
    mCommentsView.setHorizontalScrollBarEnabled(false);
  }

  @Override protected void onResume() {
    super.onResume();
    if (mItemUuid != null) {
      ApiClient.itemDetail(mItemUuid).enqueue(this);

      final String baseUrl = getString(R.string.item_url, mItemUuid);

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

  @Override protected void onDestroy() {
    mCommentComposer.removeOnPageChangeListener(mCommentComposerPageChange);
    mCommentComposerPageChange = null;
    mCommentCallback = null;
    mStockStatusResponse = null;
    mDocumentCallback = null;
    mHandler.removeCallbacksAndMessages(null);
    mHandlerCallback = null;
    super.onDestroy();
  }

  @Override protected void initState() {
    mState = new State();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_action_share) void shareArticle() {
    if (mArticle == null) {
      return;
    }

    Intent share = ShareCompat.IntentBuilder.from(this)
        .setChooserTitle("Share")
        .setType("text/plain")
        .setText(mArticle.getUrl())
        .setSubject(mArticle.getTitle())
        .createChooserIntent();

    startActivity(share);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.item_comments) void commentArticle() {
    mAppBarLayout.setExpanded(false, true);
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
      }
    }, 250);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.btn_close) void cancelComment() {
    ImeUtils.hideIme(mCommentComposer);
    mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        Snackbar.make(mContentView, R.string.notify_comment_saved, Snackbar.LENGTH_LONG).show();
      }
    }, 250);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.btn_submit) void summitComment() {
    ImeUtils.hideIme(mCommentComposer);
    String comment = mCommentComposer.getComment();
    mCommentComposer.clearComment();
    if (!UIUtil.isEmpty(comment)) {
      ApiClient.postComment(mItemUuid, comment).enqueue(mCommentCallback);
    }
    ImeUtils.hideIme(mCommentComposer);
    mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
  @OnClick(R.id.item_stocks) void stockArticle() {
    mHandler.removeMessages(MESSAGE_STOCK);
    mHandler.removeMessages(MESSAGE_UN_STOCK);
    if (!((State) mState).isStocked) {
      mHandler.sendEmptyMessageDelayed(MESSAGE_STOCK, 200);
    } else {
      mHandler.sendEmptyMessageDelayed(MESSAGE_UN_STOCK, 200);
    }
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.article;
    String userName = null;
    if (article != null) {
      mArticle = article;
      mRealm.beginTransaction();
      mRealm.copyToRealmOrUpdate(mArticle);
      mRealm.commitTransaction();

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
    ApiClient.itemComments(article.getId()).enqueue(new Callback<ArrayList<Comment>>() {
      @Override public void onResponse(Response<ArrayList<Comment>> response) {
        mComments = response.body();
        if (mComments != null) {
          EventBus.getDefault().post(new ItemCommentsEvent(true, null, mComments));
        } else {
          mComments = new ArrayList<>();
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
      ((State) mState).stockCount =
          event.document.getElementsByClass("js-stocksCount").first().text();
      EventBus.getDefault().post(new StateEvent<>(true, null, mState));
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
        mCommentsView.loadDataWithBaseURL(
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_item_detail, menu);
    mArticleHeaderMenu = menu.findItem(R.id.action_item_menu);
    return true;
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(StateEvent<State> event) {
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

  @Override public void onBackPressed() {
    if (mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
      mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
      // TODO save comment draft
    } else {
      super.onBackPressed();
    }
  }

  @Override public void onFailure(Throwable error) {
    EventBus.getDefault().post(new ItemDetailEvent(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
  }

  private static class State extends BaseState {

    private boolean isStocked;

    private String stockCount;
  }

}
