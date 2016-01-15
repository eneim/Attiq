package im.ene.lab.attiq.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import im.ene.lab.attiq.util.UIUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by eneim on 12/15/15.
 */
public class DeepLinkActivity extends BaseActivity {

  private static final String SCHEME_INTERNAL = "attiq";

  private static final String SCHEME_HTTP = "http";

  private static final String SCHEME_HTTPS = "https";

  private static final String TYPE_TAG = "tags";

  private static final String TYPE_USER = "users";

  private static final String TYPE_ITEMS = "items";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Uri data = getIntent().getData();
    String scheme = data.getScheme();
    List<String> paths = data.getPathSegments();
    if (SCHEME_INTERNAL.equals(scheme)) {
      if (!UIUtil.isEmpty(paths)) {
        if (TYPE_TAG.equals(paths.get(0))) {
          String lastPath = data.getLastPathSegment();
          TaskStackBuilder.create(this)
              .addParentStack(TagItemsActivity.class)
              .addNextIntent(TagItemsActivity.createIntent(this, lastPath))
              .startActivities();
        } else if (TYPE_USER.equals(paths.get(0))) {
          String lastPath = data.getLastPathSegment();
          startActivity(ProfileActivity.createIntent(this, lastPath));
        }
      }
    } else if (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme)) {
      if (!UIUtil.isEmpty(paths)) {
        Iterator<String> iterator = paths.iterator();
        String id = null;
        while (iterator.hasNext()) {
          if (TYPE_ITEMS.equals(iterator.next())) {
            id = iterator.next();
            break;
          }
        }

        if (id != null) {
          TaskStackBuilder.create(this)
              .addParentStack(ItemDetailActivity.class)
              .addNextIntent(ItemDetailActivity.createIntent(this, id))
              .startActivities();
          // startActivity(ItemDetailActivity.createIntent(this, id));
        }
      }
    }

    finish();
  }
}
