package im.ene.lab.attiq.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Collection;

/**
 * Created by eneim on 12/14/15.
 */
public class UIUtil {

  private UIUtil() {
    throw new AssertionError("Not supported");
  }

  public static int getStatusBarHeight(Context context) {
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

}
