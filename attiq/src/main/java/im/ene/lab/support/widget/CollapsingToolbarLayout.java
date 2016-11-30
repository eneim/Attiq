package im.ene.lab.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CollapsingToolbarLayout extends FrameLayout {

  private static final int SCRIM_ANIMATION_DURATION = 600;
  private final Rect mTmpRect = new Rect();
  private final CollapsingTextHelper mCollapsingTextHelper;
  private boolean mRefreshToolbar = true;
  private int mToolbarId;
  private Toolbar mToolbar;
  private View mDummyView;
  private int mExpandedMarginLeft;
  private int mExpandedMarginTop;
  private int mExpandedMarginRight;
  private int mExpandedMarginBottom;
  private boolean mCollapsingTitleEnabled;
  private boolean mDrawCollapsingTitle;

  private Drawable mContentScrim;
  private Drawable mStatusBarScrim;
  private int mScrimAlpha;
  private boolean mScrimsAreShown;
  private ValueAnimatorCompat mScrimAnimator;
  private ValueAnimatorCompat mTitleAlphaAnimator;

  private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

  private int mCurrentOffset;

  private WindowInsetsCompat mLastInsets;

  public CollapsingToolbarLayout(Context context) {
    this(context, null);
  }

  public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    ThemeUtils.checkAppCompatTheme(context);

    mCollapsingTextHelper = new CollapsingTextHelper(this);
    mCollapsingTextHelper.setTextSizeInterpolator(new Interpolator() {
      @Override public float getInterpolation(float input) {
        return 1.f;
      }
    });

    TypedArray a = context.obtainStyledAttributes(attrs,
        android.support.design.R.styleable.CollapsingToolbarLayout, defStyleAttr,
        android.support.design.R.style.Widget_Design_CollapsingToolbar);

    mCollapsingTextHelper.setExpandedTextGravity(
        a.getInt(android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleGravity,
            GravityCompat.START | Gravity.BOTTOM));
    mCollapsingTextHelper.setCollapsedTextGravity(
        a.getInt(android.support.design.R.styleable.CollapsingToolbarLayout_collapsedTitleGravity,
            GravityCompat.START | Gravity.CENTER_VERTICAL));

    mExpandedMarginLeft = mExpandedMarginTop = mExpandedMarginRight = mExpandedMarginBottom =
        a.getDimensionPixelSize(android.support.design.R.styleable
            .CollapsingToolbarLayout_expandedTitleMargin, 0);

    final boolean isRtl = ViewCompat.getLayoutDirection(this)
        == ViewCompat.LAYOUT_DIRECTION_RTL;
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_expandedTitleMarginStart)) {
      final int marginStart = a.getDimensionPixelSize(
          android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
      if (isRtl) {
        mExpandedMarginRight = marginStart;
      } else {
        mExpandedMarginLeft = marginStart;
      }
    }
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_expandedTitleMarginEnd)) {
      final int marginEnd = a.getDimensionPixelSize(
          android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
      if (isRtl) {
        mExpandedMarginLeft = marginEnd;
      } else {
        mExpandedMarginRight = marginEnd;
      }
    }
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_expandedTitleMarginTop)) {
      mExpandedMarginTop = a.getDimensionPixelSize(
          android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
    }
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_expandedTitleMarginBottom)) {
      mExpandedMarginBottom = a.getDimensionPixelSize(
          android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
    }

    mCollapsingTitleEnabled = a.getBoolean(
        android.support.design.R.styleable.CollapsingToolbarLayout_titleEnabled, true);
    setTitle(a.getText(android.support.design.R.styleable.CollapsingToolbarLayout_title));

    // First load the default text appearances
    mCollapsingTextHelper.setExpandedTextAppearance(
        android.support.design.R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
    mCollapsingTextHelper.setCollapsedTextAppearance(
        android.support.design.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);

    // Now overlay any custom text appearances
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_expandedTitleTextAppearance)) {
      mCollapsingTextHelper.setExpandedTextAppearance(
          a.getResourceId(
              android.support.design.R.styleable
                  .CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
    }
    if (a.hasValue(android.support.design.R.styleable
        .CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
      mCollapsingTextHelper.setCollapsedTextAppearance(
          a.getResourceId(
              android.support.design.R.styleable
                  .CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
    }

    setContentScrim(a.getDrawable(android.support.design.R.styleable
        .CollapsingToolbarLayout_contentScrim));
    setStatusBarScrim(a.getDrawable(android.support.design.R.styleable
        .CollapsingToolbarLayout_statusBarScrim));

    mToolbarId = a.getResourceId(android.support.design.R.styleable
        .CollapsingToolbarLayout_toolbarId, -1);

    a.recycle();

    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
    int titleColorId = typedValue.resourceId;
    mTitleColorSpan = new AlphaForegroundColorSpan(ContextCompat.getColor(context, titleColorId));

    setWillNotDraw(false);

    ViewCompat.setOnApplyWindowInsetsListener(this,
        new android.support.v4.view.OnApplyWindowInsetsListener() {
          @Override
          public WindowInsetsCompat onApplyWindowInsets(View v,
                                                        WindowInsetsCompat insets) {
            mLastInsets = insets;
            requestLayout();
            return insets.consumeSystemWindowInsets();
          }
        });
  }

  @Override
  protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    // This is a little weird. Our scrim needs to be behind the Toolbar (if it is present),
    // but in front of any other children which are behind it. To do this we intercept the
    // drawChild() call, and draw our scrim first when drawing the toolbar
    ensureToolbar();
    if (child == mToolbar && mContentScrim != null && mScrimAlpha > 0) {
      mContentScrim.mutate().setAlpha(mScrimAlpha);
      mContentScrim.draw(canvas);
    }

    if (mLastTitleAlpha > 0) {
      setTitleAlpha(mLastTitleAlpha);
    }

    // Carry on drawing the child...
    return super.drawChild(canvas, child, drawingTime);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    // Add an OnOffsetChangedListener if possible
    final ViewParent parent = getParent();
    if (parent instanceof AppBarLayout) {
      if (mOnOffsetChangedListener == null) {
        mOnOffsetChangedListener = new OffsetUpdateListener();
      }
      ((AppBarLayout) parent).addOnOffsetChangedListener
          (mOnOffsetChangedListener);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    // Remove our OnOffsetChangedListener if possible and it exists
    final ViewParent parent = getParent();
    if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
      ((AppBarLayout) parent).removeOnOffsetChangedListener
          (mOnOffsetChangedListener);
    }

    super.onDetachedFromWindow();
  }

  private void ensureToolbar() {
    if (!mRefreshToolbar) {
      return;
    }

    Toolbar fallback = null, selected = null;

    for (int i = 0, count = getChildCount(); i < count; i++) {
      final View child = getChildAt(i);
      if (child instanceof Toolbar) {
        if (mToolbarId != -1) {
          // There's a toolbar id set so try and find it...
          if (mToolbarId == child.getId()) {
            // We found the primary Toolbar, use it
            selected = (Toolbar) child;
            break;
          }
          if (fallback == null) {
            // We'll record the first Toolbar as our fallback
            fallback = (Toolbar) child;
          }
        } else {
          // We don't have a id to check for so just use the first we come across
          selected = (Toolbar) child;
          break;
        }
      }
    }

    if (selected == null) {
      // If we didn't find a primary Toolbar, use the fallback
      selected = fallback;
    }

    mToolbar = selected;
    updateDummyView();
    mRefreshToolbar = false;
  }

  private void updateDummyView() {
    if (!mCollapsingTitleEnabled && mDummyView != null) {
      // If we have a dummy view and we have our title disabled, remove it from its parent
      final ViewParent parent = mDummyView.getParent();
      if (parent instanceof ViewGroup) {
        ((ViewGroup) parent).removeView(mDummyView);
      }
    }
    if (mCollapsingTitleEnabled && mToolbar != null) {
      if (mDummyView == null) {
        mDummyView = new View(getContext());
      }
      if (mDummyView.getParent() == null) {
        mToolbar.addView(mDummyView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
      }
    }
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);
    if (mContentScrim != null) {
      mContentScrim.setBounds(0, 0, width, height);
    }
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    // If we don't have a toolbar, the scrim will be not be drawn in drawChild() below.
    // Instead, we draw it here, before our collapsing text.
    ensureToolbar();
    if (mToolbar == null && mContentScrim != null && mScrimAlpha > 0) {
      mContentScrim.mutate().setAlpha(mScrimAlpha);
      mContentScrim.draw(canvas);
    }

    // Let the collapsing text helper draw it's text
    if (mCollapsingTitleEnabled && mDrawCollapsingTitle) {
      mCollapsingTextHelper.draw(canvas);
    }

    // Now draw the status bar scrim
    if (mStatusBarScrim != null && mScrimAlpha > 0) {
      final int topInset = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
      if (topInset > 0) {
        mStatusBarScrim.setBounds(0, -mCurrentOffset, getWidth(),
            topInset - mCurrentOffset);
        mStatusBarScrim.mutate().setAlpha(mScrimAlpha);
        mStatusBarScrim.draw(canvas);
      }
    }
  }

  /**
   * Returns the title currently being displayed by this view. If the title is not enabled, then
   * this will return {@code null}.
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_title
   */
  @Nullable
  public CharSequence getTitle() {
    return mCollapsingTitleEnabled ? mCollapsingTextHelper.getText() : null;
  }

  /**
   * Sets the title to be displayed by this view, if enabled.
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_title
   * @see #setTitleEnabled(boolean)
   * @see #getTitle()
   */
  public void setTitle(@Nullable CharSequence title) {
    mCollapsingTextHelper.setText(title);
  }

  /**
   * Returns whether this view is currently displaying its own title.
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_titleEnabled
   * @see #setTitleEnabled(boolean)
   */
  public boolean isTitleEnabled() {
    return mCollapsingTitleEnabled;
  }

  /**
   * Sets whether this view should display its own title.
   * <p/>
   * <p>The title displayed by this view will shrink and grow based on the scroll offset.</p>
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_titleEnabled
   * @see #setTitle(CharSequence)
   * @see #isTitleEnabled()
   */
  public void setTitleEnabled(boolean enabled) {
    if (enabled != mCollapsingTitleEnabled) {
      mCollapsingTitleEnabled = enabled;
      updateDummyView();
      requestLayout();
    }
  }

  /**
   * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
   * in the vertical scroll may overwrite this value. Any visibility change will be animated if
   * this view has already been laid out.
   *
   * @param shown whether the scrims should be shown
   * @see #getStatusBarScrim()
   * @see #getContentScrim()
   */
  public void setScrimsShown(boolean shown) {
    setScrimsShown(shown, ViewCompat.isLaidOut(this) && !isInEditMode());
  }

  /**
   * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
   * in the vertical scroll may overwrite this value.
   *
   * @param shown   whether the scrims should be shown
   * @param animate whether to animate the visibility change
   * @see #getStatusBarScrim()
   * @see #getContentScrim()
   */
  public void setScrimsShown(boolean shown, boolean animate) {
    if (mScrimsAreShown != shown) {
      if (animate) {
        animateTitleAlpha(shown ? 1.f : 0.f);
        animateScrim(shown ? 0xFF : 0x0);
      } else {
        setScrimAlpha(shown ? 0xFF : 0x0);
        setTitleAlpha(shown ? 1.f : 0.f);
      }
      mScrimsAreShown = shown;
    }
  }

  private void animateScrim(int targetAlpha) {
    ensureToolbar();
    if (mScrimAnimator == null) {
      mScrimAnimator = ViewUtils.createAnimator();
      mScrimAnimator.setDuration(SCRIM_ANIMATION_DURATION);
      mScrimAnimator.setInterpolator(AnimationUtils
          .FAST_OUT_SLOW_IN_INTERPOLATOR);
      mScrimAnimator.setUpdateListener(new ValueAnimatorCompat
          .AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimatorCompat animator) {
          setScrimAlpha(animator.getAnimatedIntValue());
        }
      });
    } else if (mScrimAnimator.isRunning()) {
      mScrimAnimator.cancel();
    }

    mScrimAnimator.setIntValues(mScrimAlpha, targetAlpha);
    mScrimAnimator.start();
  }

  private void setScrimAlpha(int alpha) {
    if (alpha != mScrimAlpha) {
      final Drawable contentScrim = mContentScrim;
      if (contentScrim != null && mToolbar != null) {
        ViewCompat.postInvalidateOnAnimation(mToolbar);
      }
      mScrimAlpha = alpha;
      ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
    }
  }

  /**
   * Set the color to use for the content scrim.
   *
   * @param color the color to display
   * @attr ref R.styleable#CollapsingToolbarLayout_contentScrim
   * @see #getContentScrim()
   */
  public void setContentScrimColor(@ColorInt int color) {
    setContentScrim(new ColorDrawable(color));
  }

  /**
   * Set the drawable to use for the content scrim from resources.
   *
   * @param resId drawable resource id
   * @attr ref R.styleable#CollapsingToolbarLayout_contentScrim
   * @see #getContentScrim()
   */
  public void setContentScrimResource(@DrawableRes int resId) {
    setContentScrim(ContextCompat.getDrawable(getContext(), resId));

  }

  /**
   * Returns the drawable which is used for the foreground scrim.
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_contentScrim
   * @see #setContentScrim(Drawable)
   */
  public Drawable getContentScrim() {
    return mContentScrim;
  }

  /**
   * Set the drawable to use for the content scrim from resources. Providing null will disable
   * the scrim functionality.
   *
   * @param drawable the drawable to display
   * @attr ref R.styleable#CollapsingToolbarLayout_contentScrim
   * @see #getContentScrim()
   */
  public void setContentScrim(@Nullable Drawable drawable) {
    if (mContentScrim != drawable) {
      if (mContentScrim != null) {
        mContentScrim.setCallback(null);
      }
      if (drawable != null) {
        mContentScrim = drawable.mutate();
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.setCallback(this);
        drawable.setAlpha(mScrimAlpha);
      } else {
        mContentScrim = null;
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  /**
   * Set the color to use for the status bar scrim.
   * <p/>
   * <p>This scrim is only shown when we have been given a top system inset.</p>
   *
   * @param color the color to display
   * @attr ref R.styleable#CollapsingToolbarLayout_statusBarScrim
   * @see #getStatusBarScrim()
   */
  public void setStatusBarScrimColor(@ColorInt int color) {
    setStatusBarScrim(new ColorDrawable(color));
  }

  /**
   * Set the drawable to use for the content scrim from resources.
   *
   * @param resId drawable resource id
   * @attr ref R.styleable#CollapsingToolbarLayout_statusBarScrim
   * @see #getStatusBarScrim()
   */
  public void setStatusBarScrimResource(@DrawableRes int resId) {
    setStatusBarScrim(ContextCompat.getDrawable(getContext(), resId));
  }

  /**
   * Returns the drawable which is used for the status bar scrim.
   *
   * @attr ref R.styleable#CollapsingToolbarLayout_statusBarScrim
   * @see #setStatusBarScrim(Drawable)
   */
  public Drawable getStatusBarScrim() {
    return mStatusBarScrim;
  }

  /**
   * Set the drawable to use for the status bar scrim from resources.
   * Providing null will disable the scrim functionality.
   * <p/>
   * <p>This scrim is only shown when we have been given a top system inset.</p>
   *
   * @param drawable the drawable to display
   * @attr ref R.styleable#CollapsingToolbarLayout_statusBarScrim
   * @see #getStatusBarScrim()
   */
  public void setStatusBarScrim(@Nullable Drawable drawable) {
    if (mStatusBarScrim != drawable) {
      if (mStatusBarScrim != null) {
        mStatusBarScrim.setCallback(null);
      }

      mStatusBarScrim = drawable;
      drawable.setCallback(this);
      drawable.mutate().setAlpha(mScrimAlpha);
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  /**
   * Sets the text color and size for the collapsed title from the specified
   * TextAppearance resource.
   *
   * @attr ref android.support.design.R
   * .styleable#CollapsingToolbarLayout_collapsedTitleTextAppearance
   */
  public void setCollapsedTitleTextAppearance(@StyleRes int resId) {
    mCollapsingTextHelper.setCollapsedTextAppearance(resId);
  }

  /**
   * Sets the text color of the collapsed title.
   *
   * @param color The new text color in ARGB format
   */
  public void setCollapsedTitleTextColor(@ColorInt int color) {
    mCollapsingTextHelper.setCollapsedTextColor(color);
  }

  /**
   * Returns the horizontal and vertical alignment for title when collapsed.
   *
   * @attr ref android.support.design.R.styleable#CollapsingToolbarLayout_collapsedTitleGravity
   */
  public int getCollapsedTitleGravity() {
    return mCollapsingTextHelper.getCollapsedTextGravity();
  }

  /**
   * Sets the horizontal alignment of the collapsed title and the vertical gravity that will
   * be used when there is extra space in the collapsed bounds beyond what is required for
   * the title itself.
   *
   * @attr ref android.support.design.R.styleable#CollapsingToolbarLayout_collapsedTitleGravity
   */
  public void setCollapsedTitleGravity(int gravity) {
    mCollapsingTextHelper.setCollapsedTextGravity(gravity);
  }

  /**
   * Sets the text color and size for the expanded title from the specified
   * TextAppearance resource.
   *
   * @attr ref android.support.design.R
   * .styleable#CollapsingToolbarLayout_expandedTitleTextAppearance
   */
  public void setExpandedTitleTextAppearance(@StyleRes int resId) {
    mCollapsingTextHelper.setExpandedTextAppearance(resId);
  }

  /**
   * Sets the text color of the expanded title.
   *
   * @param color The new text color in ARGB format
   */
  public void setExpandedTitleColor(@ColorInt int color) {
    mCollapsingTextHelper.setExpandedTextColor(color);
  }

  /**
   * Returns the horizontal and vertical alignment for title when expanded.
   *
   * @attr ref android.support.design.R.styleable#CollapsingToolbarLayout_expandedTitleGravity
   */
  public int getExpandedTitleGravity() {
    return mCollapsingTextHelper.getExpandedTextGravity();
  }

  /**
   * Sets the horizontal alignment of the expanded title and the vertical gravity that will
   * be used when there is extra space in the expanded bounds beyond what is required for
   * the title itself.
   *
   * @attr ref android.support.design.R.styleable#CollapsingToolbarLayout_expandedTitleGravity
   */
  public void setExpandedTitleGravity(int gravity) {
    mCollapsingTextHelper.setExpandedTextGravity(gravity);
  }

  /**
   * Returns the typeface used for the collapsed title.
   */
  @NonNull
  public Typeface getCollapsedTitleTypeface() {
    return mCollapsingTextHelper.getCollapsedTypeface();
  }

  /**
   * Set the typeface to use for the collapsed title.
   *
   * @param typeface typeface to use, or {@code null} to use the default.
   */
  public void setCollapsedTitleTypeface(@Nullable Typeface typeface) {
    mCollapsingTextHelper.setCollapsedTypeface(typeface);
  }

  /**
   * Returns the typeface used for the expanded title.
   */
  @NonNull
  public Typeface getExpandedTitleTypeface() {
    return mCollapsingTextHelper.getExpandedTypeface();
  }

  /**
   * Set the typeface to use for the expanded title.
   *
   * @param typeface typeface to use, or {@code null} to use the default.
   */
  public void setExpandedTitleTypeface(@Nullable Typeface typeface) {
    mCollapsingTextHelper.setExpandedTypeface(typeface);
  }

  /**
   * The additional offset used to define when to trigger the scrim visibility change.
   */
  final int getScrimTriggerOffset() {
    return 2 * ViewCompat.getMinimumHeight(this);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(super.generateDefaultLayoutParams());
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    ensureToolbar();
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    // Update the collapsed bounds by getting it's transformed bounds. This needs to be done
    // before the children are offset below
    if (mCollapsingTitleEnabled && mDummyView != null) {
      // We only draw the title if the dummy view is being displayed (Toolbar removes
      // views if there is no space)
      mDrawCollapsingTitle = mDummyView.isShown();

      if (mDrawCollapsingTitle) {
        ViewGroupUtils.getDescendantRect(this, mDummyView, mTmpRect);
        mCollapsingTextHelper.setCollapsedBounds(mTmpRect.left, bottom - mTmpRect.height(),
            mTmpRect.right, bottom);
        // Update the expanded bounds
        mCollapsingTextHelper.setExpandedBounds(
            mExpandedMarginLeft,
            mTmpRect.bottom + mExpandedMarginTop,
            right - left - mExpandedMarginRight,
            bottom - top - mExpandedMarginBottom);
        // Now recalculate using the new bounds
        mCollapsingTextHelper.recalculate();
      }
    }

    // Update our child view offset helpers
    for (int i = 0, z = getChildCount(); i < z; i++) {
      final View child = getChildAt(i);

      if (mLastInsets != null && !ViewCompat.getFitsSystemWindows(child)) {
        final int insetTop = mLastInsets.getSystemWindowInsetTop();
        if (child.getTop() < insetTop) {
          // If the child isn't set to fit system windows but is drawing within the inset
          // offset it down
          child.offsetTopAndBottom(insetTop);
        }
      }

      getViewOffsetHelper(child).onViewLayout();
    }

    // Finally, set our minimum height to enable proper AppBarLayout collapsing
    if (mToolbar != null) {
      if (mCollapsingTitleEnabled && TextUtils.isEmpty(mCollapsingTextHelper.getText())) {
        // If we do not currently have a title, try and grab it from the Toolbar
        mCollapsingTextHelper.setText(mToolbar.getTitle());
      }
      setMinimumHeight(mToolbar.getHeight());
    }
  }

  private static ViewOffsetHelper getViewOffsetHelper(View view) {
    ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(
        android.support.design.R.id.view_offset_helper);
    if (offsetHelper == null) {
      offsetHelper = new ViewOffsetHelper(view);
      view.setTag(android.support.design.R.id.view_offset_helper, offsetHelper);
    }
    return offsetHelper;
  }

  @Override
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  @Override
  protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  public static class LayoutParams extends FrameLayout.LayoutParams {

    /**
     * The view will act as normal with no collapsing behavior.
     */
    public static final int COLLAPSE_MODE_OFF = 0;
    /**
     * The view will pin in place until it reaches the bottom of the
     * {@link CollapsingToolbarLayout}.
     */
    public static final int COLLAPSE_MODE_PIN = 1;
    /**
     * The view will scroll in a parallax fashion. See {@link #setParallaxMultiplier(float)}
     * to change the multiplier used.
     */
    public static final int COLLAPSE_MODE_PARALLAX = 2;
    private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
    int mCollapseMode = COLLAPSE_MODE_OFF;
    float mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);

      TypedArray a = c.obtainStyledAttributes(attrs,
          android.support.design.R.styleable.CollapsingToolbarLayout_Layout);
      mCollapseMode = a.getInt(
          android.support.design.R.styleable
              .CollapsingToolbarLayout_Layout_layout_collapseMode,
          COLLAPSE_MODE_OFF);
      setParallaxMultiplier(a.getFloat(
          android.support.design.R.styleable
              .CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier,
          DEFAULT_PARALLAX_MULTIPLIER));
      a.recycle();
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(int width, int height, int gravity) {
      super(width, height, gravity);
    }

    public LayoutParams(ViewGroup.LayoutParams p) {
      super(p);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(FrameLayout.LayoutParams source) {
      super(source);
    }

    /**
     * Returns the requested collapse mode.
     *
     * @return the current mode. One of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
     * or {@link #COLLAPSE_MODE_PARALLAX}.
     */
    @CollapseMode
    public int getCollapseMode() {
      return mCollapseMode;
    }

    /**
     * Set the collapse mode.
     *
     * @param collapseMode one of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
     *                     or {@link #COLLAPSE_MODE_PARALLAX}.
     */
    public void setCollapseMode(@CollapseMode int collapseMode) {
      mCollapseMode = collapseMode;
    }

    /**
     * Returns the parallax scroll multiplier used in conjunction with
     * {@link #COLLAPSE_MODE_PARALLAX}.
     *
     * @see #setParallaxMultiplier(float)
     */
    public float getParallaxMultiplier() {
      return mParallaxMult;
    }

    /**
     * Set the parallax scroll multiplier used in conjunction with
     * {@link #COLLAPSE_MODE_PARALLAX}. A value of {@code 0.0} indicates no movement at all,
     * {@code 1.0f} indicates normal scroll movement.
     *
     * @param multiplier the multiplier.
     * @see #getParallaxMultiplier()
     */
    public void setParallaxMultiplier(float multiplier) {
      mParallaxMult = multiplier;
    }

    /**
     * @hide
     */
    @IntDef({
        COLLAPSE_MODE_OFF,
        COLLAPSE_MODE_PIN,
        COLLAPSE_MODE_PARALLAX
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface CollapseMode {
    }
  }

  public int getInsetTop() {
    return mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
  }

  private static final String TAG = "CollapsingToolbarLayout";

  private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
    @Override
    public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
      mCurrentOffset = verticalOffset;
      final int insetTop = getInsetTop();
      final int scrollRange = layout.getTotalScrollRange();

      for (int i = 0, z = getChildCount(); i < z; i++) {
        final View child = getChildAt(i);
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

        switch (lp.mCollapseMode) {
          case LayoutParams.COLLAPSE_MODE_PIN:
            if (getHeight() - insetTop + verticalOffset >= child.getHeight()) {
              offsetHelper.setTopAndBottomOffset(-verticalOffset);
            }
            break;
          case LayoutParams.COLLAPSE_MODE_PARALLAX:
            offsetHelper.setTopAndBottomOffset(
                Math.round(-verticalOffset * lp.mParallaxMult));
            break;
        }
      }

      // Show or hide the scrims if needed
      if (mContentScrim != null || mStatusBarScrim != null) {
        setScrimsShown(shouldTriggerScrimOffset(verticalOffset));
      }

      if (mStatusBarScrim != null && insetTop > 0) {
        ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
      }

      // Update the collapsing text's fraction
      final int expandRange = getHeight() - ViewCompat.getMinimumHeight(
          CollapsingToolbarLayout.this) - insetTop;
      float fraction = Math.abs(verticalOffset) / (float) expandRange;
      mCollapsingTextHelper.setExpansionFraction(fraction);
    }
  }

  public boolean shouldTriggerScrimOffset(int verticalOffset) {
    return getHeight() + verticalOffset < getScrimOffsetBound();
  }

  public int getScrimOffsetBound() {
    return getScrimTriggerOffset() + getInsetTop();
  }

  private AlphaForegroundColorSpan mTitleColorSpan;
  private SpannableString mSpannableTitle;
  private SpannableString mSpannableSubtitle;
  private float mLastTitleAlpha = 0.0f;

  private void animateTitleAlpha(float alpha) {
    if (mTitleAlphaAnimator == null) {
      mTitleAlphaAnimator = ViewUtils.createAnimator();
      mTitleAlphaAnimator.setDuration(SCRIM_ANIMATION_DURATION);
      mTitleAlphaAnimator.setInterpolator(AnimationUtils
          .FAST_OUT_SLOW_IN_INTERPOLATOR);
      mTitleAlphaAnimator.setUpdateListener(new ValueAnimatorCompat
          .AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimatorCompat animator) {
          setTitleAlpha(animator.getAnimatedFloatValue());
        }
      });
    } else if (mTitleAlphaAnimator.isRunning()) {
      mTitleAlphaAnimator.cancel();
    }

    mTitleAlphaAnimator.setFloatValues(mLastTitleAlpha, alpha);
    mTitleAlphaAnimator.start();
  }

  private void setTitleAlpha(float alpha) {
    if (alpha == mLastTitleAlpha) {
      return;
    }

    if (mToolbar == null) {
      return;
    }

    mLastTitleAlpha = alpha;
    CharSequence title = mToolbar.getTitle();
    CharSequence subTitle = mToolbar.getSubtitle();

    if (title != null && !TextUtils.isEmpty(title)) {
      if (mSpannableTitle == null) {
        mSpannableTitle = new SpannableString(title.toString());
      }
    }

    if (subTitle != null && !TextUtils.isEmpty(subTitle)) {
      if (mSpannableSubtitle == null) {
        mSpannableSubtitle = new SpannableString(subTitle.toString());
      }
    }

    if (mSpannableTitle == null || mTitleColorSpan == null || mToolbar == null) {
      return;
    }

    mTitleColorSpan.setAlpha(alpha);
    mSpannableTitle.setSpan(mTitleColorSpan, 0, mSpannableTitle.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mToolbar.setTitle(mSpannableTitle);

    if (mSpannableSubtitle != null) {
      mSpannableSubtitle.setSpan(mTitleColorSpan, 0, mSpannableSubtitle.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      mToolbar.setSubtitle(mSpannableSubtitle);
    }
  }

}
