package im.ene.lab.attiq.ui.widgets;

import android.content.Context;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by eneim on 12/18/15.
 */
public class NotBadNavView extends ScrimInsetsFrameLayout {

  public NotBadNavView(Context context) {
    super(context);
  }

  public NotBadNavView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NotBadNavView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return true;
  }
}
