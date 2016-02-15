/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import im.ene.lab.attiq.util.UIUtil;
import java.util.Iterator;
import java.util.List;

/**
 * Created by eneim on 12/15/15.
 *
 * @Deprecated Currently, each Activity will listen to its Uri by itself. There is no need to use
 * this proxy class.
 */
@Deprecated public class DeepLinkActivity extends AppCompatActivity {

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
