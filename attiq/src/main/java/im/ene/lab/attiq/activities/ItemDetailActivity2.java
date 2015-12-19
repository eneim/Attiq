package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import android.webkit.WebSettings;
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
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.ApiClient;
import im.ene.lab.attiq.data.event.Event;
import im.ene.lab.attiq.data.event.ItemCommentsEvent;
import im.ene.lab.attiq.data.event.ItemDetailEvent;
import im.ene.lab.attiq.data.response.Article;
import im.ene.lab.attiq.data.response.Comment;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.util.HtmlUtil;
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.ObservableWebView;
import im.ene.lab.attiq.widgets.drawable.ThreadedCommentDrawable;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ItemDetailActivity2 extends BaseActivity
    implements Callback<Article>, ObservableWebView.OnScrollListener {

  private static final String EXTRA_DETAIL_ITEM_ID = "attiq_item_detail_extra_id";

  private static final String EXTRA_DETAIL_ITEM_UUID = "attiq_item_detail_extra_uuid";

  private static final String TAG = "ItemDetailActivity";

  @Bind(R.id.sliding_layout) SlidingUpPanelLayout mSlidingLayout;
  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.item_content_web) ObservableWebView mContentView;
  @Bind(R.id.item_comments) WebView mComments;
  @Bind(R.id.toolbar_overlay) View mOverLayView;
  @Bind(R.id.item_title) TextView mArticleName;
  @Bind(R.id.item_subtitle) TextView mArticleDescription;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.drawer_layout) DrawerLayout mMenuLayout;
  @Bind(R.id.html_headers_container) LinearLayout mMenuContainer;
  @BindDimen(R.dimen.header_depth_width) int mHeaderDepthWidth;
  @BindDimen(R.dimen.header_depth_gap) int mHeaderDepthGap;

  private Realm mRealm;
  private PublicItem mPublicItem;
  private Element mMenuAnchor;
  // Title support
  private AppBarLayout.Behavior mAppBarBehavior;
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

  public static Intent createIntent(Context context, @NonNull PublicItem item) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, item.getId());
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, item.getUuid());
    return intent;
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, ItemDetailActivity2.class);
  }

  public static Intent createIntent(Context context, String uuid) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, uuid);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail_v2);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // empty title at start
    setTitle("");

    trySetupDrawerLayout();

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

    if (mItemUuid != null) {
      ApiClient.itemDetail(mItemUuid).enqueue(this);
    }
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    super.onDestroy();
  }

  private void trySetupDrawerLayout() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mMenuLayout, null,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    mMenuLayout.setDrawerListener(toggle);
  }

  private void trySetupContentView() {
    mContentView.setVerticalScrollBarEnabled(false);
    mContentView.setHorizontalScrollBarEnabled(false);
    mContentView.setVerticalScrollBarEnabled(true);
    WebSettings settings = mContentView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setLoadWithOverviewMode(true);
    settings.setUseWideViewPort(true);
    mContentView.setWebChromeClient(new WebChromeClient());
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
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
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
          mContentView.clearMatches();
          mContentView.loadUrl("javascript:scrollToElement(\"" + mMenuAnchor.text() + "\");");
        }
      }
    });
    mContentView.setOnScrollListener(this);
  }

  private void trySetupCommentView() {
    mComments.setVerticalScrollBarEnabled(false);
    mComments.setHorizontalScrollBarEnabled(false);
  }

  @Override public void onResponse(Response<Article> response, Retrofit retrofit) {
    Article article = response.body();
    if (article != null) {
      EventBus.getDefault().post(new ItemDetailEvent(true, null, article));
    } else {
      EventBus.getDefault().post(new ItemDetailEvent(false,
          new Event.Error(response.code(), response.message()), null));
    }
  }

  public void onEventMainThread(ItemDetailEvent event) {
    Article article = event.getArticle();
    if (article != null) {
      mArticleName.setText(article.getTitle());
      mSpannableTitle = new SpannableString(article.getTitle());
      String userName = article.getUser().getId();
      final CharSequence subTitle;

      if (mPublicItem != null) {
        subTitle = Html.fromHtml(getString(R.string.item_user_info,
            userName, userName, TimeUtil.beautify(article.getCreatedAt())));
      } else {
        subTitle = getString(R.string.item_detail_subtitle, userName);
      }

      mArticleDescription.setText(subTitle);
      mSpannableSubtitle = new SpannableString(subTitle);

      updateTitle();
      buildArticleComments(article);
      buildArticleMenu(article);
      final String html;
      try {
        html = IOUtil.readRaw(this, R.raw.article);

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

  private void buildArticleComments(@NonNull final Article article) {
    ApiClient.itemComments(article.getId()).enqueue(new Callback<List<Comment>>() {
      @Override public void onResponse(Response<List<Comment>> response, Retrofit retrofit) {
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
      int topLevel = HtmlUtil.getHeaderLevel(items.next().tagName());
      while (items.hasNext()) {
        int level = HtmlUtil.getHeaderLevel(items.next().tagName());
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

        int currentLevel = HtmlUtil.getHeaderLevel(item.tagName());
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
    }
  }

  public void onEventMainThread(ItemCommentsEvent event) {
    if (event.isSuccess() && !UIUtil.isEmpty(event.getComments())) {
      List<Comment> comments = event.getComments();
      final String html;
      try {
        html = IOUtil.readAllFromAssets(this, "html/comments.html");

        Document fullBody = Jsoup.parse(html);
        Element content = fullBody.getElementById("content");

        // TODO support comment ordering
        for (Comment comment : comments) {
          String commentHtml = IOUtil.readAllFromAssets(this, "html/comment.html");
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_item_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_item_menu) {
      mMenuLayout.openDrawer(GravityCompat.END);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Bind(R.id.main_container) CoordinatorLayout mMainContainer;
  @BindDimen(R.dimen.app_bar_min_offset) int mMinOffset;

  @Override public void onScrolled(View view, int scrollX, int scrollY, int oldX, int oldY) {
    int diffY = scrollY - oldY;
    float oldTransY = mToolbarLayoutOffset;
    mAppBarBehavior = (AppBarLayout.Behavior) ((CoordinatorLayout.LayoutParams)
        mAppBarLayout.getLayoutParams()).getBehavior();
    if (mAppBarBehavior != null) {
      mAppBarBehavior.setHeaderTopBottomOffset(mMainContainer,
          mAppBarLayout, (int) (oldTransY - diffY), -mMinOffset, Math.min(0, mMinOffset - scrollY));
    }

    mToolBarLayout.setScrimsShown(mToolBarLayout
        .shouldTriggerScrimOffset(mToolbarLayoutOffset));
  }
}
