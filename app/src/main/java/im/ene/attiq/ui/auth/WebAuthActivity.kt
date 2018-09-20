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

package im.ene.attiq.ui.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import attiq.x.base.AttiqPref
import attiq.di.Injectable
import im.ene.attiq.ui.base.BaseActivity
import com.squareup.moshi.Moshi
import im.ene.attiq.R
import kotlinx.android.synthetic.main.activity_web_auth.authWeb
import kotlinx.android.synthetic.main.activity_web_auth.toolbar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.UUID
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named

/**
 * @author eneim (2018/05/09).
 */
class WebAuthActivity : BaseActivity(), Injectable {

  companion object {
    const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36"

    fun createIntent(context: Context): Intent {
      return Intent(context, WebAuthActivity::class.java)
    }
  }

  @Inject
  @field:Named("shared.http.client")
  lateinit var okHttpClient: OkHttpClient

  @Inject
  lateinit var moshi: Moshi

  @Inject
  @field:Named("io.executor")
  lateinit var executor: Executor

  @Inject
  lateinit var pref: AttiqPref

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_auth)
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { navigateUpOrBack(this, null) }

    CookieManager.getInstance().setAcceptCookie(true)
    authWeb.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        // catch the application callback url here and return to LoginActivity
        // attiq://lab.ene.im/qiita/oauth?code=CODE&state=STATE
        return if (url.startsWith(getString(R.string.api_callback_pattern))) {
          onAuthResult(url)
          true
        } else {
          super.shouldOverrideUrlLoading(view, request)
        }
      }
    }

    authWeb.loadUrl(Uri.parse(getString(R.string.api_token_auth, getString(R.string.client_id),
        UUID.randomUUID().toString())).toString())
  }

  fun onAuthResult(dataUri: String) {
    val callback = dataUri.replace("attiq:", "http:")
    val callbackUri = Uri.parse(callback)
    val code = callbackUri.getQueryParameter("code")
    val clientId = getString(R.string.client_id)
    val tokenRequest = TokenRequest(clientId, code)

    val url = HttpUrl.parse(getString(R.string.auth_request_token))
    //noinspection ConstantConditions
    val request = Request.Builder().url(url!!).post(RequestBody.create( //
        MediaType.parse("application/json; charset=utf-8"), //
        moshi.adapter(TokenRequest::class.java).toJson(tokenRequest))  //
    ).header("User-Agent", USER_AGENT).build()

    executor.execute {
      val response = okHttpClient.newCall(request).execute().body()
      try {
        val tokenResponse = moshi.adapter(TokenResponse::class.java)
            .fromJson(response!!.string())
        pref.setToken(tokenResponse?.token)
        finish()
      } finally {
        response!!.close()
      }
    }
  }
}