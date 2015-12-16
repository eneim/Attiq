package im.ene.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    Log.d(TAG, "onLayout() called with: " + "changed = [" + changed + "], l = [" + l + "], t = ["
        + t + "], r = [" + r + "], b = [" + b + "]");
  }

  @Override public void onLayoutChild(View child, int layoutDirection) {
    super.onLayoutChild(child, layoutDirection);
    Log.d(TAG, "onLayoutChild() called with: " + "child = [" + child + "], layoutDirection = [" +
        layoutDirection + "]");
  }

  @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    Log.e(TAG, "onStartNestedScroll(): " + "child = [" + child.getClass().getSimpleName()
        + "], target = [" + target.getClass().getSimpleName()
        + "], nestedScrollAxes = [" + nestedScrollAxes + "]");
    return super.onStartNestedScroll(child, target, nestedScrollAxes);
  }

  @Override public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    super.onNestedPreScroll(target, dx, dy, consumed);
    Log.i(TAG, "onNestedPreScroll(): " + "target = [" + target.getClass().getSimpleName()
        + "], dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed + "]");
  }

  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int
      dyUnconsumed) {
    super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    Log.i(TAG, "onNestedScroll(): " + "target = [" + target.getClass().getSimpleName()
        + "], dxConsumed = [" + dxConsumed + "], dyConsumed = [" + dyConsumed
        + "], dxUnconsumed = [" + dxUnconsumed + "], dyUnconsumed = [" + dyUnconsumed + "]");
  }

  @Override public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    Log.w(TAG, "onNestedPreFling(): " + "target = [" + target.getClass().getSimpleName()
        + "], velocityX = [" + velocityX + "], velocityY = [" + velocityY + "]");
    return super.onNestedPreFling(target, velocityX, velocityY);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    Log.w(TAG, "onNestedFling(): " + "target = [" + target.getClass().getSimpleName()
        + "], velocityX = [" + velocityX + "], velocityY = [" + velocityY
        + "], consumed = [" + consumed + "]");
    return super.onNestedFling(target, velocityX, velocityY, consumed);
  }

  @Override public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    super.onNestedScrollAccepted(child, target, nestedScrollAxes);
    Log.d(TAG, "onNestedScrollAccepted(): " + "child = [" + child.getClass().getSimpleName()
        + "], target = [" + target.getClass().getSimpleName()
        + "], nestedScrollAxes = [" + nestedScrollAxes + "]");
  }

  @Override public void onStopNestedScroll(View target) {
    super.onStopNestedScroll(target);
    Log.e(TAG, "onStopNestedScroll(): " + "target = [" + target.getClass().getSimpleName() + "]");
  }
}
