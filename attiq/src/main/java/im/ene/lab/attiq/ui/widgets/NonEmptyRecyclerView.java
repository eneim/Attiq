package im.ene.lab.attiq.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by eneim on 12/6/15.
 * <p/>
 * RecyclerView which will show an Empty View if it has no item
 */
public class NonEmptyRecyclerView extends RecyclerView {

  @Nullable
  private View mEmptyView;

  @Nullable
  private View mErrorView;

  @NonNull
  private final AdapterDataObserver observer = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      super.onChanged();
      updateEmptyView();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      updateEmptyView();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      super.onItemRangeRemoved(positionStart, itemCount);
      updateEmptyView();
    }
  };

  private boolean isError;
  private int mVisibility;

  public NonEmptyRecyclerView(Context context) {
    this(context, null);
  }

  public NonEmptyRecyclerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NonEmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mVisibility = getVisibility();
  }

  @Override
  public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(observer);
    }

    if (adapter != null) {
      adapter.registerAdapterDataObserver(observer);
    }
    super.swapAdapter(adapter, removeAndRecycleExistingViews);
    updateEmptyView();
  }

  @Override
  public void setAdapter(@Nullable Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(observer);
    }

    if (adapter != null) {
      adapter.registerAdapterDataObserver(observer);
    }
    super.setAdapter(adapter);
    updateEmptyView();
  }

  /**
   * Check adapter item count and toggle visibility of empty view if the adapter is empty
   */
  private void updateEmptyView() {
    if (mEmptyView != null && getAdapter() != null) {
      boolean shouldShowEmptyView = getAdapter().getItemCount() == 0;
      mEmptyView.setVisibility(
          shouldShowEmptyView && !shouldShowErrorView() && mVisibility == VISIBLE ?
              VISIBLE : GONE);
      super.setVisibility(
          !shouldShowEmptyView && !shouldShowErrorView() && mVisibility == VISIBLE ?
              VISIBLE : GONE);
    }
  }

  /**
   * Indicates the view to be shown when the adapter for this RecyclerView is empty
   *
   * @param emptyView
   */
  public void setEmptyView(@Nullable View emptyView) {
    if (this.mEmptyView != null) {
      this.mEmptyView.setVisibility(GONE);
    }

    this.mEmptyView = emptyView;
    updateEmptyView();
  }

  public void setErrorView(View errorView) {
    if (this.mErrorView != null) {
      this.mErrorView.setVisibility(GONE);
    }
    this.mErrorView = errorView;
    updateErrorView();
    updateEmptyView();
  }

  private void updateErrorView() {
    if (mErrorView != null) {
      mErrorView.setVisibility(shouldShowErrorView() && mVisibility == VISIBLE ? VISIBLE : GONE);
    }
  }

  private boolean shouldShowErrorView() {
    return (getAdapter() == null || getAdapter().getItemCount() == 0)
        && mErrorView != null && isError;
  }

  private void showErrorView() {
    isError = true;
    updateErrorView();
    updateEmptyView();
  }

  private void hideErrorView() {
    isError = false;
    updateErrorView();
    updateEmptyView();
  }

  public void setErrorViewShown(boolean willShow) {
    if (willShow) {
      showErrorView();
    } else {
      hideErrorView();
    }
  }
}
