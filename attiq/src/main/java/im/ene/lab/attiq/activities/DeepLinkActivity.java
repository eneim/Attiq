package im.ene.lab.attiq.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by eneim on 12/15/15.
 */
public class DeepLinkActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Uri data = getIntent().getData();
    String lastPath = data.getLastPathSegment();
    Intent intent = ItemDetailActivity.createIntent(this, lastPath);
    startActivity(intent);
    finish();
  }
}
