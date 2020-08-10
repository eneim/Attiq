/*
 * Copyright (c) 2020 Nam Nguyen, nam@ene.im
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

package app.attiq.widget

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import app.attiq.AttiqApp
import app.attiq.BuildConfig
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStreamReader
import java.util.HashMap

class ArticleView : WebView {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
  )

  private var needMathJax: Boolean = false

  init {
    settings.apply {
      setAppCacheEnabled(true)
      cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
      setAppCachePath(
        context.cacheDir
          .path + "/attiq_web_cache"
      )
      javaScriptEnabled = true
      loadWithOverviewMode = true
    }

    this.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
      ): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = request.url
        context.startActivity(intent)
        return true
      }
    }

    this.isScrollContainer = false
  }

  companion object {
    private val template: Template by lazy {
      try {
        Mustache.compiler()
          .compile(
            InputStreamReader(
              AttiqApp.get()
                .assets
                .open("web/article.html", AssetManager.ACCESS_STREAMING)
            )
          )
      } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException("Could not load html", e)
      }
    }
  }

  fun renderArticle(
    title: String,
    body: Any
  ) {
    val hashMap = HashMap<String, Any>()
    hashMap["title"] = title
    hashMap["bodyHtml"] = processTexContentHtml(body.toString()).trim()
    hashMap["need_mathjax"] = needMathJax
    super.loadDataWithBaseURL(
      BuildConfig.BASE_URL,
      template.execute(hashMap), "text/html", "utf-8", "about:blank"
    )
  }

  private fun processTexContentHtml(content: String): String {
    val document = Jsoup.parseBodyFragment(content)
    val mathElements = document.select("div.code-frame[data-lang=\"math\"]")
    needMathJax = !mathElements.isNullOrEmpty()
    if (needMathJax) {
      mathElements
        .flatMap { it.children() }
        .forEach {
          val rawTex = "$$" + it.text() + "$$"
          it.text(rawTex)
        }
    }

    return document.body().html()
  }
}
