package im.ene.lab.attiq.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
}
