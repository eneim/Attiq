package im.ene.lab.attiq.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.Header;
import im.ene.lab.attiq.ui.fragment.TagItemsFragment;
import im.ene.lab.attiq.util.UIUtil;
import okhttp3.Headers;

import java.util.Iterator;
import java.util.List;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsActivity extends BaseActivity implements TagItemsFragment.Callback {

  @Bind(R.id.toolbar) Toolbar mToolbar;
  private String mTagId; // actually the Tag name

  public static Intent createIntent(Context context, String tagName) {
    Intent intent = new Intent(context, TagItemsActivity.class);
    Uri data = Uri.parse(context.getString(R.string.data_tags_url, tagName));
    intent.setData(data);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_common_toolbar);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Uri data = getIntent().getData();
    if (data != null) {
      List<String> paths = data.getPathSegments();
      if (!UIUtil.isEmpty(paths)) {
        Iterator<String> iterator = paths.iterator();
        while (iterator.hasNext()) {
          if ("tags".equals(iterator.next())) {
            mTagId = iterator.next();
            break;
          }
        }
      }
    }

    setTitle(getString(R.string.title_activity_tag, mTagId));
    if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, TagItemsFragment.newInstance(mTagId)).commit();
    }
  }

  @Override public void onResponseHeaders(Headers headers) {
    if (headers != null) {
      String itemCount = headers.get(Header.Response.TOTAL_COUNT);
      try { // catch NumberFormatException, in case their API returns something weird
        int count = Integer.parseInt(itemCount);
        mToolbar.setSubtitle(
            getResources().getQuantityString(R.plurals.title_activity_tag_quantity, count, count));
      } catch (NumberFormatException er) {
        er.printStackTrace();
      }
    }
  }

}
