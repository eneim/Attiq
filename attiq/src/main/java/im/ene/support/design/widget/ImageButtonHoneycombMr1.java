package im.ene.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by eneim on 1/13/16.
 */
public class ImageButtonHoneycombMr1 extends ImageButtonImplEclairMr1 {
  private boolean mIsHiding;

  ImageButtonHoneycombMr1(View view) {
    super(view);
  }

  @Override boolean requirePreDrawListener() {
    return true;
  }

  @Override void hide(@Nullable final InternalVisibilityChangedListener listener) {
    if (mIsHiding || mView.getVisibility() != View.VISIBLE) {
      // A hide animation is in progress, or we're already hidden. Skip the call
      if (listener != null) {
        listener.onHidden();
      }
      return;
    }

    if (!ViewCompat.isLaidOut(mView) || mView.isInEditMode()) {
      // If the view isn't laid out, or we're in the editor, don't run the animation
      mView.setVisibility(View.GONE);
      if (listener != null) {
        listener.onHidden();
      }
    } else {
      mView.animate()
          .scaleX(0f)
          .scaleY(0f)
          .alpha(0f)
          .setDuration(SHOW_HIDE_ANIM_DURATION)
          .setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
              mIsHiding = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
              mIsHiding = true;
              mView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
              mIsHiding = false;
              mView.setVisibility(View.GONE);
              if (listener != null) {
                listener.onHidden();
              }
            }


          });
    }
  }

  @Override void show(@Nullable final InternalVisibilityChangedListener listener) {
    if (mView.getVisibility() != View.VISIBLE) {
      if (ViewCompat.isLaidOut(mView) && !mView.isInEditMode()) {
        mView.setAlpha(0f);
        mView.setScaleY(0f);
        mView.setScaleX(0f);
        mView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(SHOW_HIDE_ANIM_DURATION)
            .setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationStart(Animator animation) {
                mView.setVisibility(View.VISIBLE);
              }

              @Override
              public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                  listener.onShown();
                }
              }
            });
      } else {
        mView.setVisibility(View.VISIBLE);
        mView.setAlpha(1f);
        mView.setScaleY(1f);
        mView.setScaleX(1f);
        if (listener != null) {
          listener.onShown();
        }
      }
    }
  }
}
