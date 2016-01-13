package im.ene.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import im.ene.lab.attiq.R;

import java.util.List;

/**
 * Created by eneim on 1/13/16.
 */
@CoordinatorLayout.DefaultBehavior(ImageButton.Behavior.class)
public class ImageButton extends android.widget.ImageButton {

  private final ImageButtonImpl mImpl;

  /**
   * Callback to be invoked when the visibility of a FloatingActionButton changes.
   */
  public abstract static class OnVisibilityChangedListener {
    /**
     * Called when a FloatingActionButton has been
     * {@link #show(OnVisibilityChangedListener) shown}.
     *
     * @param fab the FloatingActionButton that was shown.
     */
    public void onShown(ImageButton fab) {
    }

    /**
     * Called when a FloatingActionButton has been
     * {@link #hide(OnVisibilityChangedListener) hidden}.
     *
     * @param fab the FloatingActionButton that was hidden.
     */
    public void onHidden(ImageButton fab) {
    }
  }

  // These values must match those in the attrs declaration
  private static final int SIZE_MINI = 1;
  private static final int SIZE_NORMAL = 0;

  private static final int SIZE_BIG = 2;

  private ColorStateList mBackgroundTint;
  private PorterDuff.Mode mBackgroundTintMode;

  private int mRippleColor;

  public ImageButton(Context context) {
    this(context, null);
  }

