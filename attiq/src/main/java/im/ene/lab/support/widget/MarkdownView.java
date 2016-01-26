package im.ene.lab.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import im.ene.lab.attiq.util.UIUtil;

/**
 * Created by eneim on 1/17/16.
 */
public class MarkdownView extends WebView {

  public MarkdownView(Context context) {
    this(context, null);
  }

  public MarkdownView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    ThemeUtils.checkAppCompatTheme(context);

  }

  /**
   * Loads the given Markdown text to the view as rich formatted HTML. The
   * HTML output will be styled based on the given CSS file.
   *
   * @param txt        - input in markdown format
   * @param cssFileUrl - a URL to css File. If the file located in the project assets
   *                   folder then the URL should start with "file:///android_asset/"
   */
  public void loadMarkdown(String txt, String cssFileUrl) {
    loadMarkdownToView(txt, cssFileUrl);
  }

  private void loadMarkdownToView(String markdown, String cssFileUrl) {
    String html = UIUtil.parseMarkdown(markdown);
    if (cssFileUrl != null) {
      html = "<link rel='stylesheet' type='text/css' href='" + cssFileUrl + "' />" + html;
    }
    loadDataWithBaseURL("fake://", html, "text/html", "UTF-8", null);
  }

}
