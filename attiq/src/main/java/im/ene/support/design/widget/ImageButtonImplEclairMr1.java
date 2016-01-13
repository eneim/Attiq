package im.ene.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by eneim on 1/13/16.
 */
public class ImageButtonImplEclairMr1 extends ImageButtonImpl {

  private boolean mIsHiding;

  ImageButtonImplEclairMr1(View view) {
    super(view);
  }

  @Override void hide(@Nullable final InternalVisibilityChangedListener listener) {
    if (mIsHiding || mView.getVisibility() != View.VISIBLE) {
      // A hide animation is in progress, or we're already hidden. Skip the call
      if (listener != null) {
        listener.onHidden();
      }
      return;
    }

    Animation anim = android.view.animation.AnimationUtils.loadAnimation(
        mView.getContext(), android.support.design.R.anim.design_fab_out);
    anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    anim.setDuration(SHOW_HIDE_ANIM_DURATION);
    anim.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
      @Override
      public void onAnimationStart(Animation animation) {
        mIsHiding = true;
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mIsHiding = false;
        mView.setVisibility(View.GONE);
        if (listener != null) {
          listener.onHidden();
        }
      }
    });
    mView.startAnimation(anim);
  }

  @Override void show(@Nullable final InternalVisibilityChangedListener listener) {
    if (mView.getVisibility() != View.VISIBLE || mIsHiding) {
      // If the view is not visible, or is visible and currently being hidden, run
      // the show animation
      mView.clearAnimation();
      mView.setVisibility(View.VISIBLE);
      Animation anim = android.view.animation.AnimationUtils.loadAnimation(
          mView.getContext(), android.support.design.R.anim.design_fab_in);
      anim.setDuration(SHOW_HIDE_ANIM_DURATION);
      anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
      anim.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
        @Override
        public void onAnimationEnd(Animation animation) {
          if (listener != null) {
            listener.onShown();
          }
        }
      });
      mView.startAnimation(anim);
    } else {
      if (listener != null) {
        listener.onShown();
      }
    }
  }

  GradientDrawable createShapeDrawable() {
    GradientDrawable d = new GradientDrawable();
    d.setShape(GradientDrawable.RECTANGLE);
    d.setColor(Color.WHITE);
    return d;
  }

  Drawable mShapeDrawable;
  Drawable mRippleDrawable;

  @Override void setBackgroundDrawable(ColorStateList backgroundTint,
                                       PorterDuff.Mode backgroundTintMode, int rippleColor) {
    // Now we need to tint the original background with the tint, using
    // an InsetDrawable if we have a border width
    mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
    DrawableCompat.setTintList(mShapeDrawable, backgroundTint);
    if (backgroundTintMode != null) {
      DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
    }

    // Now we created a mask Drawable which will be used for touch feedback.
    GradientDrawable touchFeedbackShape = createShapeDrawable();

    // We'll now wrap that touch feedback mask drawable with a ColorStateList. We do not need
    // to inset for any border here as LayerDrawable will nest the padding for us
    mRippleDrawable = DrawableCompat.wrap(touchFeedbackShape);
    DrawableCompat.setTintList(mRippleDrawable, createColorStateList(rippleColor));
    DrawableCompat.setTintMode(mRippleDrawable, PorterDuff.Mode.MULTIPLY);

    final Drawable[] layers = new Drawable[] {mShapeDrawable, mRippleDrawable};

    LayerDrawable layerDrawable = new LayerDrawable(layers);
    mView.setBackground(layerDrawable);
  }

  @Override void setBackgroundTintList(ColorStateList tint) {

  }

  @Override void setBackgroundTintMode(PorterDuff.Mode tintMode) {

  }

  @Override void setRippleColor(int rippleColor) {

  }

  @Override void setElevation(float elevation) {

  }

  @Override void setPressedTranslationZ(float translationZ) {

  }

  @Override void onDrawableStateChanged(int[] state) {

  }

  @Override void jumpDrawableToCurrentState() {

  }

  private static ColorStateList createColorStateList(int selectedColor) {
    final int[][] states = new int[3][];
    final int[] colors = new int[3];
    int i = 0;

    states[i] = FOCUSED_ENABLED_STATE_SET;
    colors[i] = selectedColor;
    i++;

    states[i] = PRESSED_ENABLED_STATE_SET;
    colors[i] = selectedColor;
    i++;

    // Default enabled state
    states[i] = new int[0];
    colors[i] = Color.TRANSPARENT;
    i++;

    return new ColorStateList(states, colors);
  }
}
