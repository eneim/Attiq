package im.ene.lab.attiq.ui.widgets;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.sothree.slidinguppanel.ScrollableViewHelper;

/**
 * Created by eneim on 1/18/16.
 */
public class NestedScrollableViewHelper extends ScrollableViewHelper {

  @Override
  public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
    if (scrollableView instanceof NestedScrollView) {
      if (isSlidingUp) {
        return scrollableView.getScrollY();
      } else {
        NestedScrollView nsv = ((NestedScrollView) scrollableView);
        View child = nsv.getChildAt(0);
        return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
      }
    } else {
      return 0;
    }
  }
}
