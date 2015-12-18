package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import butterknife.Bind;
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
import im.ene.lab.attiq.util.IOUtil;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.support.design.widget.AlphaForegroundColorSpan;
import im.ene.support.design.widget.AppBarLayout;
import im.ene.support.design.widget.CollapsingToolbarLayout;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.List;

public class ItemDetailActivity extends BaseActivity implements Callback<Article> {

  private static final String EXTRA_DETAIL_ITEM_ID = "attiq_item_detail_extra_id";

  private static final String EXTRA_DETAIL_ITEM_UUID = "attiq_item_detail_extra_uuid";
  private static final String TAG = "ItemDetailActivity";

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.sliding_layout) SlidingUpPanelLayout mSlidingLayout;
  @Bind(R.id.item_content_web) WebView mContentView;
  @Bind(R.id.item_comments) WebView mComments;
  @Bind(R.id.toolbar_overlay) View mOverLayView;
  @Bind(R.id.item_title) TextView mItemTitle;
  @Bind(R.id.item_subtitle) TextView mItemSubtitle;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
  @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;

  private Realm mRealm;
  private PublicItem mPublicItem;
  // Title support
  private AlphaForegroundColorSpan mTitleColorSpan;
  private SpannableString mSpannableTitle;
  private SpannableString mSpannableSubtitle;
  private int mCurrentVerticalOffset;
  private AppBarLayout.OnOffsetChangedListener mOffsetChangedListener =
      new AppBarLayout.OnOffsetChangedListener() {
        @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
          mCurrentVerticalOffset = verticalOffset;
          float maxOffset = mToolBarLayout.getHeight() -
              ViewCompat.getMinimumHeight(mToolBarLayout) - mToolBarLayout.getInsetTop();
          if (maxOffset > 0) {
            float offsetFraction = Math.abs(verticalOffset) / maxOffset;
            mOverLayView.setAlpha(1.f - offsetFraction);
          }
        }
      };
  private String mItemUUID;

  public static Intent createIntent(Context context, @NonNull PublicItem item) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, item.getId());
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, item.getUuid());
    return intent;
  }

  public static Intent createIntent(Context context) {
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

    // empty title at start
    setTitle("");

    trySetupDrawerLayout();

    trySetupContentView();

    trySetupCommentView();

    mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);

    mItemSubtitle.setClickable(true);
    mItemSubtitle.setMovementMethod(LinkMovementMethod.getInstance());
    // dynamically update padding
    mItemTitle.setPadding(
        mItemTitle.getPaddingLeft(),
        mItemTitle.getPaddingTop() + UIUtil.getStatusBarHeight(this),
        mItemTitle.getPaddingRight(),
        mItemTitle.getPaddingBottom()
    );

    TypedValue typedValue = new TypedValue();
    mToolbar.getContext().getTheme()
        .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
    int titleColorId = typedValue.resourceId;
    mTitleColorSpan = new AlphaForegroundColorSpan(UIUtil.getColor(this, titleColorId));

    mRealm = Attiq.realm();
    mItemUUID = getIntent().getStringExtra(EXTRA_DETAIL_ITEM_UUID);
    mPublicItem = mRealm.where(PublicItem.class).equalTo("uuid", mItemUUID).findFirst();
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    super.onDestroy();
  }

  private void trySetupDrawerLayout() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, null,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    mDrawerLayout.setDrawerListener(toggle);
  }

  private void trySetupContentView() {
    mContentView.setVerticalScrollBarEnabled(false);
    mContentView.setHorizontalScrollBarEnabled(false);
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
        // TODO show loading dialog here
        Log.e(TAG, "onPageStarted() called with: " + "view = [" + view + "], url = [" + url + "]," +
            " favicon = [" + favicon + "]");
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // TODO dismiss loading dialog here
        Log.e(TAG, "onPageFinished() called with: " + "view = [" + view + "], url = [" + url + "]");
      }
    });
  }

  private void trySetupCommentView() {
    mComments.setVerticalScrollBarEnabled(false);
    mComments.setHorizontalScrollBarEnabled(false);
    // TODO implement this
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
      mItemTitle.setText(article.getTitle());
      mSpannableTitle = new SpannableString(article.getTitle());
      String userName = article.getUser().getId();
      if (mPublicItem != null) {
        final Spanned subTitle = Html.fromHtml(getString(R.string.item_user_info,
            userName, userName, mPublicItem.getCreatedAtInWords()));
        mItemSubtitle.setText(subTitle);
      } else {
        mItemSubtitle.setText(getString(R.string.item_detail_subtitle, userName));
      }

      mSpannableSubtitle = new SpannableString("posted by " + userName);

      updateTitle();
      processComments(article);
      processMenu(article);
      final String html;
      try {
        html = IOUtil.readAllFromAssets(this, "html/article.html");

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
        mToolBarLayout.shouldTriggerScrimOffset(mCurrentVerticalOffset) ? 1.f : 0.f;
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

  private void processComments(@NonNull final Article article) {
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

  // TODO Item's menu by header tags
  private void processMenu(@NonNull Article article) {

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
            "http://qiita.com", result, null, null, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }  @Override public void onFailure(Throwable error) {
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
    if (mItemUUID != null) {
      ApiClient.itemDetail(mItemUUID).enqueue(this);
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
      mDrawerLayout.openDrawer(GravityCompat.END);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void trySetupToolbarMenu() {

  }




}
