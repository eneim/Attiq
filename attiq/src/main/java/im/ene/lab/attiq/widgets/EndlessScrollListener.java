package im.ene.lab.attiq.widgets;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by eneim on 12/15/15.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

  private final int mVisibleThreshold;
  private final LinearLayoutManager mLayoutManager;

  public EndlessScrollListener(@NonNull LinearLayoutManager layoutManager, int threshold) {
    super();
    this.mLayoutManager = layoutManager;
    this.mVisibleThreshold = threshold;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
    if ((mLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL && dy == 0)
        || (mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL && dx == 0)) {
      // so the view didn't make a real scroll, do nothing
      return;
    }

    if (mLayoutManager.findLastCompletelyVisibleItemPosition() >=
        mLayoutManager.getItemCount() - mVisibleThreshold * 0.2) {
      loadMore();
    }
  }

  protected abstract void loadMore();
}
