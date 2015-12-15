package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.util.IOUtil;
import io.realm.Realm;

import java.io.IOException;

public class ItemDetailActivity extends BaseActivity {

  private static final String EXTRA_DETAIL_ITEM_ID = "attiq_item_detail_extra_id";

  private static final String EXTRA_DETAIL_ITEM_UUID = "attiq_item_detail_extra_uuid";

  public static Intent createIntent(Context context) {
    return new Intent(context, ItemDetailActivity.class);
  }

  public static Intent creatIntent(Context context, @NonNull Integer itemId,
                                   @NonNull String itemUUID) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_DETAIL_ITEM_ID, itemId);
    intent.putExtra(EXTRA_DETAIL_ITEM_UUID, itemUUID);
    return intent;
  }

  private Realm mRealm;
  private WebView mContentWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail);
    mContentWebView = (WebView) findViewById(R.id.item_content_web);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    mRealm = Attiq.realm();
  }

  @Override protected void onDestroy() {
    if (mRealm != null) {
      mRealm.close();
    }
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    try {
      final String html = IOUtil.readAllFromAssets(this, "html/article.html");

      Document doc = Jsoup.parse(html);
      Element elem = doc.getElementById("content");
      // elem.append(content.get());

      String result = doc.outerHtml();
      mContentWebView.loadData(result, null, null);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
