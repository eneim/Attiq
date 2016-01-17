package im.ene.lab.design.widget;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by eneim on 11/19/15.
 */
public class AnimationUtils {

  public static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
  public static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
  public static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
  public static final Interpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();

  /**
   * Linear interpolation between {@code startValue} and {@code endValue} by {@code fraction}.
   */
  public static float lerp(float startValue, float endValue, float fraction) {
    return startValue + (fraction * (endValue - startValue));
  }

  static int lerp(int startValue, int endValue, float fraction) {
    return startValue + Math.round(fraction * (endValue - startValue));
  }

  static class AnimationListenerAdapter implements Animation.AnimationListener {
    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
  }
}
