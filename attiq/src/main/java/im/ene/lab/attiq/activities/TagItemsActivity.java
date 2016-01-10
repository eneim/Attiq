package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.fragment.TagItemsFragment;
import okhttp3.Headers;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsActivity extends BaseActivity implements TagItemsFragment.Callback {

  private static final String EXTRA_TAG_NAME = "attiq_object_tag_name";

  @Bind(R.id.toolbar) Toolbar mToolbar;
  private String mTagId; // actually the Tag name

  public static Intent createIntent(Context context, String tagName) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_TAG_NAME, tagName);
    return intent;
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, TagItemsActivity.class);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_common_toolbar);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    mTagId = getIntent().getStringExtra(EXTRA_TAG_NAME);
    setTitle(getString(R.string.title_activity_tag, mTagId));
    if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, TagItemsFragment.newInstance(mTagId)).commit();
    }
  }

  @Override public void onResponseHeaders(Headers headers) {
    if (headers != null) {
      String itemCount = headers.get("Total-Count");
      try {
        int count = Integer.parseInt(itemCount);
        mToolbar.setSubtitle(
            getResources().getQuantityString(R.plurals.title_activity_tag_quantity, count, count));
      } catch (NumberFormatException er) {
        er.printStackTrace();
      }
    }
  }
}
