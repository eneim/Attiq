package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

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
  @Bind(R.id.item_content_web) WebView mContentView;
  @Bind(R.id.item_comments) WebView mComments;
  @Bind(R.id.item_title) TextView mOverLayView;
  @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
  @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
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

  public static Intent createIntent(Context context, @NonNull PublicItem item) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, item.getId());
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, item.getUuid());
    return intent;
  }

  public static Intent createIntent(Context context) {
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

    mComments.setVerticalScrollBarEnabled(false);
    mComments.setHorizontalScrollBarEnabled(false);
    // TODO implement this
    mComments.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
      }
    });

    mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener);

    TypedValue typedValue = new TypedValue();
    mToolbar.getContext().getTheme()
        .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
    int titleColorId = typedValue.resourceId;
    mTitleColorSpan = new AlphaForegroundColorSpan(UIUtil.getColor(this, titleColorId));

    mRealm = Attiq.realm();
    long itemId = getIntent().getLongExtra(EXTRA_DETAIL_ITEM_ID, 0);
    mPublicItem = mRealm.where(PublicItem.class).equalTo("id", itemId).findFirst();
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    if (mPublicItem != null) {
      ApiClient.itemDetail(mPublicItem.getUuid()).enqueue(this);
    }
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
      mOverLayView.setText(article.getTitle());
      mSpannableTitle = new SpannableString(article.getTitle());
      mSpannableSubtitle = new SpannableString(article.getUser().getId());
      updateTitle();
      processComments(article);
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

  @Override public void onFailure(Throwable error) {
    EventBus.getDefault().post(new ItemDetailEvent(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
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

    NestedScrollView test;
  }

  // TODO Item's menu by header tags
  private void processMenu() {

  }


}