  public ImageButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    ThemeUtils.checkAppCompatTheme(context);

    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.ImageButton, defStyleAttr, 0);
    mBackgroundTint = a.getColorStateList(R.styleable.ImageButton_backgroundTint);
    mBackgroundTintMode = parseTintMode(a.getInt(
        R.styleable.ImageButton_backgroundTintMode, -1), null);
    mRippleColor = a.getColor(R.styleable.ImageButton_rippleColor, 0);
    mSize = a.getInt(R.styleable.ImageButton_exFabSize, SIZE_NORMAL);
    final float elevation = a.getDimension(R.styleable.ImageButton_elevation, 0f);
    a.recycle();

    ViewCompat.setElevation(this, elevation);

    final int sdk = Build.VERSION.SDK_INT;
    if (sdk >= 12) {
      mImpl = new ImageButtonHoneycombMr1(this);
    } else {
      mImpl = new ImageButtonImplEclairMr1(this);
    }

    mImpl.setBackgroundDrawable(mBackgroundTint, mBackgroundTintMode, mRippleColor);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int preferredSize = getSizeDimension();

    final int w = resolveAdjustedSize(preferredSize, widthMeasureSpec);
    final int h = resolveAdjustedSize(preferredSize, heightMeasureSpec);

    final int d = Math.min(w, h);

    // We add the shadow's padding to the measured dimension
    setMeasuredDimension(d, d);
  }

  /**
   * Set the ripple color for this {@link FabImageButton}.
   * <p/>
   * When running on devices with KitKat or below, we draw a fill rather than a ripple.
   *
   * @param color ARGB color to use for the ripple.
   */
  public void setRippleColor(@ColorInt int color) {
    if (mRippleColor != color) {
      mRippleColor = color;
      mImpl.setRippleColor(color);
    }
  }

  /**
   * Return the tint applied to the background drawable, if specified.
   *
   * @return the tint applied to the background drawable
   * @see #setBackgroundTintList(ColorStateList)
   */
  @Nullable
  @Override
  public ColorStateList getBackgroundTintList() {
    return mBackgroundTint;
  }

  /**
   * Applies a tint to the background drawable. Does not modify the current tint
   * mode, which is {@link PorterDuff.Mode#SRC_IN} by default.
   *
   * @param tint the tint to apply, may be {@code null} to clear tint
   */
  public void setBackgroundTintList(@Nullable ColorStateList tint) {
    if (mBackgroundTint != tint) {
      mBackgroundTint = tint;
      mImpl.setBackgroundTintList(tint);
    }
  }

  /**
   * Return the blending mode used to apply the tint to the background
   * drawable, if specified.
   *
   * @return the blending mode used to apply the tint to the background
   * drawable
   * @see #setBackgroundTintMode(PorterDuff.Mode)
   */
  @Nullable
  @Override
  public PorterDuff.Mode getBackgroundTintMode() {
    return mBackgroundTintMode;
  }

  /**
   * Specifies the blending mode used to apply the tint specified by
   * {@link #setBackgroundTintList(ColorStateList)}} to the background
   * drawable. The default mode is {@link PorterDuff.Mode#SRC_IN}.
   *
   * @param tintMode the blending mode used to apply the tint, may be
   *                 {@code null} to clear tint
   */
  public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
    if (mBackgroundTintMode != tintMode) {
      mBackgroundTintMode = tintMode;
      mImpl.setBackgroundTintMode(tintMode);
    }
  }

  private static final String TAG = "ImageButton";

  @Override
  public void setBackgroundDrawable(Drawable background) {
    Log.i(TAG, "Setting a custom background is not supported.");
  }

  @Override
  public void setBackgroundResource(int resid) {
    Log.i(TAG, "Setting a custom background is not supported.");
  }

  @Override
  public void setBackgroundColor(int color) {
    Log.i(TAG, "Setting a custom background is not supported.");
  }

  /**
   * Shows the button.
   * <p>This method will animate the button show if the view has already been laid out.</p>
   */
  public void show() {
    mImpl.show(null);
  }

  /**
   * Shows the button.
   * <p>This method will animate the button show if the view has already been laid out.</p>
   *
   * @param listener the listener to notify when this view is shown
   */
  public void show(@Nullable final OnVisibilityChangedListener listener) {
    mImpl.show(wrapOnVisibilityChangedListener(listener));
  }

  /**
   * Hides the button.
   * <p>This method will animate the button hide if the view has already been laid out.</p>
   */
  public void hide() {
    mImpl.hide(null);
  }

  /**
   * Hides the button.
   * <p>This method will animate the button hide if the view has already been laid out.</p>
   *
   * @param listener the listener to notify when this view is hidden
   */
  public void hide(@Nullable OnVisibilityChangedListener listener) {
    mImpl.hide(wrapOnVisibilityChangedListener(listener));
  }

  @Nullable
  private ImageButtonImpl.InternalVisibilityChangedListener
  wrapOnVisibilityChangedListener(
      @Nullable final OnVisibilityChangedListener listener) {
    if (listener == null) {
      return null;
    }

    return new ImageButtonImpl.InternalVisibilityChangedListener() {
      @Override
      public void onShown() {
        listener.onShown(ImageButton.this);
      }

      @Override
      public void onHidden() {
        listener.onHidden(ImageButton.this);
      }
    };
  }

  private Integer mSizeDimension;
  private int mSize;

  final int getSizeDimension() {
    if (mSizeDimension != null) {
      return mSizeDimension;
    }

    switch (mSize) {
      case SIZE_MINI:
        return getResources().getDimensionPixelSize(android.support.design.R.dimen
            .design_fab_size_mini);
      case SIZE_BIG:
        return getResources().getDimensionPixelSize(R.dimen.design_fab_size_big);
      case SIZE_NORMAL:
      default:
        return getResources().getDimensionPixelSize(android.support.design.R.dimen
            .design_fab_size_normal);
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mImpl.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mImpl.onDetachedFromWindow();
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    mImpl.onDrawableStateChanged(getDrawableState());
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    mImpl.jumpDrawableToCurrentState();
  }

  private static int resolveAdjustedSize(int desiredSize, int measureSpec) {
    int result = desiredSize;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    switch (specMode) {
      case MeasureSpec.UNSPECIFIED:
        // Parent says we can be as big as we want. Just don't be larger
        // than max size imposed on ourselves.
        result = desiredSize;
        break;
      case MeasureSpec.AT_MOST:
        // Parent says we can be as big as we want, up to specSize.
        // Don't be larger than specSize, and don't be larger than
        // the max size imposed on ourselves.
        result = Math.min(desiredSize, specSize);
        break;
      case MeasureSpec.EXACTLY:
        // No choice. Do what we are told.
        result = specSize;
        break;
    }
    return result;
  }

  static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
    switch (value) {
      case 3:
        return PorterDuff.Mode.SRC_OVER;
      case 5:
        return PorterDuff.Mode.SRC_IN;
      case 9:
        return PorterDuff.Mode.SRC_ATOP;
      case 14:
        return PorterDuff.Mode.MULTIPLY;
      case 15:
        return PorterDuff.Mode.SCREEN;
      default:
        return defaultMode;
    }
  }

  /**
   * Behavior designed for use with {@link FabImageButton} instances. It's main function
   * is to move {@link FabImageButton} views so that any displayed {@link Snackbar}s do
   * not cover them.
   */
  public static class Behavior extends CoordinatorLayout.Behavior<ImageButton> {
    // We only support the FAB <> Snackbar shift movement on Honeycomb and above. This is
    // because we can use view translation properties which greatly simplifies the code.
    private static final boolean SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;

    private ValueAnimatorCompat mFabTranslationYAnimator;
    private float mFabTranslationY;
    private Rect mTmpRect;

    public Behavior() {
    }

    public Behavior(Context context, AttributeSet attrs) {
      super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   ImageButton child, View dependency) {
      // We're dependent on all SnackbarLayouts (if enabled)
      return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageButton child,
                                          View dependency) {
      if (dependency instanceof Snackbar.SnackbarLayout) {
        updateFabTranslationForSnackbar(parent, child, dependency);
      } else if (dependency instanceof AppBarLayout) {
        // If we're depending on an AppBarLayout we will show/hide it automatically
        // if the FAB is anchored to the AppBarLayout
        updateFabVisibility(parent, (AppBarLayout) dependency, child);
      }
      return false;
    }

    private boolean updateFabVisibility(CoordinatorLayout parent,
                                        AppBarLayout appBarLayout, ImageButton child) {
      final CoordinatorLayout.LayoutParams lp =
          (CoordinatorLayout.LayoutParams) child.getLayoutParams();
      if (lp.getAnchorId() != appBarLayout.getId()) {
        // The anchor ID doesn't match the dependency, so we won't automatically
        // show/hide the FAB
        return false;
      }

      if (mTmpRect == null) {
        mTmpRect = new Rect();
      }

      // First, let's get the visible rect of the dependency
      final Rect rect = mTmpRect;
      ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);

      if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
        // If the anchor's bottom is below the seam, we'll animate our FAB out
        child.hide();
      } else {
        // Else, we'll animate our FAB back in
        child.show();
      }
      return true;
    }

    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
                                                 final ImageButton fab, View snackbar) {
      if (fab.getVisibility() != View.VISIBLE) {
        return;
      }

      final float targetTransY = getFabTranslationYForSnackbar(parent, fab);
      if (mFabTranslationY == targetTransY) {
        // We're already at (or currently animating to) the target value, return...
        return;
      }

      final float currentTransY = ViewCompat.getTranslationY(fab);

      // Make sure that any current animation is cancelled
      if (mFabTranslationYAnimator != null && mFabTranslationYAnimator.isRunning()) {
        mFabTranslationYAnimator.cancel();
      }

      if (Math.abs(currentTransY - targetTransY) > (fab.getHeight() * 0.667f)) {
        // If the FAB will be travelling by more than 2/3 of it's height, let's animate
        // it instead
        if (mFabTranslationYAnimator == null) {
          mFabTranslationYAnimator = ViewUtils.createAnimator();
          mFabTranslationYAnimator.setInterpolator(
              AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
          mFabTranslationYAnimator.setUpdateListener(
              new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animator) {
                  ViewCompat.setTranslationY(fab,
                      animator.getAnimatedFloatValue());
                }
              });
        }
        mFabTranslationYAnimator.setFloatValues(currentTransY, targetTransY);
        mFabTranslationYAnimator.start();
      } else {
        // Now update the translation Y
        ViewCompat.setTranslationY(fab, targetTransY);
      }

      mFabTranslationY = targetTransY;
    }

    private float getFabTranslationYForSnackbar(CoordinatorLayout parent,
                                                ImageButton fab) {
      float minOffset = 0;
      final List<View> dependencies = parent.getDependencies(fab);
      for (int i = 0, z = dependencies.size(); i < z; i++) {
        final View view = dependencies.get(i);
        if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
          minOffset = Math.min(minOffset,
              ViewCompat.getTranslationY(view) - view.getHeight());
        }
      }

      return minOffset;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, ImageButton child,
                                 int layoutDirection) {
      // First, lets make sure that the visibility of the FAB is consistent
      final List<View> dependencies = parent.getDependencies(child);
      for (int i = 0, count = dependencies.size(); i < count; i++) {
        final View dependency = dependencies.get(i);
        if (dependency instanceof AppBarLayout
            && updateFabVisibility(parent, (AppBarLayout) dependency, child)) {
          break;
        }
      }
      // Now let the CoordinatorLayout lay out the FAB
      parent.onLayoutChild(child, layoutDirection);
      // Now offset it if needed
      // offsetIfNeeded(parent, child);
      return true;
    }
  }
}
