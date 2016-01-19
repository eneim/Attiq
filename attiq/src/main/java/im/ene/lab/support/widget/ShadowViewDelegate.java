package im.ene.lab.support.widget;

import android.graphics.drawable.Drawable;

/**
 * Created by eneim on 1/7/16.
 */
interface ShadowViewDelegate {
  float getRadius();
  void setShadowPadding(int left, int top, int right, int bottom);
  void setBackgroundDrawable(Drawable background);
}
