package im.ene.lab.design.widget;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

public class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

  private ViewOffsetHelper mViewOffsetHelper;

  private int mTempTopBottomOffset = 0;
  private int mTempLeftRightOffset = 0;

  public ViewOffsetBehavior() {
  }

  public ViewOffsetBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
    // First let the parent lay it out
    parent.onLayoutChild(child, layoutDirection);

    if (mViewOffsetHelper == null) {
      mViewOffsetHelper = new ViewOffsetHelper(child);
    }
    mViewOffsetHelper.onViewLayout();

    if (mTempTopBottomOffset != 0) {
      mViewOffsetHelper.setTopAndBottomOffset(mTempTopBottomOffset);
      mTempTopBottomOffset = 0;
    }
    if (mTempLeftRightOffset != 0) {
      mViewOffsetHelper.setLeftAndRightOffset(mTempLeftRightOffset);
      mTempLeftRightOffset = 0;
    }

    return true;
  }

  public boolean setTopAndBottomOffset(int offset) {
    if (mViewOffsetHelper != null) {
      return mViewOffsetHelper.setTopAndBottomOffset(offset);
    } else {
      mTempTopBottomOffset = offset;
    }
    return false;
  }

  public boolean setLeftAndRightOffset(int offset) {
    if (mViewOffsetHelper != null) {
      return mViewOffsetHelper.setLeftAndRightOffset(offset);
    } else {
      mTempLeftRightOffset = offset;
    }
    return false;
  }

  public int getTopAndBottomOffset() {
    return mViewOffsetHelper != null ? mViewOffsetHelper.getTopAndBottomOffset() : 0;
  }

  public int getLeftAndRightOffset() {
    return mViewOffsetHelper != null ? mViewOffsetHelper.getLeftAndRightOffset() : 0;
  }

  static class ViewOffsetHelper {

    private final View mView;

    private int mLayoutTop;
    private int mLayoutLeft;
    private int mOffsetTop;
    private int mOffsetLeft;

    public ViewOffsetHelper(View view) {
      mView = view;
    }

    public void onViewLayout() {
      // Now grab the intended top
      mLayoutTop = mView.getTop();
      mLayoutLeft = mView.getLeft();

      // And offset it as needed
      updateOffsets();
    }

    private void updateOffsets() {
      ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
      ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));

      // Manually invalidate the view and parent to make sure we get drawn pre-M
      if (Build.VERSION.SDK_INT < 23) {
        tickleInvalidationFlag(mView);
        final ViewParent vp = mView.getParent();
        if (vp instanceof View) {
          tickleInvalidationFlag((View) vp);
        }
      }
    }

    private static void tickleInvalidationFlag(View view) {
      final float x = ViewCompat.getTranslationX(view);
      ViewCompat.setTranslationY(view, x + 1);
      ViewCompat.setTranslationY(view, x);
    }

    /**
     * Set the top and bottom offset for this {@link ViewOffsetHelper}'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    public boolean setTopAndBottomOffset(int offset) {
      if (mOffsetTop != offset) {
        mOffsetTop = offset;
        updateOffsets();
        return true;
      }
      return false;
    }

    /**
     * Set the left and right offset for this {@link ViewOffsetHelper}'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    public boolean setLeftAndRightOffset(int offset) {
      if (mOffsetLeft != offset) {
        mOffsetLeft = offset;
        updateOffsets();
        return true;
      }
      return false;
    }

    public int getTopAndBottomOffset() {
      return mOffsetTop;
    }

    public int getLeftAndRightOffset() {
      return mOffsetLeft;
    }
  }
}
