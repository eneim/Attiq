package im.ene.lab.support.widget;

/**
 * Created by eneim on 1/7/16.
 */

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;

/**
 * Lollipop version of {@link android.support.design.widget.CircularBorderDrawable}.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CircularBorderDrawableLollipop extends im.ene.lab.support.widget.CircularBorderDrawable {

  @Override
  public void getOutline(Outline outline) {
    copyBounds(mRect);
    outline.setOval(mRect);
  }

}
