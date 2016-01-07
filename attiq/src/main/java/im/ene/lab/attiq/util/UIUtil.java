package im.ene.lab.attiq.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Collection;

/**
 * Created by eneim on 12/14/15.
 */
public class UIUtil {

  private UIUtil() {
    throw new AssertionError("Not supported");
  }

  /**
   * Get status bar of devices. Old devices have the 25dp of that height, but new ones have 24dp
   *
   * @param context from Hosted Activity
   * @return current status bar height
   */
  public static int getStatusBarHeight(@NonNull Context context) {
    Resources resources = context.getResources();
    int result = 0;
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId);
    }
    return result;
  }

  public static boolean isEmpty(CharSequence text) {
    return text == null || TextUtils.isEmpty(text);
  }

  public static boolean isEmpty(Collection list) {
    return list == null || list.size() == 0;
  }

  public static int getColor(@NonNull Context context, @ColorRes int colorId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources().getColor(colorId, context.getTheme());
    } else {
      return context.getResources().getColor(colorId);
    }
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
}
