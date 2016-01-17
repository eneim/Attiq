package im.ene.lab.design.widget;

import android.content.Context;
import android.util.AttributeSet;

public class CoordinatorLayout extends android.support.design.widget.CoordinatorLayout {

  public static final String TAG = CoordinatorLayout.class.getSimpleName();

  public CoordinatorLayout(Context context) {
    super(context);
  }

  public CoordinatorLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
