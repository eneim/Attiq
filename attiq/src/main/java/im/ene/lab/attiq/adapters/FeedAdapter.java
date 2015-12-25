package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import im.ene.lab.attiq.data.api.open.FeedItem;
import retrofit.Callback;

import java.util.List;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedAdapter extends AttiqListAdapter<FeedItem> {

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int
      viewType) {
    return null;
  }

  @Override public int getItemCount() {
    return 0;
  }

  @Override public FeedItem getItem(int position) {
    return null;
  }

  @Override
  public void loadItems(boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, Callback<List<FeedItem>> callback) {

  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<FeedItem> {

    public ViewHolder(View itemView) {
      super(itemView);
    }

    @Override public void bind(FeedItem item) {

    }
  }
}
