package im.ene.lab.attiq.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import im.ene.lab.attiq.fragment.TagItemsFragment;

/**
 * Created by eneim on 1/10/16.
 */
public class TagItemsActivity extends BaseActivity {

  private static final String EXTRA_TAG_NAME = "attiq_profile_tag_name";

  private String mTagId; // actually the User name

  public static Intent createIntent(Context context, String userName) {
    Intent intent = createIntent(context);
    intent.putExtra(EXTRA_TAG_NAME, userName);
    return intent;
  }

  private static Intent createIntent(Context context) {
    return new Intent(context, TagItemsActivity.class);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTagId = getIntent().getStringExtra(EXTRA_TAG_NAME);
    if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, TagItemsFragment.newInstance(mTagId))
          .commit();
    }
  }
}
