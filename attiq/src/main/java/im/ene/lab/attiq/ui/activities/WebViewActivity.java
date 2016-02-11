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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.util.PrefUtil;
import im.ene.lab.attiq.util.UIUtil;

/**
 * Created by eneim on 1/15/16.
 */
public class WebViewActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    WebView mWebView = new WebView(this);
    setContentView(mWebView);
    mWebView.setWebChromeClient(new WebChromeClient());
    if (PrefUtil.checkNetwork(this)) {
      mWebView.loadUrl(getIntent().getDataString().replace("attiq://helper.", "http://"));
    }
  }

  @Override protected int lookupTheme(UIUtil.Themes themes) {
    return themes == UIUtil.Themes.DARK ? R.style.Attiq_Theme_Dark : R.style.Attiq_Theme_Light;
  }
}
