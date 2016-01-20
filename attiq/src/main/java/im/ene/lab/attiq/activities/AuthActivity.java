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

package im.ene.lab.attiq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;

/**
 * Created by eneim on 12/13/15.
 */
public class AuthActivity extends AppCompatActivity {

  private static final String TAG = "WebViewActivity";
  private WebViewClient mWebClient = new WebViewClient() {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      // catch the application callback url here and return to LoginActivity
      // attiq://lab.ene.im/qiita/oauth?code=CODE&state=STATE
      if (url.startsWith(getString(R.string.api_callback))) {
        Intent result = new Intent(AuthActivity.this, MainActivity.class);
        // setup return result
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.EXTRA_AUTH_CALLBACK, url);
        result.putExtras(bundle);
        setResult(RESULT_OK, result);
        finish();
        return true;
      } else {
        return super.shouldOverrideUrlLoading(view, url);
      }
    }

    @Override public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (url.startsWith(getString(R.string.api_callback))) {
        String cookies = CookieManager.getInstance().getCookie(url);
        Log.e(TAG, "onPageFinished: " + cookies);
      }
    }
  };

  private WebChromeClient mChromeClient = new WebChromeClient() {

  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    WebView webView = new WebView(this);
    setContentView(webView);
    webView.setWebViewClient(mWebClient);
    webView.loadUrl(ApiClient.authCallback());
  }

}
