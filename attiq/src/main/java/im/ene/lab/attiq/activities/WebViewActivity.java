package im.ene.lab.attiq.activities;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by eneim on 1/15/16.
 */
public class WebViewActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    WebView mWebView = new WebView(this);
    setContentView(mWebView);
    mWebView.setWebChromeClient(new WebChromeClient());
    mWebView.loadUrl(getIntent().getDataString().replace("attiq://helper.", "http://"));
  }
}
