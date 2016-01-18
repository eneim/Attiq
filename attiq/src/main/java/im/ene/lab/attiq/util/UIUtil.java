package im.ene.lab.attiq.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Property;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.ene.lab.attiq.R;
import im.ene.lab.attiq.util.markdown.Marked;

import java.util.Collection;

/**
 * Created by eneim on 12/14/15.
 */
public class UIUtil {

  private UIUtil() {
    throw new AssertionError("Not supported");
  }

  public static String parseMarkdown(String markdown) {
    return Marked.marked(markdown);
  }

  /**
   * Get status bar of devices. Old devices have the 25dp of that height, but new ones have 24dp
   *
   * @param context from Hosted Activity
   * @return current status bar height
   */
  public static int getStatusBarHeight(@NonNull Context context) {
    Resources resources = context.getResources();
    final int result;
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId);
    } else {
      // fallback to local resource
      result = resources.getDimensionPixelSize(R.dimen.status_bar_height);
    }
    return result;
  }

  public static boolean isEmpty(CharSequence text) {
    return text == null || TextUtils.isEmpty(text);
  }

  public static boolean isEmpty(Collection list) {
    return list == null || list.size() == 0;
  }

  public static int getDimen(Context context, @DimenRes int dimenId) {
    return context.getResources().getDimensionPixelSize(dimenId);
  }

  public static void commingSoon(Context context) {
    if (context != null) {
      Toast.makeText(context, "Coming Soon! ^^", Toast.LENGTH_SHORT).show();
    }
  }

  public static void openWebsite(Activity activity, String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }

  public static void openFacebookUser(Activity activity, String username) {
    String url = "https://www.facebook.com/" + username;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }

  public static void openTwitterUser(Activity activity, String username) {
    String url = "https://twitter.com/" + username;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }

  public static void openGithubUser(Activity activity, String username) {
    String url = "https://github.com/" + username;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }

  public static void openLinkedinUser(Activity activity, String username) {
    String url = "https://www.linkedin.com/in/" + username;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }

  public static String beautify(String text) {
    return text == null ? null : text.trim();
  }

  public static void stripUnderlines(TextView textView, Spannable ignoredUrl, boolean strip) {
    Spannable s = (Spannable) textView.getText();
    URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
    for (URLSpan span : spans) {
      int start = s.getSpanStart(span);
      int end = s.getSpanEnd(span);
      s.removeSpan(span);
      span = new NoUnderlineURLSpan(span.getURL(), ignoredUrl, strip);
      s.setSpan(span, start, end, 0);
    }
    textView.setText(s);
  }

  public static void stripUnderlines(TextView textView, Spannable ignoredUrl) {
    stripUnderlines(textView, ignoredUrl, true);
  }

  public static void stripUnderlines(TextView textView) {
    stripUnderlines(textView, null);
  }

  private static class NoUnderlineURLSpan extends URLSpan {

    private static final String TAG = "NoUnderlineURLSpan";

    private final Spannable ignoredUrl;
    private final boolean isStrip;

    public NoUnderlineURLSpan(String url, Spannable ignoredUrl, boolean isStrip) {
      super(url);
      this.ignoredUrl = ignoredUrl;
      this.isStrip = isStrip;
    }

    @Override public void updateDrawState(TextPaint ds) {
      super.updateDrawState(ds);
      ds.setUnderlineText(!isStrip);
    }

    @Override public void onClick(View widget) {
      URLSpan[] spans = ignoredUrl != null ? ignoredUrl.getSpans(0, ignoredUrl.length(),
          URLSpan.class) : null;
      boolean isClickable = true; // true at first
      if (spans == null) {
        isClickable = true;       // no reference, then true
      } else {
        for (URLSpan span : spans) {
          if (span.getURL().equals(getURL())) {
            isClickable = false;  // find an existed url, break
            break;
          }
        }
      }

      if (isClickable) {
        super.onClick(widget);
      } else {

      }
    }
  }

  public static final Property<View, Integer> BACKGROUND_COLOR
      = new AnimUtils.IntProperty<View>("backgroundColor") {

    @Override
    public void setValue(View view, int value) {
      view.setBackgroundColor(value);
    }

    @Override
    public Integer get(View view) {
      Drawable d = view.getBackground();
      if (d instanceof ColorDrawable) {
        return ((ColorDrawable) d).getColor();
      }
      return Color.TRANSPARENT;
    }
  };
}
