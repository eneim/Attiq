package im.ene.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by eneim on 1/7/16.
 */
abstract class ImageButtonImpl {

  interface InternalVisibilityChangedListener {
    void onShown();

    void onHidden();
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

  private ViewTreeObserver.OnPreDrawListener mPreDrawListener;

  ImageButtonImpl(View view) {
    mView = view;
  }

  abstract void hide(@Nullable InternalVisibilityChangedListener listener);

  abstract void show(@Nullable InternalVisibilityChangedListener listener);

  abstract void setBackgroundDrawable(ColorStateList backgroundTint,
                                      PorterDuff.Mode backgroundTintMode, int rippleColor);

  abstract void setBackgroundTintList(ColorStateList tint);

  abstract void setBackgroundTintMode(PorterDuff.Mode tintMode);

  abstract void setRippleColor(int rippleColor);

  abstract void setElevation(float elevation);

  abstract void setPressedTranslationZ(float translationZ);

  abstract void onDrawableStateChanged(int[] state);

  abstract void jumpDrawableToCurrentState();

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

  void onPreDraw() {
  }

  private void ensurePreDrawListener() {
    if (mPreDrawListener == null) {
      mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          ImageButtonImpl.this.onPreDraw();
          return true;
        }
      };
    }
  }
}
