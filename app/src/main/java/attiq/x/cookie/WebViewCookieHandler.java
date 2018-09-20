/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
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

package attiq.x.cookie;

import android.webkit.CookieManager;
import attiq.x.cookie.cache.CookieCache;
import attiq.x.cookie.persistence.CookiePersistor;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * A CookieJar used by OkHttp that merges Cookie of WebView and OkHttp cookies.
 *
 * @author eneim
 * @since 2/1/17
 */

public final class WebViewCookieHandler extends PersistentCookieJar {

  private CookieManager cookieManager = CookieManager.getInstance();

  public WebViewCookieHandler(CookieCache cache, CookiePersistor persistor) {
    super(cache, persistor);
  }

  @Override public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    super.saveFromResponse(url, cookies);
    String urlString = url.toString();

    for (Cookie cookie : cookies) {
      cookieManager.setCookie(urlString, cookie.toString());
    }
  }

  @Override public synchronized List<Cookie> loadForRequest(HttpUrl url) {
    List<Cookie> presets = super.loadForRequest(url);

    String urlString = url.toString();
    String cookiesString = cookieManager.getCookie(urlString);

    if (cookiesString != null && !cookiesString.isEmpty()) {
      //We can split on the ';' char as the cookie manager only returns cookies
      //that match the url and haven't expired, so the cookie attributes aren't included
      String[] cookieHeaders = cookiesString.split(";");

      for (String header : cookieHeaders) {
        presets.add(Cookie.parse(url, header));
      }
    }

    return presets;
  }

  @Override public synchronized void clearSession() {
    super.clearSession();
    cookieManager.removeSessionCookies(null);
  }

  @Override public synchronized void clear() {
    super.clear();
    cookieManager.removeAllCookies(null);
  }
}
