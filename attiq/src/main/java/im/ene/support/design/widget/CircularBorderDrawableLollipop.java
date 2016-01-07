package im.ene.support.design.widget;

/**
 * Created by eneim on 1/7/16.
 */

import android.graphics.Outline;

/**
 * Lollipop version of {@link android.support.design.widget.CircularBorderDrawable}.
 */
class CircularBorderDrawableLollipop extends CircularBorderDrawable {

  @Override
  public void getOutline(Outline outline) {
    copyBounds(mRect);
    outline.setOval(mRect);
  }

}
