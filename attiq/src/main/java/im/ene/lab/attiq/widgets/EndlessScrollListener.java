package im.ene.lab.attiq.widgets;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by eneim on 12/15/15.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

  // The minimum number of items remaining before we should loading more.
  private static final int VISIBLE_THRESHOLD = 5;
  private final LinearLayoutManager mLayoutManager;

  public EndlessScrollListener(@NonNull LinearLayoutManager layoutManager) {
    super();
    this.mLayoutManager = layoutManager;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    final int visibleItemCount = recyclerView.getChildCount();
    final int totalItemCount = mLayoutManager.getItemCount();
    final int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

    if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
      loadMore();
    }
  }

  protected abstract void loadMore();
}
