package im.ene.lab.attiq.activities;

import android.net.Uri;
import android.os.Bundle;

import im.ene.lab.attiq.util.UIUtil;

import java.util.List;

/**
 * Created by eneim on 12/15/15.
 */
public class DeepLinkActivity extends BaseActivity {

  private static final String SCHEME_INTERNAL = "attiq";

  private static final String SCHEME_HTTP = "http";

  private static final String SCHEME_HTTPS = "https";

  private static final String INTERNAL_TYPE_TAG = "tag";

  private static final String INTERNAL_TYPE_USER = "user";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Uri data = getIntent().getData();
    String scheme = data.getScheme();
    if (SCHEME_INTERNAL.equals(scheme)) {
      List<String> paths = data.getPathSegments();
      if (!UIUtil.isEmpty(paths)) {
        if (INTERNAL_TYPE_TAG.equals(paths.get(0))) {
          String lastPath = data.getLastPathSegment();
          startActivity(TagItemsActivity.createIntent(this, lastPath));
          finish();
        } else if (INTERNAL_TYPE_USER.equals(paths.get(0))) {
          String lastPath = data.getLastPathSegment();
          startActivity(ProfileActivity.createIntent(this, lastPath));
          finish();
        }
      }
    } else {
      // TODO
    }
  }
}
