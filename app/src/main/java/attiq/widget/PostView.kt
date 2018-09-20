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

package attiq.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import im.ene.attiq.Attiq
import im.ene.attiq.BuildConfig
import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStreamReader
import java.util.HashMap

/**
 * @author eneim
 * @since 12/18/16
 *
 * Tribute to the source of motivation
 */

class PostView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {

  companion object {
    val template: Template by lazy {
      try {
        Mustache.compiler().compile(InputStreamReader(Attiq.get()!!.assets //
            .open("web/post.html", AssetManager.ACCESS_STREAMING)))
      } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException("Could not load html", e)
      }
    }
  }

  private var needMathJax = false

  init {
    this.settings.setAppCacheEnabled(true)
    this.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
    this.settings.setAppCachePath(context.cacheDir.path + "/web_cache")
  }

  @Suppress("RedundantOverride")
  @SuppressLint("SetJavaScriptEnabled")
  fun render(title: String, body: Any) {
    this.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
      }

      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
      }
    }

    this.settings.javaScriptEnabled = true
    this.settings.loadWithOverviewMode = true
    this.isScrollContainer = false

    val hashMap = HashMap<String, Any>()
    hashMap["title"] = title
    hashMap["bodyHtml"] = processTexContentHtml(body.toString())
    hashMap["need_mathjax"] = needMathJax
    super.loadDataWithBaseURL(BuildConfig.BASE_URL, //
        template.execute(hashMap), "text/html", "utf-8", "about:blank")
  }

  private fun processTexContentHtml(content: String): String {
    val contentHtml = Jsoup.parseBodyFragment(content)
    val mathElements = contentHtml.select("div.code-frame[data-lang=\"math\"]")
    needMathJax = mathElements != null && mathElements.size > 0
    if (needMathJax) {
      mathElements.flatMap { it.children() }.forEach {
        val rawTex = "$$" + it.text() + "$$"
        it.text(rawTex)
      }
    }

    return contentHtml.body().html()
  }
}
