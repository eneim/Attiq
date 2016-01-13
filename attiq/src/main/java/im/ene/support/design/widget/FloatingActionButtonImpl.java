package im.ene.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by eneim on 1/7/16.
 */
abstract class FloatingActionButtonImpl {

  interface InternalVisibilityChangedListener {
    public void onShown();

    public void onHidden();
  }

  static final int SHOW_HIDE_ANIM_DURATION = 200;

  static final int[] PRESSED_ENABLED_STATE_SET = {
      android.R.attr.state_pressed,
      android.R.attr.state_enabled
  };

  static final int[] FOCUSED_ENABLED_STATE_SET = {
      android.R.attr.state_focused,
      android.R.attr.state_enabled
  };

  static final int[] EMPTY_STATE_SET = new int[0];

  final View mView;
  final ShadowViewDelegate mShadowViewDelegate;

  private ViewTreeObserver.OnPreDrawListener mPreDrawListener;

  FloatingActionButtonImpl(View view, ShadowViewDelegate shadowViewDelegate) {
    mView = view;
    mShadowViewDelegate = shadowViewDelegate;
  }

  abstract void setBackgroundDrawable(ColorStateList backgroundTint,
                                      PorterDuff.Mode backgroundTintMode, int rippleColor, int
                                          borderWidth);

  abstract void setBackgroundTintList(ColorStateList tint);

  abstract void setBackgroundTintMode(PorterDuff.Mode tintMode);

  abstract void setRippleColor(int rippleColor);

  abstract void setElevation(float elevation);

  abstract void setPressedTranslationZ(float translationZ);

  abstract void onDrawableStateChanged(int[] state);

  abstract void jumpDrawableToCurrentState();

  abstract void hide(@Nullable InternalVisibilityChangedListener listener);

  abstract void show(@Nullable InternalVisibilityChangedListener listener);

  void onAttachedToWindow() {
    if (requirePreDrawListener()) {
      ensurePreDrawListener();
      mView.getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
    }
  }

  void onDetachedFromWindow() {
    if (mPreDrawListener != null) {
      mView.getViewTreeObserver().removeOnPreDrawListener(mPreDrawListener);
      mPreDrawListener = null;
    }
  }

  boolean requirePreDrawListener() {
    return false;
  }

  CircularBorderDrawable createBorderDrawable(int borderWidth, ColorStateList backgroundTint) {
    final Resources resources = mView.getResources();
    CircularBorderDrawable borderDrawable = newCircularDrawable();
    borderDrawable.setGradientColors(
        resources.getColor(android.support.design.R.color.design_fab_stroke_top_outer_color),
        resources.getColor(android.support.design.R.color.design_fab_stroke_top_inner_color),
        resources.getColor(android.support.design.R.color.design_fab_stroke_end_inner_color),
        resources.getColor(android.support.design.R.color.design_fab_stroke_end_outer_color));
    borderDrawable.setBorderWidth(borderWidth);
    borderDrawable.setBorderTint(backgroundTint);
    return borderDrawable;
  }

  CircularBorderDrawable newCircularDrawable() {
    return new CircularBorderDrawable();
  }

  GradientDrawable createShapeDrawable() {
    GradientDrawable d = new GradientDrawable();
    d.setShape(GradientDrawable.OVAL);
    d.setColor(Color.WHITE);
    return d;
  }

  void onPreDraw() {
  }

  private void ensurePreDrawListener() {
    if (mPreDrawListener == null) {
      mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          FloatingActionButtonImpl.this.onPreDraw();
          return true;
        }
      };
    }
  }
}
