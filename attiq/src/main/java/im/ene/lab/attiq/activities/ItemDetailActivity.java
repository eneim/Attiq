package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.ApiClient;
import im.ene.lab.attiq.data.event.Event;
import im.ene.lab.attiq.data.event.ItemDetailEvent;
import im.ene.lab.attiq.data.response.Item;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.util.IOUtil;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import us.feras.mdv.MarkdownView;

import java.io.IOException;

public class ItemDetailActivity extends BaseActivity implements Callback<Item> {

  private static final String EXTRA_DETAIL_ITEM_ID = "attiq_item_detail_extra_id";

  private static final String EXTRA_DETAIL_ITEM_UUID = "attiq_item_detail_extra_uuid";

  public static Intent createIntent(Context context) {
    return new Intent(context, ItemDetailActivity.class);
  }

  public static Intent createIntent(Context context, @NonNull PublicItem item) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, item.getId());
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, item.getUuid());
    return intent;
  }

  private Realm mRealm;
  private MarkdownView mContentWebView;

  private PublicItem mPublicItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail);
    mContentWebView = (MarkdownView) findViewById(R.id.item_content_web);
    mContentWebView.setVerticalScrollBarEnabled(false);
    mContentWebView.setHorizontalScrollBarEnabled(false);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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

  @Override public void onResponse(Response<Item> response, Retrofit retrofit) {
    Item item = response.body();
    if (item != null) {
      mEventBus.post(new ItemDetailEvent(true, null, item));
    } else {
      mEventBus.post(new ItemDetailEvent(false,
          new Event.Error(response.code(), response.message()), null));
    }
  }

  @Override public void onFailure(Throwable error) {
    mEventBus.post(new ItemDetailEvent(false,
        new Event.Error(Event.Error.ERROR_UNKNOWN, error.getLocalizedMessage()), null));
  }

  public void onEventMainThread(ItemDetailEvent event) {
    Item item = event.getItem();
    if (item != null) {
      final String html;
      try {
        html = IOUtil.readAllFromAssets(this, "html/article.html");

        Document doc = Jsoup.parse(html);
        Element elem = doc.getElementById("content");
        elem.append(item.getRenderedBody());

        String result = doc.outerHtml();
        mContentWebView.loadDataWithBaseURL(
            item.getUrl(), result, null, null, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
